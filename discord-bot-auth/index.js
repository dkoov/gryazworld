'use strict';
require('dotenv').config();

const {
  Client, GatewayIntentBits, Partials,
  ContainerBuilder, SectionBuilder, TextDisplayBuilder, ThumbnailBuilder,
  SeparatorBuilder, ActionRowBuilder, ButtonBuilder, ButtonStyle, MessageFlags,
} = require('discord.js');
const express       = require('express');
const { lookupGeo } = require('./geo');

const TOKEN        = process.env.TOKEN || process.env.DISCORD_TOKEN;
const BACKEND_URL  = process.env.BACKEND_URL || 'http://backend:8000';
const API_KEY      = process.env.API_KEY     || 'DiscordAuth_Ix2026';
const WEBHOOK_PORT = parseInt(process.env.WEBHOOK_PORT || '5001', 10);
const SESSION_DAYS = parseInt(process.env.SESSION_DAYS || '30', 10);

if (!TOKEN) { console.error('[AuthBot] TOKEN не задан в .env'); process.exit(1); }

// pendingId → { discordUserId, playerName, ip, timeoutHandle }
const pending = new Map();

const client = new Client({
  intents:  [GatewayIntentBits.DirectMessages],
  partials: [Partials.Channel, Partials.Message],
});

async function backendPost(path, body) {
  const r = await fetch(`${BACKEND_URL}${path}`, {
    method:  'POST',
    headers: { 'Content-Type': 'application/json', 'x-api-key': API_KEY },
    body:    JSON.stringify(body),
  });
  return r.json();
}

// ── Обработка кнопок ──────────────────────────────────────────────────────────

client.on('interactionCreate', async (interaction) => {
  if (!interaction.isButton()) return;
  const { customId } = interaction;

  if (customId.startsWith('da_allow:')) {
    const pendingId = customId.slice('da_allow:'.length);
    const ctx = pending.get(pendingId);
    pending.delete(pendingId);
    if (ctx) clearTimeout(ctx.timeoutHandle);

    await interaction.deferUpdate();
    try {
      const data = await backendPost('/internal/auth/confirm', { pendingId, durationDays: SESSION_DAYS });
      const text = data.ok
        ? '✅ Вход разрешён. Игрок авторизован!'
        : data.error === 'expired'           ? '❌ Запрос истёк. Игрок уже отключён.'
        : data.error === 'pending_not_found' ? '❌ Запрос не найден.'
        : `❌ Ошибка: ${data.error}`;
      await interaction.editReply({
        components: [
          new ContainerBuilder()
            .setAccentColor(data.ok ? 0x4CAF50 : 0xF44336)
            .addTextDisplayComponents(new TextDisplayBuilder().setContent(text)),
        ],
        flags: MessageFlags.IsComponentsV2,
      });
    } catch (e) {
      await interaction.editReply({ content: `❌ Ошибка сервера: ${e.message}` }).catch(() => {});
    }

  } else if (customId.startsWith('da_deny:')) {
    const pendingId = customId.slice('da_deny:'.length);
    const ctx = pending.get(pendingId);
    pending.delete(pendingId);
    if (ctx) clearTimeout(ctx.timeoutHandle);

    await interaction.deferUpdate();
    try { await backendPost('/internal/auth/cancel', { pendingId }); } catch (_) {}
    await interaction.editReply({
      components: [
        new ContainerBuilder()
          .setAccentColor(0xF44336)
          .addTextDisplayComponents(new TextDisplayBuilder().setContent('❌ Вход отклонён.')),
      ],
      flags: MessageFlags.IsComponentsV2,
    }).catch(() => {});

  } else if (customId.startsWith('da_banip:')) {
    const pendingId = customId.slice('da_banip:'.length);
    const ctx = pending.get(pendingId);
    pending.delete(pendingId);
    if (ctx) clearTimeout(ctx.timeoutHandle);

    await interaction.deferUpdate();
    try { await backendPost('/internal/auth/cancel', { pendingId }); } catch (_) {}
    if (ctx) {
      try { await backendPost('/internal/ban/ip', { ip: ctx.ip, minecraftName: ctx.playerName }); } catch (_) {}
    }
    await interaction.editReply({
      components: [
        new ContainerBuilder()
          .setAccentColor(0xFF4444)
          .addTextDisplayComponents(
            new TextDisplayBuilder().setContent(
              `🔨 IP \`${ctx?.ip ?? '?'}\` забанен для \`${ctx?.playerName ?? '?'}\`.`
            )
          ),
      ],
      flags: MessageFlags.IsComponentsV2,
    }).catch(() => {});
  }
});

