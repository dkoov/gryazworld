import {
  MessageFlags,
  TextChannel,
  ChannelType,
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
  ActionRowBuilder,
  ButtonBuilder,
  ButtonStyle,
} from "discord.js";
import { Modal } from "../../types";
import { getCategoryLabel, getCategoryEmoji } from "../../modules/support/supportMessage";

const modal: Modal = {
  customId: "support_modal_",
  prefix: true,
  execute: async (interaction) => {
    const category = interaction.customId.replace("support_modal_", "");
    const subject = interaction.fields.getTextInputValue("subject");
    const description = interaction.fields.getTextInputValue("description");

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const channel = interaction.channel as TextChannel;
    if (!channel) return;

    const categoryLabel = getCategoryLabel(category);
    const categoryEmoji = getCategoryEmoji(category);

    const thread = await channel.threads.create({
      name: `${categoryEmoji} ${categoryLabel} - ${interaction.user.username}`,
      type: ChannelType.PrivateThread,
      invitable: false,
    });

    const staffRoleId = process.env.STAFF_ROLE_ID;

    const container = new ContainerBuilder()
      .setAccentColor(0x9E6BFF)
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `## Тикет поддержки`
        )
      )
      .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `**Тип:** ${categoryEmoji} ${categoryLabel}\n**Тема:** \`${subject}\``
        )
      )
      .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `**Описание:**\n\`\`\`${description}\`\`\``
        )
      )
      .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `-# ||<@&${staffRoleId}>||`
        )
      )
      .addActionRowComponents(
        new ActionRowBuilder<ButtonBuilder>().addComponents(
          new ButtonBuilder()
            .setCustomId(`ticket_close_${interaction.user.id}`)
            .setLabel("Закрыть тикет")
            .setStyle(ButtonStyle.Danger),
        )
      );

    await thread.members.add(interaction.user.id);

    await thread.send({
      content: "<@&1470512352943407330> <@&1470761608228634767> <@&1518725041653551214>",
    });
    await thread.send({
      components: [container],
      flags: MessageFlags.IsComponentsV2,
    });

    await thread.send({
      content: `-# <@${interaction.user.id}>, ваш тикет создан. Ожидайте ответа от модерации.`,
      flags: MessageFlags.SuppressNotifications,
      allowedMentions: { parse: [] },
    });

    await interaction.editReply({
      content: `Ваш тикет создан: ${thread.url}`,
    });
  },
};

export default modal;
