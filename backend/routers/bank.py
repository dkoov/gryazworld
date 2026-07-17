import logging
from datetime import datetime

import aiohttp
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from auth import verify_plugin_secret
from database import get_db, Player, BankAccount, Transaction, Fine

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
