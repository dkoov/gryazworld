import {
  MessageFlags,
  GuildMember,
  ModalBuilder,
  TextInputBuilder,
  TextInputStyle,
  LabelBuilder,
} from "discord.js";
import { Button } from "../../types";

const button: Button = {
  customId: "ticket_close_",
  prefix: true,
  execute: async (interaction) => {
    const ageMs = Date.now() - interaction.createdTimestamp;
    if (ageMs > 2000) {
      console.warn(
        `[TicketCloseModalSlow] id=${interaction.id} ageMs=${ageMs} pid=${process.pid}`
      );
    }

    const member = interaction.member as GuildMember;
    const ticketAuthorId = interaction.customId.replace("ticket_close_", "");

    const MOD_ROLE_IDS = ["1470512352943407330", "1470761608228634767", "1518725041653551214"];
    const isMod = MOD_ROLE_IDS.some(id => member.roles.cache.has(id));

    if (!isMod) {
      await interaction.reply({
        content: "Только модерация может закрывать тикет.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    const verdictInput = new TextInputBuilder()
      .setCustomId("verdict")
      .setStyle(TextInputStyle.Paragraph)
      .setPlaceholder("Опишите итог обращения")
      .setMaxLength(500)
      .setRequired(false);

    const modal = new ModalBuilder()
      .setCustomId(`ticket_verdict_${ticketAuthorId}`)
      .setTitle("Закрытие тикета")
      .addLabelComponents(
        new LabelBuilder()
          .setLabel("Вердикт")
          .setTextInputComponent(verdictInput),
      );

    try {
      await interaction.showModal(modal);
    } catch (error) {
      const err = error as { code?: number };
      if (err.code === 10062) {
        console.error(
          `[TicketCloseModalExpired] id=${interaction.id} ageMs=${ageMs} pid=${process.pid}`
        );
        return;
      }
      throw error;
    }
  },
};

export default button;
