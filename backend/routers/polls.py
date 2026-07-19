import logging
from datetime import datetime
from typing import Optional

import aiohttp
import jwt
from fastapi import APIRouter, Depends, Header, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func, delete
from sqlalchemy.ext.asyncio import AsyncSession

from auth import CurrentUser, current_user, verify_plugin_secret, SESSION_SECRET, JWT_ALG
from database import get_db, Player, Poll, PollCandidate, PollVote
from routers.admin import require_admin

log = logging.getLogger(__name__)

DISCORD_BOT_URL = "http://ichorix-bot-main:5050/discord/notify"


async def _notify_discord(payload: dict):
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(DISCORD_BOT_URL, json=payload, timeout=aiohttp.ClientTimeout(total=5)) as resp:
                if resp.status != 200:
                    log.warning("Discord notify вернул статус %s", resp.status)
    except Exception as e:
        log.warning("Не удалось отправить уведомление в Discord: %s", e)


async def _optional_current_user(
    authorization: Optional[str] = Header(default=None),
) -> Optional[CurrentUser]:
    if not authorization or not authorization.lower().startswith("bearer "):
        return None
    token = authorization.split(" ", 1)[1].strip()
    if not SESSION_SECRET:
        return None
    try:
        payload = jwt.decode(token, SESSION_SECRET, algorithms=[JWT_ALG])
    except Exception:
        return None
    sub = payload.get("sub")
    if not sub:
        return None
    return CurrentUser(discord_id=str(sub), nickname=payload.get("nick"))


router = APIRouter(prefix="/web/polls", tags=["polls"])
admin_router = APIRouter(prefix="/web/admin/polls", tags=["polls-admin"])
game_router = APIRouter(prefix="/mc/polls", tags=["polls-game"])


# ─── Общее ───────────────────────────────────────────────────────────────────

async def _get_poll(poll_id: int, db: AsyncSession) -> Poll:
    result = await db.execute(select(Poll).where(Poll.id == poll_id))
    poll = result.scalar_one_or_none()
    if poll is None:
        raise HTTPException(status_code=404, detail="Голосование не найдено")
    return poll


async def _get_candidates(poll_id: int, db: AsyncSession) -> list[PollCandidate]:
    result = await db.execute(
        select(PollCandidate).where(PollCandidate.poll_id == poll_id).order_by(PollCandidate.id)
    )
    return list(result.scalars().all())


async def _vote_counts(poll_id: int, db: AsyncSession) -> dict[int, int]:
    result = await db.execute(
        select(PollVote.candidate_id, func.count(PollVote.id))
        .where(PollVote.poll_id == poll_id)
        .group_by(PollVote.candidate_id)
    )
    return {cid: cnt for cid, cnt in result.all()}


def _poll_dict(poll: Poll, candidates: list[PollCandidate], vote_counts: dict[int, int], voted_candidate_id=None) -> dict:
    total = sum(vote_counts.values())
    cands = []
    for c in candidates:
        cnt = vote_counts.get(c.id, 0)
        pct = round(cnt / total * 100, 1) if total else 0.0
        cands.append({"id": c.id, "nickname": c.nickname, "votes": cnt, "percent": pct})
    is_open = (not poll.is_closed) and (poll.deadline is None or poll.deadline > datetime.utcnow())
    return {
        "id": poll.id,
        "title": poll.title,
        "visible": poll.visible,
        "is_closed": poll.is_closed,
        "is_open": is_open,
        "deadline": poll.deadline.isoformat() if poll.deadline else None,
        "created_by": poll.created_by,
        "created_at": poll.created_at.isoformat() if poll.created_at else None,
        "total_votes": total,
        "candidates": cands,
        "voted_candidate_id": voted_candidate_id,
    }


async def _voted_candidate_id(poll_id: int, player_id: int, db: AsyncSession) -> Optional[int]:
    result = await db.execute(
        select(PollVote.candidate_id).where(PollVote.poll_id == poll_id, PollVote.voter_player_id == player_id)
    )
    return result.scalar_one_or_none()


