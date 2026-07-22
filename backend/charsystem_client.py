import os
import logging
from typing import Optional

import aiomysql

log = logging.getLogger(__name__)

CHARSYSTEM_HOST = os.getenv("CHARSYSTEM_MYSQL_HOST", "65.109.50.156")
CHARSYSTEM_PORT = int(os.getenv("CHARSYSTEM_MYSQL_PORT", "3306"))
CHARSYSTEM_USER = os.getenv("CHARSYSTEM_MYSQL_USER", "vz_web_ro")
CHARSYSTEM_PASSWORD = os.getenv("CHARSYSTEM_MYSQL_PASSWORD", "")
CHARSYSTEM_DB = os.getenv("CHARSYSTEM_MYSQL_DB", "charsystem")


async def _connect() -> aiomysql.Connection:
    return await aiomysql.connect(
        host=CHARSYSTEM_HOST,
        port=CHARSYSTEM_PORT,
        user=CHARSYSTEM_USER,
        password=CHARSYSTEM_PASSWORD,
        db=CHARSYSTEM_DB,
        connect_timeout=4,
    )


async def get_characters(uuid: str) -> Optional[list[dict]]:
    """Return this player's characters (name + char_id), or None if the game VPS is unreachable."""
    meta = await get_player_meta(uuid)
    return meta["characters"] if meta is not None else None


async def get_player_meta(uuid: str) -> Optional[dict]:
    """Return {characters, role_name} for a player, or None if the game VPS is unreachable."""
    try:
        conn = await _connect()
    except Exception as e:
        log.warning("charsystem недоступен: %s", e)
        return None

    try:
        async with conn.cursor() as cur:
            await cur.execute(
                "SELECT char_id, name FROM cs_characters WHERE uuid = %s ORDER BY char_id",
                (uuid,),
            )
            rows = await cur.fetchall()
            active_char = -1
            role_name = None
            await cur.execute(
                "SELECT active_char, role_name FROM cs_players WHERE uuid = %s",
                (uuid,),
            )
            player_row = await cur.fetchone()
            if player_row is not None:
                active_char, role_name = player_row
        return {
            "characters": [
                {"char_id": r[0], "name": r[1], "is_active": r[0] == active_char}
                for r in rows
            ],
            "role_name": role_name,
        }
    finally:
        conn.close()


async def get_skin(uuid: str, nickname: Optional[str] = None) -> Optional[dict]:
    """Return {skinUrl, skinModel} for a player's custom vzSkins skin (set via /skin
    in-game), or None if not set / game VPS unreachable. Falls back to lookup by
    nickname when the uuid isn't found (covers manual:<nick> placeholder uuids)."""
    try:
        conn = await _connect()
    except Exception as e:
        log.warning("charsystem недоступен (skin): %s", e)
        return None

    try:
        async with conn.cursor() as cur:
            await cur.execute(
                "SELECT skin_url, skin_model FROM vz_skins WHERE uuid = %s",
                (uuid,),
            )
            row = await cur.fetchone()
            if row is None and nickname:
                await cur.execute(
                    "SELECT skin_url, skin_model FROM vz_skins "
                    "WHERE LOWER(player_name) = LOWER(%s) LIMIT 1",
                    (nickname,),
                )
                row = await cur.fetchone()
        if row is None or not row[0]:
            return None
        return {"skinUrl": row[0], "skinModel": row[1]}
    finally:
        conn.close()


async def get_roles() -> list[str]:
    """All role names defined in cs_roles (includes Owner -- callers that expose this
    to the web admin panel must filter it out themselves)."""
    try:
        conn = await _connect()
    except Exception as e:
        log.warning("charsystem недоступен (roles): %s", e)
        return []

    try:
        async with conn.cursor() as cur:
            await cur.execute("SELECT name FROM cs_roles ORDER BY name")
            rows = await cur.fetchall()
        return [r[0] for r in rows]
    finally:
        conn.close()


async def get_player_roles(uuid: str) -> list[str]:
    try:
        conn = await _connect()
    except Exception as e:
        log.warning("charsystem недоступен (player roles): %s", e)
        return []

    try:
        async with conn.cursor() as cur:
            await cur.execute(
                "SELECT role_name FROM cs_player_roles WHERE uuid = %s ORDER BY sort DESC, role_name",
                (uuid,),
            )
            rows = await cur.fetchall()
        return [r[0] for r in rows]
    finally:
        conn.close()


