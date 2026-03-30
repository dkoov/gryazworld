import os
import aiohttp
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from database import get_db, Player, BankAccount, Fine

import logging

log = logging.getLogger(__name__)

DISCORD_BOT_URL = "http://localhost:5000/discord/notify"

router = APIRouter(prefix="/web", tags=["web"])

DISCORD_CLIENT_ID = os.getenv("DISCORD_CLIENT_ID", "")
DISCORD_CLIENT_SECRET = os.getenv("DISCORD_CLIENT_SECRET", "")
DISCORD_REDIRECT_URI = os.getenv("DISCORD_REDIRECT_URI", "https://gryazworld.ru/cabinet")

MC_SERVER_HOST = os.getenv("MC_SERVER_HOST", "play.gryazworld.ru")
MC_SERVER_PORT = int(os.getenv("MC_SERVER_PORT", "25565"))


class TokenRequest(BaseModel):
    code: str


class LinkRequest(BaseModel):
    discord_id: str
    minecraft_nick: str


@router.post("/discord/token")
async def discord_token(data: TokenRequest):
    """Exchange Discord OAuth2 code for access token and return user info."""
    async with aiohttp.ClientSession() as session:
        token_resp = await session.post(
            "https://discord.com/api/oauth2/token",
            data={
                "client_id": DISCORD_CLIENT_ID,
                "client_secret": DISCORD_CLIENT_SECRET,
                "grant_type": "authorization_code",
                "code": data.code,
                "redirect_uri": DISCORD_REDIRECT_URI,
            },
            headers={"Content-Type": "application/x-www-form-urlencoded"},
        )
        if token_resp.status != 200:
            err = await token_resp.text()
            raise HTTPException(status_code=400, detail=f"Discord error: {err}")

        token_data = await token_resp.json()
        access_token = token_data.get("access_token")

        user_resp = await session.get(
            "https://discord.com/api/users/@me",
            headers={"Authorization": f"Bearer {access_token}"},
        )
        if user_resp.status != 200:
            raise HTTPException(status_code=400, detail="Failed to get Discord user info")

        user = await user_resp.json()

    return {
        "id": user["id"],
        "username": user["username"],
        "avatar": user.get("avatar"),
        "global_name": user.get("global_name") or user["username"],
    }


@router.post("/link")
async def link_account(data: LinkRequest, db: AsyncSession = Depends(get_db)):
    """Link Discord ID to a Minecraft nickname."""
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.minecraft_nick.lower())
    )
    player = result.scalar_one_or_none()

    if player is None:
        raise HTTPException(
            status_code=404,
            detail="Игрок не найден. Убедись, что ты заходил на сервер хотя бы раз.",
        )

    existing = await db.execute(
        select(Player).where(Player.discord_id == data.discord_id)
    )
    other = existing.scalar_one_or_none()
    if other is not None and other.id != player.id:
        raise HTTPException(
            status_code=400,
            detail="Этот Discord аккаунт уже привязан к другому Minecraft нику.",
        )

    player.discord_id = data.discord_id
    await db.commit()

    # Notify Discord bot to update nickname
    try:
        async with aiohttp.ClientSession() as session:
            await session.post(
                DISCORD_BOT_URL,
                json={
                    "type": "nick_linked",
                    "discord_id": data.discord_id,
                    "nickname": player.nickname,
                },
                timeout=aiohttp.ClientTimeout(total=5),
            )
    except Exception as e:
        log.warning("Не удалось уведомить Discord бота о привязке: %s", e)

    return {"status": "ok", "nickname": player.nickname, "discord_id": data.discord_id}


@router.get("/profile/{discord_id}")
async def get_profile(discord_id: str, db: AsyncSession = Depends(get_db)):
    """Return player profile by Discord ID."""
    result = await db.execute(
        select(Player).where(Player.discord_id == discord_id)
    )
    player = result.scalar_one_or_none()

    if player is None:
        return {"linked": False}

    bank_result = await db.execute(
        select(BankAccount).where(BankAccount.player_id == player.id)
    )
    bank = bank_result.scalar_one_or_none()
    balance = bank.balance if bank else 0.0

    fines_result = await db.execute(
        select(Fine).where(Fine.player_id == player.id, Fine.status == "pending")
    )
    active_fines = fines_result.scalars().all()

    hours = player.total_seconds // 3600
    minutes = (player.total_seconds % 3600) // 60

    return {
        "linked": True,
        "uuid": player.uuid,
        "nickname": player.nickname,
        "discord_id": player.discord_id,
        "total_seconds": player.total_seconds,
        "hours": hours,
        "minutes": minutes,
        "warns": player.warns,
        "balance": balance,
        "active_fines": [
            {
                "id": f.id,
                "amount": f.amount,
                "reason": f.reason,
                "issued_by": f.issued_by,
                "deadline": f.deadline.isoformat() if f.deadline else None,
                "created_at": f.created_at.isoformat(),
            }
            for f in active_fines
        ],
    }


@router.get("/server-stats")
async def server_stats():
    """Return basic server stats (online count)."""
    online = 0
    try:
        import asyncio
        reader, writer = await asyncio.wait_for(
            asyncio.open_connection(MC_SERVER_HOST, MC_SERVER_PORT),
            timeout=3,
        )
        writer.close()
        await writer.wait_closed()
    except Exception:
        pass
    return {"online": online, "max": 20, "tps": "20.0"}


@router.get("/stats")
async def player_stats(db: AsyncSession = Depends(get_db)):
    """Return all players sorted by playtime for the leaderboard."""
    result = await db.execute(
        select(Player).order_by(Player.total_seconds.desc())
    )
    players = result.scalars().all()
    return [
        {
            "nickname": p.nickname,
            "total_seconds": p.total_seconds,
            "is_online": False,
        }
        for p in players
    ]
