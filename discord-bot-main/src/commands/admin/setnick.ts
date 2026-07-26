import { SlashCommandBuilder, PermissionFlagsBits, MessageFlags } from "discord.js";
import { Command } from "../../types";
import { adminRename } from "../../services/backendClient";

const ERROR_MESSAGES: Record<string, string> = {
  not_found: "Игрок с таким текущим ником не найден.",
  bad_nickname: "Некорректный никнейм.",
  same_nickname: "Новый ник совпадает со старым.",
  nickname_taken: "Этот никнейм уже занят другим игроком.",
};

const command: Command = {
  data: new SlashCommandBuilder()
    .setName("setnick")
    .setDescription("Принудительно сменить ник игрока (для уже играющих аккаунтов)")
    .setDefaultMemberPermissions(PermissionFlagsBits.Administrator)
    .addStringOption((opt) =>
      opt
        .setName("current")
        .setDescription("Текущий ник игрока в Minecraft")
        .setRequired(true)
        .setMinLength(3)
        .setMaxLength(16),
    )
    .addStringOption((opt) =>
      opt
        .setName("new")
        .setDescription("Новый ник")
        .setRequired(true)
        .setMinLength(3)
        .setMaxLength(16),
    ),
  execute: async (interaction) => {
    const current = interaction.options.getString("current", true).trim();
    const newNick = interaction.options.getString("new", true).trim();

    if (!/^[A-Za-z0-9_]{3,16}$/.test(newNick)) {
      await interaction.reply({
        content: "Никнейм может содержать только латинские буквы, цифры и `_`, от 3 до 16 символов.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    try {
      const data = await adminRename({ currentNickname: current, newNickname: newNick });
      if (!data.ok) {
        await interaction.editReply({
          content: ERROR_MESSAGES[data.error ?? ""] ?? `Ошибка: ${data.error}`,
        });
        return;
      }
      await interaction.editReply({
        content: `Ник изменён: **${data.oldNickname}** → **${data.newNickname}**.`,
      });
    } catch (e) {
      await interaction.editReply({
        content: `Ошибка сервера: ${e instanceof Error ? e.message : "unknown"}`,
      });
    }
  },
};

export default command;
