import logging
from datetime import datetime
from typing import Optional

import aiohttp
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from auth import verify_plugin_secret
from database import get_db, Player, Fine, Warn

log = logging.getLogger(__name__)

DISCORD_BOT_URL = "http://localhost:5000/discord/notify"


async def _notify_discord(payload: dict):
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(DISCORD_BOT_URL, json=payload, timeout=aiohttp.ClientTimeout(total=5)) as resp:
                if resp.status != 200:
                    log.warning("Discord notify вернул статус %s", resp.status)
    except Exception as e:
        log.warning("Не удалось отправить уведомление в Discord: %s", e)


router = APIRouter(prefix="/mc/fines", tags=["fines"])
warn_router = APIRouter(prefix="/mc/warn", tags=["warns"])


class IssueFineRequest(BaseModel):
    uuid: str
    issued_by: str
    amount: float
    reason: str
    deadline: Optional[datetime] = None


class IssueWarnRequest(BaseModel):
    uuid: str
    issued_by: str
    reason: str


class CancelFineRequest(BaseModel):
    fine_id: int


class RemoveWarnRequest(BaseModel):
    uuid: str
    amount: int = 1


async def get_player(uuid: str, db: AsyncSession) -> Player:
    result = await db.execute(select(Player).where(Player.uuid == uuid))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Player not found")
    return player


@router.get("/by-nick/{nickname}", dependencies=[Depends(verify_plugin_secret)])
async def get_fines_by_nickname(nickname: str, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == nickname.lower())
    )
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Player not found")

    fines_result = await db.execute(select(Fine).where(Fine.player_id == player.id))
    fines = fines_result.scalars().all()

    warns_result = await db.execute(select(Warn).where(Warn.player_id == player.id))
    warns = warns_result.scalars().all()

    return {
        "uuid": player.uuid,
        "nickname": player.nickname,
        "total_warns": player.warns,
        "fines": [
            {
                "id": f.id,
                "amount": f.amount,
                "reason": f.reason,
                "issued_by": f.issued_by,
                "deadline": f.deadline,
                "status": f.status,
                "created_at": f.created_at,
            }
            for f in fines
        ],
        "warns": [
            {
                "id": w.id,
                "reason": w.reason,
                "issued_by": w.issued_by,
                "created_at": w.created_at,
            }
            for w in warns
        ],
    }


@router.post("/issue", dependencies=[Depends(verify_plugin_secret)])
async def issue_fine(data: IssueFineRequest, db: AsyncSession = Depends(get_db)):
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be positive")

    player = await get_player(data.uuid, db)

    fine = Fine(
        player_id=player.id,
        issued_by=data.issued_by,
        amount=data.amount,
        reason=data.reason,
        deadline=data.deadline,
        status="pending"
    )
    db.add(fine)
    await db.commit()
    await db.refresh(fine)

    await _notify_discord({
        "type": "fine",
        "fine_id": fine.id,
        "player": player.nickname,
        "amount": fine.amount,
        "reason": fine.reason,
        "issued_by": fine.issued_by,
        "deadline": fine.deadline.isoformat() if fine.deadline else None,
    })

    return {
        "status": "ok",
        "fine_id": fine.id,
        "player": player.nickname,
        "amount": fine.amount,
        "reason": fine.reason,
        "deadline": fine.deadline
    }


@router.get("/overdue", dependencies=[Depends(verify_plugin_secret)])
async def process_overdue(db: AsyncSession = Depends(get_db)):
    now = datetime.utcnow()
    result = await db.execute(
        select(Fine).where(Fine.status == "pending", Fine.deadline < now, Fine.deadline.isnot(None))
    )
    overdue_fines = result.scalars().all()

    processed = []
    for fine in overdue_fines:
        fine.status = "overdue"

        player_result = await db.execute(select(Player).where(Player.id == fine.player_id))
        player = player_result.scalar_one_or_none()
        if player is None:
            continue

        await _notify_discord({
            "type": "fine_overdue",
            "fine_id": fine.id,
            "player": player.nickname,
            "amount": fine.amount,
            "reason": fine.reason,
        })

        processed.append({
            "fine_id": fine.id,
            "player": player.nickname,
        })

    await db.commit()
    return {"processed": len(processed), "fines": processed}


@router.get("/{uuid}", dependencies=[Depends(verify_plugin_secret)])
async def get_fines(uuid: str, db: AsyncSession = Depends(get_db)):
    player = await get_player(uuid, db)

    result = await db.execute(select(Fine).where(Fine.player_id == player.id, Fine.status.in_(["pending", "overdue", "paid"])))
    fines = result.scalars().all()

    return {
        "uuid": uuid,
        "fines": [
            {
                "id": f.id,
                "amount": f.amount,
                "reason": f.reason,
                "issued_by": f.issued_by,
                "deadline": f.deadline,
                "status": f.status,
                "created_at": f.created_at
            }
            for f in fines
        ]
    }


@router.post("/cancel", dependencies=[Depends(verify_plugin_secret)])
async def cancel_fine(data: CancelFineRequest, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Fine).where(Fine.id == data.fine_id))
    fine = result.scalar_one_or_none()

    if fine is None:
        raise HTTPException(status_code=404, detail="Fine not found")
    if fine.status != "pending":
        raise HTTPException(status_code=400, detail=f"Fine is already {fine.status}")

    fine.status = "cancelled"
    await db.commit()

    return {"status": "ok", "fine_id": fine.id}


@router.post("/warn", dependencies=[Depends(verify_plugin_secret)])
async def issue_warn(data: IssueWarnRequest, db: AsyncSession = Depends(get_db)):
    player = await get_player(data.uuid, db)

    warn = Warn(
        player_id=player.id,
        issued_by=data.issued_by,
        reason=data.reason
    )
    db.add(warn)
    player.warns += 1
    await db.commit()
    await db.refresh(warn)

    await _notify_discord({
        "type": "warn",
        "warn_id": warn.id,
        "player": player.nickname,
        "total_warns": player.warns,
        "reason": warn.reason,
        "issued_by": warn.issued_by,
    })

    if player.warns >= 3:
        await _notify_discord({
            "type": "ban",
            "player": player.nickname,
        })

    return {
        "status": "ok",
        "warn_id": warn.id,
        "player": player.nickname,
        "total_warns": player.warns,
        "reason": warn.reason
    }


@router.get("/{uuid}/warns", dependencies=[Depends(verify_plugin_secret)])
async def get_warns(uuid: str, db: AsyncSession = Depends(get_db)):
    player = await get_player(uuid, db)

    result = await db.execute(select(Warn).where(Warn.player_id == player.id))
    warns = result.scalars().all()

    return {
        "uuid": uuid,
        "total_warns": player.warns,
        "warns": [
            {
                "id": w.id,
                "reason": w.reason,
                "issued_by": w.issued_by,
                "created_at": w.created_at
            }
            for w in warns
        ]
    }


@warn_router.post("/remove", dependencies=[Depends(verify_plugin_secret)])
async def remove_warn(data: RemoveWarnRequest, db: AsyncSession = Depends(get_db)):
    player = await get_player(data.uuid, db)

    was_banned = player.warns >= 3
    player.warns = max(0, player.warns - data.amount)
    await db.commit()

    await _notify_discord({
        "type": "warn_remove",
        "uuid": player.uuid,
        "player": player.nickname,
        "total_warns": player.warns,
    })

    if was_banned and player.warns < 3:
        await _notify_discord({
            "type": "unban",
            "player": player.nickname,
        })

    return {
        "status": "ok",
        "player": player.nickname,
        "warns": player.warns,
    }


