import os
from datetime import datetime
from dotenv import load_dotenv
from sqlalchemy import (
    Column, Integer, String, Float, DateTime, Boolean, Text,
    ForeignKey, Enum as SAEnum, text, UniqueConstraint
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
    uuid = Column(String, unique=True, nullable=True, index=True)
    nickname = Column(String, nullable=False)
    discord_id = Column(String, nullable=True)
    total_seconds = Column(Integer, default=0, nullable=False)
    warns = Column(Integer, default=0, nullable=False)
    is_online = Column(Boolean, default=False, nullable=False)
    has_access = Column(Boolean, default=False, nullable=False)
    server = Column(String, nullable=True)  # gamegraz, farmserv, None=offline
    whitelisted = Column(Boolean, default=False, nullable=False)  # True = одобрен (заявка/ручной)
    is_admin = Column(Boolean, default=False, nullable=False)  # доступ к /admin (выдача игровых ролей)

    bank_accounts = relationship("BankAccount", back_populates="player")
    fines = relationship("Fine", foreign_keys="Fine.player_id", back_populates="player")
    warn_records = relationship("Warn", foreign_keys="Warn.player_id", back_populates="player")
    subscriptions = relationship("Subscription", back_populates="player")


class Subscription(Base):
    __tablename__ = "subscriptions"

    id = Column(Integer, primary_key=True)
    player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    sku = Column(String, nullable=False)
    expires_at = Column(DateTime, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    player = relationship("Player", back_populates="subscriptions")


class BankAccount(Base):
    __tablename__ = "bank_accounts"

    id = Column(Integer, primary_key=True, index=True)
    player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    label = Column(String, nullable=True)  # своё название карты, задаётся владельцем
    is_primary = Column(Boolean, default=False, nullable=False)  # тот самый счёт, с которым работает vzBank в игре
    hide_balance = Column(Boolean, default=False, nullable=False)  # личная настройка отображения, не влияет на доступ
    balance = Column(Float, default=0.0, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    player = relationship("Player", back_populates="bank_accounts")


class BankAccountAccess(Base):
    """Игрок, которому владелец счёта дал доступ (видеть баланс/историю, переводить со счёта)."""
    __tablename__ = "bank_account_access"

    id = Column(Integer, primary_key=True)
    account_id = Column(Integer, ForeignKey("bank_accounts.id", ondelete="CASCADE"), nullable=False)
    player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    granted_at = Column(DateTime, default=datetime.utcnow)


class Transaction(Base):
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True, index=True)
    from_player_id = Column(Integer, ForeignKey("players.id"), nullable=True)
    to_player_id = Column(Integer, ForeignKey("players.id"), nullable=True)
    from_account_id = Column(Integer, ForeignKey("bank_accounts.id"), nullable=True)  # null для старых записей
    to_account_id = Column(Integer, ForeignKey("bank_accounts.id"), nullable=True)
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
    comment = Column(String, nullable=True)  # доп. комментарий, напр. "положить вещи в ячейку №5"
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


class Message(Base):
    __tablename__ = "messages"

    id = Column(Integer, primary_key=True, index=True)
    from_player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    to_player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    text = Column(String, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    read_at = Column(DateTime, nullable=True)


class Invoice(Base):
    # "Выставить счёт" -- владелец счёта просит другого игрока заплатить
    __tablename__ = "invoices"

    id = Column(Integer, primary_key=True, index=True)
    account_id = Column(Integer, ForeignKey("bank_accounts.id"), nullable=False)  # куда пойдут деньги
    creditor_player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    debtor_player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    amount = Column(Float, nullable=False)
    comment = Column(String, nullable=True)
    status = Column(String, default="pending", nullable=False)  # pending, paid, declined
    created_at = Column(DateTime, default=datetime.utcnow)
    resolved_at = Column(DateTime, nullable=True)

class PlayerIP(Base):
    __tablename__ = "player_ips"

    id = Column(Integer, primary_key=True)
    player_id = Column(Integer, ForeignKey("players.id"))
    ip_address = Column(String, nullable=False)
    confirmed = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class Like(Base):
    __tablename__ = "likes"
    __table_args__ = (
        UniqueConstraint("liker_discord_id", "target_player_id", name="uq_like_once"),
    )

    id = Column(Integer, primary_key=True)
    liker_discord_id = Column(String, nullable=False)
    target_player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class PlaytimeDaily(Base):
    __tablename__ = "playtime_daily"
    __table_args__ = (
        UniqueConstraint("player_id", "day", name="uq_playtime_day"),
    )

    id = Column(Integer, primary_key=True)
    player_id = Column(Integer, ForeignKey("players.id"), nullable=False)
    day = Column(String, nullable=False)  # YYYY-MM-DD (UTC)
    seconds = Column(Integer, default=0, nullable=False)


class PendingAuth(Base):
    __tablename__ = "pending_auths"

    id = Column(Integer, primary_key=True)
    player_id = Column(Integer, ForeignKey("players.id"))
    ip_address = Column(String, nullable=False)
    token = Column(String, nullable=False, unique=True)
    created_at = Column(DateTime, default=datetime.utcnow)


class Order(Base):
    __tablename__ = "orders"

    id = Column(String, primary_key=True)  # uuid
    discord_id = Column(String, nullable=False, index=True)
    minecraft_nick = Column(String, nullable=False)
    items = Column(Text, nullable=False)  # JSON: [{sku, name, price, qty}]
    amount = Column(Float, nullable=False)
    currency = Column(String, default="RUB", nullable=False)
    status = Column(String, default="pending", nullable=False)  # pending|paid|failed|canceled|refunded
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)


class Payment(Base):
    __tablename__ = "payments"

    id = Column(String, primary_key=True)  # payment_id из YooKassa
    order_id = Column(String, ForeignKey("orders.id"), nullable=False, index=True)
    status = Column(String, nullable=False)
    raw = Column(Text, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)


# ── Auth / bans (used by routers/internal.py) — восстановлено по схеме БД ──────

class AuthSession(Base):
    __tablename__ = "auth_sessions"

    id = Column(Integer, primary_key=True)
    minecraft_name = Column(String, nullable=False)
    ip = Column(String, nullable=False)
    session_id = Column(String, nullable=False)
    expires_at = Column(DateTime, nullable=False)


class DiscordAuthRequest(Base):
    __tablename__ = "discord_auth_requests"

    id = Column(Integer, primary_key=True)
    pending_id = Column(String, nullable=False)
    minecraft_name = Column(String, nullable=False)
    discord_user_id = Column(String, nullable=False)
    ip_address = Column(String, nullable=False)
    timeout_sec = Column(Integer, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class IpBan(Base):
    __tablename__ = "ip_bans"

    id = Column(Integer, primary_key=True)
    ip = Column(String, nullable=False)
    minecraft_name = Column(String, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)


async def get_db() -> AsyncSession:
    async with SessionLocal() as session:
        yield session


async def init_db():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