async def _cast_vote(poll: Poll, candidate_id: int, player: Player, source: str, db: AsyncSession) -> None:
    if poll.is_closed or (poll.deadline is not None and poll.deadline <= datetime.utcnow()):
        raise HTTPException(status_code=400, detail="Голосование уже закрыто")

    cand_result = await db.execute(
        select(PollCandidate).where(PollCandidate.id == candidate_id, PollCandidate.poll_id == poll.id)
    )
    candidate = cand_result.scalar_one_or_none()
    if candidate is None:
        raise HTTPException(status_code=404, detail="Кандидат не найден")

    existing = await _voted_candidate_id(poll.id, player.id, db)
    if existing is not None:
        raise HTTPException(status_code=400, detail="Ты уже голосовал в этом опросе")

    db.add(
        PollVote(
            poll_id=poll.id,
            candidate_id=candidate.id,
            voter_player_id=player.id,
            source=source,
        )
    )
    await db.commit()

    await _notify_discord(
        {
            "type": "poll_vote",
            "poll_title": poll.title,
            "candidate_nickname": candidate.nickname,
            "voter_nickname": player.nickname,
        }
    )


async def _close_poll(poll: Poll, db: AsyncSession) -> None:
    poll.is_closed = True
    poll.closed_at = datetime.utcnow()

    candidates = await _get_candidates(poll.id, db)
    vote_counts = await _vote_counts(poll.id, db)
    total = sum(vote_counts.values())

    winner = None
    winner_votes = 0
    for c in candidates:
        cnt = vote_counts.get(c.id, 0)
        if cnt > winner_votes:
            winner = c
            winner_votes = cnt

    poll.winner_announced = True
    await db.commit()

    await _notify_discord(
        {
            "type": "poll_closed",
            "poll_title": poll.title,
            "winner_nickname": winner.nickname if winner else None,
            "winner_votes": winner_votes,
            "total_votes": total,
        }
    )


# ─── Публичные (сайт) ────────────────────────────────────────────────────────

@router.get("")
async def list_polls(
    user: Optional[CurrentUser] = Depends(_optional_current_user),
    db: AsyncSession = Depends(get_db),
):
    result = await db.execute(select(Poll).where(Poll.visible == True).order_by(Poll.created_at.desc()))  # noqa: E712
    polls = result.scalars().all()

    player = None
    if user:
        p_result = await db.execute(select(Player).where(Player.discord_id == user.discord_id))
        player = p_result.scalar_one_or_none()

    out = []
    for poll in polls:
        candidates = await _get_candidates(poll.id, db)
        vote_counts = await _vote_counts(poll.id, db)
        voted_id = await _voted_candidate_id(poll.id, player.id, db) if player else None
        out.append(_poll_dict(poll, candidates, vote_counts, voted_id))
    return out


@router.get("/{poll_id}")
async def get_poll(
    poll_id: int,
    user: Optional[CurrentUser] = Depends(_optional_current_user),
    db: AsyncSession = Depends(get_db),
):
    poll = await _get_poll(poll_id, db)
    if not poll.visible:
        raise HTTPException(status_code=404, detail="Голосование не найдено")

    player = None
    if user:
        p_result = await db.execute(select(Player).where(Player.discord_id == user.discord_id))
        player = p_result.scalar_one_or_none()

    candidates = await _get_candidates(poll.id, db)
    vote_counts = await _vote_counts(poll.id, db)
    voted_id = await _voted_candidate_id(poll.id, player.id, db) if player else None
    return _poll_dict(poll, candidates, vote_counts, voted_id)


class VoteRequest(BaseModel):
    candidate_id: int


@router.post("/{poll_id}/vote")
async def vote_web(
    poll_id: int,
    data: VoteRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    poll = await _get_poll(poll_id, db)
    result = await db.execute(select(Player).where(Player.discord_id == user.discord_id))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Сначала привяжи Minecraft-аккаунт")

    await _cast_vote(poll, data.candidate_id, player, "web", db)
    return {"status": "ok"}


# ─── Админ (сайт) ────────────────────────────────────────────────────────────

class CreatePollRequest(BaseModel):
    title: str
    candidates: list[str]
    deadline: Optional[datetime] = None
    visible: bool = True


@admin_router.get("")
async def list_polls_admin(_admin: Player = Depends(require_admin), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Poll).order_by(Poll.created_at.desc()))
    polls = result.scalars().all()
    out = []
    for poll in polls:
        candidates = await _get_candidates(poll.id, db)
        vote_counts = await _vote_counts(poll.id, db)
        out.append(_poll_dict(poll, candidates, vote_counts))
    return out


