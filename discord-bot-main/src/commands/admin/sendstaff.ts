import { SlashCommandBuilder, PermissionFlagsBits, MessageFlags } from "discord.js";
import { Command } from "../../types";
import { sendStaffMessage } from "../../modules/staff/staffMessage";
import { ExtendedClient } from "../../structures/Client";

const command: Command = {
  data: new SlashCommandBuilder()
    .setName("sendstaff")
    .setDescription("Отправить embed набора в команду в канал")
    .setDefaultMemberPermissions(PermissionFlagsBits.Administrator),
  execute: async (interaction) => {
    await interaction.deferReply({ flags: MessageFlags.Ephemeral });
    await sendStaffMessage(interaction.client as ExtendedClient);
    await interaction.editReply({ content: "Embed набора в команду отправлен." });
  },
};

export default command;
