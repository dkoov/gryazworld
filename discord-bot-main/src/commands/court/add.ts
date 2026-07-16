import { SlashCommandBuilder, MessageFlags, ThreadChannel } from "discord.js";
import { Command } from "../../types";

const command: Command = {
  data: new SlashCommandBuilder()
    .setName("add")
    .setDescription("Добавить участника в судебный тред")
    .addUserOption((opt) =>
      opt.setName("user").setDescription("Игрок, которого нужно добавить").setRequired(true)
    ),
  execute: async (interaction) => {
    const channel = interaction.channel;
    const courtChannelId = process.env.COURT_CHANNEL_ID;

    if (!(channel instanceof ThreadChannel) || channel.parentId !== courtChannelId) {
      await interaction.reply({
        content: "Команда работает только внутри судебных тредов.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    const chiefJudgeRoleId = process.env.CHIEF_JUDGE_ROLE_ID;
    const member = interaction.member;
    const hasRole =
      chiefJudgeRoleId &&
      member &&
      "roles" in member &&
      typeof member.roles !== "string" &&
      member.roles.cache.has(chiefJudgeRoleId);

    if (!hasRole) {
      await interaction.reply({
        content: "Добавлять участников может только Верховный Судья.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    const user = interaction.options.getUser("user", true);
    await channel.members.add(user.id);

    await interaction.reply({
      content: `<@${user.id}> добавлен(а) в тред судьёй <@${interaction.user.id}>.`,
    });
  },
};

export default command;
