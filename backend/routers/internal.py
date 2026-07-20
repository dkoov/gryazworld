import os
import uuid
from datetime import datetime, timedelta

from fastapi import APIRouter, Depends, Header, HTTPException
from typing import Optional
from pydantic import BaseModel
from sqlalchemy import select, and_, delete, func
from sqlalchemy.ext.asyncio import AsyncSession

from database import get_db, Player, AuthSession, DiscordAuthRequest, IpBan, PlaytimeDaily
import charsystem_client

DISCORD_AUTH_API_KEY = os.getenv("DISCORD_AUTH_API_KEY", "")

_denied: set[str] = set()

router = APIRouter(prefix="/internal", tags=["internal"])


async def verify_api_key(x_api_key: str = Header(...)):
    if not DISCORD_AUTH_API_KEY or x_api_key != DISCORD_AUTH_API_KEY:
        raise HTTPException(status_code=403, detail="Invalid API key")


# ── 1. GET /internal/whitelist/check/{name} ───────────────────────────────────

@router.get("/whitelist/check/{name}", dependencies=[Depends(verify_api_key)])
async def whitelist_check(name: str, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(Player).where(Player.nickname.ilike(name))
    )
    player = result.scalar_one_or_none()
    if player is None or not player.whitelisted:
        return {"whitelisted": False, "discordUserId": None}
    return {"whitelisted": True, "discordUserId": player.discord_id}


# ── 2. GET /internal/session/check/{name}?ip= ────────────────────────────────

@router.get("/session/check/{name}", dependencies=[Depends(verify_api_key)])
async def session_check(name: str, ip: str, db: AsyncSession = Depends(get_db)):
    if name.lower() in _denied:
        _denied.discard(name.lower())
        return {"valid": False, "sessionId": None, "denied": True}

    now = datetime.utcnow()
    result = await db.execute(
        select(AuthSession).where(
            and_(
                AuthSession.minecraft_name.ilike(name),
                AuthSession.ip == ip,
                AuthSession.expires_at > now,
            )
        )
    )
    session = result.scalar_one_or_none()
    if session is None:
        return {"valid": False, "sessionId": None, "denied": False}
    return {"valid": True, "sessionId": session.session_id, "denied": False}


# ── 3. POST /internal/auth/request ───────────────────────────────────────────

class AuthRequestBody(BaseModel):
    minecraftName: str
    discordUserId: str
    ipAddress: str
    timeoutSec: int


@router.post("/auth/request", dependencies=[Depends(verify_api_key)])
async def auth_request(body: AuthRequestBody, db: AsyncSession = Depends(get_db)):
    pending_id = str(uuid.uuid4())
    req = DiscordAuthRequest(
        pending_id=pending_id,
        minecraft_name=body.minecraftName,
        discord_user_id=body.discordUserId,
        ip_address=body.ipAddress,
        timeout_sec=body.timeoutSec,
    )
    db.add(req)
    await db.commit()

    # Notify auth bot to send Discord DM
    import aiohttp, os as _os
    auth_bot_url = _os.getenv("AUTH_BOT_URL", "http://discord-bot-auth:5001")
    try:
        async with aiohttp.ClientSession() as s:
            await s.post(
                f"{auth_bot_url}/auth/notify",
                json={
                    "pendingId":    pending_id,
                    "discordUserId": body.discordUserId,
                    "playerName":   body.minecraftName,
                    "ip":           body.ipAddress,
                    "timeoutSec":   body.timeoutSec,
                },
                timeout=aiohttp.ClientTimeout(total=5),
            )
    except Exception as e:
        print(f"[AuthBot] Webhook notify failed: {e}")

    return {"pendingId": pending_id}


# ── 4. POST /internal/auth/confirm ───────────────────────────────────────────

class ConfirmBody(BaseModel):
    pendingId: str
    durationDays: int


@router.post("/auth/confirm", dependencies=[Depends(verify_api_key)])
async def auth_confirm(body: ConfirmBody, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(DiscordAuthRequest).where(DiscordAuthRequest.pending_id == body.pendingId)
    )
    req = result.scalar_one_or_none()
    if req is None:
        return {"ok": False, "error": "pending_not_found", "minecraftName": ""}

    now = datetime.utcnow()
    if (now - req.created_at).total_seconds() > req.timeout_sec:
        await db.delete(req)
        await db.commit()
        return {"ok": False, "error": "expired", "minecraftName": req.minecraft_name}

    session = AuthSession(
        minecraft_name=req.minecraft_name,
        ip=req.ip_address,
        session_id=str(uuid.uuid4()),
        expires_at=now + timedelta(days=body.durationDays),
    )
    db.add(session)
    await db.delete(req)
    await db.commit()
    return {"ok": True, "error": None, "minecraftName": req.minecraft_name}


