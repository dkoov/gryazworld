from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func, or_
from sqlalchemy.ext.asyncio import AsyncSession

from auth import CurrentUser, current_user
from database import get_db, Player, BankAccount, BankAccountAccess, Transaction, Invoice

router = APIRouter(prefix="/web/bank", tags=["bank-web"])


async def _resolve_self(user: CurrentUser, db: AsyncSession) -> Player:
    result = await db.execute(select(Player).where(Player.discord_id == user.discord_id))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Сначала привяжи Minecraft-аккаунт")
    return player


async def _own_accounts(player_id: int, db: AsyncSession) -> list[BankAccount]:
    result = await db.execute(
        select(BankAccount).where(BankAccount.player_id == player_id).order_by(BankAccount.is_primary.desc(), BankAccount.id)
    )
    return list(result.scalars().all())


async def _accessible_account_ids(player_id: int, db: AsyncSession) -> set[int]:
    result = await db.execute(select(BankAccountAccess.account_id).where(BankAccountAccess.player_id == player_id))
    return {r[0] for r in result.all()}


async def _get_account_for(account_id: int, player: Player, db: AsyncSession, require_owner: bool = False) -> BankAccount:
    result = await db.execute(select(BankAccount).where(BankAccount.id == account_id))
    account = result.scalar_one_or_none()
    if account is None:
        raise HTTPException(status_code=404, detail="Счёт не найден")

    is_owner = account.player_id == player.id
    if require_owner and not is_owner:
        raise HTTPException(status_code=403, detail="Только владелец счёта может это сделать")

    if not is_owner:
        access = await db.execute(
            select(BankAccountAccess).where(
                BankAccountAccess.account_id == account_id, BankAccountAccess.player_id == player.id
            )
        )
        if access.scalar_one_or_none() is None:
            raise HTTPException(status_code=403, detail="Нет доступа к этому счёту")

    return account


def _account_label(account: BankAccount, owner_nickname: str) -> str:
    if account.label:
        return account.label
    return "Основная карта" if account.is_primary else "Карта"


@router.get("/accounts")
async def list_accounts(user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)):
    me = await _resolve_self(user, db)

    own = await _own_accounts(me.id, db)
    accessible_ids = await _accessible_account_ids(me.id, db)

    accounts = list(own)
    if accessible_ids:
        shared_result = await db.execute(select(BankAccount).where(BankAccount.id.in_(accessible_ids)))
        accounts += list(shared_result.scalars().all())

    owner_ids = {a.player_id for a in accounts}
    owners_result = await db.execute(select(Player).where(Player.id.in_(owner_ids)))
    owners = {p.id: p for p in owners_result.scalars().all()}

    return [
        {
            "id": a.id,
            "label": _account_label(a, owners[a.player_id].nickname if a.player_id in owners else ""),
            "balance": a.balance,
            "hide_balance": a.hide_balance,
            "is_primary": a.is_primary,
            "is_owner": a.player_id == me.id,
            "owner_nickname": owners[a.player_id].nickname if a.player_id in owners else None,
        }
        for a in accounts
    ]


class CreateAccountRequest(BaseModel):
    label: str = ""


@router.post("/accounts")
async def create_account(
    data: CreateAccountRequest, user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)
):
    me = await _resolve_self(user, db)
    own = await _own_accounts(me.id, db)
    if not any(a.is_primary for a in own):
        raise HTTPException(status_code=403, detail="Сначала обратись к банкиру за основным счётом")

    account = BankAccount(
        player_id=me.id,
        balance=0.0,
        is_primary=False,
        label=data.label.strip()[:40] or None,
        created_at=datetime.utcnow(),
    )
    db.add(account)
    await db.commit()
    await db.refresh(account)
    return {"id": account.id, "label": _account_label(account, me.nickname), "balance": 0.0}


class UpdateAccountRequest(BaseModel):
    label: str | None = None
    hide_balance: bool | None = None


