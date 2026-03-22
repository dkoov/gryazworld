import os
import aiohttp
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from database import get_db, Player, BankAccount, Fine

router = APIRouter(prefix="/web", tags=["web"])

DISCORD_CLIENT_ID = os.getenv("DISCORD_CLIENT_ID", "1481258609902882888")
DISCORD_CLIENT_SECRET = os.getenv("DISCORD_CLIENT_SECRET", "REDACTED_CLIENT_SECRET")
DISCORD_REDIRECT_URI = os.getenv("DISCORD_REDIRECT_URI", "https://gryazworld.ru/cabinet.html")
DISCORD_BOT_URL = "http://localhost:5000/discord/notify"


class TokenRequest(BaseModel):
    code: str


class LinkRequest(BaseModel):
    discord_id: str
    minecraft_nick: str


async def _notify_discord(payload: dict):
    import logging
    log = logging.getLogger(__name__)
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(DISCORD_BOT_URL, json=payload, timeout=aiohttp.ClientTimeout(total=5)) as resp:
                if resp.status != 200:
                    log.warning("Discord notify вернул статус %s", resp.status)
    except Exception as e:
        log.warning("Не удалось отправить уведомление в Discord: %s", e)


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
    # Check if nickname is already taken by another discord_id
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.minecraft_nick.lower())
    )
    player = result.scalar_one_or_none()

    if player is not None and player.discord_id is not None and player.discord_id != data.discord_id:
        raise HTTPException(
            status_code=409,
            detail="Ник уже зарегистрирован",
        )

    if player is None:
        # Player not found in DB — create new one (web registration without MC UUID)
        player = Player(
            uuid=f"web-{data.discord_id}",
            nickname=data.minecraft_nick,
            discord_id=data.discord_id,
        )
        db.add(player)
    else:
        player.discord_id = data.discord_id

    await db.commit()

    # Notify discord bot to sync the nickname
    import asyncio
    asyncio.ensure_future(_notify_discord({
        "type": "nick_sync",
        "discord_id": data.discord_id,
        "minecraft_nick": player.nickname,
    }))

    return {"status": "ok", "nickname": player.nickname, "discord_id": data.discord_id}


@router.get("/all_profiles")
async def get_all_profiles(db: AsyncSession = Depends(get_db)):
    """Return all players that have a discord_id linked."""
    result = await db.execute(
        select(Player).where(Player.discord_id != None)
    )
    players = result.scalars().all()
    return [
        {"discord_id": p.discord_id, "minecraft_nick": p.nickname}
        for p in players
    ]


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
