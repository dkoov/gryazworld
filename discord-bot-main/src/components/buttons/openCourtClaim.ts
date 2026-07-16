import {
  ModalBuilder,
  TextInputBuilder,
  TextInputStyle,
  LabelBuilder,
} from "discord.js";
import { Button } from "../../types";

const button: Button = {
  customId: "open_court_claim",
  execute: async (interaction) => {
    const defendantInput = new TextInputBuilder()
      .setCustomId("defendant")
      .setStyle(TextInputStyle.Short)
      .setPlaceholder("Ник или Discord игрока, на которого подаёте иск")
      .setMinLength(2)
      .setMaxLength(100)
      .setRequired(true);

    const subjectInput = new TextInputBuilder()
      .setCustomId("subject")
      .setStyle(TextInputStyle.Short)
      .setPlaceholder("Кратко: суть иска")
      .setMinLength(5)
      .setMaxLength(100)
      .setRequired(true);

    const descInput = new TextInputBuilder()
      .setCustomId("description")
      .setStyle(TextInputStyle.Paragraph)
      .setPlaceholder("Подробно опишите ситуацию: что произошло, когда, доказательства")
      .setMinLength(10)
      .setMaxLength(1000)
      .setRequired(true);

    const modal = new ModalBuilder()
      .setCustomId("court_modal")
      .setTitle("Подача иска")
      .addLabelComponents(
        new LabelBuilder().setLabel("Ответчик").setTextInputComponent(defendantInput),
        new LabelBuilder().setLabel("Суть иска").setTextInputComponent(subjectInput),
        new LabelBuilder().setLabel("Описание").setTextInputComponent(descInput),
      );

    try {
      await interaction.showModal(modal);
    } catch (error) {
      const err = error as { code?: number };
      if (err.code === 10062) return;
      throw error;
    }
  },
};

export default button;
