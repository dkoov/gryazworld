import {
  Client,
  GatewayIntentBits,
  Partials,
  ContainerBuilder,
  SectionBuilder,
  TextDisplayBuilder,
  ThumbnailBuilder,
  SeparatorBuilder,
  ActionRowBuilder,
  ButtonBuilder,
  ButtonStyle,
  MessageFlags,
} from "discord.js";
import express from "express";
import dotenv from "dotenv";
dotenv.config();

const TOKEN        = process.env.TOKEN!;
const BACKEND_URL  = process.env.BACKEND_URL!;
const API_KEY      = process.env.API_KEY!;
const WEBHOOK_PORT = parseInt(process.env.WEBHOOK_PORT ?? "5001");
const SESSION_DAYS = parseInt(process.env.SESSION_DAYS  ?? "30");

const client = new Client({
  intents: [
    GatewayIntentBits.Guilds,
    GatewayIntentBits.GuildMembers,
    GatewayIntentBits.DirectMessages,
  ],
  partials: [Partials.Channel, Partials.Message],
});

client.once("ready", () => {
  console.log(`[AuthBot] Logged in as ${client.user?.tag}`);
});

// ── Кнопки в DM ──────────────────────────────────────────────────────────────
client.on("interactionCreate", async (interaction) => {
  if (!interaction.isButton()) return;
  const { customId } = interaction;

  if (customId.startsWith("da_allow:")) {
    const pendingId = customId.slice("da_allow:".length);
    await interaction.deferUpdate();

    try {
      const res = await fetch(`${BACKEND_URL}/internal/auth/confirm`, {
        method: "POST",
        headers: { "Content-Type": "application/json", "x-api-key": API_KEY },
        body: JSON.stringify({ pendingId, durationDays: SESSION_DAYS }),
      });
      const data = await res.json() as { ok: boolean; error?: string };

      const text = data.ok
        ? "✅ Вход разрешён. Можете играть!"
        : data.error === "expired"   ? "❌ Запрос истёк. Перезайдите на сервер."
        : data.error === "pending_not_found" ? "❌ Запрос не найден."
        : `❌ Ошибка: ${data.error}`;

      await interaction.editReply({
        components: [
          new ContainerBuilder()
            .setAccentColor(data.ok ? 0x4CAF50 : 0xF44336)
            .addTextDisplayComponents(new TextDisplayBuilder().setContent(text))
        ],
        flags: MessageFlags.IsComponentsV2,
      });
    } catch (e) {
      await interaction.editReply({ content: `❌ Ошибка сервера: ${e}` });
    }

  } else if (customId.startsWith("da_deny:")) {
    const pendingId = customId.slice("da_deny:".length);
    await interaction.deferUpdate();

    try {
      await fetch(`${BACKEND_URL}/internal/auth/cancel`, {
        method: "POST",
        headers: { "Content-Type": "application/json", "x-api-key": API_KEY },
        body: JSON.stringify({ pendingId }),
      });
    } catch {}

    await interaction.editReply({
      components: [
        new ContainerBuilder()
          .setAccentColor(0xF44336)
          .addTextDisplayComponents(
            new TextDisplayBuilder().setContent("❌ Вы отклонили запрос на вход.")
          ),
      ],
      flags: MessageFlags.IsComponentsV2,
    });
  }
});

// ── HTTP-сервер для вебхука от бэкенда ───────────────────────────────────────
const app = express();
app.use(express.json());

app.post("/auth/notify", async (req, res) => {
  const { pendingId, discordUserId, playerName, ip, timeoutSec } = req.body;
  if (!pendingId || !discordUserId || !playerName) {
    res.status(400).json({ ok: false, error: "missing fields" });
    return;
  }

  try {
    const user = await client.users.fetch(discordUserId);

    const container = new ContainerBuilder()
      .setAccentColor(0x9e6bff)
      .addSectionComponents(
        new SectionBuilder()
          .addTextDisplayComponents(
            new TextDisplayBuilder().setContent(
              `## Запрос на вход в Ichorix`
            )
          )
          .setThumbnailAccessory(
            new ThumbnailBuilder().setURL(
              `https://mc-heads.net/avatar/${playerName}/64`
            )
          )
      )
      .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `**Игрок:** \`${playerName}\`\n**IP:** \`${ip}\`\n**Время на ответ:** ${timeoutSec} сек`
        )
      )
      .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `-# Если это не вы — нажмите Отклонить`
        )
      )
      .addActionRowComponents(
        new ActionRowBuilder<ButtonBuilder>().addComponents(
          new ButtonBuilder()
            .setCustomId(`da_allow:${pendingId}`)
            .setLabel("Разрешить")
            .setStyle(ButtonStyle.Success),
          new ButtonBuilder()
            .setCustomId(`da_deny:${pendingId}`)
            .setLabel("Отклонить")
            .setStyle(ButtonStyle.Danger)
        )
      );

    await user.send({
      components: [container],
      flags: MessageFlags.IsComponentsV2,
    });

    console.log(`[AuthBot] DM sent to ${discordUserId} for ${playerName}`);
    res.json({ ok: true });
  } catch (e) {
    console.error(`[AuthBot] Failed to send DM to ${discordUserId}:`, e);
    res.status(500).json({ ok: false, error: String(e) });
  }
});

app.listen(WEBHOOK_PORT, () => {
  console.log(`[AuthBot] Webhook server on port ${WEBHOOK_PORT}`);
});

client.login(TOKEN);
