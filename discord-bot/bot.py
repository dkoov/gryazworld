import asyncio
import logging
import math
from datetime import datetime

import aiohttp
import discord
from aiohttp import web
from discord import app_commands

# ─── Config ────────────────────────────────────────────────────────────────────
BOT_TOKEN = "REDACTED_BOT_TOKEN"
GUILD_ID = 1032350404035493918
FINES_CHANNEL_ID = 1481259071742152797
BACKEND_URL = "http://155.212.210.252:8000"
API_SECRET = "CHANGE_ME"
WEBHOOK_PORT = 5000

# ─── Logging ───────────────────────────────────────────────────────────────────
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)
log = logging.getLogger("grworld-bot")

# ─── Bot setup ─────────────────────────────────────────────────────────────────
intents = discord.Intents.default()
client = discord.Client(intents=intents)
tree = app_commands.CommandTree(client)


# ─── Helpers ───────────────────────────────────────────────────────────────────

def _fine_embed(data: dict) -> discord.Embed:
    embed = discord.Embed(
        title="Штраф выдан",
        color=0xFFFF00,
        timestamp=datetime.utcnow(),
    )
    embed.add_field(name="Игрок", value=data.get("player", "?"), inline=True)
    embed.add_field(name="Сумма", value=f"{int(data.get('amount', 0))} алмазов", inline=True)
    embed.add_field(name="Причина", value=data.get("reason", "?"), inline=False)

    deadline_str = data.get("deadline")
    if deadline_str:
        try:
            deadline_dt = datetime.fromisoformat(deadline_str)
            seconds_left = max(0, (deadline_dt - datetime.utcnow()).total_seconds())
            if seconds_left < 3600:
                value = f"{math.ceil(seconds_left / 60)} минут"
            else:
                value = f"{math.ceil(seconds_left / 3600)} часов"
            embed.add_field(name="Срок оплаты", value=value, inline=True)
        except Exception:
            embed.add_field(name="Срок оплаты", value=deadline_str, inline=True)

    return embed


def _warn_embed(data: dict) -> discord.Embed:
    embed = discord.Embed(
        title="Варн выдан",
        color=0xFF0000,
        timestamp=datetime.utcnow(),
    )
    embed.add_field(name="Игрок", value=data.get("player", "?"), inline=True)
    embed.add_field(name="Причина", value=data.get("reason", "?"), inline=False)
    return embed


def _fine_overdue_embed(data: dict) -> discord.Embed:
    embed = discord.Embed(
        title="Штраф просрочен! Выдан варн.",
        color=0xFF6600,
        timestamp=datetime.utcnow(),
    )
    embed.add_field(name="Игрок", value=data.get("player", "?"), inline=True)
    embed.add_field(name="Сумма", value=f"{int(data.get('amount', 0))} алмазов", inline=True)
    embed.add_field(name="Причина", value=data.get("reason", "?"), inline=False)
    return embed


def _warn_remove_embed(data: dict) -> discord.Embed:
    embed = discord.Embed(
        title="Варн снят",
        color=0x00FF00,
        timestamp=datetime.utcnow(),
    )
    embed.add_field(name="Игрок", value=data.get("player", "?"), inline=True)
    embed.add_field(name="Осталось варнов", value=str(data.get("total_warns", 0)), inline=True)
    return embed


def _unban_embed(data: dict) -> discord.Embed:
    embed = discord.Embed(
        title="Игрок разбанен",
        color=0x00AA00,
        timestamp=datetime.utcnow(),
    )
    nickname = data.get("player", "?")
    embed.add_field(name="Игрок", value=nickname, inline=True)
    embed.add_field(name="Причина", value="Варны сняты", inline=True)
    return embed


def _ban_embed(data: dict) -> discord.Embed:
    embed = discord.Embed(
        title="Игрок забанен",
        color=0x000000,
        timestamp=datetime.utcnow(),
    )
    nickname = data.get("player", "?")
    embed.add_field(name="Игрок", value=nickname, inline=True)
    embed.add_field(name="Причина", value="3 варна", inline=True)
    return embed


async def send_to_channel(embed: discord.Embed):
    channel = client.get_channel(FINES_CHANNEL_ID)
    if channel is None:
        log.warning("Канал %s не найден — пробую fetch", FINES_CHANNEL_ID)
        try:
            channel = await client.fetch_channel(FINES_CHANNEL_ID)
        except Exception as e:
            log.error("Не удалось получить канал: %s", e)
            return
    await channel.send(embed=embed)


# ─── Webhook server (aiohttp) ──────────────────────────────────────────────────