# ── 5. POST /internal/auth/cancel ────────────────────────────────────────────

class CancelBody(BaseModel):
    pendingId: str


@router.post("/auth/cancel", dependencies=[Depends(verify_api_key)])
async def auth_cancel(body: CancelBody, db: AsyncSession = Depends(get_db)):
    _req = await db.execute(
        select(DiscordAuthRequest).where(DiscordAuthRequest.pending_id == body.pendingId)
    )
    _row = _req.scalar_one_or_none()
    if _row:
        _denied.add(_row.minecraft_name.lower())
    await db.execute(
        delete(DiscordAuthRequest).where(DiscordAuthRequest.pending_id == body.pendingId)
    )
    await db.commit()
    return {"ok": True}


# ── 6. POST /internal/ban/ip ─────────────────────────────────────────────────

class IpBanBody(BaseModel):
    ip: str
    minecraftName: str


@router.post("/ban/ip", dependencies=[Depends(verify_api_key)])
async def ban_ip(body: IpBanBody, db: AsyncSession = Depends(get_db)):
    existing = await db.execute(
        select(IpBan).where(and_(IpBan.ip == body.ip, IpBan.minecraft_name == body.minecraftName))
    )
    if existing.scalar_one_or_none() is None:
        db.add(IpBan(ip=body.ip, minecraft_name=body.minecraftName))
        await db.commit()
    return {"ok": True}


# ── 7. POST /internal/ban/ip/remove ──────────────────────────────────────────

@router.post("/ban/ip/remove", dependencies=[Depends(verify_api_key)])
async def unban_ip(body: IpBanBody, db: AsyncSession = Depends(get_db)):
    await db.execute(
        delete(IpBan).where(and_(IpBan.ip == body.ip, IpBan.minecraft_name == body.minecraftName))
    )
    await db.commit()
    return {"ok": True}


# ── 8. GET /internal/ban/ip/check?ip=&name= ──────────────────────────────────

@router.get("/ban/ip/check", dependencies=[Depends(verify_api_key)])
async def ban_ip_check(ip: str, name: str, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(IpBan).where(
            and_(IpBan.ip == ip, IpBan.minecraft_name.ilike(name))
        )
    )
    banned = result.scalar_one_or_none() is not None
    return {"banned": banned}


# ── 9. POST /internal/whitelist/add ──────────────────────────────────────────

class WhitelistAddBody(BaseModel):
    minecraftName: str
    discordUserId: Optional[str] = None
    moderatorDiscordUserId: Optional[str] = None
    reason: Optional[str] = None


@router.post("/whitelist/add", dependencies=[Depends(verify_api_key)])
async def whitelist_add(body: WhitelistAddBody, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(Player).where(Player.nickname.ilike(body.minecraftName))
    )
    player = result.scalar_one_or_none()

    # Тот же дискорд-аккаунт мог быть принят раньше под другим ником —
    # обновляем его запись (players.uuid уникален), а не создаём дубль.
    if player is None and body.discordUserId:
        result = await db.execute(
            select(Player).where(Player.uuid == f"discord:{body.discordUserId}")
        )
        player = result.scalar_one_or_none()
        if player is None:
            result = await db.execute(
                select(Player).where(Player.discord_id == body.discordUserId)
            )
            player = result.scalars().first()
        if player is not None:
            player.nickname = body.minecraftName

    if player is None:
        # Unique placeholder uuid: by discord id when known, otherwise by nickname
        # (avoids colliding on a shared "discord:unknown" for manual/console adds).
        if body.discordUserId:
            placeholder_uuid = f"discord:{body.discordUserId}"
        else:
            placeholder_uuid = f"manual:{body.minecraftName.lower()}"
        player = Player(
            uuid=placeholder_uuid,
            nickname=body.minecraftName,
            discord_id=body.discordUserId,
            whitelisted=True,
        )
        db.add(player)
    else:
        if body.discordUserId:
            player.discord_id = body.discordUserId
        player.whitelisted = True
    await db.commit()
    return {"ok": True, "minecraftName": body.minecraftName}


