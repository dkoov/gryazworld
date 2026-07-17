import asyncio
import os
import time
from collections import defaultdict, deque
from contextlib import asynccontextmanager
from urllib.parse import urlparse

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from starlette.middleware.base import BaseHTTPMiddleware

from database import init_db
from routers import player, bank, fines, web, payments, portals
from routers import admin
from routers import messenger
from routers import bank_web
try:
    from routers import internal
except ImportError:
    internal = None
from routers.fines import warn_router
from cron import check_expired_subscriptions
from auth import PLUGIN_SECRET


@asynccontextmanager
async def lifespan(app: FastAPI):
    await init_db()
    task = asyncio.create_task(check_expired_subscriptions())
    yield
    task.cancel()
    try:
        await task
    except asyncio.CancelledError:
        pass


ALLOWED_ORIGINS = [
    "https://ichorix.cc",
    "https://www.ichorix.cc",
    "https://gryazworld.ru",
    "http://gryazworld.ru",
    "http://localhost:5173",
    "http://127.0.0.1:5173",
]

CSP_HEADER = (
    "default-src 'self'; "
    "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://mc-heads.net https://vzge.me; "
    "style-src 'self' 'unsafe-inline'; "
    "img-src 'self' data: https:; "
    "font-src 'self'; "
    "connect-src 'self' https://api.yookassa.ru https://api.cloudinary.com; "
    "frame-src https://vzge.me;"
)


class SecurityHeadersMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request, call_next):
        response = await call_next(request)
        response.headers["Strict-Transport-Security"] = "max-age=31536000; includeSubDomains"
        response.headers["X-Frame-Options"] = "DENY"
        response.headers["X-Content-Type-Options"] = "nosniff"
        response.headers["Referrer-Policy"] = "strict-origin-when-cross-origin"
        response.headers["Content-Security-Policy"] = CSP_HEADER
        return response


def _client_ip(request: Request) -> str:
    xff = request.headers.get("x-forwarded-for")
    if xff:
        # За nginx первый IP в цепочке — реальный клиент.
        return xff.split(",")[0].strip()
    if request.client:
        return request.client.host
    return ""


class RateLimitMiddleware(BaseHTTPMiddleware):
    def __init__(self, app, limits):
        super().__init__(app)
        self.limits = limits
        self._windows = defaultdict(lambda: deque())

    async def dispatch(self, request, call_next):
        path = request.url.path
        method = request.method
        if method == "OPTIONS":
            return await call_next(request)
        for prefix, limit, window in self.limits:
            if path.startswith(prefix):
                key = f"{prefix}:{_client_ip(request)}"
                now = time.time()
                dq = self._windows[key]
                while dq and dq[0] < now - window:
                    dq.popleft()
                if len(dq) >= limit:
                    return JSONResponse(
                        {"detail": "Rate limit exceeded"},
                        status_code=429,
                    )
                dq.append(now)
                break
        return await call_next(request)


class CSRFProtectionMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request, call_next):
        path = request.url.path
        method = request.method.upper()
        if method in ("GET", "HEAD", "OPTIONS", "TRACE"):
            return await call_next(request)
        if not path.startswith("/web/") or path.startswith("/web/payments/webhook"):
            return await call_next(request)

        # Сервер-плагин не шлёт Origin/Referer; пропускаем при валидном секрете.
        x_plugin_secret = request.headers.get("x-plugin-secret")
        if x_plugin_secret and PLUGIN_SECRET and x_plugin_secret == PLUGIN_SECRET:
            return await call_next(request)

        origin = request.headers.get("origin")
        if origin:
            if origin in ALLOWED_ORIGINS:
                return await call_next(request)
            return JSONResponse({"detail": "Invalid origin"}, status_code=403)

        referer = request.headers.get("referer")
        if referer:
            parsed = urlparse(referer)
            referer_origin = f"{parsed.scheme}://{parsed.netloc}"
            if referer_origin in ALLOWED_ORIGINS:
                return await call_next(request)
            return JSONResponse({"detail": "Invalid referer"}, status_code=403)

        return JSONResponse({"detail": "CSRF protection failed"}, status_code=403)


app = FastAPI(title="GryazWorld Server Panel", lifespan=lifespan)

app.add_middleware(SecurityHeadersMiddleware)
app.add_middleware(CSRFProtectionMiddleware)
app.add_middleware(
    RateLimitMiddleware,
    limits=[
        ("/web/payments/create", 5, 60),
        ("/web/payments/webhook", 60, 60),
        ("/web/", 30, 60),
        ("/mc/", 100, 60),
        ("/auth/", 10, 60),
    ],
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(player.router)
app.include_router(bank.router)
app.include_router(fines.router)
app.include_router(warn_router)
app.include_router(web.router)
app.include_router(admin.router)
app.include_router(messenger.router)
app.include_router(bank_web.router)
app.include_router(payments.router)
app.include_router(portals.router)
if internal is not None:
    app.include_router(internal.router)


@app.get("/health")
async def health():
    return {"status": "ok"}