@router.patch("/accounts/{account_id}")
async def update_account(
    account_id: int,
    data: UpdateAccountRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    me = await _resolve_self(user, db)
    account = await _get_account_for(account_id, me, db, require_owner=True)

    if data.label is not None:
        account.label = data.label.strip()[:40] or None
    if data.hide_balance is not None:
        account.hide_balance = data.hide_balance

    await db.commit()
    return {"status": "ok"}


@router.delete("/accounts/{account_id}")
async def delete_account(
    account_id: int, user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)
):
    me = await _resolve_self(user, db)
    account = await _get_account_for(account_id, me, db, require_owner=True)

    if account.is_primary:
        raise HTTPException(status_code=400, detail="Основной счёт удалить нельзя")

    if account.balance > 0:
        primary_result = await db.execute(
            select(BankAccount).where(BankAccount.player_id == me.id, BankAccount.is_primary == True)  # noqa: E712
        )
        primary = primary_result.scalar_one_or_none()
        if primary is None:
            raise HTTPException(status_code=400, detail="Нет основного счёта для возврата остатка")
        primary.balance += account.balance
        db.add(Transaction(
            from_player_id=me.id, to_player_id=me.id,
            from_account_id=account.id, to_account_id=primary.id,
            amount=account.balance, type="transfer", comment="Закрытие карты -- возврат остатка",
        ))
        account.balance = 0.0

    await db.execute(
        BankAccountAccess.__table__.delete().where(BankAccountAccess.account_id == account_id)
    )
    await db.delete(account)
    await db.commit()
    return {"status": "ok"}


@router.get("/accounts/{account_id}/transactions")
async def account_transactions(
    account_id: int, user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)
):
    me = await _resolve_self(user, db)
    account = await _get_account_for(account_id, me, db)

    tx_result = await db.execute(
        select(Transaction)
        .where(or_(Transaction.from_account_id == account_id, Transaction.to_account_id == account_id))
        .order_by(Transaction.created_at.desc())
        .limit(100)
    )
    txs = tx_result.scalars().all()

    counterparty_ids = {t.from_player_id for t in txs} | {t.to_player_id for t in txs}
    counterparty_ids.discard(None)
    names = {}
    if counterparty_ids:
        players_result = await db.execute(select(Player).where(Player.id.in_(counterparty_ids)))
        names = {p.id: p.nickname for p in players_result.scalars().all()}

    result = []
    for t in txs:
        outgoing = t.from_account_id == account_id
        counterparty_id = t.to_player_id if outgoing else t.from_player_id
        result.append({
            "id": t.id,
            "type": t.type,
            "amount": t.amount,
            "outgoing": outgoing,
            "comment": t.comment,
            "counterparty": names.get(counterparty_id) if counterparty_id else None,
            "created_at": t.created_at.isoformat(),
        })
    return result


class AccountTransferRequest(BaseModel):
    to_nickname: str
    amount: float
    comment: str = ""


@router.post("/accounts/{account_id}/transfer")
async def account_transfer(
    account_id: int,
    data: AccountTransferRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Сумма должна быть положительной")

    me = await _resolve_self(user, db)
    from_account = await _get_account_for(account_id, me, db)

    to_result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.to_nickname.strip().lower())
    )
    to_player = to_result.scalar_one_or_none()
    if not to_player:
        raise HTTPException(status_code=404, detail="Игрок не найден")
    if to_player.id == from_account.player_id and to_player.id == me.id:
        raise HTTPException(status_code=400, detail="Нельзя перевести самому себе")

    to_account_result = await db.execute(
        select(BankAccount).where(BankAccount.player_id == to_player.id, BankAccount.is_primary == True)  # noqa: E712
    )
    to_account = to_account_result.scalar_one_or_none()
    if not to_account:
        raise HTTPException(status_code=404, detail="У получателя нет счёта")
    if to_account.id == from_account.id:
        raise HTTPException(status_code=400, detail="Нельзя перевести на этот же счёт")

    if from_account.balance < data.amount:
        raise HTTPException(status_code=400, detail="Недостаточно средств")

    from_account.balance -= data.amount
    to_account.balance += data.amount

    db.add(Transaction(
        from_player_id=from_account.player_id, to_player_id=to_player.id,
        from_account_id=from_account.id, to_account_id=to_account.id,
        amount=data.amount, type="transfer", comment=data.comment,
    ))
    await db.commit()

    return {"status": "ok", "balance": from_account.balance}