@admin_router.post("")
async def create_poll(
    data: CreatePollRequest, admin: Player = Depends(require_admin), db: AsyncSession = Depends(get_db)
):
    title = data.title.strip()
    if not title:
        raise HTTPException(status_code=400, detail="Укажи тему голосования")
    names = [n.strip() for n in data.candidates if n.strip()]
    if len(names) < 2:
        raise HTTPException(status_code=400, detail="Нужно минимум 2 кандидата")

    poll = Poll(title=title, visible=data.visible, deadline=data.deadline, created_by=admin.nickname)
    db.add(poll)
    await db.flush()
    for name in names:
        db.add(PollCandidate(poll_id=poll.id, nickname=name))
    await db.commit()
    return {"status": "ok", "id": poll.id}


class UpdatePollRequest(BaseModel):
    title: Optional[str] = None
    visible: Optional[bool] = None
    deadline: Optional[datetime] = None
    clear_deadline: bool = False


@admin_router.patch("/{poll_id}")
async def update_poll(
    poll_id: int,
    data: UpdatePollRequest,
    _admin: Player = Depends(require_admin),
    db: AsyncSession = Depends(get_db),
):
    poll = await _get_poll(poll_id, db)
    if data.title is not None and data.title.strip():
        poll.title = data.title.strip()
    if data.visible is not None:
        poll.visible = data.visible
    if data.clear_deadline:
        poll.deadline = None
    elif data.deadline is not None:
        poll.deadline = data.deadline
    await db.commit()
    return {"status": "ok"}


@admin_router.post("/{poll_id}/close")
async def close_poll_admin(
    poll_id: int, _admin: Player = Depends(require_admin), db: AsyncSession = Depends(get_db)
):
    poll = await _get_poll(poll_id, db)
    if poll.is_closed:
        raise HTTPException(status_code=400, detail="Голосование уже закрыто")
    await _close_poll(poll, db)
    return {"status": "ok"}


@admin_router.delete("/{poll_id}")
async def delete_poll(poll_id: int, _admin: Player = Depends(require_admin), db: AsyncSession = Depends(get_db)):
    poll = await _get_poll(poll_id, db)
    # SQLite не включает PRAGMA foreign_keys по умолчанию, поэтому ondelete="CASCADE"
    # в модели ничего не каскадирует сам по себе -- удаляем детей вручную, иначе
    # осиротевшие PollVote/PollCandidate "прилипают" к следующему опросу, переиспользующему тот же id.
    candidate_ids_result = await db.execute(select(PollCandidate.id).where(PollCandidate.poll_id == poll_id))
    candidate_ids = [row[0] for row in candidate_ids_result.all()]
    if candidate_ids:
        await db.execute(delete(PollVote).where(PollVote.candidate_id.in_(candidate_ids)))
    await db.execute(delete(PollCandidate).where(PollCandidate.poll_id == poll_id))
    await db.delete(poll)
    await db.commit()
    return {"status": "ok"}


# ─── Игровые (плагин) ────────────────────────────────────────────────────────

async def _get_player_by_uuid(uuid: str, db: AsyncSession) -> Player:
    result = await db.execute(select(Player).where(Player.uuid == uuid))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Player not found")
    return player


@game_router.get("/active", dependencies=[Depends(verify_plugin_secret)])
async def list_active_polls_game(db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(Poll)
        .where(Poll.visible == True, Poll.is_closed == False)  # noqa: E712
        .order_by(Poll.created_at.desc())
    )
    polls = result.scalars().all()
    now = datetime.utcnow()
    return [
        {"id": p.id, "title": p.title}
        for p in polls
        if p.deadline is None or p.deadline > now
    ]


@game_router.get("/{poll_id}/candidates", dependencies=[Depends(verify_plugin_secret)])
async def get_poll_candidates_game(poll_id: int, uuid: str, db: AsyncSession = Depends(get_db)):
    poll = await _get_poll(poll_id, db)
    candidates = await _get_candidates(poll.id, db)

    player_result = await db.execute(select(Player).where(Player.uuid == uuid))
    player = player_result.scalar_one_or_none()
    voted_id = await _voted_candidate_id(poll.id, player.id, db) if player else None

    return {
        "id": poll.id,
        "title": poll.title,
        "voted_candidate_id": voted_id,
        "candidates": [{"id": c.id, "nickname": c.nickname} for c in candidates],
    }


class GameVoteRequest(BaseModel):
    uuid: str
    candidate_id: int


@game_router.post("/{poll_id}/vote", dependencies=[Depends(verify_plugin_secret)])
async def vote_game(poll_id: int, data: GameVoteRequest, db: AsyncSession = Depends(get_db)):
    poll = await _get_poll(poll_id, db)
    player = await _get_player_by_uuid(data.uuid, db)
    await _cast_vote(poll, data.candidate_id, player, "game", db)
    return {"status": "ok"}
