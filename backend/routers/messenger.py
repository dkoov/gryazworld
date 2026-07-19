import asyncio
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func, update, or_, and_
from sqlalchemy.ext.asyncio import AsyncSession

from auth import CurrentUser, current_user, verify_plugin_secret
from database import get_db, Player, Message
import charsystem_client

router = APIRouter(prefix="/web/messenger", tags=["messenger"])


def _deliver_ingame(recipient: Player, sender_nickname: str, text: str):
    """Если получатель сейчас online в майне -- кладём сообщение в очередь доставки (cs_pm_bus).
    Не блокирует ответ пользователю -- бэкграунд-таск, ошибки просто логируются внутри."""
    if recipient.is_online and recipient.uuid and not recipient.uuid.startswith("web-") and not recipient.uuid.startswith("manual:"):
        asyncio.ensure_future(charsystem_client.push_private_message(recipient.uuid, sender_nickname, text))


async def _resolve_self(user: CurrentUser, db: AsyncSession) -> Player:
    result = await db.execute(select(Player).where(Player.discord_id == user.discord_id))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Сначала привяжи Minecraft-аккаунт")
    return player


async def _resolve_by_nickname(nickname: str, db: AsyncSession) -> Player:
    result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == nickname.strip().lower())
    )
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Игрок не найден")
    return player


@router.get("/conversations")
async def list_conversations(user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)):
    me = await _resolve_self(user, db)

    result = await db.execute(
        select(Message)
        .where(or_(Message.from_player_id == me.id, Message.to_player_id == me.id))
        .order_by(Message.created_at.desc())
    )
    messages = result.scalars().all()

    by_other: dict[int, dict] = {}
    for m in messages:
        other_id = m.to_player_id if m.from_player_id == me.id else m.from_player_id
        entry = by_other.setdefault(other_id, {"last": m, "unread": 0})
        if m.to_player_id == me.id and m.read_at is None:
            entry["unread"] += 1

    if not by_other:
        return []

    players_result = await db.execute(select(Player).where(Player.id.in_(by_other.keys())))
    players_by_id = {p.id: p for p in players_result.scalars().all()}

    conversations = []
    for other_id, entry in by_other.items():
        other = players_by_id.get(other_id)
        if other is None:
            continue
        last = entry["last"]
        conversations.append({
            "nickname": other.nickname,
            "last_message": last.text,
            "last_message_at": last.created_at.isoformat(),
            "outgoing": last.from_player_id == me.id,
            "unread": entry["unread"],
        })

    conversations.sort(key=lambda c: c["last_message_at"], reverse=True)
    return conversations


@router.get("/unread-count")
async def unread_count(user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)):
    me = await _resolve_self(user, db)
    result = await db.execute(
        select(func.count()).select_from(Message).where(Message.to_player_id == me.id, Message.read_at.is_(None))
    )
    return {"count": result.scalar() or 0}


@router.get("/messages/{nickname}")
async def get_messages(
    nickname: str, user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)
):
    me = await _resolve_self(user, db)
    other = await _resolve_by_nickname(nickname, db)

    result = await db.execute(
        select(Message)
        .where(
            or_(
                and_(Message.from_player_id == me.id, Message.to_player_id == other.id),
                and_(Message.from_player_id == other.id, Message.to_player_id == me.id),
            )
        )
        .order_by(Message.created_at.asc())
    )
    messages = result.scalars().all()

    unread_ids = [m.id for m in messages if m.to_player_id == me.id and m.read_at is None]
    if unread_ids:
        await db.execute(update(Message).where(Message.id.in_(unread_ids)).values(read_at=datetime.utcnow()))
        await db.commit()

    return {
        "nickname": other.nickname,
        "messages": [
            {
                "id": m.id,
                "text": m.text,
                "outgoing": m.from_player_id == me.id,
                "created_at": m.created_at.isoformat(),
            }
            for m in messages
        ],
    }


class SendMessageRequest(BaseModel):
    text: str


@router.post("/messages/{nickname}")
async def send_message(
    nickname: str,
    data: SendMessageRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    me = await _resolve_self(user, db)
    other = await _resolve_by_nickname(nickname, db)

    if other.id == me.id:
        raise HTTPException(status_code=400, detail="Нельзя написать самому себе")

    text = data.text.strip()
    if not text:
        raise HTTPException(status_code=400, detail="Пустое сообщение")
    if len(text) > 2000:
        raise HTTPException(status_code=400, detail="Слишком длинное сообщение")

    msg = Message(from_player_id=me.id, to_player_id=other.id, text=text)
    db.add(msg)
    await db.commit()
    await db.refresh(msg)

    _deliver_ingame(other, me.nickname, text)

    return {"id": msg.id, "text": msg.text, "outgoing": True, "created_at": msg.created_at.isoformat()}


# ─── Из игры (/msg в майне -> тот же мессенджер сайта) ───────────────────────

class IngameSendRequest(BaseModel):
    from_uuid: str
    to_nickname: str
    text: str


@router.post("/mc/send", dependencies=[Depends(verify_plugin_secret)])
async def send_message_ingame(data: IngameSendRequest, db: AsyncSession = Depends(get_db)):
    sender_result = await db.execute(select(Player).where(Player.uuid == data.from_uuid))
    sender = sender_result.scalar_one_or_none()
    if sender is None:
        raise HTTPException(status_code=404, detail="Отправитель не найден")

    other = await _resolve_by_nickname(data.to_nickname, db)
    if other.id == sender.id:
        raise HTTPException(status_code=400, detail="Нельзя написать самому себе")

    text = data.text.strip()
    if not text:
        raise HTTPException(status_code=400, detail="Пустое сообщение")
    if len(text) > 2000:
        text = text[:2000]

    msg = Message(from_player_id=sender.id, to_player_id=other.id, text=text)
    db.add(msg)
    await db.commit()

    _deliver_ingame(other, sender.nickname, text)

    return {"status": "ok"}