@router.get("/accounts/{account_id}/access")
async def list_access(
    account_id: int, user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)
):
    me = await _resolve_self(user, db)
    account = await _get_account_for(account_id, me, db)

    owner_result = await db.execute(select(Player).where(Player.id == account.player_id))
    owner = owner_result.scalar_one_or_none()

    access_result = await db.execute(select(BankAccountAccess).where(BankAccountAccess.account_id == account_id))
    grants = access_result.scalars().all()
    grantee_ids = [g.player_id for g in grants]
    grantees = {}
    if grantee_ids:
        gr = await db.execute(select(Player).where(Player.id.in_(grantee_ids)))
        grantees = {p.id: p for p in gr.scalars().all()}

    return {
        "owner": owner.nickname if owner else None,
        "members": [
            {"player_id": g.player_id, "nickname": grantees[g.player_id].nickname}
            for g in grants if g.player_id in grantees
        ],
    }


class GrantAccessRequest(BaseModel):
    nickname: str


@router.post("/accounts/{account_id}/access")
async def grant_access(
    account_id: int,
    data: GrantAccessRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    me = await _resolve_self(user, db)
    account = await _get_account_for(account_id, me, db, require_owner=True)

    target_result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.nickname.strip().lower())
    )
    target = target_result.scalar_one_or_none()
    if not target:
        raise HTTPException(status_code=404, detail="Игрок не найден")
    if target.id == account.player_id:
        raise HTTPException(status_code=400, detail="Владелец и так имеет доступ")

    existing = await db.execute(
        select(BankAccountAccess).where(
            BankAccountAccess.account_id == account_id, BankAccountAccess.player_id == target.id
        )
    )
    if existing.scalar_one_or_none() is not None:
        raise HTTPException(status_code=400, detail="У игрока уже есть доступ")

    db.add(BankAccountAccess(account_id=account_id, player_id=target.id, granted_at=datetime.utcnow()))
    await db.commit()
    return {"status": "ok"}


@router.delete("/accounts/{account_id}/access/{player_id}")
async def revoke_access(
    account_id: int,
    player_id: int,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    me = await _resolve_self(user, db)
    await _get_account_for(account_id, me, db, require_owner=True)

    await db.execute(
        BankAccountAccess.__table__.delete().where(
            BankAccountAccess.account_id == account_id, BankAccountAccess.player_id == player_id
        )
    )
    await db.commit()
    return {"status": "ok"}


# ─── Счета ("Выставить счёт") ────────────────────────────────────────────────

class CreateInvoiceRequest(BaseModel):
    debtor_nickname: str
    amount: float
    comment: str = ""


@router.post("/accounts/{account_id}/invoices")
async def create_invoice(
    account_id: int,
    data: CreateInvoiceRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Сумма должна быть положительной")

    me = await _resolve_self(user, db)
    account = await _get_account_for(account_id, me, db)

    debtor_result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.debtor_nickname.strip().lower())
    )
    debtor = debtor_result.scalar_one_or_none()
    if not debtor:
        raise HTTPException(status_code=404, detail="Игрок не найден")
    if debtor.id == me.id:
        raise HTTPException(status_code=400, detail="Нельзя выставить счёт самому себе")

    invoice = Invoice(
        account_id=account.id,
        creditor_player_id=me.id,
        debtor_player_id=debtor.id,
        amount=data.amount,
        comment=data.comment.strip()[:350],
    )
    db.add(invoice)
    await db.commit()
    return {"status": "ok"}


