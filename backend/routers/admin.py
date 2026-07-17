from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

from auth import CurrentUser, current_user
from database import get_db, Player
import charsystem_client

router = APIRouter(prefix="/web/admin", tags=["admin"])

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
