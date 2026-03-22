import os
from datetime import datetime
from dotenv import load_dotenv
from sqlalchemy import (
    Column, Integer, String, Float, DateTime,
    ForeignKey, Enum as SAEnum
)
from sqlalchemy.orm import DeclarativeBase, relationship
from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession

load_dotenv()

DATABASE_URL = os.getenv("DATABASE_URL", "sqlite+aiosqlite:///./serverpanel.db")

engine = create_async_engine(DATABASE_URL, echo=False)
SessionLocal = async_sessionmaker(engine, expire_on_commit=False)


class Base(DeclarativeBase):
    pass


class Player(Base):
    __tablename__ = "players"

    id = Column(Integer, primary_key=True, index=True)
    uuid = Column(String, unique=True, nullable=False, index=True)
    nickname = Column(String, nullable=False)
    discord_id = Column(String, nullable=True)
    total_seconds = Column(Integer, default=0, nullable=False)
    warns = Column(Integer, default=0, nullable=False)

    bank_account = relationship("BankAccount", back_populates="player", uselist=False)
    fines = relationship("Fine", foreign_keys="Fine.player_id", back_populates="player")
    warn_records = relationship("Warn", foreign_keys="Warn.player_id", back_populates="player")


class BankAccount(Base):
    __tablename__ = "bank_accounts"

    id = Column(Integer, primary_key=True, index=True)
    player_id = Column(Integer, ForeignKey("players.id"), unique=True, nullable=False)
    balance = Column(Float, default=0.0, nullable=False)

    player = relationship("Player", back_populates="bank_account")


class Transaction(Base):
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True, index=True)
    from_player_id = Column(Integer, ForeignKey("players.id"), nullable=True)
    to_player_id = Column(Integer, ForeignKey("players.id"), nullable=True)
    amount = Column(Float, nullable=False)
    type = Column(String, nullable=False)  # deposit, transfer, fine_payment
    comment = Column(String, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)


class Fine(Base):
    __tablename__ = "fines"

    id = Column(Integer, primary_key=True, index=True)
    player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    issued_by = Column(String, nullable=False)
    amount = Column(Float, nullable=False)
    reason = Column(String, nullable=False)
    deadline = Column(DateTime, nullable=True)
    status = Column(String, default="pending", nullable=False)  # pending, paid, cancelled
    created_at = Column(DateTime, default=datetime.utcnow)

    player = relationship("Player", foreign_keys=[player_id], back_populates="fines")


class Warn(Base):
    __tablename__ = "warns"

    id = Column(Integer, primary_key=True, index=True)
    player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    issued_by = Column(String, nullable=False)
    reason = Column(String, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    player = relationship("Player", foreign_keys=[player_id], back_populates="warn_records")


async def get_db() -> AsyncSession:
    async with SessionLocal() as session:
        yield session


async def init_db():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
