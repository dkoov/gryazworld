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
import { createStaffApplication, getStaffApplicationByUser } from "../../services/staffDatabase";

const MOD_ROLE_PINGS = "<@&1470756818354438281>";

const modal: Modal = {
  customId: "staff_modal_",
  prefix: true,
  execute: async (interaction) => {
    const type = interaction.customId.replace("staff_modal_", "") as "moderator" | "media";

    try {
      if (interaction.deferred || interaction.replied) return;
      await interaction.deferReply({ flags: MessageFlags.Ephemeral });
    } catch {
      return;
    }

    const nickname = interaction.fields.getTextInputValue("nickname");
    const age      = interaction.fields.getTextInputValue("age");

    const existing = getStaffApplicationByUser(interaction.user.id);
    if (existing) {
      await interaction.editReply({ content: "У вас уже есть активная заявка в команду." });
      return;
    }

    const channel = interaction.channel as TextChannel;
    if (!channel) {
      await interaction.editReply({ content: "Ошибка: канал не найден" });
      return;
    }

    const isModeratorType = type === "moderator";
    const threadName = isModeratorType
      ? `Заявка-мод - ${nickname}`
      : `Заявка-медиа - ${nickname}`;

    try {
      const thread = await channel.threads.create({
        name: threadName,
        type: ChannelType.PrivateThread,
        invitable: false,
      });

      let contentText: string;
      let appData: Parameters<typeof createStaffApplication>[0] = {
        userId: interaction.user.id,
        threadId: thread.id,
        type,
        nickname,
        age,
      };

      if (isModeratorType) {
        const experience = interaction.fields.getTextInputValue("experience");
        const why        = interaction.fields.getTextInputValue("why");
        const time       = interaction.fields.getTextInputValue("time");
        appData = { ...appData, experience, why, time };
        contentText =
          `**Никнейм:** \`${nickname}\`\n` +
          `**Возраст:** \`${age}\`\n\n` +
          `**Опыт модерирования:**\n\`\`\`${experience}\`\`\`\n` +
          `**Почему хочет стать модератором:**\n\`\`\`${why}\`\`\`\n` +
          `**Готов уделять время:** \`${time}\``;
      } else {
        const links      = interaction.fields.getTextInputValue("links");
        const contentExp = interaction.fields.getTextInputValue("content_exp");
        const why        = interaction.fields.getTextInputValue("why");
        appData = { ...appData, links, contentExp, why };
        contentText =
          `**Никнейм:** \`${nickname}\`\n` +
          `**Возраст:** \`${age}\`\n\n` +
          `**Ссылка на канал/соцсети:** \`${links}\`\n\n` +
          `**Опыт в создании контента:**\n\`\`\`${contentExp}\`\`\`\n` +
          `**Почему хочет стать медиа:**\n\`\`\`${why}\`\`\``;
      }

      const container = new ContainerBuilder()
        .setAccentColor(0x7e4dd9)
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(
            `## ${isModeratorType ? "🛡️ Заявка на модератора" : "🎬 Заявка на медиа"} от ${nickname}`
          )
        )
        .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
        .addTextDisplayComponents(new TextDisplayBuilder().setContent(contentText))
        .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
        .addActionRowComponents(
          new ActionRowBuilder<ButtonBuilder>().addComponents(
            new ButtonBuilder()
              .setCustomId(`staff_accept_${interaction.user.id}`)
              .setLabel("Принять")
              .setStyle(ButtonStyle.Success),
            new ButtonBuilder()
              .setCustomId(`staff_decline_${interaction.user.id}`)
              .setLabel("Отклонить")
              .setStyle(ButtonStyle.Danger),
          )
        );

      await thread.members.add(interaction.user.id);
      createStaffApplication(appData);

      await thread.send({ content: MOD_ROLE_PINGS });
      await thread.send({ components: [container], flags: MessageFlags.IsComponentsV2 });
      await thread.send({
        content: `-# Уважаемый <@${interaction.user.id}>, ожидайте, пока модерация рассмотрит вашу заявку.`,
        flags: MessageFlags.SuppressNotifications,
        allowedMentions: { parse: [] },
      });

      await interaction.editReply({ content: `Заявка создана: <#${thread.id}>` });
    } catch (err) {
      console.error(err);
      if (!interaction.replied) {
        await interaction.editReply({ content: "Не удалось создать заявку" });
      }
    }
  },
};

export default modal;
