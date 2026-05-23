import asyncio
import secrets
from datetime import datetime
from typing import Dict
import aiohttp
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from auth import verify_plugin_secret
from database import get_db, Player, BankAccount, PlayerIP, PendingAuth

import os
DISCORD_BOT_URL = "http://gryazworld-bot:5000/discord/notify"

# Хранит результаты confirm-auth до опроса плагина: token -> "confirmed" | "denied"
_auth_results: Dict[str, str] = {}


async def _notify_discord(payload: dict):
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(DISCORD_BOT_URL, json=payload, timeout=aiohttp.ClientTimeout(total=5)) as resp:
                if resp.status != 200:
                    import logging
                    logging.getLogger(__name__).warning("Discord notify вернул статус %s", resp.status)
    except Exception as e:
        import logging
        logging.getLogger(__name__).warning("Не удалось отправить уведомление в Discord: %s", e)

router = APIRouter(prefix="/mc/player", tags=["player"])


class PlayerJoinRequest(BaseModel):
    uuid: str
    nickname: str
    server: str = "unknown"


class PlayerQuitRequest(BaseModel):
    uuid: str
    session_seconds: int
    server: str = "unknown"


@router.post("/join", dependencies=[Depends(verify_plugin_secret)])
async def player_join(data: PlayerJoinRequest, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Player).where(Player.uuid == data.uuid))
    player = result.scalar_one_or_none()

    if player is None:
        nick_result = await db.execute(
            select(Player).where(func.lower(Player.nickname) == data.nickname.lower())
        )
        player = nick_result.scalar_one_or_none()

    if player is not None:
        player.uuid = data.uuid
        player.nickname = data.nickname
        player.is_online = True
        player.server = data.server
        await db.commit()
        status = "updated"
    else:
        player = Player(uuid=data.uuid, nickname=data.nickname, is_online=True, server=data.server)
        db.add(player)
        await db.flush()
        account = BankAccount(player_id=player.id, balance=0.0)
        db.add(account)
        await db.commit()
        status = "created"

    asyncio.ensure_future(_notify_discord({
        "type": "join",
        "nickname": data.nickname,
        "server": data.server,
    }))
    return {"status": status, "uuid": data.uuid, "nickname": data.nickname}


@router.post("/quit", dependencies=[Depends(verify_plugin_secret)])
async def player_quit(data: PlayerQuitRequest, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Player).where(Player.uuid == data.uuid))
    player = result.scalar_one_or_none()

    if player is None:
        raise HTTPException(status_code=404, detail="Player not found")

    nickname = player.nickname
    player.total_seconds += data.session_seconds
    if player.server is None or player.server == data.server:
        player.is_online = False
        player.server = None
    await db.commit()

    asyncio.ensure_future(_notify_discord({
        "type": "quit",
        "nickname": nickname,
        "server": data.server,
    }))
    return {
        "status": "ok",
        "uuid": data.uuid,
        "total_seconds": player.total_seconds
    }


class PlayerDeathRequest(BaseModel):
    uuid: str
    nickname: str
    death_message: str
    server: str = "unknown"


class PlayerChatRequest(BaseModel):
    uuid: str
    nickname: str
    message: str
    server: str = "unknown"


@router.post("/death", dependencies=[Depends(verify_plugin_secret)])
async def player_death(data: PlayerDeathRequest):
    asyncio.ensure_future(_notify_discord({
        "type": "death",
        "nickname": data.nickname,
        "death_message": data.death_message,
        "server": data.server,
    }))
    return {"status": "ok"}


@router.post("/chat", dependencies=[Depends(verify_plugin_secret)])
async def player_chat(data: PlayerChatRequest):
    asyncio.ensure_future(_notify_discord({
        "type": "chat",
        "nickname": data.nickname,
        "message": data.message,
        "server": data.server,
    }))
    return {"status": "ok"}


@router.post("/check-ip", dependencies=[Depends(verify_plugin_secret)])
async def check_ip(data: dict, db: AsyncSession = Depends(get_db)):
    uuid = data.get("uuid")
    ip = data.get("ip")

    player_result = await db.execute(select(Player).where(Player.uuid == uuid))
    player = player_result.scalar_one_or_none()

    # Если не нашли по UUID — пробуем по нику (web-зарегистрированные игроки)
    if not player:
        nickname = data.get("nickname")
        if nickname:
            nick_result = await db.execute(
                select(Player).where(func.lower(Player.nickname) == nickname.lower())
            )
            player = nick_result.scalar_one_or_none()

    if not player:
        return {"allowed": False, "reason": "not_registered"}

    # Проверяем знакомый IP
    ip_result = await db.execute(
        select(PlayerIP).where(PlayerIP.player_id == player.id, PlayerIP.ip_address == ip, PlayerIP.confirmed == True)
    )
    known_ip = ip_result.scalar_one_or_none()
    if known_ip:
        return {"allowed": True}

    # Новый IP — создаём pending auth
    token = secrets.token_urlsafe(16)
    pending = PendingAuth(player_id=player.id, ip_address=ip, token=token)
    db.add(pending)
    await db.commit()

    asyncio.ensure_future(_notify_discord({
        "type": "auth_request",
        "discord_id": player.discord_id,
        "nickname": player.nickname,
        "ip": ip,
        "token": token,
    }))

    return {"allowed": False, "reason": "new_ip", "token": token}


@router.post("/confirm-auth", dependencies=[Depends(verify_plugin_secret)])
async def confirm_auth(data: dict, db: AsyncSession = Depends(get_db)):
    token = data.get("token")
    confirmed = data.get("confirmed", False)

    pending_result = await db.execute(select(PendingAuth).where(PendingAuth.token == token))
    pending = pending_result.scalar_one_or_none()
    if not pending:
        raise HTTPException(status_code=404, detail="Token not found")

    player_result = await db.execute(select(Player).where(Player.id == pending.player_id))
    player = player_result.scalar_one_or_none()

    if confirmed:
        new_ip = PlayerIP(player_id=pending.player_id, ip_address=pending.ip_address, confirmed=True)
        db.add(new_ip)

    # Сохраняем результат для поллинга плагином
    _auth_results[token] = "confirmed" if confirmed else "denied"

    await db.delete(pending)
    await db.commit()

    return {"status": "ok", "confirmed": confirmed, "uuid": player.uuid if player else None}


@router.get("/auth-status", dependencies=[Depends(verify_plugin_secret)])
async def auth_status(token: str, db: AsyncSession = Depends(get_db)):
    # Если результат уже готов — возвращаем и удаляем из кэша
    if token in _auth_results:
        result = _auth_results.pop(token)
        return {"status": result}

    # Ещё ожидает — pending запись должна существовать
    pending_result = await db.execute(select(PendingAuth).where(PendingAuth.token == token))
    pending = pending_result.scalar_one_or_none()
    if pending:
        return {"status": "pending"}

    # Pending удалён, но результата нет — считаем denied (истёк таймаут Discord)
    return {"status": "denied"}


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
