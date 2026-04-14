import os
from datetime import datetime
from dotenv import load_dotenv
from sqlalchemy import (
    Column, Integer, String, Float, DateTime, Boolean,
    ForeignKey, Enum as SAEnum, text
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
    is_online = Column(Boolean, default=False, nullable=False)
    server = Column(String, nullable=True)  # gamegraz, farmserv, None=offline

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


class Community(Base):
    __tablename__ = "communities"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    description = Column(String, default="")
    tag = Column(String, default="")
    icon = Column(String, default="🏘️")
    owner_discord_id = Column(String, nullable=False)
    member_count = Column(Integer, default=1, nullable=False)
    banner_url = Column(String, nullable=True)
    discord_url = Column(String, nullable=True)
    members_can_invite = Column(Integer, default=0, nullable=False)
    is_recruiting = Column(Integer, default=0, nullable=False)
    is_private = Column(Integer, default=0, nullable=False)
    slug = Column(String, nullable=True)
    info_blocks = Column(String, default="[]")  # JSON array
    images = Column(String, default="[]")  # JSON array
    created_at = Column(DateTime, default=datetime.utcnow)

    members = relationship("CommunityMember", back_populates="community", cascade="all, delete-orphan")


class CommunityMember(Base):
    __tablename__ = "community_members"

    id = Column(Integer, primary_key=True, index=True)
    community_id = Column(Integer, ForeignKey("communities.id", ondelete="CASCADE"), nullable=False)
    discord_id = Column(String, nullable=False)
    role = Column(String, default="member", nullable=False)
    joined_at = Column(DateTime, default=datetime.utcnow)

    community = relationship("Community", back_populates="members")


class CommunityInvite(Base):
    __tablename__ = "community_invites"

    id = Column(Integer, primary_key=True)
    community_id = Column(Integer, ForeignKey("communities.id", ondelete="CASCADE"), nullable=False)
    invited_nickname = Column(String, nullable=False)
    invited_by_discord_id = Column(String, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class PlayerIP(Base):
    __tablename__ = "player_ips"

    id = Column(Integer, primary_key=True)
    player_id = Column(Integer, ForeignKey("players.id"))
    ip_address = Column(String, nullable=False)
    confirmed = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class PendingAuth(Base):
    __tablename__ = "pending_auths"

    id = Column(Integer, primary_key=True)
    player_id = Column(Integer, ForeignKey("players.id"))
    ip_address = Column(String, nullable=False)
    token = Column(String, nullable=False, unique=True)
    created_at = Column(DateTime, default=datetime.utcnow)


async def get_db() -> AsyncSession:
    async with SessionLocal() as session:
        yield session


async def init_db():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