# ── 10. POST /internal/whitelist/remove ──────────────────────────────────────

class WhitelistRemoveBody(BaseModel):
    minecraftName: str


@router.post("/whitelist/remove", dependencies=[Depends(verify_api_key)])
async def whitelist_remove(body: WhitelistRemoveBody, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(Player).where(Player.nickname.ilike(body.minecraftName))
    )
    player = result.scalar_one_or_none()
    if player is not None:
        player.whitelisted = False
    await db.commit()
    return {"ok": True}


# ── 11. GET /internal/whitelist/list ──────────────────────────────────────────

@router.get("/whitelist/list", dependencies=[Depends(verify_api_key)])
async def whitelist_list(db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(Player).where(Player.whitelisted == True)  # noqa: E712
    )
    players = result.scalars().all()
    return {"players": [p.nickname for p in players]}


# ── 12. POST /internal/whitelist/changenick ──────────────────────────────────

class ChangeNickBody(BaseModel):
    discordUserId: str
    newNickname: str


@router.post("/whitelist/changenick", dependencies=[Depends(verify_api_key)])
async def whitelist_changenick(body: ChangeNickBody, db: AsyncSession = Depends(get_db)):
    """Самостоятельное исправление ника в вайтлисте -- на случай опечатки в заявке,
    из-за которой игрок физически не может зайти (whitelist_check матчит по нику)."""
    result = await db.execute(select(Player).where(Player.discord_id == body.discordUserId))
    player = result.scalar_one_or_none()
    if player is None:
        return {"ok": False, "error": "not_found"}
    if not player.whitelisted:
        return {"ok": False, "error": "not_whitelisted"}

    new_nickname = body.newNickname.strip()
    if not new_nickname:
        return {"ok": False, "error": "bad_nickname"}

    if new_nickname.lower() == player.nickname.lower():
        return {"ok": False, "error": "same_nickname"}

    taken_result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == new_nickname.lower(), Player.id != player.id)
    )
    if taken_result.scalar_one_or_none() is not None:
        return {"ok": False, "error": "nickname_taken"}

    old_nickname = player.nickname
    player.nickname = new_nickname
    # плейсхолдер-uuid из manual: собран из старого ника -- обновляем вместе с ним
    if player.uuid and player.uuid.startswith("manual:"):
        player.uuid = f"manual:{new_nickname.lower()}"
    await db.commit()

    return {"ok": True, "oldNickname": old_nickname, "newNickname": new_nickname}


# ── 13. GET /internal/playtime/{discord_id} ──────────────────────────────────

@router.get("/playtime/{discord_id}", dependencies=[Depends(verify_api_key)])
async def get_total_playtime(discord_id: str, db: AsyncSession = Depends(get_db)):
    """Суммарное наигранное время привязанного игрока (секунды), для выборов в Парламент."""
    result = await db.execute(select(Player).where(Player.discord_id == discord_id))
    player = result.scalar_one_or_none()
    if player is None:
        return {"seconds": 0}

    total_result = await db.execute(
        select(func.sum(PlaytimeDaily.seconds)).where(PlaytimeDaily.player_id == player.id)
    )
    total = total_result.scalar() or 0
    return {"seconds": int(total)}


# ── 14. GET /internal/discord/role-sync ────────────────────────────────────────

@router.get("/discord/role-sync", dependencies=[Depends(verify_api_key)])
async def discord_role_sync(db: AsyncSession = Depends(get_db)):
    """Для периодической синхронизации Minecraft-ролей -> Discord-роли (discord-bot-main).
    Отдаёт {discord_id: [role_name, ...]} для всех привязанных игроков с реальным uuid."""
    result = await db.execute(
        select(Player.uuid, Player.discord_id).where(Player.discord_id != None, Player.uuid != None)  # noqa: E711
    )
    rows = result.all()

    all_roles = await charsystem_client.get_all_player_roles()

    out = []
    for uuid_, discord_id in rows:
        if uuid_.startswith("web-") or uuid_.startswith("manual:"):
            continue
        out.append({"discord_id": discord_id, "roles": all_roles.get(uuid_, [])})
    return out
