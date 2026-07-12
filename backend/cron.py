import asyncio
import logging
import os
from datetime import datetime

import aiohttp
from sqlalchemy import select, delete

from database import SessionLocal, Player, Subscription

logger = logging.getLogger(__name__)


async def check_expired_subscriptions():
    while True:
        try:
            bot_token = os.getenv("DISCORD_BOT_TOKEN")
            guild_id = os.getenv("DISCORD_GUILD_ID")
            role_id = os.getenv("ICHOPLUS_ROLE_ID")

            if not bot_token or not guild_id or not role_id:
                logger.warning("Discord env variables for IchoPlus cron are not configured")
            else:
                session = SessionLocal()
                try:
                    result = await session.execute(
                        select(Subscription).where(Subscription.expires_at < datetime.utcnow())
                    )
                    expired = result.scalars().all()

                    if expired:
                        async with aiohttp.ClientSession() as http:
                            for sub in expired:
                                player_result = await session.execute(
                                    select(Player).where(Player.id == sub.player_id)
                                )
                                player = player_result.scalar_one_or_none()
                                if player is None:
                                    logger.warning(
                                        "Subscription %s references missing player %s",
                                        sub.id,
                                        sub.player_id,
                                    )
                                    continue
                                if not player.discord_id:
                                    logger.warning(
                                        "Player %s has no discord_id, skipping subscription %s",
                                        player.id,
                                        sub.id,
                                    )
                                    continue

                                url = (
                                    f"https://discord.com/api/v10/guilds/{guild_id}"
                                    f"/members/{player.discord_id}/roles/{role_id}"
                                )
                                headers = {"Authorization": f"Bot {bot_token}"}

                                try:
                                    async with http.delete(url, headers=headers) as resp:
                                        if resp.status in (204, 404):
                                            await session.execute(
                                                delete(Subscription).where(
                                                    Subscription.id == sub.id
                                                )
                                            )
                                            await session.commit()
                                            logger.info(
                                                "Removed IchoPlus role for player %s "
                                                "(subscription %s): status %s",
                                                player.id,
                                                sub.id,
                                                resp.status,
                                            )
                                        else:
                                            logger.warning(
                                                "Discord API returned %s for player %s "
                                                "subscription %s",
                                                resp.status,
                                                player.id,
                                                sub.id,
                                            )
                                except aiohttp.ClientError as exc:
                                    logger.warning(
                                        "Network error removing role for player %s "
                                        "subscription %s: %s",
                                        player.id,
                                        sub.id,
                                        exc,
                                    )
                finally:
                    await session.close()
        except Exception:
            logger.exception("Unexpected error in check_expired_subscriptions")

        await asyncio.sleep(3600)