// ── Webhook от бэкенда ────────────────────────────────────────────────────────

const app = express();
app.use(express.json());

app.post('/auth/notify', async (req, res) => {
  const { pendingId, discordUserId, playerName, ip, timeoutSec } = req.body;
  if (!pendingId || !discordUserId || !playerName) {
    return res.status(400).json({ ok: false, error: 'missing fields' });
  }
  res.json({ ok: true });

  setImmediate(() =>
    handleAuthNotify(pendingId, discordUserId, playerName, ip ?? '?', timeoutSec ?? 120)
      .catch(e => console.error('[AuthBot] handleAuthNotify error:', e.message))
  );
});

app.listen(WEBHOOK_PORT, () => console.log(`[AuthBot] Webhook server on port ${WEBHOOK_PORT}`));

// ── Отправка DM ───────────────────────────────────────────────────────────────

async function handleAuthNotify(pendingId, discordUserId, playerName, ip, timeoutSec) {
  const geo  = await lookupGeo(ip);
  const user = await client.users.fetch(discordUserId);

  const msg = await user.send({
    components: [buildContainer(pendingId, playerName, ip, geo, timeoutSec)],
    flags:      MessageFlags.IsComponentsV2,
  });
  console.log(`[AuthBot] DM sent to ${discordUserId} for ${playerName}`);

  const timeoutHandle = setTimeout(async () => {
    pending.delete(pendingId);
    try { await backendPost('/internal/auth/cancel', { pendingId }); } catch (_) {}
    try {
      await msg.edit({
        components: [
          new ContainerBuilder()
            .setAccentColor(0xFF6600)
            .addTextDisplayComponents(
              new TextDisplayBuilder().setContent('⏰ Время авторизации истекло. Игрок отключён.')
            ),
        ],
        flags: MessageFlags.IsComponentsV2,
      });
    } catch (_) {}
  }, timeoutSec * 1000);

  pending.set(pendingId, { discordUserId, playerName, ip, timeoutHandle });
}

function buildContainer(pendingId, playerName, ip, geo, timeoutSec) {
  return new ContainerBuilder()
    .setAccentColor(0x9E6BFF)
    .addSectionComponents(
      new SectionBuilder()
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent('## Запрос на вход в Ichorix')
        )
        .setThumbnailAccessory(
          new ThumbnailBuilder().setURL(`https://mc-heads.net/avatar/${playerName}/64`)
        )
    )
    .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        `**Игрок:** \`${playerName}\`\n**IP:** \`${ip}\`\n**Локация:** ${geo}\n**Время на ответ:** ${timeoutSec} сек`
      )
    )
    .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent('-# Если это не вы — нажмите Отклонить')
    )
    .addActionRowComponents(
      new ActionRowBuilder().addComponents(
        new ButtonBuilder()
          .setCustomId(`da_allow:${pendingId}`)
          .setLabel('✅ Разрешить')
          .setStyle(ButtonStyle.Success),
        new ButtonBuilder()
          .setCustomId(`da_deny:${pendingId}`)
          .setLabel('❌ Отклонить')
          .setStyle(ButtonStyle.Danger),
        new ButtonBuilder()
          .setCustomId(`da_banip:${pendingId}`)
          .setLabel('🔨 Забанить IP')
          .setStyle(ButtonStyle.Secondary),
      )
    );
}

client.once('clientReady', () => console.log(`[AuthBot] Logged in as ${client.user.tag}`));
client.login(TOKEN);
