import { MessageFlags } from "discord.js";
import { Button } from "../../types";
import { withdrawClaim } from "../../services/courtClient";

const button: Button = {
  customId: "court_withdraw_",
  prefix: true,
  execute: async (interaction) => {
    const claimId = Number(interaction.customId.replace("court_withdraw_", ""));
    if (!Number.isFinite(claimId)) return;

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    try {
      await withdrawClaim(claimId, interaction.user.id);
    } catch (err: any) {
      const message = String(err?.message ?? "").includes("403")
        ? "Отозвать иск может только истец."
        : String(err?.message ?? "").includes("400")
          ? "Иск уже рассмотрен -- отозвать нельзя."
          : "Не удалось отозвать иск -- попробуйте ещё раз.";
      await interaction.editReply({ content: message }).catch(() => null);
      return;
    }

    await interaction.editReply({ content: "Иск отозван, тред закрыт." }).catch(() => null);

    const channel = interaction.channel;
    if (channel?.isThread()) {
      await channel
        .send({
          content: `-# Иск отозван истцом <@${interaction.user.id}>. Тред закрыт.`,
          allowedMentions: { parse: [] },
        })
        .catch(() => null);
      await channel.setArchived(true).catch((e: unknown) =>
        console.error("[CourtWithdraw] не удалось архивировать тред:", e)
      );
    }
  },
};

export default button;
