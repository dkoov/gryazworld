import logging
from datetime import datetime
from typing import Optional
from urllib.parse import urlparse

import aiohttp
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession

import charsystem_client
from auth import CurrentUser, current_user, verify_plugin_secret
from database import get_db, Player, Fine, Claim

log = logging.getLogger(__name__)

DISCORD_BOT_URL = "http://ichorix-bot-main:5050/discord/notify"
COURT_ROLES = {"судья"}

router = APIRouter(tags=["court"])


async def _notify_discord(payload: dict):
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(DISCORD_BOT_URL, json=payload, timeout=aiohttp.ClientTimeout(total=5)) as resp:
                if resp.status != 200:
                    log.warning("Discord notify вернул статус %s", resp.status)
    except Exception as e:
        log.warning("Не удалось отправить уведомление в Discord: %s", e)


def _thread_id_from_url(url: Optional[str]) -> Optional[str]:
    if not url:
        return None
    parts = urlparse(url).path.strip("/").split("/")
    return parts[-1] if parts else None


async def _resolve_self(user: CurrentUser, db: AsyncSession) -> Player:
    result = await db.execute(select(Player).where(Player.discord_id == user.discord_id))
    player = result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Сначала привяжи Minecraft-аккаунт")
    return player


async def _can_review_claims(player: Player) -> bool:
    if player.is_admin:
        return True
    if not player.uuid or player.uuid.startswith("web-") or player.uuid.startswith("manual:"):
        return False
    roles = await charsystem_client.get_player_roles(player.uuid)
    return any(r.lower() in COURT_ROLES for r in roles)


async def _require_reviewer(user: CurrentUser, db: AsyncSession) -> Player:
    me = await _resolve_self(user, db)
    if not await _can_review_claims(me):
        raise HTTPException(status_code=403, detail="Рассматривать иски могут только Судья и администраторы")
    return me


# ─── Приём иска от Discord-бота ──────────────────────────────────────────────

class CreateClaimRequest(BaseModel):
    plaintiff_discord_id: str
    defendant_text: str
    subject: str
    description: str
    thread_url: Optional[str] = None


@router.post("/mc/claims", dependencies=[Depends(verify_plugin_secret)])
async def create_claim(data: CreateClaimRequest, db: AsyncSession = Depends(get_db)):
    plaintiff_result = await db.execute(select(Player).where(Player.discord_id == data.plaintiff_discord_id))
    plaintiff = plaintiff_result.scalar_one_or_none()

    defendant_result = await db.execute(
        select(Player).where(func.lower(Player.nickname) == data.defendant_text.strip().lower())
    )
    defendant = defendant_result.scalar_one_or_none()

    claim = Claim(
        plaintiff_player_id=plaintiff.id if plaintiff else None,
        plaintiff_discord_id=data.plaintiff_discord_id,
        defendant_player_id=defendant.id if defendant else None,
        defendant_text=data.defendant_text.strip()[:100],
        subject=data.subject.strip()[:100],
        description=data.description.strip()[:1000],
        thread_url=data.thread_url,
        status="pending",
    )
    db.add(claim)
    await db.commit()
    await db.refresh(claim)
    return {"status": "ok", "claim_id": claim.id}


# ─── Рассмотрение на сайте (Судья / Админ) ───────────────────────────────────

@router.get("/web/court/permissions")
async def get_court_permissions(user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)):
    me = await _resolve_self(user, db)
    return {"can_review": await _can_review_claims(me)}


@router.get("/web/court/claims")
async def list_claims(user: CurrentUser = Depends(current_user), db: AsyncSession = Depends(get_db)):
    await _require_reviewer(user, db)

    result = await db.execute(select(Claim).order_by(Claim.created_at.desc()).limit(200))
    claims = result.scalars().all()

    player_ids = {c.plaintiff_player_id for c in claims if c.plaintiff_player_id}
    player_ids |= {c.defendant_player_id for c in claims if c.defendant_player_id}
    players = {}
    if player_ids:
        pr = await db.execute(select(Player).where(Player.id.in_(player_ids)))
        players = {p.id: p for p in pr.scalars().all()}

    return [
        {
            "id": c.id,
            "plaintiff": players[c.plaintiff_player_id].nickname if c.plaintiff_player_id in players else None,
            "defendant": players[c.defendant_player_id].nickname if c.defendant_player_id in players else c.defendant_text,
            "defendant_resolved": c.defendant_player_id is not None,
            "subject": c.subject,
            "description": c.description,
            "thread_url": c.thread_url,
            "status": c.status,
            "created_at": c.created_at.isoformat(),
            "resolved_at": c.resolved_at.isoformat() if c.resolved_at else None,
            "resolved_by": c.resolved_by,
            "fine_id": c.fine_id,
        }
        for c in claims
    ]


