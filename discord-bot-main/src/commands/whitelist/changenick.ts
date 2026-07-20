import { SlashCommandBuilder, MessageFlags } from "discord.js";
import { Command } from "../../types";
import { rconWhitelistChangeNick, WhitelistChangeError } from "../../services/rconClient";

const ERROR_MESSAGES: Record<string, string> = {
  not_found: "Заявка на вайтлист от вашего аккаунта не найдена -- сначала подайте заявку и дождитесь принятия.",
  not_whitelisted: "Вы ещё не в вайтлисте -- дождитесь принятия заявки.",
  same_nickname: "Этот никнейм уже указан у вас в вайтлисте.",
  nickname_taken: "Этот никнейм уже занят другим игроком.",
  bad_nickname: "Некорректный никнейм.",
};

const command: Command = {
  data: new SlashCommandBuilder()
    .setName("changenick")
    .setDescription("Исправить свой никнейм в вайтлисте, если он был указан с ошибкой")
    .addStringOption((opt) =>
      opt
        .setName("nickname")
        .setDescription("Правильный никнейм в Minecraft")
        .setRequired(true)
        .setMinLength(3)
        .setMaxLength(16),
    ),
  execute: async (interaction) => {
    const newNickname = interaction.options.getString("nickname", true).trim();

    if (!/^[A-Za-z0-9_]{3,16}$/.test(newNickname)) {
      await interaction.reply({
        content: "Никнейм может содержать только латинские буквы, цифры и `_`, от 3 до 16 символов.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    try {
      const { oldNickname } = await rconWhitelistChangeNick(interaction.user.id, newNickname);
      await interaction.editReply({
        content: `Никнейм в вайтлисте изменён: **${oldNickname}** → **${newNickname}**. Теперь можно зайти на сервер под этим ником.`,
      });
    } catch (e) {
      const code = e instanceof WhitelistChangeError ? e.code : "unknown_error";
      console.error("[ChangeNick] Error:", e);
      await interaction.editReply({
        content: ERROR_MESSAGES[code] ?? "Не удалось изменить никнейм, попробуйте позже.",
      });
    }
  },
};

export default command;
