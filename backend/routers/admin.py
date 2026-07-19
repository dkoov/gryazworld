import logging
from datetime import datetime, timedelta
from typing import Optional

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from auth import CurrentUser, current_user
from database import get_db, Player, Subscription
import charsystem_client

log = logging.getLogger(__name__)

router = APIRouter(prefix="/web/admin", tags=["admin"])

# "Навсегда" -- не трогаем схему (expires_at NOT NULL), просто ставим дату в далёком будущем.
FOREVER_DATE = datetime(2999, 1, 1)

# Owner раздаёт право "*", которое автоматически делает игрока опом на серверах --
# выдавать эту роль через веб-форму запрещено, что бы ни лежало в cs_roles.
EXCLUDED_ROLES = {"owner"}


async def require_admin(user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)) -> Player:
    result = await db.execute(select(Player).where(Player.discord_id == user.discord_id))
    player = result.scalar_one_or_none()
    if player is None or not player.is_admin:
        raise HTTPException(status_code=403, detail="Нет доступа")
    return player


async def _resolve_player(nickname: str, db: AsyncSession) -> Player:
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == nickname.lower())
    )
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Игрок не найден")
    if not player.uuid or player.uuid.startswith("web-") or player.uuid.startswith("manual:"):
        raise HTTPException(status_code=400, detail="У игрока нет привязанного игрового аккаунта")
    return player


@router.get("/roles")
async def list_roles(_admin: Player = Depends(require_admin)):
    roles = await charsystem_client.get_roles()
    return [r for r in roles if r.lower() not in EXCLUDED_ROLES]


@router.get("/players")
async def search_players(q: str = "", _admin: Player = Depends(require_admin), db: AsyncSession = Depends(get_db)):
    query = select(Player).order_by(Player.nickname).limit(20)
    q = q.strip()
    if q:
        query = select(Player).where(Player.nickname.ilike(f"%{q}%")).order_by(Player.nickname).limit(20)
    result = await db.execute(query)
    players = result.scalars().all()
    return [
        {"nickname": p.nickname, "whitelisted": p.whitelisted, "has_linked_account": bool(p.discord_id)}
        for p in players
    ]


@router.get("/player/{nickname}/roles")
async def get_player_roles_endpoint(
    nickname: str, _admin: Player = Depends(require_admin), db: AsyncSession = Depends(get_db)
):
    player = await _resolve_player(nickname, db)
    roles = await charsystem_client.get_player_roles(player.uuid)
    return {"nickname": player.nickname, "roles": roles}


class RoleActionRequest(BaseModel):
    role_name: str


@router.post("/player/{nickname}/roles")
async def grant_role_endpoint(
    nickname: str,
    data: RoleActionRequest,
    _admin: Player = Depends(require_admin),
    db: AsyncSession = Depends(get_db),
):
    player = await _resolve_player(nickname, db)
    if data.role_name.lower() in EXCLUDED_ROLES:
        raise HTTPException(status_code=403, detail="Эту роль нельзя выдавать через сайт")
    valid_roles = await charsystem_client.get_roles()
    if data.role_name not in valid_roles:
        raise HTTPException(status_code=400, detail="Неизвестная роль")
    await charsystem_client.grant_role(player.uuid, data.role_name)
    return {"status": "ok"}


@router.delete("/player/{nickname}/roles/{role_name}")
async def revoke_role_endpoint(
    nickname: str,
    role_name: str,
    _admin: Player = Depends(require_admin),
    db: AsyncSession = Depends(get_db),
):
    player = await _resolve_player(nickname, db)
    await charsystem_client.revoke_role(player.uuid, role_name)
    return {"status": "ok"}


async def _latest_ichoplus(player_id: int, db: AsyncSession) -> Optional[Subscription]:
    result = await db.execute(
        select(Subscription)
        .where(Subscription.player_id == player_id, Subscription.sku.like("ichoplus_%"))
        .order_by(Subscription.expires_at.desc())
    )
    return result.scalars().first()


@router.get("/player/{nickname}/subscription")
async def get_player_subscription(
    nickname: str, _admin: Player = Depends(require_admin), db: AsyncSession = Depends(get_db)
):
    player = await _resolve_player(nickname, db)
    sub = await _latest_ichoplus(player.id, db)
    active = bool(sub and sub.expires_at > datetime.utcnow())
    return {
        "active": active,
        "expires_at": sub.expires_at.isoformat() if sub and active else None,
        "forever": bool(active and sub.expires_at >= FOREVER_DATE),
    }


class GrantSubscriptionRequest(BaseModel):
    months: Optional[int] = None
    forever: bool = False


@router.post("/player/{nickname}/subscription")
async def grant_subscription(
    nickname: str,
    data: GrantSubscriptionRequest,
    _admin: Player = Depends(require_admin),
    db: AsyncSession = Depends(get_db),
):
    player = await _resolve_player(nickname, db)

    if data.forever:
        expires_at = FOREVER_DATE
    else:
        if not data.months or data.months <= 0:
            raise HTTPException(status_code=400, detail="Укажи количество месяцев или выбери «навсегда»")
        current = await _latest_ichoplus(player.id, db)
        base = max(datetime.utcnow(), current.expires_at) if current else datetime.utcnow()
        expires_at = base + timedelta(days=30 * data.months)

    db.add(Subscription(player_id=player.id, sku="ichoplus_admin", expires_at=expires_at))
    await db.commit()

    try:
        await charsystem_client.grant_role(player.uuid, "IchoPlus")
    except Exception:
        log.exception("Failed to grant in-game IchoPlus role for player %s", player.id)

    return {"status": "ok", "expires_at": expires_at.isoformat(), "forever": data.forever}


@router.delete("/player/{nickname}/subscription")
async def revoke_subscription(
    nickname: str, _admin: Player = Depends(require_admin), db: AsyncSession = Depends(get_db)
):
    player = await _resolve_player(nickname, db)
    result = await db.execute(
        select(Subscription).where(
            Subscription.player_id == player.id,
            Subscription.sku.like("ichoplus_%"),
            Subscription.expires_at > datetime.utcnow(),
        )
    )
    for sub in result.scalars().all():
        sub.expires_at = datetime.utcnow()
    await db.commit()

    try:
        await charsystem_client.revoke_role(player.uuid, "IchoPlus")
    except Exception:
        log.exception("Failed to revoke in-game IchoPlus role for player %s", player.id)

    return {"status": "ok"}