async def get_all_player_roles() -> dict[str, list[str]]:
    """Bulk: {uuid: [role_name, ...]} for every player who has at least one role.
    Used by the Discord role-sync job -- one query instead of one per player."""
    try:
        conn = await _connect()
    except Exception as e:
        log.warning("charsystem недоступен (all player roles): %s", e)
        return {}

    try:
        async with conn.cursor() as cur:
            await cur.execute("SELECT uuid, role_name FROM cs_player_roles")
            rows = await cur.fetchall()
        result: dict[str, list[str]] = {}
        for uuid, role_name in rows:
            result.setdefault(uuid, []).append(role_name)
        return result
    finally:
        conn.close()


async def _signal_role_refresh(uuid: str) -> None:
    """Будит NetworkManager того сервера, на котором игрок сейчас online, чтобы он
    перечитал роли и применил права живому игроку немедленно (без ожидания /rejoin)."""
    try:
        conn = await _connect()
    except Exception as e:
        log.warning("charsystem недоступен (role refresh signal): %s", e)
        return
    try:
        async with conn.cursor() as cur:
            await cur.execute(
                "INSERT INTO cs_role_refresh_bus (uuid) VALUES (%s)",
                (uuid,),
            )
        await conn.commit()
    finally:
        conn.close()


async def grant_role(uuid: str, role_name: str) -> None:
    """Add a role in cs_player_roles and make it the displayed primary role
    (cs_players.role_name) -- mirrors what /role give does in-game."""
    conn = await _connect()
    try:
        async with conn.cursor() as cur:
            await cur.execute(
                "INSERT IGNORE INTO cs_player_roles (uuid, role_name, sort) VALUES (%s, %s, 0)",
                (uuid, role_name),
            )
            await cur.execute(
                "UPDATE cs_players SET role_name = %s WHERE uuid = %s",
                (role_name, uuid),
            )
        await conn.commit()
    finally:
        conn.close()
    await _signal_role_refresh(uuid)


async def revoke_role(uuid: str, role_name: str) -> None:
    """Remove a role from cs_player_roles. If it was the displayed primary role,
    fall back to any other role the player still has, or clear it."""
    conn = await _connect()
    try:
        async with conn.cursor() as cur:
            await cur.execute(
                "DELETE FROM cs_player_roles WHERE uuid = %s AND role_name = %s",
                (uuid, role_name),
            )
            await cur.execute(
                "SELECT role_name FROM cs_players WHERE uuid = %s",
                (uuid,),
            )
            row = await cur.fetchone()
            if row is not None and row[0] == role_name:
                await cur.execute(
                    "SELECT role_name FROM cs_player_roles WHERE uuid = %s ORDER BY sort DESC, role_name LIMIT 1",
                    (uuid,),
                )
                remaining = await cur.fetchone()
                await cur.execute(
                    "UPDATE cs_players SET role_name = %s WHERE uuid = %s",
                    (remaining[0] if remaining else None, uuid),
                )
        await conn.commit()
    finally:
        conn.close()
    await _signal_role_refresh(uuid)


async def push_private_message(target_uuid: str, from_name: str, message: str) -> bool:
    """Кладёт личное сообщение в очередь доставки in-game (cs_pm_bus). Каждый из 4 игровых
    серверов (через vzCharSystem NetworkManager) сам заберёт его, если игрок online именно там.
    Возвращает False, если игровой VPS недоступен -- сообщение на сайте всё равно уже сохранено."""
    name = _strip_section_codes(from_name)[:32]
    text = _strip_section_codes(message)[:512]
    payload = f"§8[§dЛС§8] §f{name}§d: §f{text}"

    try:
        conn = await _connect()
    except Exception as e:
        log.warning("charsystem недоступен (pm bus): %s", e)
        return False

    try:
        async with conn.cursor() as cur:
            await cur.execute(
                "INSERT INTO cs_pm_bus (target_uuid, from_name, payload) VALUES (%s, %s, %s)",
                (target_uuid, name, payload),
            )
        await conn.commit()
        return True
    finally:
        conn.close()


def _strip_section_codes(s: str) -> str:
    return s.replace("§", "").replace("&", "")


async def publish_chat_message(display_name: str, message: str) -> bool:
    """Insert a Discord chat message onto cs_chat_bus so every Minecraft
    server (via vzCharSystem's NetworkManager) broadcasts it. Returns False
    if the game VPS is unreachable."""
    name = _strip_section_codes(display_name)[:32]
    text = _strip_section_codes(message)[:256]
    payload = f"§8[§9Discord§8] §f{name}§9: §f{text}"

    try:
        conn = await _connect()
    except Exception as e:
        log.warning("charsystem недоступен (chat relay): %s", e)
        return False

    try:
        async with conn.cursor() as cur:
            await cur.execute(
                "INSERT INTO cs_chat_bus (origin, payload) VALUES (%s, %s)",
                ("discord-bridge", payload),
            )
        await conn.commit()
        return True
    finally:
        conn.close()
