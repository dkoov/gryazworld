import http from "http";
import { EmbedBuilder, ActionRowBuilder, ButtonBuilder, ButtonStyle, TextChannel } from "discord.js";
import { ExtendedClient } from "../structures/Client";
import { recordFineMessage, getFineMessage, removeFineMessage } from "./finesStore";

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

/** Треды (частные обсуждения исков) не всегда в кеше -- ищем так же, как обычный канал. */
async function getThreadSendable(client: ExtendedClient, id: string) {
  let channel = client.channels.cache.get(id);
  if (!channel) {
    channel = await client.channels.fetch(id).catch(() => null) ?? undefined;
  }
  if (channel && channel.isTextBased() && "send" in channel) return channel;
  return null;
}

/** Иск рассмотрен -- закрываем (архивируем) тред, чтобы было видно, что дело закрыто. */
async function archiveThread(thread: any): Promise<void> {
  if (thread?.isThread?.()) {
    await thread.setArchived(true).catch((e: unknown) =>
      console.error("[Notify] не удалось архивировать тред:", e)
    );
  }
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

  // канал уведомлений: пингуем оштрафованного игрока (allowedMentions -- чтобы пинг точно доходил)
  const notifyChannel = await getChannel(client, NOTIFICATIONS_CHANNEL_ID);
  if (notifyChannel) {
    const content = data.discord_id ? `<@${data.discord_id}>` : undefined;
    await notifyChannel.send({
      content,
      embeds: [embed],
      allowedMentions: data.discord_id ? { users: [String(data.discord_id)] } : undefined,
    }).catch((e) => console.error("[Notify] fine -> уведомления:", e));
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
    try {
      const sent = await sbiChannel.send({
        content,
        embeds: [embed],
        components: [row],
        allowedMentions: data.issued_by_discord_id ? { users: [String(data.issued_by_discord_id)] } : undefined,
      });
      if (data.fine_id != null) recordFineMessage(Number(data.fine_id), sbiChannel.id, sent.id);
    } catch (e) {
      console.error("[Notify] fine -> сби-штрафы:", e);
    }
  }

  // если штраф выдан по результату рассмотрения иска -- отчитываемся прямо в тред дела и закрываем его
  if (data.claim_thread_id) {
    const thread = await getThreadSendable(client, String(data.claim_thread_id));
    if (thread) {
      await thread.send({ embeds: [embed] }).catch((e) =>
        console.error("[Notify] fine -> тред иска:", e)
      );
      await archiveThread(thread);
    }
  }
}

async function handleClaimDismissed(client: ExtendedClient, data: any): Promise<void> {
  const embed = new EmbedBuilder()
    .setTitle("Иск отклонён")
    .setColor(0x95a5a6)
    .addFields({ name: "Суть иска", value: String(data.subject ?? "?"), inline: false })
    .setFooter({ text: `Рассмотрел: ${data.resolved_by ?? "?"} • #${data.claim_id}` })
    .setTimestamp();
  if (data.comment) {
    embed.addFields({ name: "Комментарий", value: String(data.comment), inline: false });
  }

  if (data.claim_thread_id) {
    const thread = await getThreadSendable(client, String(data.claim_thread_id));
    if (thread) {
      const content = data.plaintiff_discord_id ? `<@${data.plaintiff_discord_id}>` : undefined;
      await thread.send({ content, embeds: [embed] }).catch((e) =>
        console.error("[Notify] claim_dismissed -> тред иска:", e)
      );
      await archiveThread(thread);
    }
  }
}

