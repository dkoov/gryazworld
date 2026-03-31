import os
import aiohttp
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from typing import Optional

from database import get_db, Player, BankAccount, Fine, Community, CommunityMember

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


async def _notify_discord(payload: dict):
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
    # Check if this nick is already taken by another discord user
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.minecraft_nick.lower())
    )
    player = result.scalar_one_or_none()

    if player is not None and player.discord_id is not None and player.discord_id != data.discord_id:
        raise HTTPException(
            status_code=409,
            detail="Ник уже зарегистрирован",
        )

    # Check if this discord_id is already linked to a different nick
    existing = await db.execute(
        select(Player).where(Player.discord_id == data.discord_id)
    )
    other = existing.scalar_one_or_none()
    if other is not None and (player is None or other.id != player.id):
        raise HTTPException(
            status_code=400,
            detail="Этот Discord аккаунт уже привязан к другому Minecraft нику.",
        )

    if player is None:
        player = Player(
            uuid=f"web-{data.discord_id}",
            nickname=data.minecraft_nick,
            discord_id=data.discord_id,
        )
        db.add(player)
        await db.flush()
        account = BankAccount(player_id=player.id, balance=0.0)
        db.add(account)
    else:
        player.discord_id = data.discord_id

    await db.commit()

    import asyncio
    asyncio.ensure_future(_notify_discord({
        "type": "nick_linked",
        "discord_id": data.discord_id,
        "nickname": player.nickname,
    }))

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


# ─── Pydantic schemas ────────────────────────────────────────────────────────

class CommunityCreate(BaseModel):
    name: str
    discord_id: str


class CommunityJoin(BaseModel):
    discord_id: str


class CommunityDelete(BaseModel):
    discord_id: str


class CommunityUpdate(BaseModel):
    discord_id: str
    name: Optional[str] = None
    description: Optional[str] = None
    tag: Optional[str] = None
    icon: Optional[str] = None
    banner_url: Optional[str] = None
    discord_url: Optional[str] = None
    members_can_invite: Optional[int] = None


# ─── Communities endpoints ───────────────────────────────────────────────────

@router.get("/communities")
async def list_communities(db: AsyncSession = Depends(get_db)):
    """Return all communities sorted by member_count desc."""
    result = await db.execute(
        select(Community).order_by(Community.member_count.desc())
    )
    comms = result.scalars().all()
    return [
        {
            "id": cm.id,
            "name": cm.name,
            "description": cm.description or "",
            "tag": cm.tag or "",
            "icon": cm.icon or "🏘️",
            "owner_discord_id": cm.owner_discord_id,
            "member_count": cm.member_count,
            "banner_url": cm.banner_url,
            "discord_url": cm.discord_url,
            "members_can_invite": cm.members_can_invite,
            "created_at": cm.created_at.isoformat() if cm.created_at else None,
        }
        for cm in comms
    ]


@router.post("/communities")
async def create_community(data: CommunityCreate, db: AsyncSession = Depends(get_db)):
    """Create a new community. Limit: 3 per discord_id."""
    result = await db.execute(
        select(func.count()).select_from(Community)
        .where(Community.owner_discord_id == data.discord_id)
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
        owner_discord_id=data.discord_id,
        member_count=1,
    )
    db.add(comm)
    await db.flush()
    member = CommunityMember(community_id=comm.id, discord_id=data.discord_id)
    db.add(member)
    await db.commit()
    await db.refresh(comm)
    return {
        "id": comm.id,
        "name": comm.name,
        "description": comm.description,
        "tag": comm.tag,
        "icon": comm.icon,
        "owner_discord_id": comm.owner_discord_id,
        "member_count": comm.member_count,
        "banner_url": comm.banner_url,
        "discord_url": comm.discord_url,
        "members_can_invite": comm.members_can_invite,
    }


@router.patch("/communities/{community_id}")
async def update_community(community_id: int, data: CommunityUpdate, db: AsyncSession = Depends(get_db)):
    """Update community. Only the owner can edit."""
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.owner_discord_id != data.discord_id:
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
    await db.commit()
    await db.refresh(comm)
    return {
        "id": comm.id, "name": comm.name, "description": comm.description,
        "tag": comm.tag, "icon": comm.icon, "owner_discord_id": comm.owner_discord_id,
        "member_count": comm.member_count, "banner_url": comm.banner_url,
        "discord_url": comm.discord_url, "members_can_invite": comm.members_can_invite,
    }


@router.post("/communities/{community_id}/join")
async def join_community(community_id: int, data: CommunityJoin, db: AsyncSession = Depends(get_db)):
    """Join a community. Cannot join twice."""
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    existing = await db.execute(
        select(CommunityMember)
        .where(CommunityMember.community_id == community_id)
        .where(CommunityMember.discord_id == data.discord_id)
    )
    if existing.scalar_one_or_none() is not None:
        raise HTTPException(status_code=400, detail="Вы уже состоите в этой общине")
    member = CommunityMember(community_id=community_id, discord_id=data.discord_id)
    db.add(member)
    comm.member_count += 1
    await db.commit()
    return {"status": "ok", "community_id": community_id, "member_count": comm.member_count}


@router.delete("/communities/{community_id}")
async def delete_community(community_id: int, data: CommunityDelete, db: AsyncSession = Depends(get_db)):
    """Delete community. Only the owner can delete."""
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.owner_discord_id != data.discord_id:
        raise HTTPException(status_code=403, detail="Только создатель может удалить общину")
    await db.delete(comm)
    await db.commit()
    return {"status": "deleted", "community_id": community_id}
