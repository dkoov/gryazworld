import {
  ModalBuilder,
  TextInputBuilder,
  TextInputStyle,
  LabelBuilder,
} from "discord.js";
import { SelectMenu } from "../../types";

const selectMenu: SelectMenu = {
  customId: "open_staff_select",
  execute: async (interaction) => {
    const ageMs = Date.now() - interaction.createdTimestamp;
    if (ageMs > 2000) {
      console.warn(`[OpenStaffSelectSlow] id=${interaction.id} ageMs=${ageMs} pid=${process.pid}`);
    }

    const type = interaction.values[0];

    const nicknameInput = new TextInputBuilder()
      .setCustomId("nickname")
      .setStyle(TextInputStyle.Short)
      .setRequired(true)
      .setMinLength(1)
      .setMaxLength(100);

    const ageInput = new TextInputBuilder()
      .setCustomId("age")
      .setStyle(TextInputStyle.Short)
      .setRequired(true)
      .setMinLength(1)
      .setMaxLength(3);

    let modal: ModalBuilder;

    if (type === "staff_moderator") {
      const experienceInput = new TextInputBuilder()
        .setCustomId("experience")
        .setStyle(TextInputStyle.Paragraph)
        .setRequired(true)
        .setMinLength(10)
        .setMaxLength(1000);

      const whyInput = new TextInputBuilder()
        .setCustomId("why")
        .setStyle(TextInputStyle.Paragraph)
        .setRequired(true)
        .setMinLength(10)
        .setMaxLength(1000);

      const timeInput = new TextInputBuilder()
        .setCustomId("time")
        .setStyle(TextInputStyle.Short)
        .setRequired(true)
        .setMinLength(1)
        .setMaxLength(100);

      modal = new ModalBuilder()
        .setCustomId("staff_modal_moderator")
        .setTitle("Заявка на модератора")
        .addLabelComponents(
          new LabelBuilder().setLabel("Ваш никнейм").setTextInputComponent(nicknameInput),
          new LabelBuilder().setLabel("Ваш возраст").setTextInputComponent(ageInput),
          new LabelBuilder().setLabel("Опыт модерирования").setTextInputComponent(experienceInput),
          new LabelBuilder().setLabel("Почему хотите стать модератором?").setTextInputComponent(whyInput),
          new LabelBuilder().setLabel("Сколько времени готовы уделять?").setTextInputComponent(timeInput),
        );
    } else {
      const linksInput = new TextInputBuilder()
        .setCustomId("links")
        .setStyle(TextInputStyle.Short)
        .setRequired(true)
        .setMinLength(1)
        .setMaxLength(500);

      const contentExpInput = new TextInputBuilder()
        .setCustomId("content_exp")
        .setStyle(TextInputStyle.Paragraph)
        .setRequired(true)
        .setMinLength(10)
        .setMaxLength(1000);

      const whyInput = new TextInputBuilder()
        .setCustomId("why")
        .setStyle(TextInputStyle.Paragraph)
        .setRequired(true)
        .setMinLength(10)
        .setMaxLength(1000);

      modal = new ModalBuilder()
        .setCustomId("staff_modal_media")
        .setTitle("Заявка на медиа")
        .addLabelComponents(
          new LabelBuilder().setLabel("Ваш никнейм").setTextInputComponent(nicknameInput),
          new LabelBuilder().setLabel("Ваш возраст").setTextInputComponent(ageInput),
          new LabelBuilder().setLabel("Ссылка на канал/соцсети").setTextInputComponent(linksInput),
          new LabelBuilder().setLabel("Опыт в создании контента").setTextInputComponent(contentExpInput),
          new LabelBuilder().setLabel("Почему хотите стать медиа?").setTextInputComponent(whyInput),
        );
    }

    try {
      await interaction.showModal(modal);
    } catch (error) {
      const err = error as { code?: number };
      if (err.code === 10062) {
        console.error(`[OpenStaffSelectExpired] id=${interaction.id} ageMs=${ageMs} pid=${process.pid}`);
        return;
      }
      throw error;
    }
  },
};

export default selectMenu;
