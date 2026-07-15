import {
  MessageFlags,
  ThreadChannel,
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
} from "discord.js";
import { Modal } from "../../types";
import { logTicketClose } from "../../services/supportLog";

const modal: Modal = {
  customId: "ticket_verdict_",
  prefix: true,
  execute: async (interaction) => {
    const ticketAuthorId = interaction.customId.replace("ticket_verdict_", "");
    const verdict = interaction.fields.getTextInputValue("verdict") || "Пусто...";

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const thread = interaction.channel as ThreadChannel;
    if (!thread?.isThread()) return;

    const author = await interaction.client.users.fetch(ticketAuthorId).catch(() => null);

    if (author) {
      const dmContainer = new ContainerBuilder()
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(`## Ваш тикет закрыт`)
        )
        .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(
            `**Тикет:** \`${thread.name}\`\n**Закрыл:** ${interaction.user}`
          )
        )
        .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(
            `**Вердикт:**\n\`\`\`${verdict}\`\`\``
          )
        );

      await author.send({
        components: [dmContainer],
        flags: MessageFlags.IsComponentsV2,
      }).catch(() => null);
    }

    await logTicketClose(
      interaction.client,
      thread,
      verdict,
      interaction.user.id,
      ticketAuthorId,
    );

    setTimeout(async () => {
      await thread.delete().catch(() => null);
    }, 5000);
  },
};

export default modal;
