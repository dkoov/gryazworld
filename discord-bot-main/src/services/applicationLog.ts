import {
  Client,
  TextChannel,
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
  MessageFlags,
  AttachmentBuilder,
  FileBuilder,
  ThreadChannel,
} from "discord.js";
import { Application } from "./database";
import discordTranscripts from "discord-html-transcripts"

function buildTxt(app: Application, action: "accepted" | "declined", reviewerId: string, reviewerUsername?: string): string {
  const lines = [
    `ЗАЯВКА ${action === "accepted" ? "ПРИНЯТА" : "ОТКЛОНЕНА"}`,
    ``,
    `Никнейм: ${app.nickname}`,
    `Возраст: ${app.age}`,
    `ID: ${app.userId}`,
    ``,
    `Чем хочет заниматься`,
    app.reason,
    ``,
    `Откуда узнал`,
    app.source,
    ``,
    `О себе`,
    app.about,
    ``,
    `Решение: ${action === "accepted" ? "Принята" : "Отклонена"}`,
    `Рассмотрел: ${reviewerUsername} (${reviewerId})`,
    `Дата подачи: ${new Date(app.createdAt * 1000).toLocaleString("ru-RU")}`,
    `Дата решения: ${new Date().toLocaleString("ru-RU")}`,
  ];
  return lines.join("\n");
}

export async function logApplication(
  client: Client,
  app: Application,
  thread: ThreadChannel,
  action: "accepted" | "declined",
  reviewerId: string,
  reviewerUsername?: string
): Promise<void> {
  const logChannelId = process.env.LOG_CHANNEL_ID;
  if (!logChannelId) return;

  const logChannel = client.channels.cache.get(logChannelId) as TextChannel;
  if (!logChannel) return;

  const isAccepted = action === "accepted";
  const now = Math.floor(Date.now() / 1000);

  // @ts-ignore
  const transcript = await discordTranscripts.createTranscript(thread, {
    returnType: "attachment",
    filename: `application-${app.userId}.html`,
    poweredBy: false,
  }).catch(() => null);

  const txt = buildTxt(app, action, reviewerId, reviewerUsername);
  const attachment = new AttachmentBuilder(Buffer.from(txt, "utf-8"), {
    name: `application_${app.userId}.txt`,
  });
  

  const container = new ContainerBuilder()
    .setAccentColor(isAccepted ? 0x4CAF50 : 0xF44336)
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        `## ${isAccepted ? "Заявка принята" : "Заявка отклонена"}`
      )
    )
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        `**Никнейм:** \`${app.nickname}\`\n**Пользователь:** <@${app.userId}>\n**${isAccepted ? "Принял" : "Отклонил"}:** <@${reviewerId}>\n**Дата:** <t:${now}:f>`
      )
    )
    .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
    .addFileComponents(
      new FileBuilder().setURL(`attachment://application_${app.userId}.txt`)
    )
    .addFileComponents(
      new FileBuilder().setURL(`attachment://application-${app.userId}.html`)
    );


  const msg = await logChannel.send({
    components: [container],
    files: [attachment, transcript as AttachmentBuilder],
    flags: MessageFlags.IsComponentsV2,
    allowedMentions: { parse: [] },
  }).catch(() => null);

  msg?.startThread({
      name: `Обсуждение`,      
  });
}
