import {
  Client,
  TextChannel,
  ThreadChannel,
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
  FileBuilder,
  MessageFlags,
  AttachmentBuilder,
} from "discord.js";

export async function logTicketClose(
  client: Client,
  thread: ThreadChannel,
  verdict: string,
  closedById: string,
  ticketAuthorId: string,
  transcript: AttachmentBuilder | null,
): Promise<void> {
  const logChannelId = process.env.SUPPORT_LOG_CHANNEL_ID;
  if (!logChannelId) return;

  const logChannel = client.channels.cache.get(logChannelId) as TextChannel;
  if (!logChannel) return;

  const now = Math.floor(Date.now() / 1000);

  const container = new ContainerBuilder()
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(`## Тикет закрыт`)
    )
    .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        `**Тикет:** \`${thread.name}\`\n**Автор:** <@${ticketAuthorId}>\n**Закрыл:** <@${closedById}>\n**Дата:** <t:${now}:f>`
      )
    )
    .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        `**Вердикт:**\n\`\`\`${verdict}\`\`\``
      )
    );

  if (transcript) {
    container.addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addFileComponents(
        new FileBuilder().setURL(`attachment://ticket-${thread.id}.html`)
      );
  }

  const msg = await logChannel.send({
    components: [container],
    files: transcript ? [transcript] : [],
    flags: MessageFlags.IsComponentsV2,
    allowedMentions: { parse: [] },
  }).catch(() => null);
  msg?.startThread({
      name: `Обсуждение`,
  });
}
