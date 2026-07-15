import os
import re
from datetime import datetime, timedelta, timezone
from typing import Optional

import jwt
from dotenv import load_dotenv
from fastapi import Depends, Header, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

load_dotenv()

PLUGIN_SECRET = os.getenv("PLUGIN_SECRET", "")
SESSION_SECRET = os.getenv("SESSION_SECRET", "")
JWT_ALG = "HS256"
JWT_TTL = timedelta(days=7)

DISCORD_ID_RE = re.compile(r"^\d{5,30}$")

_bearer = HTTPBearer(auto_error=False)


async def verify_plugin_secret(x_plugin_secret: str = Header(...)):
    if x_plugin_secret != PLUGIN_SECRET:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Invalid plugin secret",
        )


def issue_session_token(discord_id: str, nickname: Optional[str] = None) -> str:
    if not SESSION_SECRET:
        raise RuntimeError("SESSION_SECRET is not configured")
    now = datetime.now(timezone.utc)
    payload = {
        "sub": str(discord_id),
        "nick": nickname,
        "iat": int(now.timestamp()),
        "exp": int((now + JWT_TTL).timestamp()),
    }
    return jwt.encode(payload, SESSION_SECRET, algorithm=JWT_ALG)


class CurrentUser:
    def __init__(self, discord_id: str, nickname: Optional[str]):
        self.discord_id = discord_id
        self.nickname = nickname


async def current_user(
    creds: Optional[HTTPAuthorizationCredentials] = Depends(_bearer),
) -> CurrentUser:
    if creds is None or not creds.credentials:
        raise HTTPException(status_code=401, detail="not_authenticated")
    if not SESSION_SECRET:
        raise HTTPException(status_code=500, detail="session_not_configured")
    try:
        payload = jwt.decode(creds.credentials, SESSION_SECRET, algorithms=[JWT_ALG])
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="token_expired")
    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="invalid_token")
    sub = payload.get("sub")
    if not sub:
        raise HTTPException(status_code=401, detail="invalid_token")
    return CurrentUser(discord_id=str(sub), nickname=payload.get("nick"))


def resolve_actor_discord_id(
    authorization: Optional[str],
    x_plugin_secret: Optional[str],
    body_discord_id: Optional[str],
) -> str:
    """Return actor's discord_id for endpoints reachable from both web and plugin.

    - Bearer JWT  → discord_id from token sub (body_discord_id ignored).
    - X-Plugin-Secret → body_discord_id, validated by DISCORD_ID_RE
      (даже при валидном PLUGIN_SECRET не доверяем содержимому тела автоматически).
    - Иначе → 401.
    """
    if authorization and authorization.lower().startswith("bearer "):
        token = authorization.split(" ", 1)[1].strip()
        if not token or not SESSION_SECRET:
            raise HTTPException(status_code=401, detail="not_authenticated")
        try:
            payload = jwt.decode(token, SESSION_SECRET, algorithms=[JWT_ALG])
        except jwt.ExpiredSignatureError:
            raise HTTPException(status_code=401, detail="token_expired")
        except jwt.InvalidTokenError:
            raise HTTPException(status_code=401, detail="invalid_token")
        sub = str(payload.get("sub") or "")
        if not sub:
            raise HTTPException(status_code=401, detail="invalid_token")
        return sub

    if x_plugin_secret is not None:
        if not PLUGIN_SECRET or x_plugin_secret != PLUGIN_SECRET:
            raise HTTPException(status_code=403, detail="Invalid plugin secret")
        if not body_discord_id or not DISCORD_ID_RE.fullmatch(body_discord_id):
            raise HTTPException(status_code=400, detail="Некорректный discord_id")
        return body_discord_id

    raise HTTPException(status_code=401, detail="not_authenticated")
