from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from auth import verify_plugin_secret
from database import get_db, Player, BankAccount

router = APIRouter(prefix="/mc/player", tags=["player"])


class PlayerJoinRequest(BaseModel):
    uuid: str
    nickname: str


class PlayerQuitRequest(BaseModel):
    uuid: str
    session_seconds: int


@router.post("/join", dependencies=[Depends(verify_plugin_secret)])
async def player_join(data: PlayerJoinRequest, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Player).where(Player.uuid == data.uuid))
    player = result.scalar_one_or_none()

    if player is None:
        player = Player(uuid=data.uuid, nickname=data.nickname)
        db.add(player)
        await db.flush()

        account = BankAccount(player_id=player.id, balance=0.0)
        db.add(account)
        await db.commit()
        return {"status": "created", "uuid": data.uuid, "nickname": data.nickname}
    else:
        player.nickname = data.nickname
        await db.commit()
        return {"status": "updated", "uuid": data.uuid, "nickname": data.nickname}


@router.post("/quit", dependencies=[Depends(verify_plugin_secret)])
async def player_quit(data: PlayerQuitRequest, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Player).where(Player.uuid == data.uuid))
    player = result.scalar_one_or_none()

    if player is None:
        raise HTTPException(status_code=404, detail="Player not found")

    player.total_seconds += data.session_seconds
    await db.commit()

    return {
        "status": "ok",
        "uuid": data.uuid,
        "total_seconds": player.total_seconds
    }


@router.get("/discord-id")
async def get_discord_id(nickname: str, db: AsyncSession = Depends(get_db)):
    """Get discord_id by Minecraft nickname."""
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == nickname.lower())
    )
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Игрок не найден")
    if not player.discord_id:
        raise HTTPException(status_code=404, detail="Discord не привязан")
    return {"discord_id": player.discord_id}
