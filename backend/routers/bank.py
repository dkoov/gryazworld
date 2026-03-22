from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from auth import verify_plugin_secret
from database import get_db, Player, BankAccount, Transaction, Fine

router = APIRouter(prefix="/mc/bank", tags=["bank"])


async def get_player_and_account(uuid: str, db: AsyncSession):
    result = await db.execute(select(Player).where(Player.uuid == uuid))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Player not found")

    result = await db.execute(select(BankAccount).where(BankAccount.player_id == player.id))
    account = result.scalar_one_or_none()
    if account is None:
        raise HTTPException(status_code=404, detail="Bank account not found")

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


@router.get("/{uuid}/balance", dependencies=[Depends(verify_plugin_secret)])
async def get_balance(uuid: str, db: AsyncSession = Depends(get_db)):
    _, account = await get_player_and_account(uuid, db)
    return {"uuid": uuid, "balance": account.balance}


@router.post("/deposit", dependencies=[Depends(verify_plugin_secret)])
async def deposit(data: DepositRequest, db: AsyncSession = Depends(get_db)):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive")

    player, account = await get_player_and_account(data.uuid, db)
    account.balance += data.amount

    tx = Transaction(
        to_player_id=player.id,
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
        amount=data.amount,
        type="transfer",
        comment=data.comment
    )
    db.add(tx)
    await db.commit()

    return {"status": "ok", "from_balance": from_account.balance, "to_balance": to_account.balance}


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
        amount=fine.amount,
        type="fine_payment",
        comment=f"Fine #{fine.id}: {fine.reason}"
    )
    db.add(tx)
    await db.commit()

    return {"status": "ok", "fine_id": fine.id, "balance": account.balance}
