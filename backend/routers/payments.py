"""Приём платежей через ЮKassa: создание заказа, вебхук, статус заказа."""
import json
import logging
import os
import uuid
from typing import List

from fastapi import APIRouter, Depends, HTTPException, Request
from pydantic import BaseModel, Field
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from yookassa import Payment as YooPayment

from auth import CurrentUser, current_user
from database import Order, Payment, Player, get_db
from products import get_product
from yookassa_client import (
    client_ip_from_xff,
    configure,
    is_yookassa_ip,
    serialize_yookassa,
)

log = logging.getLogger(__name__)

# Инициализация SDK один раз при импорте модуля.
configure()

router = APIRouter(prefix="/web", tags=["payments"])

RETURN_URL_BASE = os.getenv("YOOKASSA_RETURN_URL", "https://gryazworld.ru/payment/return")


# ─── Schemas ─────────────────────────────────────────────────────────────────

class OrderItem(BaseModel):
    sku: str
    qty: int = Field(default=1, ge=1)


class CreatePaymentRequest(BaseModel):
    items: List[OrderItem]


# ─── Создание платежа ────────────────────────────────────────────────────────

@router.post("/payments/create")
async def create_payment(
    data: CreatePaymentRequest,
    db: AsyncSession = Depends(get_db),
    user: CurrentUser = Depends(current_user),
):
    if not data.items:
        raise HTTPException(status_code=400, detail="Список товаров пуст")

    player_result = await db.execute(
        select(Player).where(Player.discord_id == user.discord_id)
    )
    player = player_result.scalar_one_or_none()
    if player is None or not player.nickname:
        raise HTTPException(status_code=400, detail="Discord не привязан к Minecraft-нику")

    # Пересчёт суммы на сервере: цены из products.PRODUCTS, а не из тела запроса.
    order_items = []
    amount = 0.0
    for it in data.items:
        product = get_product(it.sku)
        if product is None:
            raise HTTPException(status_code=400, detail=f"Неизвестный товар: {it.sku}")
        order_items.append({
            "sku": it.sku,
            "name": product["name"],
            "price": product["price"],
            "qty": it.qty,
        })
        amount += product["price"] * it.qty

    order = Order(
        id=str(uuid.uuid4()),
        discord_id=user.discord_id,
        minecraft_nick=player.nickname,
        items=json.dumps(order_items, ensure_ascii=False),
        amount=amount,
        currency="RUB",
        status="pending",
    )
    db.add(order)
    await db.flush()

    try:
        py = YooPayment.create(
            {
                "amount": {"value": f"{amount:.2f}", "currency": "RUB"},
                "confirmation": {
                    "type": "redirect",
                    # TODO(privacy): order_id в query утекает в Referer на стороне YooKassa.
                    # Перенести идентификатор в opaque-токен или серверный lookup по сессии.
                    "return_url": f"{RETURN_URL_BASE}?order={order.id}",
                },
                "capture": True,
                "description": f"Заказ {order.id} — {player.nickname}",
                "metadata": {
                    "order_id": order.id,
                    "minecraft_nick": player.nickname,
                },
            },
            str(uuid.uuid4()),  # idempotence key
        )
    except Exception:
        log.exception("YooKassa Payment.create failed for order %s", order.id)
        order.status = "failed"
        await db.commit()
        raise HTTPException(status_code=502, detail="Ошибка провайдера оплаты")

    payment = Payment(
        id=py.id,
        order_id=order.id,
        status=py.status,
        raw=serialize_yookassa(py),
    )
    db.add(payment)
    await db.commit()

    return {
        "order_id": order.id,
        "confirmation_url": py.confirmation.confirmation_url,
    }


# ─── Статус заказа ───────────────────────────────────────────────────────────

