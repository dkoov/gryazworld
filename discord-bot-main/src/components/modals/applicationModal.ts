import {
  MessageFlags,
  TextChannel,
  ChannelType,
  ContainerBuilder,
  SectionBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
  ThumbnailBuilder,
  ActionRowBuilder,
  ButtonBuilder,
  ButtonStyle,
} from "discord.js";
import { Modal } from "../../types";
import {
  createApplication,
  getApplicationByUser,
} from "../../services/database";

const modal: Modal = {
  customId: "application_modal",
  execute: async (interaction) => {
    try {
      if (interaction.deferred || interaction.replied) return;
      await interaction.deferReply({ flags: MessageFlags.Ephemeral });
    } catch {
      return;
    }

    const nickname = interaction.fields.getTextInputValue("nickname");
    const age = interaction.fields.getTextInputValue("age");
    const about = interaction.fields.getTextInputValue("about");
    const reason = interaction.fields.getTextInputValue("reason");
    const source = interaction.fields.getTextInputValue("source");

    const existing = getApplicationByUser(interaction.user.id);
    if (existing) {
      await interaction.editReply({
        content: "У вас уже есть заявка брух",
      });
      return;
    }

    const channel = interaction.channel as TextChannel;
    if (!channel) {
      await interaction.editReply({ content: "Ошибка: канал не найден" });
      return;
    }

    try {
      const thread = await channel.threads.create({
        name: `Заявка - ${nickname}`,
        type: ChannelType.PrivateThread,
        invitable: false,
      });

      const staffRoleId = process.env.STAFF_ROLE_ID;

      const container = new ContainerBuilder()
        .setAccentColor(0x9e6bff)
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(`## Заявка от ${nickname}`),
        )
        .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(
            `**Никнейм:** \`${nickname}\`
**Возраст:** \`${age}\``,
          ),
        )
        .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(
            `**Чем хотите заниматься:**
\`\`\`${reason}\`\`\``,
          ),
        )
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(
            `**Откуда узнали:**
\`\`\`${source}\`\`\``,
          ),
        )
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(
            `**О себе:**
\`\`\`${about}\`\`\``,
          ),
        )
        .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(`-# ||<@&${staffRoleId}>||`),
        )
        .addActionRowComponents(
          new ActionRowBuilder<ButtonBuilder>().addComponents(
            new ButtonBuilder()
              .setCustomId(`app_accept_${interaction.user.id}`)
              .setLabel("Принять")
              .setStyle(ButtonStyle.Success),
            new ButtonBuilder()
              .setCustomId(`app_decline_${interaction.user.id}`)
              .setLabel("Отклонить")
              .setStyle(ButtonStyle.Danger),
          ),
        );

      await thread.members.add(interaction.user.id);

      createApplication({
        userId: interaction.user.id,
        threadId: thread.id,
        nickname,
        age,
        about,
        reason,
        source,
      });

      await thread.send({
        content: "<@&1470512352943407330> <@&1470761608228634767> <@&1518725041653551214>",
      });
      await thread
        .send({
          components: [container],
          flags: MessageFlags.IsComponentsV2,
        })
        .then((msg) => msg.pin())
        .catch(console.error);
      await thread.send({
        content: `-# Уважаемый <@${interaction.user.id}>, ожидайте, пока модерация рассмотрит вашу заявку. Это может занимать до 24 часов.`,
        flags: MessageFlags.SuppressNotifications,
        allowedMentions: { parse: [] },
      });
      await interaction.editReply({
        content: `Заявка создана: <#${thread.id}>`,
      });
    } catch (err) {
      console.error(err);
      if (!interaction.replied) {
        await interaction.editReply({ content: "Не удалось создать заявку" });
      }
    }
  },
};

export default modal;
