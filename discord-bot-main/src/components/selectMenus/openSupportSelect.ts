import {
  ModalBuilder,
  TextInputBuilder,
  TextInputStyle,
  LabelBuilder,
} from "discord.js";
import { SelectMenu } from "../../types";

const selectMenu: SelectMenu = {
  customId: "open_support_select",
  execute: async (interaction) => {
    const ageMs = Date.now() - interaction.createdTimestamp;
    if (ageMs > 2000) {
      console.warn(
        `[OpenSupportModalSlow] id=${interaction.id} ageMs=${ageMs} pid=${process.pid}`
      );
    }

    const category = interaction.values[0];

    const subjectInput = new TextInputBuilder()
      .setCustomId("subject")
      .setStyle(TextInputStyle.Short)
      .setPlaceholder("Кратко опишите тему обращения")
      .setMinLength(5)
      .setMaxLength(100)
      .setRequired(true);

    const descInput = new TextInputBuilder()
      .setCustomId("description")
      .setStyle(TextInputStyle.Paragraph)
      .setPlaceholder("Подробно опишите вашу ситуацию")
      .setMinLength(10)
      .setMaxLength(1000)
      .setRequired(true);

    const modal = new ModalBuilder()
      .setCustomId(`support_modal_${category}`)
      .setTitle("Создание тикета")
      .addLabelComponents(
        new LabelBuilder()
          .setLabel("Тема")
          .setTextInputComponent(subjectInput),
        new LabelBuilder()
          .setLabel("Описание")
          .setTextInputComponent(descInput),
      );

    try {
      await interaction.showModal(modal);
    } catch (error) {
      const err = error as { code?: number };
      if (err.code === 10062) {
        console.error(
          `[OpenSupportModalExpired] id=${interaction.id} ageMs=${ageMs} pid=${process.pid}`
        );
        return;
      }
      throw error;
    }
  },
};

export default selectMenu;
