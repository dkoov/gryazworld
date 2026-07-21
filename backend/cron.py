import asyncio
import logging
from datetime import datetime

from sqlalchemy import select, delete

from database import SessionLocal, Player, Subscription, Poll
import charsystem_client

logger = logging.getLogger(__name__)


async def check_expired_subscriptions():
    """Раз в час: снимает игровую роль IchoPlus у игроков, чья IchoPlus-подписка истекла
    И у которых нет другой, всё ещё действующей IchoPlus-подписки (иначе старая просроченная
    запись отбирала бы роль, выданную по новой/повторной подписке -- был баг).

    Discord-роль IchoPlus отдельно поддерживает в актуальном состоянии общий синк ролей
    Minecraft -> Discord (discord-bot-main, каждые 3 минуты, через /internal/discord/role-sync).
    Раньше здесь был прямой вызов Discord API, но он был завязан на другой, заброшенный
    Discord-сервер и никогда не попадал в реальный -- убран как мёртвый и вводящий в заблуждение код.
    """
    while True:
        try:
            session = SessionLocal()
            try:
                result = await session.execute(
                    select(Subscription).where(
                        Subscription.expires_at < datetime.utcnow(),
                        Subscription.sku.like("ichoplus_%"),
                    )
                )
                expired = result.scalars().all()

                processed_player_ids: set[int] = set()
                for sub in expired:
                    if sub.player_id not in processed_player_ids:
                        processed_player_ids.add(sub.player_id)

                        active_result = await session.execute(
                            select(Subscription).where(
                                Subscription.player_id == sub.player_id,
                                Subscription.sku.like("ichoplus_%"),
                                Subscription.expires_at > datetime.utcnow(),
                            )
                        )
                        has_active = active_result.scalars().first() is not None

                        if not has_active:
                            player_result = await session.execute(
                                select(Player).where(Player.id == sub.player_id)
                            )
                            player = player_result.scalar_one_or_none()
                            if player is None:
                                logger.warning(
                                    "Subscription %s references missing player %s", sub.id, sub.player_id
                                )
                            elif (
                                player.uuid
                                and not player.uuid.startswith("web-")
                                and not player.uuid.startswith("manual:")
                            ):
                                try:
                                    await charsystem_client.revoke_role(player.uuid, "IchoPlus")
                                    logger.info(
                                        "Снята игровая роль IchoPlus у игрока %s (подписка истекла)",
                                        player.nickname,
                                    )
                                except Exception:
                                    logger.exception(
                                        "Failed to revoke in-game IchoPlus role for player %s", player.id
                                    )

                    await session.execute(delete(Subscription).where(Subscription.id == sub.id))

                if expired:
                    await session.commit()
            finally:
                await session.close()
        except Exception:
            logger.exception("Unexpected error in check_expired_subscriptions")

        await asyncio.sleep(3600)


async def check_expired_polls():
    """Раз в минуту: закрывает голосования, у которых истёк дедлайн, считает победителя
    и шлёт уведомление в Discord (общая логика с ручным закрытием -- routers.polls._close_poll)."""
    from routers.polls import _close_poll  # локальный импорт -- избегаем цикла импортов при старте

    while True:
        try:
            session = SessionLocal()
            try:
                result = await session.execute(
                    select(Poll).where(
                        Poll.is_closed == False,  # noqa: E712
                        Poll.deadline.is_not(None),
                        Poll.deadline <= datetime.utcnow(),
                    )
                )
                expired_polls = result.scalars().all()
                for poll in expired_polls:
                    try:
                        await _close_poll(poll, session)
                        logger.info("Голосование #%s закрыто по дедлайну", poll.id)
                    except Exception:
                        logger.exception("Не удалось закрыть голосование #%s", poll.id)
            finally:
                await session.close()
        except Exception:
            logger.exception("Unexpected error in check_expired_polls")

        await asyncio.sleep(60)
