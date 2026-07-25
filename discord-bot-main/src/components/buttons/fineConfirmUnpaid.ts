import { MessageFlags, EmbedBuilder } from "discord.js";
import { Button } from "../../types";
import { markFineUnpaid } from "../../services/finesClient";
import { removeFineMessage } from "../../services/finesStore";

const button: Button = {
  customId: "fine_unpaid_",
  prefix: true,
  execute: async (interaction) => {
    const fineId = Number(interaction.customId.replace("fine_unpaid_", ""));
    if (!Number.isFinite(fineId)) return;

    let result: { player: string; total_warns: number };
    try {
      result = await markFineUnpaid(fineId);
    } catch (err) {
      console.error("[FineConfirm] mark-unpaid не удался:", err);
      await interaction.reply({
        content: "Не удалось отметить штраф неоплаченным — бэкенд недоступен. Попробуйте ещё раз.",
        flags: MessageFlags.Ephemeral,
      }).catch(() => null);
      return;
    }

    const original = interaction.message;
    const embed = original.embeds[0]
      ? EmbedBuilder.from(original.embeds[0])
      : new EmbedBuilder().setTitle("Штраф");
    embed.setColor(0xff6600);
    embed.addFields({
      name: "Статус",
      value: `⚠️ Не оплачен, варн выдан (всего: ${result.total_warns}) — отметил <@${interaction.user.id}>`,
    });

    await interaction.update({ embeds: [embed], components: [] });
    removeFineMessage(fineId);
  },
};

export default button;
