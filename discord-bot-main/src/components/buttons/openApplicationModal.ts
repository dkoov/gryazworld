import {
  ModalBuilder,
  LabelBuilder,
  TextInputBuilder,
  TextInputStyle,
} from "discord.js";
import { hostname } from "os";
import { Button } from "../../types";

const hostId = process.env.HOSTNAME || process.env.COMPUTERNAME || hostname();

const button: Button = {
  customId: "open_application_modal",
  execute: async (interaction) => {
    const ageMs = Date.now() - interaction.createdTimestamp;
    if (ageMs < -1000) {
      console.warn(
        `[OpenApplicationModalClockSkew] id=${interaction.id} ageMs=${ageMs} createdTs=${interaction.createdTimestamp} nowTs=${Date.now()} host=${hostId} pid=${process.pid}`
      );
    }

    if (ageMs > 2000) {
      console.warn(
        `[OpenApplicationModalSlow] id=${interaction.id} ageMs=${ageMs} host=${hostId} pid=${process.pid}`
      );
    }

    const nicknameInput = new TextInputBuilder()
      .setCustomId("nickname")
      .setStyle(TextInputStyle.Short)
      .setRequired(true)
      .setMinLength(3)
      .setMaxLength(16);

    const ageInput = new TextInputBuilder()
      .setCustomId("age")
      .setStyle(TextInputStyle.Short)
      .setRequired(true)
      .setMinLength(2)
      .setMaxLength(2);

    const aboutInput = new TextInputBuilder()
      .setCustomId("about")
      .setStyle(TextInputStyle.Paragraph)
      .setRequired(true)
      .setMinLength(50)
      .setMaxLength(2000);

    const reasonInput = new TextInputBuilder()
      .setCustomId("reason")
      .setStyle(TextInputStyle.Paragraph)
      .setRequired(true)
      .setMinLength(10)
      .setMaxLength(2000);

    const sourceInput = new TextInputBuilder()
      .setCustomId("source")
      .setStyle(TextInputStyle.Paragraph)
      .setRequired(true)
      .setMinLength(10)
      .setMaxLength(2000);

    const modal = new ModalBuilder()
      .setCustomId("application_modal")
      .setTitle("Заявка на сервер")
      .addLabelComponents(
        new LabelBuilder()
          .setLabel("Ваш никнейм")
          .setTextInputComponent(nicknameInput),
        new LabelBuilder()
          .setLabel("Ваш возраст")
          .setTextInputComponent(ageInput),
        new LabelBuilder()
          .setLabel("Чем вы хотите заниматься на сервере?")
          .setTextInputComponent(reasonInput),
        new LabelBuilder()
          .setLabel("Как вы узнали о нас?")
          .setTextInputComponent(sourceInput),
        new LabelBuilder()
          .setLabel("Расскажите о себе")
          .setDescription("Чем больше - тем лучше!")
          .setTextInputComponent(aboutInput),
      );

    try {
      const showModalStartedAt = Date.now();
      await interaction.showModal(modal);
      const showModalRequestMs = Date.now() - showModalStartedAt;
      if (showModalRequestMs > 1500) {
        console.warn(
          `[OpenApplicationModalRequestSlow] id=${interaction.id} ageMs=${ageMs} requestMs=${showModalRequestMs} host=${hostId} pid=${process.pid}`
        );
      }
    } catch (error) {
      const err = error as { code?: number };
      if (err.code === 10062) {
        console.error(
          `[OpenApplicationModalExpired] id=${interaction.id} ageMs=${ageMs} host=${hostId} pid=${process.pid}`
        );
        return;
      }
      throw error;
    }
  },
};

export default button;