@router.get("/invoices")
async def list_invoices(user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)):
    me = await _resolve_self(user, db)

    result = await db.execute(
        select(Invoice)
        .where(or_(Invoice.creditor_player_id == me.id, Invoice.debtor_player_id == me.id))
        .order_by(Invoice.created_at.desc())
        .limit(100)
    )
    invoices = result.scalars().all()
    if not invoices:
        return []

    player_ids = {i.creditor_player_id for i in invoices} | {i.debtor_player_id for i in invoices}
    players_result = await db.execute(select(Player).where(Player.id.in_(player_ids)))
    players = {p.id: p for p in players_result.scalars().all()}

    account_ids = {i.account_id for i in invoices}
    accounts_result = await db.execute(select(BankAccount).where(BankAccount.id.in_(account_ids)))
    accounts_by_id = {a.id: a for a in accounts_result.scalars().all()}

    out = []
    for i in invoices:
        outgoing = i.creditor_player_id == me.id  # выставил я -- жду оплаты
        counterparty_id = i.debtor_player_id if outgoing else i.creditor_player_id
        counterparty = players.get(counterparty_id)
        account = accounts_by_id.get(i.account_id)
        out.append({
            "id": i.id,
            "outgoing": outgoing,
            "counterparty": counterparty.nickname if counterparty else None,
            "account_label": _account_label(account, "") if account else None,
            "amount": i.amount,
            "comment": i.comment,
            "status": i.status,
            "created_at": i.created_at.isoformat(),
        })
    return out


@router.post("/invoices/{invoice_id}/pay")
async def pay_invoice(
    invoice_id: int, user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)
):
    me = await _resolve_self(user, db)

    result = await db.execute(select(Invoice).where(Invoice.id == invoice_id))
    invoice = result.scalar_one_or_none()
    if invoice is None:
        raise HTTPException(status_code=404, detail="Счёт не найден")
    if invoice.debtor_player_id != me.id:
        raise HTTPException(status_code=403, detail="Это не твой счёт для оплаты")
    if invoice.status != "pending":
        raise HTTPException(status_code=400, detail="Счёт уже закрыт")

    my_primary_result = await db.execute(
        select(BankAccount).where(BankAccount.player_id == me.id, BankAccount.is_primary == True)  # noqa: E712
    )
    my_primary = my_primary_result.scalar_one_or_none()
    if my_primary is None:
        raise HTTPException(status_code=400, detail="У тебя нет счёта для оплаты")
    if my_primary.balance < invoice.amount:
        raise HTTPException(status_code=400, detail="Недостаточно средств")

    creditor_account_result = await db.execute(select(BankAccount).where(BankAccount.id == invoice.account_id))
    creditor_account = creditor_account_result.scalar_one_or_none()
    if creditor_account is None:
        raise HTTPException(status_code=400, detail="Счёт получателя не найден")

    my_primary.balance -= invoice.amount
    creditor_account.balance += invoice.amount
    invoice.status = "paid"
    invoice.resolved_at = datetime.utcnow()

    db.add(Transaction(
        from_player_id=me.id, to_player_id=invoice.creditor_player_id,
        from_account_id=my_primary.id, to_account_id=creditor_account.id,
        amount=invoice.amount, type="invoice_payment", comment=invoice.comment,
    ))
    await db.commit()
    return {"status": "ok", "balance": my_primary.balance}


@router.post("/invoices/{invoice_id}/decline")
async def decline_invoice(
    invoice_id: int, user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)
):
    me = await _resolve_self(user, db)

    result = await db.execute(select(Invoice).where(Invoice.id == invoice_id))
    invoice = result.scalar_one_or_none()
    if invoice is None:
        raise HTTPException(status_code=404, detail="Счёт не найден")
    if invoice.debtor_player_id != me.id:
        raise HTTPException(status_code=403, detail="Это не твой счёт")
    if invoice.status != "pending":
        raise HTTPException(status_code=400, detail="Счёт уже закрыт")

    invoice.status = "declined"
    invoice.resolved_at = datetime.utcnow()
    await db.commit()
    return {"status": "ok"}
