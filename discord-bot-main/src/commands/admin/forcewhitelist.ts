import {
  SlashCommandBuilder,
  PermissionFlagsBits,
  MessageFlags,
} from "discord.js";
import { Command } from "../../types";
import { rconWhitelistAdd } from "../../services/rconClient";

const command: Command = {
  data: new SlashCommandBuilder()
    .setName("forcewhitelist")
    .setDescription("ForceWhiteList")
    .setDefaultMemberPermissions(PermissionFlagsBits.Administrator)
    .addStringOption((opt) =>
      opt
        .setName("nick")
        .setDescription("nick")
        .setRequired(true)
        .setMinLength(3)
        .setMaxLength(16),
    )
    .addUserOption((opt) =>
      opt
        .setName("user")
        .setDescription("user")
        .setRequired(true),
    )
    .addStringOption((opt) =>
      opt
        .setName("reason")
        .setDescription("reason")
        .setRequired(false),
    ),
  execute: async (interaction) => {
    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const nick = interaction.options.getString("nick", true);
    const user = interaction.options.getUser("user", true);
    const reason = interaction.options.getString("reason") ?? "ForceWhiteList";

    try {
      await rconWhitelistAdd(nick, user.id, interaction.user.id, reason);

      await interaction.editReply({
        content: `\`${nick}\` added (Discord: ${user})`,
      });
    } catch (err) {
      console.error("[ForceWhitelist] Error:", err);
      await interaction.editReply({
        content: `err: ${err instanceof Error ? err.message : "Unknown error"}`,
      });
    }
  },
};

export default command;