async function handleClaimApproved(client: ExtendedClient, data: any): Promise<void> {
  const embed = new EmbedBuilder()
    .setTitle("Иск одобрен (без штрафа)")
    .setColor(0x3498db)
    .addFields(
      { name: "Суть иска", value: String(data.subject ?? "?"), inline: false },
      { name: "Игрок", value: String(data.player ?? "?"), inline: true },
      { name: "Причина решения", value: String(data.reason ?? "?"), inline: false }
    )
    .setFooter({ text: `Рассмотрел: ${data.issued_by ?? "?"} • #${data.claim_id}` })
    .setTimestamp();
  if (data.comment) {
    embed.addFields({ name: "Комментарий", value: String(data.comment), inline: false });
  }

  if (data.claim_thread_id) {
    const thread = await getThreadSendable(client, String(data.claim_thread_id));
    if (thread) {
      const content = data.discord_id ? `<@${data.discord_id}>` : undefined;
      await thread.send({ content, embeds: [embed] }).catch((e) =>
        console.error("[Notify] claim_approved -> тред иска:", e)
      );
      await archiveThread(thread);
    }
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
    await notifyChannel.send({
      content,
      embeds: [embed],
      allowedMentions: data.discord_id ? { users: [String(data.discord_id)] } : undefined,
    }).catch((e) => console.error("[Notify] fine_paid:", e));
  }

  // сами подтверждаем оплату в сби-канале -- убираем кнопку, чтобы полицейский не жал её вручную
  if (data.fine_id != null) {
    const ref = getFineMessage(Number(data.fine_id));
    if (ref) {
      try {
        const channel = await getChannel(client, ref.channelId);
        const message = await channel?.messages.fetch(ref.messageId).catch(() => null);
        if (message) {
          const original = message.embeds[0]
            ? EmbedBuilder.from(message.embeds[0])
            : new EmbedBuilder().setTitle("Штраф");
          original.setColor(0x2ecc71);
          original.addFields({ name: "Статус", value: "✅ Оплачено на сайте -- подтверждено автоматически" });
          await message.edit({ embeds: [original], components: [] });
        }
      } catch (e) {
        console.error("[Notify] авто-подтверждение оплаты в сби-канале:", e);
      } finally {
        removeFineMessage(Number(data.fine_id));
      }
    }
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

async function handlePollVote(client: ExtendedClient, data: any): Promise<void> {
  const notifyChannel = await getChannel(client, NOTIFICATIONS_CHANNEL_ID);
  if (!notifyChannel) return;

  const embed = new EmbedBuilder()
    .setTitle("Новый голос")
    .setColor(0x8b5cf6)
    .setDescription(`**${String(data.poll_title ?? "?")}**`)
    .addFields(
      { name: "Проголосовали за", value: String(data.candidate_nickname ?? "?"), inline: true },
      { name: "Игрок", value: String(data.voter_nickname ?? "?"), inline: true }
    )
    .setTimestamp();

  await notifyChannel.send({ embeds: [embed] }).catch((e) =>
    console.error("[Notify] poll_vote:", e)
  );
}

async function handlePollClosed(client: ExtendedClient, data: any): Promise<void> {
  const notifyChannel = await getChannel(client, NOTIFICATIONS_CHANNEL_ID);
  if (!notifyChannel) return;

  const embed = new EmbedBuilder()
    .setTitle("Голосование завершено")
    .setColor(0xffd700)
    .setDescription(`**${String(data.poll_title ?? "?")}**`)
    .addFields(
      { name: "Победитель", value: String(data.winner_nickname ?? "Без победителя"), inline: true },
      { name: "Голосов", value: `${data.winner_votes ?? 0} из ${data.total_votes ?? 0}`, inline: true }
    )
    .setTimestamp();

  await notifyChannel.send({ embeds: [embed] }).catch((e) =>
    console.error("[Notify] poll_closed:", e)
  );
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
          case "claim_dismissed":
            await handleClaimDismissed(client, data);
            break;
          case "claim_approved":
            await handleClaimApproved(client, data);
            break;
          case "chat":
            await handleChat(client, data);
            break;
          case "poll_vote":
            await handlePollVote(client, data);
            break;
          case "poll_closed":
            await handlePollClosed(client, data);
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
