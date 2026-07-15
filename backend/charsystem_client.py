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
            await cur.execute(
                "SELECT active_char FROM cs_players WHERE uuid = %s",
                (uuid,),
            )
            active_row = await cur.fetchone()
            if active_row is not None:
                active_char = active_row[0]
        return [
            {"char_id": r[0], "name": r[1], "is_active": r[0] == active_char}
            for r in rows
        ]
    finally:
        conn.close()
