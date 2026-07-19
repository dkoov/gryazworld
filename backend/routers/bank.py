import logging
from datetime import datetime

import aiohttp
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from auth import verify_plugin_secret
from database import get_db, Player, BankAccount, BankAccountAccess, Transaction, Fine

log = logging.getLogger(__name__)

DISCORD_BOT_URL = "http://gryazworld-bot:5000/discord/notify"


async def _notify_discord(payload: dict):
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(DISCORD_BOT_URL, json=payload, timeout=aiohttp.ClientTimeout(total=5)) as resp:
                if resp.status != 200:
                    log.warning("Discord notify вернул статус %s", resp.status)
    except Exception as e:
        log.warning("Не удалось отправить уведомление в Discord: %s", e)


router = APIRouter(prefix="/mc/bank", tags=["bank"])


async def get_primary_account(player_id: int, db: AsyncSession):
    """Игровой плагин всегда работает только с ОСНОВНЫМ (is_primary) счётом игрока --
    дополнительные карты существуют только на сайте."""
    result = await db.execute(
        select(BankAccount).where(BankAccount.player_id == player_id, BankAccount.is_primary == True)  # noqa: E712
    )
    return result.scalar_one_or_none()


async def get_player_and_account(uuid: str, db: AsyncSession):
    result = await db.execute(select(Player).where(Player.uuid == uuid))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Player not found")

    account = await get_primary_account(player.id, db)
    if account is None:
        raise HTTPException(status_code=404, detail="У вас ещё нет банковского счёта. Обратитесь к банкиру.")

    return player, account


async def get_player_by_uuid(uuid: str, db: AsyncSession) -> Player:
    result = await db.execute(select(Player).where(Player.uuid == uuid))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Player not found")
    return player


async def get_own_accounts(player_id: int, db: AsyncSession) -> list[BankAccount]:
    result = await db.execute(
        select(BankAccount).where(BankAccount.player_id == player_id).order_by(BankAccount.is_primary.desc(), BankAccount.id)
    )
    return list(result.scalars().all())


async def get_own_account(uuid: str, account_id: int, db: AsyncSession) -> tuple[Player, BankAccount]:
    """Карта должна принадлежать игроку с этим uuid -- банкомат в игре работает только со своими картами."""
    player = await get_player_by_uuid(uuid, db)
    result = await db.execute(select(BankAccount).where(BankAccount.id == account_id))
    account = result.scalar_one_or_none()
    if account is None or account.player_id != player.id:
        raise HTTPException(status_code=404, detail="Карта не найдена")
    return player, account


def _card_label(account: BankAccount) -> str:
    if account.label:
        return account.label
    return "Основная карта" if account.is_primary else "Карта"


class DepositRequest(BaseModel):
    uuid: str
    amount: float
    comment: str = ""


class TransferRequest(BaseModel):
    from_uuid: str
    to_uuid: str
    amount: float
    comment: str = ""


class PayFineRequest(BaseModel):
    uuid: str
    fine_id: int


class CreateAccountRequest(BaseModel):
    uuid: str
    nickname: str


@router.get("/{uuid}/balance", dependencies=[Depends(verify_plugin_secret)])
async def get_balance(uuid: str, db: AsyncSession = Depends(get_db)):
    _, account = await get_player_and_account(uuid, db)
    return {"uuid": uuid, "balance": account.balance}


@router.post("/create", dependencies=[Depends(verify_plugin_secret)])
async def create_account(data: CreateAccountRequest, db: AsyncSession = Depends(get_db)):
    """Банкир открывает игроку счёт (/bank <ник> в игре, когда счёта ещё нет)."""
    result = await db.execute(select(Player).where(Player.uuid == data.uuid))
    player = result.scalar_one_or_none()
    if player is None:
        player = Player(uuid=data.uuid, nickname=data.nickname, is_online=True)
        db.add(player)
        await db.flush()

    existing = await get_primary_account(player.id, db)
    if existing is not None:
        raise HTTPException(status_code=400, detail="У игрока уже есть счёт")

    account = BankAccount(player_id=player.id, balance=0.0, is_primary=True, created_at=datetime.utcnow())
    db.add(account)
    await db.commit()
    await db.refresh(account)

    return {"status": "ok", "balance": account.balance}


@router.post("/deposit", dependencies=[Depends(verify_plugin_secret)])
async def deposit(data: DepositRequest, db: AsyncSession = Depends(get_db)):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive")

    player, account = await get_player_and_account(data.uuid, db)
    account.balance += data.amount

    tx = Transaction(
        to_player_id=player.id,
        to_account_id=account.id,
        amount=data.amount,
        type="deposit",
        comment=data.comment
    )
    db.add(tx)
    await db.commit()

    return {"status": "ok", "balance": account.balance}


