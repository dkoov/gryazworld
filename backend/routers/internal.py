import hashlib
import os
import uuid
from datetime import datetime, timedelta

from fastapi import APIRouter, Depends, Header, HTTPException
from typing import Optional
from pydantic import BaseModel
from sqlalchemy import select, and_, delete, func
from sqlalchemy.ext.asyncio import AsyncSession

from database import get_db, Player, AuthSession, DiscordAuthRequest, IpBan, PlaytimeDaily, Subscription
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
            )
        ).order_by(AuthSession.expires_at.desc())
    )
    sessions = result.scalars().all()
    if not sessions:
        return {"valid": False, "sessionId": None, "denied": False}

    valid = sessions[0]
    if valid.expires_at <= now:
        # Просрочена -- подчищаем при обращении (никакого отдельного cron для этого не нужно,
        # строк на пару-тройку на игрока, ничего не растёт бесконтрольно).
        for s in sessions:
            await db.delete(s)
        await db.commit()
        return {"valid": False, "sessionId": None, "denied": False}

    # Кэш подтверждённого IP на час: сессия НЕ удаляется при успешной проверке, так что
    # повторные входы с того же IP в течение часа не переспрашивают Discord. Час отсчитывается
    # от момента подтверждения (см. auth_confirm) -- не продлевается на каждый вход.
    return {"valid": True, "sessionId": valid.session_id, "denied": False}


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


AUTH_SESSION_HOURS = 1


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

    existing_result = await db.execute(
        select(AuthSession).where(
            and_(
                AuthSession.minecraft_name.ilike(req.minecraft_name),
                AuthSession.ip == req.ip_address,
            )
        ).order_by(AuthSession.expires_at.desc())
    )
    existing_sessions = existing_result.scalars().all()
    # Подтверждённый IP кэшируется на фиксированный час (не настраивается со стороны бота --
    # раньше здесь был durationDays от бота, но session_check всё равно удалял сессию на
    # первой же проверке, так что то значение ни на что не влияло; теперь влияет, поэтому
    # держим его здесь одним местом, а не разбросанным по ботам).
    new_expires_at = now + timedelta(hours=AUTH_SESSION_HOURS)
    if existing_sessions:
        # Обновляем самую свежую запись, удаляем остальные дубликаты (если накопились раньше).
        existing_sessions[0].expires_at = new_expires_at
        for stale in existing_sessions[1:]:
            await db.delete(stale)
    else:
        db.add(AuthSession(
            minecraft_name=req.minecraft_name,
            ip=req.ip_address,
            session_id=str(uuid.uuid4()),
            expires_at=new_expires_at,
        ))
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

    # Тот же дискорд-аккаунт мог быть принят раньше под другим ником -- ищем
    # его запись ПЕРЕД тем, как трогать discord_id у найденной по нику записи,
    # иначе можно попытаться поставить уже занятый discord_id на чужую
    # запись и словить UNIQUE constraint failed: players.discord_id.
    discord_owner = None
    if body.discordUserId:
        result = await db.execute(
            select(Player).where(Player.uuid == f"discord:{body.discordUserId}")
        )
        discord_owner = result.scalar_one_or_none()
        if discord_owner is None:
            result = await db.execute(
                select(Player).where(Player.discord_id == body.discordUserId)
            )
            discord_owner = result.scalars().first()

    if discord_owner is not None and discord_owner is not player:
        # Этот дискорд-аккаунт уже владеет другой записью -- она каноничная,
        # просто обновляем её ник вместо перезаписи discord_id у записи,
        # найденной по нику (та может принадлежать другому/старому игроку).
        discord_owner.nickname = body.minecraftName
        discord_owner.whitelisted = True
        player = discord_owner
    elif player is None:
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

def _offline_uuid(name: str) -> str:
    """Оффлайн-uuid Minecraft (сервера сети работают без online-mode), тот же алгоритм,
    что использует сам игровой сервер: UUID.nameUUIDFromBytes("OfflinePlayer:<ник>")."""
    digest = bytearray(hashlib.md5(f"OfflinePlayer:{name}".encode("utf-8")).digest())
    digest[6] = (digest[6] & 0x0F) | 0x30
    digest[8] = (digest[8] & 0x3F) | 0x80
    return str(uuid.UUID(bytes=bytes(digest)))


class ChangeNickBody(BaseModel):
    discordUserId: str
    newNickname: str
    currentNickname: Optional[str] = None