class ApproveClaimRequest(BaseModel):
    defendant_nickname: Optional[str] = None
    amount: float
    reason: str
    comment: Optional[str] = None


@router.post("/web/court/claims/{claim_id}/approve")
async def approve_claim(
    claim_id: int,
    data: ApproveClaimRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    me = await _require_reviewer(user, db)
    if data.amount <= 0:
        raise HTTPException(status_code=400, detail="Сумма должна быть положительной")

    result = await db.execute(select(Claim).where(Claim.id == claim_id))
    claim = result.scalar_one_or_none()
    if claim is None:
        raise HTTPException(status_code=404, detail="Иск не найден")
    if claim.status != "pending":
        raise HTTPException(status_code=400, detail="Иск уже рассмотрен")

    defendant = None
    if claim.defendant_player_id:
        dr = await db.execute(select(Player).where(Player.id == claim.defendant_player_id))
        defendant = dr.scalar_one_or_none()
    if defendant is None and data.defendant_nickname:
        dr = await db.execute(
            select(Player).where(func.lower(Player.nickname) == data.defendant_nickname.strip().lower())
        )
        defendant = dr.scalar_one_or_none()
    if defendant is None:
        raise HTTPException(status_code=400, detail="Не удалось определить ответчика -- укажи ник вручную")

    fine = Fine(
        player_id=defendant.id,
        issued_by=me.nickname,
        amount=data.amount,
        reason=data.reason.strip()[:200],
        comment=data.comment.strip()[:350] if data.comment else None,
        status="pending",
    )
    db.add(fine)
    claim.status = "approved"
    claim.resolved_at = datetime.utcnow()
    claim.resolved_by = me.nickname
    await db.flush()
    claim.fine_id = fine.id
    await db.commit()

    await _notify_discord({
        "type": "fine",
        "fine_id": fine.id,
        "player": defendant.nickname,
        "discord_id": defendant.discord_id,
        "amount": fine.amount,
        "reason": fine.reason,
        "comment": fine.comment,
        "issued_by": fine.issued_by,
        "issued_by_discord_id": me.discord_id,
        "claim_thread_id": _thread_id_from_url(claim.thread_url),
    })

    return {"status": "ok", "fine_id": fine.id}


class DismissClaimRequest(BaseModel):
    comment: Optional[str] = None


@router.post("/web/court/claims/{claim_id}/dismiss")
async def dismiss_claim(
    claim_id: int,
    data: DismissClaimRequest,
    user: CurrentUser = Depends(current_user),
    db: AsyncSession = Depends(get_db),
):
    me = await _require_reviewer(user, db)

    result = await db.execute(select(Claim).where(Claim.id == claim_id))
    claim = result.scalar_one_or_none()
    if claim is None:
        raise HTTPException(status_code=404, detail="Иск не найден")
    if claim.status != "pending":
        raise HTTPException(status_code=400, detail="Иск уже рассмотрен")

    claim.status = "dismissed"
    claim.resolved_at = datetime.utcnow()
    claim.resolved_by = me.nickname

    plaintiff = None
    if claim.plaintiff_player_id:
        pr = await db.execute(select(Player).where(Player.id == claim.plaintiff_player_id))
        plaintiff = pr.scalar_one_or_none()

    await db.commit()

    await _notify_discord({
        "type": "claim_dismissed",
        "claim_id": claim.id,
        "subject": claim.subject,
        "resolved_by": claim.resolved_by,
        "comment": data.comment.strip()[:350] if data.comment else None,
        "plaintiff_discord_id": plaintiff.discord_id if plaintiff else claim.plaintiff_discord_id,
        "claim_thread_id": _thread_id_from_url(claim.thread_url),
    })

    return {"status": "ok"}
