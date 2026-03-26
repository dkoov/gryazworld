from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager

from database import init_db
from routers import player, bank, fines, web
from routers.fines import warn_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    await init_db()
    yield


app = FastAPI(title="GryazWorld Server Panel", lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "https://gryazworld.ru",
        "http://gryazworld.ru",
        "http://localhost:5173",
        "http://127.0.0.1:5173",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(player.router)
app.include_router(bank.router)
app.include_router(fines.router)
app.include_router(warn_router)
app.include_router(web.router)


@app.get("/health")
async def health():
    return {"status": "ok"}
