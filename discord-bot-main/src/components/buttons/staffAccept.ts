import { MessageFlags, GuildMember } from "discord.js";
import { Button } from "../../types";
import { getStaffApplicationByThread, removeStaffApplication } from "../../services/staffDatabase";

const MOD_ROLE_IDS = [
  "1470512352943407330",
  "1470761608228634767",
  "1518725041653551214",
];

const button: Button = {
  customId: "staff_accept_",
  prefix: true,
  execute: async (interaction) => {
    const member = interaction.member as GuildMember;
    const hasModRole = MOD_ROLE_IDS.some((id) => member.roles.cache.has(id));

    if (!hasModRole) {
      await interaction.reply({
        content: "Только модерация может принимать/отклонять заявки. Ожидайте, пока модерация рассмотрит заявку.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    const threadId = interaction.channel?.id;
    if (!threadId) return;

    const app = getStaffApplicationByThread(threadId);
    if (!app) return;

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const guild = interaction.guild!;
    const applicant = await guild.members.fetch(app.userId).catch(() => null);

    if (applicant) {
      const roleLabel = app.type === "moderator" ? "модератора" : "медиа";
      await applicant
        .send(`✅ Поздравляем! Ваша заявка на **${roleLabel}** была **принята**. Ожидайте дальнейших инструкций от модерации.`)
        .catch(() => null);
    }

    removeStaffApplication(threadId);

    const thread = interaction.channel;
    if (thread?.isThread()) {
      await thread.delete().catch(() => null);
    }
  },
};

export default button;