@router.get("/orders/{order_id}")
async def get_order(
    order_id: str,
    db: AsyncSession = Depends(get_db),
    user: CurrentUser = Depends(current_user),
):
    result = await db.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one_or_none()
    # 404 (а не 403) при чужом заказе — чтобы не палить существование order_id.
    if order is None or order.discord_id != user.discord_id:
        raise HTTPException(status_code=404, detail="Заказ не найден")
    return {
        "id": order.id,
        "status": order.status,
        "amount": order.amount,
        "currency": order.currency,
        "items": json.loads(order.items) if order.items else [],
        "minecraft_nick": order.minecraft_nick,
        "created_at": order.created_at.isoformat() if order.created_at else None,
    }


# ─── Выдача товара ───────────────────────────────────────────────────────────

async def deliver_goods(order: Order) -> None:
    """Выдача купленного товара игроку.

    TODO: интеграция с minecraft-plugin через HTTP-эндпоинт плагина.
    Пока только логируем.
    """
    log.info(
        "deliver_goods TODO: order=%s nick=%s items=%s amount=%s",
        order.id, order.minecraft_nick, order.items, order.amount,
    )


# ─── Webhook ЮKassa ──────────────────────────────────────────────────────────

@router.post("/payments/webhook")
async def yookassa_webhook(request: Request, db: AsyncSession = Depends(get_db)):
    # 1) IP whitelist. Бэк за nginx — берём первый IP из X-Forwarded-For.
    client_ip = client_ip_from_xff(
        request.headers.get("x-forwarded-for"),
        request.client.host if request.client else None,
    )
    if not client_ip or not is_yookassa_ip(client_ip):
        log.warning("Webhook от недоверенного IP: %s", client_ip)
        raise HTTPException(status_code=403, detail="Forbidden")

    body = await request.json()
    event = body.get("event")
    obj = body.get("object") or {}

    # 2) Не доверяем телу — перезапрашиваем платёж через SDK.
    if event in ("payment.succeeded", "payment.canceled"):
        payment_id = obj.get("id")
        if not payment_id:
            log.warning("Webhook без payment_id: %s", body)
            return {"status": "ignored"}
        fresh = YooPayment.find_one(payment_id)
        fresh_status = fresh.status
        raw = serialize_yookassa(fresh)
        metadata = getattr(fresh, "metadata", None) or {}
        order_id = metadata.get("order_id") if isinstance(metadata, dict) else None
    elif event == "refund.succeeded":
        # Для refund в object лежит сам refund; связанный payment — в payment_id.
        payment_id = obj.get("payment_id") or obj.get("id")
        if not payment_id:
            log.warning("Refund webhook без payment_id: %s", body)
            return {"status": "ignored"}
        fresh = YooPayment.find_one(payment_id)
        fresh_status = "refunded"
        raw = serialize_yookassa(fresh)
        metadata = getattr(fresh, "metadata", None) or {}
        order_id = metadata.get("order_id") if isinstance(metadata, dict) else None
    else:
        log.info("Webhook: событие не обрабатывается: %s", event)
        return {"status": "ignored"}

    if not order_id:
        log.warning("Webhook: платёж %s без order_id в metadata", payment_id)
        return {"status": "ignored"}

    order_result = await db.execute(select(Order).where(Order.id == order_id))
    order = order_result.scalar_one_or_none()
    if order is None:
        log.warning("Webhook: заказ %s не найден", order_id)
        return {"status": "ignored"}

    # 3) Сохраняем/обновляем Payment.
    pay_result = await db.execute(select(Payment).where(Payment.id == payment_id))
    payment = pay_result.scalar_one_or_none()
    if payment is None:
        payment = Payment(id=payment_id, order_id=order.id, status=fresh_status, raw=raw)
        db.add(payment)
    else:
        payment.status = fresh_status
        payment.raw = raw

    # 4) Идемпотентно обновляем заказ и выдаём товар.
    if event == "payment.succeeded":
        if order.status == "paid":
            await db.commit()
            return {"status": "ok", "already_paid": True}
        order.status = "paid"
        await db.commit()
        await deliver_goods(order)
    elif event == "payment.canceled":
        if order.status not in ("paid", "refunded"):
            order.status = "canceled"
        await db.commit()
    elif event == "refund.succeeded":
        order.status = "refunded"
        await db.commit()

    return {"status": "ok"}
