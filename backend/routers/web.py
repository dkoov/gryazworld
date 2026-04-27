import asyncio
import os
import re
import aiohttp
import jwt
from fastapi import APIRouter, Depends, HTTPException, Header
from pydantic import BaseModel
from sqlalchemy import select, func, delete
from sqlalchemy.ext.asyncio import AsyncSession
from typing import Optional

from auth import (
    JWT_ALG,
    PLUGIN_SECRET,
    SESSION_SECRET,
    CurrentUser,
    current_user,
    issue_session_token,
)
from database import get_db, Player, BankAccount, Fine, Community, CommunityMember, CommunityInvite

import json
import logging

DISCORD_ID_RE = re.compile(r"^\d{5,30}$")


def validate_discord_id(value: str, field: str = "discord_id") -> str:
    if not isinstance(value, str) or not DISCORD_ID_RE.fullmatch(value):
        raise HTTPException(status_code=400, detail=f"Некорректный {field}")
    return value

log = logging.getLogger(__name__)

DISCORD_BOT_URL = "http://gryazworld-bot:5000/discord/notify"

router = APIRouter(prefix="/web", tags=["web"])

DISCORD_CLIENT_ID = os.getenv("DISCORD_CLIENT_ID", "")
DISCORD_CLIENT_SECRET = os.getenv("DISCORD_CLIENT_SECRET", "")
DISCORD_REDIRECT_URI = os.getenv("DISCORD_REDIRECT_URI", "https://gryazworld.ru/cabinet")

MC_SERVER_HOST = os.getenv("MC_SERVER_HOST", "play.gryazworld.ru")
MC_SERVER_PORT = int(os.getenv("MC_SERVER_PORT", "25565"))


class TokenRequest(BaseModel):
    code: str


class LinkRequest(BaseModel):
    minecraft_nick: str


class PayFineRequest(BaseModel):
    fine_id: int


async def _notify_discord(payload: dict):
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(DISCORD_BOT_URL, json=payload, timeout=aiohttp.ClientTimeout(total=5)) as resp:
                if resp.status != 200:
                    log.warning("Discord notify вернул статус %s", resp.status)
    except Exception as e:
        log.warning("Не удалось отправить уведомление в Discord: %s", e)


async def _rename_discord_member(discord_id: str, nickname: str):
    guild_id = os.getenv("DISCORD_GUILD_ID")
    bot_token = os.getenv("DISCORD_BOT_TOKEN")
    if not guild_id or not bot_token:
        return
    try:
        async with aiohttp.ClientSession() as session:
            async with session.patch(
                f"https://discord.com/api/v10/guilds/{guild_id}/members/{discord_id}",
                headers={"Authorization": f"Bot {bot_token}"},
                json={"nick": nickname},
                timeout=aiohttp.ClientTimeout(total=5),
            ) as resp:
                if resp.status not in (200, 204):
                    log.warning("Discord rename вернул статус %s", resp.status)
    except Exception as e:
        log.warning("Не удалось переименовать участника Discord: %s", e)


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

    display_name = user.get("global_name") or user["username"]
    token = issue_session_token(discord_id=user["id"], nickname=display_name)

    return {
        "id": user["id"],
        "username": user["username"],
        "avatar": user.get("avatar"),
        "global_name": display_name,
        "token": token,
    }


@router.post("/link")
async def link_account(
    data: LinkRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Link the current Discord session to a Minecraft nickname."""
    discord_id = user.discord_id

    # Check if this nick is already taken by another discord user
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.minecraft_nick.lower())
    )
    player = result.scalar_one_or_none()

    if player is not None and player.discord_id is not None and player.discord_id != discord_id:
        raise HTTPException(
            status_code=409,
            detail="Ник уже зарегистрирован",
        )

    # Check if this discord_id is already linked to a different nick
    existing = await db.execute(
        select(Player).where(Player.discord_id == discord_id)
    )
    other = existing.scalar_one_or_none()
    if other is not None and (player is None or other.id != player.id):
        raise HTTPException(
            status_code=400,
            detail="Этот Discord аккаунт уже привязан к другому Minecraft нику.",
        )

    if player is None:
        player = Player(
            uuid=f"web-{discord_id}",
            nickname=data.minecraft_nick,
            discord_id=discord_id,
        )
        db.add(player)
        await db.flush()
        account = BankAccount(player_id=player.id, balance=0.0)
        db.add(account)
    else:
        player.discord_id = discord_id

    await db.commit()

    asyncio.ensure_future(_rename_discord_member(discord_id, player.nickname))
    asyncio.ensure_future(_notify_discord({
        "type": "nick_linked",
        "discord_id": discord_id,
        "nickname": player.nickname,
    }))

    return {"status": "ok", "nickname": player.nickname, "discord_id": discord_id}


@router.get("/me")
async def get_me(
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Return the current player's profile (resolved from session)."""
    result = await db.execute(
        select(Player).where(Player.discord_id == user.discord_id)
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
                "deadline": f.deadline.isoformat() + "Z" if f.deadline else None,
                "created_at": f.created_at.isoformat(),
            }
            for f in active_fines
        ],
    }


