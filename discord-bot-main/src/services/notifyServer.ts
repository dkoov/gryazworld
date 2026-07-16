import http from "http";
import { EmbedBuilder, ActionRowBuilder, ButtonBuilder, ButtonStyle, TextChannel } from "discord.js";
import { ExtendedClient } from "../structures/Client";

const PORT = Number(process.env.NOTIFY_PORT ?? 5050);
const NOTIFICATIONS_CHANNEL_ID = process.env.FINES_NOTIFY_CHANNEL_ID ?? "1522965883738128455";
const SBI_FINES_CHANNEL_ID = process.env.FINES_SBI_CHANNEL_ID ?? "1522966760737734747";
const MC_CHAT_CHANNEL_ID = process.env.MC_CHAT_CHANNEL_ID ?? "1504176921833902100";
const AVATAR_URL = (nickname: string) => `https://mc-heads.net/avatar/${encodeURIComponent(nickname)}/64`;

async function getChannel(client: ExtendedClient, id: string): Promise<TextChannel | null> {
  let channel = client.channels.cache.get(id) as TextChannel | undefined;
  if (!channel) {
    channel = (await client.channels.fetch(id).catch(() => null)) as TextChannel | null ?? undefined;
  }
  return channel ?? null;
}

function fineFields(data: any): { name: string; value: string; inline?: boolean }[] {
  const fields = [
    { name: "Игрок", value: String(data.player ?? "?"), inline: true },
    { name: "Сумма", value: `${Math.trunc(data.amount ?? 0)} алмазов`, inline: true },
    { name: "Причина", value: String(data.reason ?? "?"), inline: false },
  ];
  if (data.comment) {
    fields.push({ name: "Комментарий", value: String(data.comment), inline: false });
  }
  if (data.deadline) {
    fields.push({ name: "Срок оплаты", value: new Date(data.deadline).toLocaleString("ru-RU"), inline: true });
  }
  return fields;
}

async function handleFine(client: ExtendedClient, data: any): Promise<void> {
  const embed = new EmbedBuilder()
    .setTitle("Штраф выдан")
    .setColor(0xffcc00)
    .addFields(fineFields(data))
    .setFooter({ text: `Выдал: ${data.issued_by ?? "?"} • #${data.fine_id}` })
    .setTimestamp();

  // канал уведомлений: пингуем оштрафованного игрока
  const notifyChannel = await getChannel(client, NOTIFICATIONS_CHANNEL_ID);
  if (notifyChannel) {
    const content = data.discord_id ? `<@${data.discord_id}>` : undefined;
    await notifyChannel.send({ content, embeds: [embed] }).catch((e) =>
      console.error("[Notify] fine -> уведомления:", e)
    );
  }

  // канал сби-штрафы: пингуем полицейского, который выписал штраф, + кнопка подтверждения
  const sbiChannel = await getChannel(client, SBI_FINES_CHANNEL_ID);
  if (sbiChannel) {
    const row = new ActionRowBuilder<ButtonBuilder>().addComponents(
      new ButtonBuilder()
        .setCustomId(`fine_paid_${data.fine_id}`)
        .setLabel("Подтвердить оплату")
        .setStyle(ButtonStyle.Success)
    );
    const content = data.issued_by_discord_id ? `<@${data.issued_by_discord_id}>` : undefined;
    await sbiChannel.send({ content, embeds: [embed], components: [row] }).catch((e) =>
      console.error("[Notify] fine -> сби-штрафы:", e)
    );
  }
}

async function handleFinePaid(client: ExtendedClient, data: any): Promise<void> {
  const embed = new EmbedBuilder()
    .setTitle("Штраф оплачен")
    .setColor(0x2ecc71)
    .addFields(
      { name: "Игрок", value: String(data.nickname ?? "?"), inline: true },
      { name: "Сумма", value: `${Math.trunc(data.amount ?? 0)} алмазов`, inline: true },
      { name: "Причина", value: String(data.reason ?? "?"), inline: false }
    )
    .setFooter({ text: `#${data.fine_id}` })
    .setTimestamp();

  const notifyChannel = await getChannel(client, NOTIFICATIONS_CHANNEL_ID);
  if (notifyChannel) {
    const content = data.discord_id ? `<@${data.discord_id}>` : undefined;
    await notifyChannel.send({ content, embeds: [embed] }).catch((e) =>
      console.error("[Notify] fine_paid:", e)
    );
  }
}

async function handleWarn(client: ExtendedClient, data: any, overdue = false): Promise<void> {
  const embed = new EmbedBuilder()
    .setTitle(overdue ? "Штраф просрочен! Выдан варн." : "Варн выдан")
    .setColor(overdue ? 0xff6600 : 0xff0000)
    .addFields(
      { name: "Игрок", value: String(data.player ?? "?"), inline: true },
      { name: "Причина", value: String(data.reason ?? "?"), inline: false }
    )
    .setTimestamp();
  if (overdue && data.amount != null) {
    embed.spliceFields(1, 0, { name: "Сумма", value: `${Math.trunc(data.amount)} алмазов`, inline: true });
  }

  const notifyChannel = await getChannel(client, NOTIFICATIONS_CHANNEL_ID);
  if (notifyChannel) {
    const content = data.discord_id ? `<@${data.discord_id}>` : undefined;
    await notifyChannel.send({ content, embeds: [embed] }).catch((e) =>
      console.error("[Notify] warn:", e)
    );
  }
}

async function handleChat(client: ExtendedClient, data: any): Promise<void> {
  const nickname = String(data.nickname ?? "?");
  const message = String(data.message ?? "").slice(0, 1900);
  if (!message) return;

  const channel = await getChannel(client, MC_CHAT_CHANNEL_ID);
  if (!channel) return;

  const embed = new EmbedBuilder()
    .setColor(0x95a5a6)
    .setDescription(message)
    .setAuthor({ name: nickname, iconURL: AVATAR_URL(nickname) });

  await channel.send({ embeds: [embed] }).catch((e) =>
    console.error("[Notify] chat -> minecraft-чат:", e)
  );
}

export function startNotifyServer(client: ExtendedClient): void {
  const server = http.createServer((req, res) => {
    if (req.method !== "POST" || req.url !== "/discord/notify") {
      res.writeHead(404).end();
      return;
    }

    let body = "";
    req.on("data", (chunk) => (body += chunk));
    req.on("end", async () => {
      try {
        const data = JSON.parse(body);
        const type = data.type;
        console.log(`[Notify] type=${type}`);

        switch (type) {
          case "fine":
            await handleFine(client, data);
            break;
          case "fine_paid":
            await handleFinePaid(client, data);
            break;
          case "warn":
            await handleWarn(client, data, false);
            break;
          case "fine_overdue":
            await handleWarn(client, data, true);
            break;
          case "chat":
            await handleChat(client, data);
            break;
          default:
            console.warn(`[Notify] неизвестный тип: ${type}`);
        }

        res.writeHead(200, { "Content-Type": "application/json" }).end(JSON.stringify({ status: "ok" }));
      } catch (e) {
        console.error("[Notify] ошибка обработки:", e);
        res.writeHead(500, { "Content-Type": "application/json" }).end(JSON.stringify({ error: String(e) }));
      }
    });
  });

  server.listen(PORT, "0.0.0.0", () => {
    console.log(`[Notify] HTTP-сервер уведомлений запущен на порту ${PORT}`);
  });
}