async def handle_notify(request: web.Request) -> web.Response:
    try:
        data = await request.json()
    except Exception:
        return web.json_response({"error": "bad json"}, status=400)

    event_type = data.get("type")
    log.info("Получено уведомление: type=%s data=%s", event_type, data)

    try:
        if event_type == "fine":
            embed = _fine_embed(data)
        elif event_type == "warn":
            embed = _warn_embed(data)
        elif event_type == "fine_overdue":
            embed = _fine_overdue_embed(data)
        elif event_type == "warn_remove":
            embed = _warn_remove_embed(data)
        elif event_type == "unban":
            embed = _unban_embed(data)
        elif event_type == "ban":
            embed = _ban_embed(data)
        else:
            return web.json_response({"error": "unknown type"}, status=400)

        asyncio.ensure_future(send_to_channel(embed))
    except Exception as e:
        log.error("Ошибка при отправке embed: %s", e)
        return web.json_response({"error": str(e)}, status=500)

    return web.json_response({"status": "ok"})


async def start_webhook_server():
    app = web.Application()
    app.router.add_post("/discord/notify", handle_notify)
    runner = web.AppRunner(app)
    await runner.setup()
    site = web.TCPSite(runner, "0.0.0.0", WEBHOOK_PORT)
    await site.start()
    log.info("Webhook сервер запущен на порту %s", WEBHOOK_PORT)


# ─── Slash команды ─────────────────────────────────────────────────────────────

@tree.command(
    name="штрафы",
    description="Показывает активные штрафы и варны игрока",
    guild=discord.Object(id=GUILD_ID),
)
@app_commands.describe(ник="Никнейм игрока на сервере")
async def cmd_fines(interaction: discord.Interaction, ник: str):
    await interaction.response.defer(thinking=True)

    headers = {"X-Plugin-Secret": API_SECRET}

    async with aiohttp.ClientSession() as session:
        try:
            async with session.get(
                f"{BACKEND_URL}/mc/fines/by-nick/{ник}",
                headers=headers,
            ) as resp:
                if resp.status == 404:
                    await interaction.followup.send(
                        f"Игрок `{ник}` не найден в базе данных.", ephemeral=True
                    )
                    return
                if resp.status != 200:
                    await interaction.followup.send(
                        f"Ошибка бэкенда: HTTP {resp.status}", ephemeral=True
                    )
                    return
                data = await resp.json()
        except aiohttp.ClientError as e:
            await interaction.followup.send(f"Не удалось подключиться к бэкенду: {e}", ephemeral=True)
            return

    nickname = data.get("nickname", ник)
    fines = [f for f in data.get("fines", []) if f["status"] == "pending"]
    warns = data.get("warns", [])

    # Формируем embed
    embed = discord.Embed(
        title=f"Штрафы и варны: {nickname}",
        color=discord.Color.blurple(),
        timestamp=datetime.utcnow(),
    )

    if fines:
        lines = []
        for f in fines:
            deadline = f.get("deadline") or "без срока"
            lines.append(
                f"**#{f['id']}** — {f['amount']:,.2f} грн\n"
                f"  Причина: {f['reason']}\n"
                f"  Выдал: {f['issued_by']} | Срок: {deadline}"
            )
        embed.add_field(
            name=f"Активные штрафы ({len(fines)})",
            value="\n\n".join(lines)[:1024],
            inline=False,
        )
    else:
        embed.add_field(name="Активные штрафы", value="Нет активных штрафов", inline=False)

    if warns:
        lines = []
        for w in warns:
            lines.append(
                f"**#{w['id']}** — {w['reason']}\n"
                f"  Выдал: {w['issued_by']}"
            )
        embed.add_field(
            name=f"Варны ({len(warns)})",
            value="\n\n".join(lines)[:1024],
            inline=False,
        )
    else:
        embed.add_field(name="Варны", value="Нет варнов", inline=False)

    await interaction.followup.send(embed=embed)


# ─── Events ───────────────────────────────────────────────────────────────────

@client.event
async def on_ready():
    log.info("Бот запущен как %s (ID: %s)", client.user, client.user.id)
    try:
        synced = await tree.sync(guild=discord.Object(id=GUILD_ID))
        log.info("Slash-команды синхронизированы: %d", len(synced))
    except Exception as e:
        log.error("Ошибка синхронизации команд: %s", e)


# ─── Main ─────────────────────────────────────────────────────────────────────

async def main():
    await start_webhook_server()
    async with client:
        await client.start(BOT_TOKEN)


if __name__ == "__main__":
    asyncio.run(main())