@router.post("/transfer", dependencies=[Depends(verify_plugin_secret)])
async def transfer(data: TransferRequest, db: AsyncSession = Depends(get_db)):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive")
    if data.from_uuid == data.to_uuid:
        raise HTTPException(status_code=400, detail="Cannot transfer to yourself")

    from_player, from_account = await get_player_and_account(data.from_uuid, db)
    to_player, to_account = await get_player_and_account(data.to_uuid, db)

    if from_account.balance < data.amount:
        raise HTTPException(status_code=400, detail="Insufficient funds")

    from_account.balance -= data.amount
    to_account.balance += data.amount

    tx = Transaction(
        from_player_id=from_player.id,
        to_player_id=to_player.id,
        from_account_id=from_account.id,
        to_account_id=to_account.id,
        amount=data.amount,
        type="transfer",
        comment=data.comment
    )
    db.add(tx)
    await db.commit()

    return {"status": "ok", "from_balance": from_account.balance, "to_balance": to_account.balance}


class WithdrawRequest(BaseModel):
    nickname: str
    amount: int
    comment: str = ""


@router.post("/withdraw", dependencies=[Depends(verify_plugin_secret)])
async def withdraw(data: WithdrawRequest, db: AsyncSession = Depends(get_db)):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive")

    player = await db.execute(select(Player).where(func.lower(Player.nickname) == data.nickname.lower()))
    player = player.scalar_one_or_none()
    if not player:
        raise HTTPException(404, "Игрок не найден")

    account = await get_primary_account(player.id, db)
    if not account:
        raise HTTPException(404, "Счёт не найден")

    if account.balance < data.amount:
        raise HTTPException(400, "Недостаточно средств")

    account.balance -= data.amount

    tx = Transaction(
        from_player_id=player.id,
        from_account_id=account.id,
        amount=data.amount,
        type="withdraw",
        comment=data.comment,
    )
    db.add(tx)
    await db.commit()
    return {"balance": account.balance}


@router.post("/pay_fine", dependencies=[Depends(verify_plugin_secret)])
async def pay_fine(data: PayFineRequest, db: AsyncSession = Depends(get_db)):
    player, account = await get_player_and_account(data.uuid, db)

    result = await db.execute(
        select(Fine).where(Fine.id == data.fine_id, Fine.player_id == player.id)
    )
    fine = result.scalar_one_or_none()

    if fine is None:
        raise HTTPException(status_code=404, detail="Fine not found")
    if fine.status != "pending":
        raise HTTPException(status_code=400, detail=f"Fine is already {fine.status}")
    if account.balance < fine.amount:
        raise HTTPException(status_code=400, detail="Insufficient funds")

    account.balance -= fine.amount
    fine.status = "paid"

    tx = Transaction(
        from_player_id=player.id,
        from_account_id=account.id,
        amount=fine.amount,
        type="fine_payment",
        comment=f"Fine #{fine.id}: {fine.reason}"
    )
    db.add(tx)
    await db.commit()

    await _notify_discord({
        "type": "fine_paid",
        "nickname": player.nickname,
        "discord_id": player.discord_id if hasattr(player, "discord_id") else None,
        "fine_id": fine.id,
        "reason": fine.reason,
        "amount": fine.amount,
    })

    return {"status": "ok", "fine_id": fine.id, "balance": account.balance}


# ─── Карты (банкомат в игре: /bank cards) ────────────────────────────────────
# Дополнительные счета игрока -- то же самое, что "карты" на сайте (bank_web.py),
# но доступ только к своим собственным (в игре доступ другим картам не даётся).

class CreateCardRequest(BaseModel):
    uuid: str
    label: str = ""


class CardAmountRequest(BaseModel):
    amount: float
    comment: str = ""


class CardTransferRequest(BaseModel):
    to_nickname: str
    amount: float
    comment: str = ""


@router.get("/{uuid}/accounts", dependencies=[Depends(verify_plugin_secret)])
async def list_cards(uuid: str, db: AsyncSession = Depends(get_db)):
    player = await get_player_by_uuid(uuid, db)
    accounts = await get_own_accounts(player.id, db)
    return [
        {
            "id": a.id,
            "label": _card_label(a),
            "balance": a.balance,
            "is_primary": a.is_primary,
        }
        for a in accounts
    ]


