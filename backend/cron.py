import asyncio
import json
import logging
import os
from datetime import datetime, timedelta

import aiohttp
from sqlalchemy import select, delete

from database import SessionLocal, Player, Subscription, Poll, DeliveryTask
import charsystem_client

logger = logging.getLogger(__name__)

DISCORD_BOT_URL = os.getenv("DISCORD_BOT_URL", "")
MC_PLUGIN_BASE_URL = (
    f"http://{os.getenv('MC_SERVER_HOST', 'localhost')}:{os.getenv('MC_BAN_PORT', '8080')}"
)
PLUGIN_SECRET = os.getenv("PLUGIN_SECRET", "")


async def _plugin_post(path: str, payload: dict) -> None:
    async with aiohttp.ClientSession() as session:
        async with session.post(
            f"{MC_PLUGIN_BASE_URL}{path}",
            json=payload,
            timeout=aiohttp.ClientTimeout(total=5),
        ) as resp:
            if resp.status >= 400:
                text = await resp.text()
                raise RuntimeError(f"plugin HTTP {resp.status}: {text}")


async def _notify_discord(payload: dict) -> None:
    if not DISCORD_BOT_URL:
        return
    async with aiohttp.ClientSession() as session:
        async with session.post(
            DISCORD_BOT_URL,
            json=payload,
            timeout=aiohttp.ClientTimeout(total=5),
        ) as resp:
            if resp.status >= 400:
                text = await resp.text()
                raise RuntimeError(f"discord HTTP {resp.status}: {text}")


async def _execute_delivery_task(task: DeliveryTask) -> None:
    """Выполняет одну отложенную задачу выдачи товара."""
    payload = json.loads(task.payload)
    action = task.action

    if action == "whitelist_add":
        await _plugin_post("/api/whitelist/add", {"secret": PLUGIN_SECRET, "nickname": payload["nickname"]})
    elif action == "unban":
        await _plugin_post("/api/unban", {"secret": PLUGIN_SECRET, "nickname": payload["nickname"]})
    elif action == "unmute":
        await _plugin_post("/api/unmute", {"secret": PLUGIN_SECRET, "nickname": payload["nickname"]})
    elif action == "charsystem_grant_role":
        await charsystem_client.grant_role(payload["uuid"], payload["role_name"])
    elif action == "discord_notify":
        await _notify_discord(payload)
    else:
        raise RuntimeError(f"unknown delivery action: {action}")


async def process_delivery_tasks():
    """Раз в минуту: ретраит упавшие задачи выдачи товара с экспоненциальным бэкоффом."""
    while True:
        try:
            session = SessionLocal()
            try:
                result = await session.execute(
                    select(DeliveryTask).where(
                        DeliveryTask.status == "pending",
                        DeliveryTask.next_attempt_at <= datetime.utcnow(),
                    ).order_by(DeliveryTask.next_attempt_at)
                )
                tasks = result.scalars().all()
                for task in tasks:
                    try:
                        await _execute_delivery_task(task)
                        task.status = "completed"
                        task.last_error = None
                        logger.info("Delivery task %s completed (%s)", task.id, task.action)
                    except Exception as e:
                        task.attempts += 1
                        task.last_error = str(e)[:1024]
                        if task.attempts >= task.max_attempts:
                            task.status = "failed"
                            logger.error(
                                "Delivery task %s failed after %s attempts: %s",
                                task.id, task.attempts, e
                            )
                        else:
                            backoff_seconds = min(2 ** task.attempts * 60, 3600)
                            task.next_attempt_at = datetime.utcnow() + timedelta(seconds=backoff_seconds)
                            logger.warning(
                                "Delivery task %s failed (attempt %s/%s): %s",
                                task.id, task.attempts, task.max_attempts, e
                            )
                    task.updated_at = datetime.utcnow()
                if tasks:
                    await session.commit()
            finally:
                await session.close()
        except Exception:
            logger.exception("Unexpected error in process_delivery_tasks")

        await asyncio.sleep(60)


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


async def check_overdue_fines():
    """Раз в минуту: помечает просроченные штрафы и выдаёт за них варн (см. правила 1.1/1.2).
    Раньше это делал плагин ServerPanel (fines.check-interval в его config.yml) -- плагин убран
    как дублирующий vzBank/vzPunish, а этот периодический вызов был единственным, кто дёргал
    /mc/fines/overdue, так что без него просроченные штрафы просто зависали в статусе pending."""
    from routers.fines import process_overdue  # локальный импорт -- избегаем цикла импортов при старте

    while True:
        try:
            session = SessionLocal()
            try:
                result = await process_overdue(db=session)
                if result.get("processed"):
                    logger.info("Просроченные штрафы обработаны: %s", result["processed"])
            finally:
                await session.close()
        except Exception:
            logger.exception("Unexpected error in check_overdue_fines")

        await asyncio.sleep(60)