@router.post("/whitelist/changenick", dependencies=[Depends(verify_api_key)])
async def whitelist_changenick(body: ChangeNickBody, db: AsyncSession = Depends(get_db)):
    """Самостоятельное исправление ника в вайтлисте -- на случай опечатки в заявке,
    из-за которой игрок физически не может зайти (whitelist_check матчит по нику)."""
    result = await db.execute(select(Player).where(Player.discord_id == body.discordUserId))
    player = result.scalar_one_or_none()

    # Часть записей попала в вайтлист без привязки discord_id (например, вайтлист вручную
    # по нику, без OAuth) -- по нику их не найти через discord_id. Если игрок указал свой
    # ТЕКУЩИЙ ник в вайтлисте, пробуем найти по нему и линкуем discord_id тут же.
    if player is None and body.currentNickname:
        nick_result = await db.execute(
            select(Player).where(func.lower(Player.nickname) == body.currentNickname.strip().lower())
        )
        candidate = nick_result.scalar_one_or_none()
        if candidate is not None:
            if candidate.discord_id and candidate.discord_id != body.discordUserId:
                return {"ok": False, "error": "nickname_owned_by_other"}
            player = candidate
            player.discord_id = body.discordUserId

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
    if player.uuid and player.uuid.startswith("manual:"):
        # плейсхолдер-uuid из manual: собран из старого ника -- обновляем вместе с ним
        player.uuid = f"manual:{new_nickname.lower()}"
    elif player.uuid and not player.uuid.startswith("web-") and not player.uuid.startswith("discord:"):
        # Игрок уже физически заходил на сервер -- его uuid детерминированно зависит от ника
        # (сеть работает без online-mode), пересчитываем вместе с ником. Без этого запись в
        # бэкенде "отъезжает" от uuid, который реальный игровой сервер посчитает при следующем
        # входе под новым ником -- ломается синк онлайна/наигранного времени/варнов по uuid.
        player.uuid = _offline_uuid(new_nickname)
    await db.commit()

    return {"ok": True, "oldNickname": old_nickname, "newNickname": new_nickname}


# ── 12b. POST /internal/admin/rename ──────────────────────────────────────────

class AdminRenameBody(BaseModel):
    currentNickname: str
    newNickname: str


@router.post("/admin/rename", dependencies=[Depends(verify_api_key)])
async def admin_rename(body: AdminRenameBody, db: AsyncSession = Depends(get_db)):
    """Принудительная смена ника администратором -- в отличие от /whitelist/changenick,
    не привязана к discord_id вызывающего (тот эндпоинт для игрока, меняющего СВОЙ ник,
    и специально отклоняет чужие аккаунты; здесь доверяем самому вызову, т.к. он уже
    защищён verify_api_key и вызывается только явной админ-командой в Discord)."""
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == body.currentNickname.strip().lower())
    )
    player = result.scalar_one_or_none()
    if player is None:
        return {"ok": False, "error": "not_found"}

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
    if player.uuid and player.uuid.startswith("manual:"):
        player.uuid = f"manual:{new_nickname.lower()}"
    elif player.uuid and not player.uuid.startswith("web-") and not player.uuid.startswith("discord:"):
        player.uuid = _offline_uuid(new_nickname)
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
        select(Player.id, Player.uuid, Player.discord_id).where(Player.discord_id != None, Player.uuid != None)  # noqa: E711
    )
    rows = result.all()

    all_roles = await charsystem_client.get_all_player_roles()

    # Активная IchoPlus-подписка, выданная заранее (до первого захода игрока на сервер) --
    # charsystem ещё не знает о такой роли (она выставляется только при первом /mc/player/join),
    # поэтому явно добавляем её сюда, иначе синк решает, что IchoPlus нужно снять в Discord.
    active_sub_result = await db.execute(
        select(Subscription.player_id).where(
            Subscription.sku.like("ichoplus_%"),
            Subscription.expires_at > datetime.utcnow(),
        )
    )
    player_ids_with_ichoplus = {row[0] for row in active_sub_result.all()}

    # Игроки, купившие проходку (access_seasonal), получают Discord-роль AccessPass.
    # Роль синхронизируется тем же механизмом roleSync, что и IchoPlus.
    access_result = await db.execute(
        select(Player.id).where(Player.has_access == True)  # noqa: E712
    )
    player_ids_with_access = {row[0] for row in access_result.all()}

    out = []
    for player_id, uuid_, discord_id in rows:
        if uuid_.startswith("web-") or uuid_.startswith("manual:"):
            continue
        roles = list(all_roles.get(uuid_, []))
        if player_id in player_ids_with_ichoplus and "IchoPlus" not in roles:
            roles.append("IchoPlus")
        if player_id in player_ids_with_access and "AccessPass" not in roles:
            roles.append("AccessPass")
        out.append({"discord_id": discord_id, "roles": roles})
    return out