@router.post("/{uuid}/accounts", dependencies=[Depends(verify_plugin_secret)])
async def create_card(uuid: str, data: CreateCardRequest, db: AsyncSession = Depends(get_db)):
    player = await get_player_by_uuid(uuid, db)
    own = await get_own_accounts(player.id, db)
    if not any(a.is_primary for a in own):
        raise HTTPException(status_code=400, detail="Сначала обратитесь к банкиру за основным счётом")
    if len(own) >= 8:
        raise HTTPException(status_code=400, detail="Слишком много карт (максимум 8)")

    account = BankAccount(
        player_id=player.id, balance=0.0, is_primary=False,
        label=data.label.strip()[:40] or None, created_at=datetime.utcnow(),
    )
    db.add(account)
    await db.commit()
    await db.refresh(account)
    return {"id": account.id, "label": _card_label(account), "balance": 0.0, "is_primary": False}


@router.post("/{uuid}/accounts/{account_id}/deposit", dependencies=[Depends(verify_plugin_secret)])
async def deposit_card(uuid: str, account_id: int, data: CardAmountRequest, db: AsyncSession = Depends(get_db)):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive")

    player, account = await get_own_account(uuid, account_id, db)
    account.balance += data.amount
    db.add(Transaction(
        to_player_id=player.id, to_account_id=account.id,
        amount=data.amount, type="deposit", comment=data.comment,
    ))
    await db.commit()
    return {"status": "ok", "balance": account.balance}


@router.post("/{uuid}/accounts/{account_id}/withdraw", dependencies=[Depends(verify_plugin_secret)])
async def withdraw_card(uuid: str, account_id: int, data: CardAmountRequest, db: AsyncSession = Depends(get_db)):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive")

    player, account = await get_own_account(uuid, account_id, db)
    if account.balance < data.amount:
        raise HTTPException(status_code=400, detail="Недостаточно средств")

    account.balance -= data.amount
    db.add(Transaction(
        from_player_id=player.id, from_account_id=account.id,
        amount=data.amount, type="withdraw", comment=data.comment,
    ))
    await db.commit()
    return {"status": "ok", "balance": account.balance}


@router.post("/{uuid}/accounts/{account_id}/transfer", dependencies=[Depends(verify_plugin_secret)])
async def transfer_card(uuid: str, account_id: int, data: CardTransferRequest, db: AsyncSession = Depends(get_db)):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive")

    from_player, from_account = await get_own_account(uuid, account_id, db)

    to_result = await db.execute(select(Player).where(func.lower(Player.nickname) == data.to_nickname.strip().lower()))
    to_player = to_result.scalar_one_or_none()
    if not to_player:
        raise HTTPException(status_code=404, detail="Игрок не найден")

    to_account = await get_primary_account(to_player.id, db)
    if not to_account:
        raise HTTPException(status_code=404, detail="У получателя нет счёта")
    if to_account.id == from_account.id:
        raise HTTPException(status_code=400, detail="Нельзя перевести на эту же карту")

    if from_account.balance < data.amount:
        raise HTTPException(status_code=400, detail="Недостаточно средств")

    from_account.balance -= data.amount
    to_account.balance += data.amount
    db.add(Transaction(
        from_player_id=from_player.id, to_player_id=to_player.id,
        from_account_id=from_account.id, to_account_id=to_account.id,
        amount=data.amount, type="transfer", comment=data.comment,
    ))
    await db.commit()
    return {"status": "ok", "balance": from_account.balance}


@router.delete("/{uuid}/accounts/{account_id}", dependencies=[Depends(verify_plugin_secret)])
async def close_card(uuid: str, account_id: int, db: AsyncSession = Depends(get_db)):
    player, account = await get_own_account(uuid, account_id, db)
    if account.is_primary:
        raise HTTPException(status_code=400, detail="Основную карту закрыть нельзя")

    if account.balance > 0:
        primary = await get_primary_account(player.id, db)
        if primary is None:
            raise HTTPException(status_code=400, detail="Нет основного счёта для возврата остатка")
        primary.balance += account.balance
        db.add(Transaction(
            from_player_id=player.id, to_player_id=player.id,
            from_account_id=account.id, to_account_id=primary.id,
            amount=account.balance, type="transfer", comment="Закрытие карты -- возврат остатка",
        ))
        account.balance = 0.0

    await db.execute(BankAccountAccess.__table__.delete().where(BankAccountAccess.account_id == account_id))
    await db.delete(account)
    await db.commit()
    return {"status": "ok"}