@router.post("/pay-fine")
async def web_pay_fine(
    data: PayFineRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    player_result = await db.execute(select(Player).where(Player.discord_id == user.discord_id))
    player = player_result.scalar_one_or_none()
    if not player:
        raise HTTPException(status_code=404, detail="Игрок не найден")

    account_result = await db.execute(select(BankAccount).where(BankAccount.player_id == player.id))
    account = account_result.scalar_one_or_none()
    if not account:
        raise HTTPException(status_code=404, detail="Счёт не найден")

    fine_result = await db.execute(select(Fine).where(Fine.id == data.fine_id, Fine.player_id == player.id))
    fine = fine_result.scalar_one_or_none()
    if not fine:
        raise HTTPException(status_code=404, detail="Штраф не найден")
    if fine.status != "pending":
        raise HTTPException(status_code=400, detail=f"Штраф уже {fine.status}")
    if account.balance < fine.amount:
        raise HTTPException(status_code=400, detail="Недостаточно средств")

    account.balance -= fine.amount
    fine.status = "paid"
    await db.commit()

    asyncio.ensure_future(_notify_discord({
        "type": "fine_paid",
        "nickname": player.nickname,
        "discord_id": player.discord_id,
        "fine_id": fine.id,
        "reason": fine.reason,
        "amount": fine.amount,
    }))

    return {"status": "ok", "fine_id": fine.id, "balance": account.balance}


@router.get("/server-stats")
async def server_stats(db: AsyncSession = Depends(get_db)):
    """Return server stats with per-server breakdown."""
    result = await db.execute(
        select(Player.server, Player.nickname)
        .where(Player.is_online == True, Player.server != None)
    )
    rows = result.all()

    servers = {}
    for server, nickname in rows:
        if server not in servers:
            servers[server] = {"online": 0, "players": []}
        servers[server]["online"] += 1
        servers[server]["players"].append(nickname)

    total_online = sum(s["online"] for s in servers.values())
    return {"online": total_online, "servers": servers}


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
            "is_online": p.is_online,
            "server": p.server,
        }
        for p in players
    ]


# ─── Pydantic schemas ────────────────────────────────────────────────────────

class CommunityCreate(BaseModel):
    name: str


class CommunityUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    tag: Optional[str] = None
    icon: Optional[str] = None
    banner_url: Optional[str] = None
    discord_url: Optional[str] = None
    slug: Optional[str] = None
    members_can_invite: Optional[int] = None
    is_recruiting: Optional[int] = None
    is_private: Optional[int] = None
    info_blocks: Optional[list] = None
    images: Optional[list] = None


class KickRequest(BaseModel):
    target_discord_id: str


class SetRoleRequest(BaseModel):
    target_discord_id: str
    role: str


class CommunityInviteBody(BaseModel):
    nickname: str


# ─── Communities endpoints ───────────────────────────────────────────────────

def _parse_json_field(value, default=None):
    if default is None:
        default = []
    if not value:
        return default
    if isinstance(value, list):
        return value
    try:
        return json.loads(value)
    except (json.JSONDecodeError, TypeError):
        return default


async def _community_dict(cm, db):
    """Build a full community dict with total_hours and members_discord_ids."""
    # Get members
    mem_result = await db.execute(
        select(CommunityMember).where(CommunityMember.community_id == cm.id)
    )
    members = mem_result.scalars().all()
    members_discord_ids = [m.discord_id for m in members]

    # Calculate total hours from players
    total_seconds = 0
    if members_discord_ids:
        players_result = await db.execute(
            select(Player).where(Player.discord_id.in_(members_discord_ids))
        )
        players = players_result.scalars().all()
        total_seconds = sum(p.total_seconds for p in players)

    return {
        "id": cm.id,
        "name": cm.name,
        "description": cm.description or "",
        "tag": cm.tag or "",
        "icon": cm.icon or "\U0001F3D8\uFE0F",
        "owner_discord_id": cm.owner_discord_id,
        "member_count": cm.member_count,
        "banner_url": cm.banner_url,
        "discord_url": cm.discord_url,
        "members_can_invite": cm.members_can_invite,
        "is_recruiting": cm.is_recruiting,
        "is_private": cm.is_private,
        "slug": cm.slug,
        "info_blocks": _parse_json_field(cm.info_blocks),
        "images": _parse_json_field(cm.images),
        "total_hours": total_seconds // 3600,
        "members_discord_ids": members_discord_ids,
        "created_at": cm.created_at.isoformat() if cm.created_at else None,
    }


