"""Единая точка инициализации YooKassa SDK + утилиты для вебхука."""
import json
import logging
import os
from ipaddress import ip_address, ip_network
from typing import Optional

from yookassa import Configuration

log = logging.getLogger(__name__)

_configured = False


def configure() -> None:
    """Настроить SDK из env. Идемпотентно."""
    global _configured
    if _configured:
        return
    shop_id = os.getenv("YOOKASSA_SHOP_ID")
    secret = os.getenv("YOOKASSA_SECRET_KEY")
    if not shop_id or not secret:
        raise RuntimeError("YOOKASSA_SHOP_ID / YOOKASSA_SECRET_KEY не заданы в окружении")
    Configuration.account_id = shop_id
    Configuration.secret_key = secret
    _configured = True
    log.info("YooKassa SDK configured (shop_id=%s)", shop_id)


# Whitelist IP ЮKassa. Источник — https://yookassa.ru/developers/using-api/webhooks
# При изменениях в документации — обновить.
WEBHOOK_IP_WHITELIST = [
    "185.71.76.0/27",
    "185.71.77.0/27",
    "77.75.153.0/25",
    "77.75.156.11",
    "77.75.156.35",
    "77.75.154.128/25",
    "2a02:5180::/32",
]

_WEBHOOK_NETS = [ip_network(c) for c in WEBHOOK_IP_WHITELIST]


def is_yookassa_ip(ip: str) -> bool:
    try:
        addr = ip_address(ip)
    except ValueError:
        return False
    return any(addr in net for net in _WEBHOOK_NETS)


def client_ip_from_xff(xff: Optional[str], fallback: Optional[str]) -> Optional[str]:
    """Достаём реальный IP клиента из X-Forwarded-For (первый элемент цепочки).

    Бэк за nginx — request.client.host = IP nginx'а, поэтому опираемся на XFF.
    """
    if xff:
        first = xff.split(",")[0].strip()
        if first:
            return first
    return fallback


def serialize_yookassa(obj) -> str:
    """Сериализуем ответ SDK в JSON-строку (хранится в Payment.raw)."""
    try:
        return json.dumps(
            obj, default=lambda o: getattr(o, "__dict__", str(o)), ensure_ascii=False
        )
    except Exception as e:
        log.warning("Не удалось сериализовать объект YooKassa: %s", e)
        return json.dumps({"_repr": str(obj)}, ensure_ascii=False)
