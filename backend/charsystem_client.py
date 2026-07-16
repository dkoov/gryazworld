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