@router.get("/communities")
async def list_communities(db: AsyncSession = Depends(get_db)):
    """Return all communities sorted by member_count desc."""
    result = await db.execute(
        select(Community).order_by(Community.member_count.desc())
    )
    comms = result.scalars().all()
    return [await _community_dict(cm, db) for cm in comms]


@router.post("/communities")
async def create_community(
    data: CommunityCreate,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Create a new community. Limit: 3 per discord_id."""
    discord_id = user.discord_id
    result = await db.execute(
        select(func.count()).select_from(Community)
        .where(Community.owner_discord_id == discord_id)
    )
    count = result.scalar()
    if count >= 3:
        raise HTTPException(status_code=400, detail="Максимум 3 общины на игрока")
    if not data.name.strip():
        raise HTTPException(status_code=400, detail="Название не может быть пустым")
    comm = Community(
        name=data.name.strip(),
        description="",
        tag="",
        icon="🏘️",
        owner_discord_id=discord_id,
        member_count=1,
    )
    db.add(comm)
    await db.flush()
    member = CommunityMember(community_id=comm.id, discord_id=discord_id, role='owner')
    db.add(member)
    await db.commit()
    await db.refresh(comm)
    return await _community_dict(comm, db)


@router.patch("/communities/{community_id}")
async def update_community(
    community_id: int,
    data: CommunityUpdate,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Update community. Only the owner can edit."""
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.owner_discord_id != user.discord_id:
        raise HTTPException(status_code=403, detail="Только создатель может редактировать общину")
    if data.name is not None:
        comm.name = data.name.strip() or comm.name
    if data.description is not None:
        comm.description = data.description
    if data.tag is not None:
        comm.tag = data.tag
    if data.icon is not None:
        comm.icon = data.icon
    if data.banner_url is not None:
        comm.banner_url = data.banner_url or None
    if data.discord_url is not None:
        comm.discord_url = data.discord_url or None
    if data.members_can_invite is not None:
        comm.members_can_invite = data.members_can_invite
    if data.is_recruiting is not None:
        comm.is_recruiting = data.is_recruiting
    if data.is_private is not None:
        comm.is_private = data.is_private
    if data.slug is not None:
        comm.slug = data.slug or None
    if data.info_blocks is not None:
        comm.info_blocks = json.dumps(data.info_blocks)
    if data.images is not None:
        comm.images = json.dumps(data.images)
    await db.commit()
    await db.refresh(comm)
    return await _community_dict(comm, db)


@router.get("/communities/{community_id}/members")
async def community_members(community_id: int, db: AsyncSession = Depends(get_db)):
    """Return members of a community with their nicknames and roles."""
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    if comm_result.scalar_one_or_none() is None:
        raise HTTPException(status_code=404, detail="Община не найдена")

    mem_result = await db.execute(
        select(CommunityMember).where(CommunityMember.community_id == community_id)
    )
    members = mem_result.scalars().all()

    result = []
    for m in members:
        player_result = await db.execute(
            select(Player).where(Player.discord_id == m.discord_id)
        )
        player = player_result.scalar_one_or_none()
        result.append({
            "discord_id": m.discord_id,
            "nickname": player.nickname if player else m.discord_id,
            "role": m.role,
            "joined_at": m.joined_at.isoformat() if m.joined_at else None,
        })

    # Sort: owner first, then deputy, then member
    role_order = {"owner": 0, "deputy": 1, "member": 2}
    result.sort(key=lambda x: role_order.get(x["role"], 9))
    return result


@router.post("/communities/{community_id}/join")
async def join_community(
    community_id: int,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Join a community. Cannot join twice."""
    discord_id = user.discord_id
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.is_private:
        raise HTTPException(status_code=403, detail="Община закрыта для вступления")
    existing = await db.execute(
        select(CommunityMember)
        .where(CommunityMember.community_id == community_id)
        .where(CommunityMember.discord_id == discord_id)
    )
    if existing.scalar_one_or_none() is not None:
        raise HTTPException(status_code=400, detail="Вы уже состоите в этой общине")
    member = CommunityMember(community_id=community_id, discord_id=discord_id)
    db.add(member)
    comm.member_count += 1
    await db.commit()
    return {"status": "ok", "community_id": community_id, "member_count": comm.member_count}


@router.get("/communities/owned")
async def get_owned_community(
    discord_id: Optional[str] = None,
    db: AsyncSession = Depends(get_db),
    authorization: Optional[str] = Header(default=None),
    x_plugin_secret: Optional[str] = Header(default=None),
):
    """Return community owned by the current user (web) or by the given discord_id (plugin).

    Two auth modes:
    - Web: Authorization: Bearer <jwt> — discord_id берётся из сессии, query игнорируется.
    - Plugin: X-Plugin-Secret header + ?discord_id=<id> в query.
    """
    resolved_id: Optional[str] = None
    if authorization and authorization.lower().startswith("bearer "):
        token = authorization.split(" ", 1)[1].strip()
        if not token or not SESSION_SECRET:
            raise HTTPException(status_code=401, detail="not_authenticated")
        try:
            payload = jwt.decode(token, SESSION_SECRET, algorithms=[JWT_ALG])
            resolved_id = str(payload.get("sub") or "")
        except jwt.PyJWTError:
            raise HTTPException(status_code=401, detail="invalid_token")
    elif x_plugin_secret is not None:
        if not PLUGIN_SECRET or x_plugin_secret != PLUGIN_SECRET:
            raise HTTPException(status_code=403, detail="Invalid plugin secret")
        if not discord_id:
            raise HTTPException(status_code=400, detail="discord_id обязателен")
        resolved_id = validate_discord_id(discord_id)
    else:
        raise HTTPException(status_code=401, detail="not_authenticated")

    if not resolved_id:
        raise HTTPException(status_code=401, detail="not_authenticated")

    result = await db.execute(select(Community).where(Community.owner_discord_id == resolved_id))
    comm = result.scalars().first()
    if comm is None:
        raise HTTPException(status_code=404, detail="Своей общины нет")
    return {"id": comm.id, "name": comm.name}


@router.get("/community-by-slug/{slug}")
async def community_by_slug(slug: str, db: AsyncSession = Depends(get_db)):
    """Find community by slug for direct links."""
    result = await db.execute(select(Community).where(Community.slug == slug))
    comm = result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    return await _community_dict(comm, db)


@router.post("/communities/{community_id}/kick")
async def kick_member(
    community_id: int,
    data: KickRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Kick a member. Owner or deputy can kick; deputy cannot kick owner or another deputy."""
    target_id = validate_discord_id(data.target_discord_id, field="target_discord_id")
    actor_id = user.discord_id

    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if not comm:
        raise HTTPException(status_code=404, detail="Община не найдена")

    is_owner = comm.owner_discord_id == actor_id
    kicker_result = await db.execute(
        select(CommunityMember).where(
            CommunityMember.community_id == community_id,
            CommunityMember.discord_id == actor_id,
        )
    )
    kicker = kicker_result.scalar_one_or_none()
    is_deputy = kicker and kicker.role == "deputy"

    if not is_owner and not is_deputy:
        raise HTTPException(status_code=403, detail="Нет прав")

    if comm.owner_discord_id == target_id:
        raise HTTPException(status_code=403, detail="Нельзя кикнуть владельца")

    target_result = await db.execute(
        select(CommunityMember).where(
            CommunityMember.community_id == community_id,
            CommunityMember.discord_id == target_id,
        )
    )
    target = target_result.scalar_one_or_none()
    if not target:
        raise HTTPException(status_code=404, detail="Участник не найден")

    if is_deputy and target.role == "deputy":
        raise HTTPException(status_code=403, detail="Зам не может кикнуть другого зама")

    await db.delete(target)
    comm.member_count = max(0, (comm.member_count or 1) - 1)
    await db.commit()
    return {"detail": "Участник исключён"}


@router.post("/communities/{community_id}/set-role")
async def set_role(
    community_id: int,
    data: SetRoleRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Set member role. Only the owner can assign roles."""
    target_id = validate_discord_id(data.target_discord_id, field="target_discord_id")

    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if not comm or comm.owner_discord_id != user.discord_id:
        raise HTTPException(status_code=403, detail="Только владелец может назначать роли")

    target_result = await db.execute(
        select(CommunityMember).where(
            CommunityMember.community_id == community_id,
            CommunityMember.discord_id == target_id,
        )
    )
    target = target_result.scalar_one_or_none()
    if not target:
        raise HTTPException(status_code=404, detail="Участник не найден")

    target.role = data.role
    await db.commit()
    return {"detail": "Роль обновлена"}


@router.post("/communities/{community_id}/leave")
async def leave_community(
    community_id: int,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Leave a community. Owner cannot leave."""
    discord_id = user.discord_id
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if not comm:
        raise HTTPException(status_code=404, detail="Община не найдена")

    if comm.owner_discord_id == discord_id:
        raise HTTPException(status_code=400, detail="Владелец не может покинуть общину. Удалите её.")

    member_result = await db.execute(
        select(CommunityMember).where(
            CommunityMember.community_id == community_id,
            CommunityMember.discord_id == discord_id,
        )
    )
    member = member_result.scalar_one_or_none()
    if not member:
        raise HTTPException(status_code=404, detail="Вы не состоите в этой общине")

    await db.delete(member)
    comm.member_count = max(0, (comm.member_count or 1) - 1)
    await db.commit()
    return {"detail": "Вы покинули общину"}


@router.post("/communities/{community_id}/invite")
async def invite_to_community(
    community_id: int,
    data: CommunityInviteBody,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Invite a player by nickname. Only the owner can invite."""
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.owner_discord_id != user.discord_id:
        raise HTTPException(status_code=403, detail="Только владелец может приглашать")

    player_result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.nickname.lower())
    )
    if player_result.scalar_one_or_none() is None:
        raise HTTPException(status_code=404, detail="Игрок не найден")

    invite = CommunityInvite(
        community_id=community_id,
        invited_nickname=data.nickname.lower(),
        invited_by_discord_id=user.discord_id,
    )
    db.add(invite)
    await db.commit()
    return {"detail": "Приглашение отправлено"}


@router.post("/communities/{community_id}/accept-invite")
async def accept_invite(
    community_id: int,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Accept a pending invite addressed to the current user's linked nickname."""
    player_result = await db.execute(
        select(Player).where(Player.discord_id == user.discord_id)
    )
    player = player_result.scalar_one_or_none()
    if player is None or not player.nickname:
        raise HTTPException(status_code=400, detail="Discord не привязан к Minecraft-нику")

    invite_result = await db.execute(
        select(CommunityInvite).where(
            CommunityInvite.community_id == community_id,
            func.lower(CommunityInvite.invited_nickname) == player.nickname.lower(),
        )
    )
    invite = invite_result.scalar_one_or_none()
    if invite is None:
        raise HTTPException(status_code=404, detail="Приглашение не найдено")

    existing = await db.execute(
        select(CommunityMember).where(
            CommunityMember.community_id == community_id,
            CommunityMember.discord_id == user.discord_id,
        )
    )
    if existing.scalar_one_or_none() is None:
        member = CommunityMember(community_id=community_id, discord_id=user.discord_id)
        db.add(member)
        comm_result = await db.execute(select(Community).where(Community.id == community_id))
        comm = comm_result.scalar_one_or_none()
        if comm:
            comm.member_count += 1

    await db.delete(invite)
    await db.commit()
    return {"detail": "Вступление успешно"}


@router.get("/communities/{community_id}/invites")
async def list_invites(
    community_id: int,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """List pending invites. Only the owner can view."""
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.owner_discord_id != user.discord_id:
        raise HTTPException(status_code=403, detail="Доступ запрещён")

    invites_result = await db.execute(
        select(CommunityInvite).where(CommunityInvite.community_id == community_id)
    )
    invites = invites_result.scalars().all()
    return [
        {"nickname": i.invited_nickname, "created_at": i.created_at.isoformat() if i.created_at else None}
        for i in invites
    ]


@router.get("/all-linked-players")
async def get_all_linked_players(db: AsyncSession = Depends(get_db)):
    """Return all players with a linked Discord account."""
    result = await db.execute(
        select(Player).where(Player.discord_id != None)
    )
    players = result.scalars().all()
    return [{"discord_id": p.discord_id, "nickname": p.nickname} for p in players]


@router.delete("/communities/{community_id}")
async def delete_community(
    community_id: int,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    """Delete community. Only the owner can delete."""
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.owner_discord_id != user.discord_id:
        raise HTTPException(status_code=403, detail="Только создатель может удалить общину")
    await db.execute(delete(CommunityMember).where(CommunityMember.community_id == community_id))
    await db.execute(delete(CommunityInvite).where(CommunityInvite.community_id == community_id))
    await db.delete(comm)
    await db.commit()
    return {"status": "deleted", "community_id": community_id}
