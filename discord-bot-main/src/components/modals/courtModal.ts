import {
  MessageFlags,
  TextChannel,
  ChannelType,
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
} from "discord.js";
import { Modal } from "../../types";

const modal: Modal = {
  customId: "court_modal",
  execute: async (interaction) => {
    const defendant = interaction.fields.getTextInputValue("defendant");
    const subject = interaction.fields.getTextInputValue("subject");
    const description = interaction.fields.getTextInputValue("description");

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const channel = interaction.channel as TextChannel;
    if (!channel) return;

    const threadName = `⚖️ ${interaction.user.username} vs ${defendant}`.slice(0, 100);
    const thread = await channel.threads.create({
      name: threadName,
      type: ChannelType.PrivateThread,
      invitable: false,
    });

    const chiefJudgeRoleId = process.env.CHIEF_JUDGE_ROLE_ID;

    const container = new ContainerBuilder()
      .setAccentColor(0xe8d26a)
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(`## Иск подан`)
      )
      .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `**Истец:** <@${interaction.user.id}>\n**Ответчик:** \`${defendant}\`\n**Суть иска:** \`${subject}\``
        )
      )
      .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(`**Описание:**\n\`\`\`${description}\`\`\``)
      );

    await thread.members.add(interaction.user.id);

    if (chiefJudgeRoleId) {
      await thread.send({ content: `<@&${chiefJudgeRoleId}>` });
    }
    await thread.send({
      components: [container],
      flags: MessageFlags.IsComponentsV2,
    });
    await thread.send({
      content: `-# <@${interaction.user.id}>, ваш иск зарегистрирован. Верховный Судья добавит нужных участников через /add.`,
      flags: MessageFlags.SuppressNotifications,
      allowedMentions: { parse: [] },
    });

    await interaction.editReply({
      content: `Ваш иск зарегистрирован: ${thread.url}`,
    });
  },
};

export default modal;
