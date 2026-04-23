import os
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
