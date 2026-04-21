from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from database import get_db
from pydantic import BaseModel

router = APIRouter(tags=["portals"])


class RegisterPortalRequest(BaseModel):
    portal_id: str
    server: str
    world: str
    x: int
    y: int
    z: int


class TeleportRequest(BaseModel):
    portal_id: str
    player_uuid: str
    player_name: str
    from_server: str


SERVER_PAIRS = {
    "gamegraz": "farmgame",
    "farmgame": "gamegraz",
}


@router.post("/portals/register")
async def register_portal(req: RegisterPortalRequest, db: AsyncSession = Depends(get_db)):
    await db.execute(
        text(
            "INSERT INTO portals (portal_id, server, world, x, y, z) "
            "VALUES (:pid, :srv, :w, :x, :y, :z) "
            "ON CONFLICT(portal_id) DO UPDATE SET "
            "server=excluded.server, world=excluded.world, "
            "x=excluded.x, y=excluded.y, z=excluded.z"
        ),
        {"pid": req.portal_id, "srv": req.server, "w": req.world,
         "x": req.x, "y": req.y, "z": req.z},
    )
    await db.commit()
    return {"status": "ok", "portal_id": req.portal_id}


@router.post("/portals/teleport")
async def request_teleport(req: TeleportRequest, db: AsyncSession = Depends(get_db)):
    target_server = SERVER_PAIRS.get(req.from_server, "lobby")

    await db.execute(
        text(
            "INSERT INTO pending_teleports (player_uuid, world, x, y, z) "
            "VALUES (:uuid, :w, :x, :y, :z) "
            "ON CONFLICT(player_uuid) DO UPDATE SET "
            "world=excluded.world, x=excluded.x, y=excluded.y, z=excluded.z, "
            "created_at=strftime('%s','now')"
        ),
        {"uuid": req.player_uuid, "w": "world", "x": 0.5, "y": 64.0, "z": 0.5},
    )
    await db.commit()

    return {
        "target_server": target_server,
        "world": "world",
        "x": 0.5,
        "y": 64.0,
        "z": 0.5,
    }


@router.get("/players/{player_uuid}/pending")
async def get_pending_teleport(player_uuid: str, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        text("SELECT world, x, y, z FROM pending_teleports WHERE player_uuid = :uuid"),
        {"uuid": player_uuid},
    )
    row = result.fetchone()
    if row is None:
        raise HTTPException(status_code=404, detail="No pending teleport")

    await db.execute(
        text("DELETE FROM pending_teleports WHERE player_uuid = :uuid"),
        {"uuid": player_uuid},
    )
    await db.commit()
    return {"world": row.world, "x": row.x, "y": row.y, "z": row.z}


@router.get("/portals/nearest")
async def get_nearest_portal(server: str, x: int, z: int, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        text("""
            SELECT portal_id, x, y, z, world,
                   ((x - :x) * (x - :x) + (z - :z) * (z - :z)) AS dist_sq
            FROM portals
            WHERE server = :server
            ORDER BY dist_sq ASC
            LIMIT 1
        """),
        {"server": server, "x": x, "z": z}
    )
    row = result.fetchone()
    if row is None:
        raise HTTPException(status_code=404, detail="No portal found")
    if row.dist_sq > 2500:  # > 50 блоков
        raise HTTPException(status_code=404, detail="No portal within 50 blocks")
    return {"portal_id": row.portal_id, "x": row.x, "y": row.y, "z": row.z, "world": row.world}


@router.get("/portals")
async def get_server_portals(server: str, db: AsyncSession = Depends(get_db)):
    """Возвращает все зарегистрированные порталы для указанного сервера."""
    result = await db.execute(
        text("SELECT portal_id, x, y, z, world FROM portals WHERE server = :server"),
        {"server": server}
    )
    rows = result.fetchall()
    return [
        {"portal_id": row.portal_id, "x": row.x, "y": row.y, "z": row.z, "world": row.world}
        for row in rows
    ]
