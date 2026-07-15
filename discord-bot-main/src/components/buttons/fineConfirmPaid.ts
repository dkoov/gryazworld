import { MessageFlags, EmbedBuilder } from "discord.js";
import { Button } from "../../types";
import { markFinePaid } from "../../services/finesClient";

const button: Button = {
  customId: "fine_paid_",
  prefix: true,
  execute: async (interaction) => {
    const fineId = Number(interaction.customId.replace("fine_paid_", ""));
    if (!Number.isFinite(fineId)) return;

    try {
      await markFinePaid(fineId);
    } catch (err) {
      console.error("[FineConfirm] mark-paid не удался:", err);
      await interaction.reply({
        content: "Не удалось подтвердить оплату — бэкенд недоступен. Попробуйте ещё раз.",
        flags: MessageFlags.Ephemeral,
      }).catch(() => null);
      return;
    }

    const original = interaction.message;
    const embed = original.embeds[0]
      ? EmbedBuilder.from(original.embeds[0])
      : new EmbedBuilder().setTitle("Штраф");
    embed.setColor(0x2ecc71);
    embed.addFields({
      name: "Статус",
      value: `✅ Подтверждено оплаченным — <@${interaction.user.id}>`,
    });

    await interaction.update({ embeds: [embed], components: [] });
  },
};

export default button;
