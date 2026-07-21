import { SlashCommandBuilder, MessageFlags } from "discord.js";
import { Command } from "../../types";
import { rconWhitelistChangeNick, WhitelistChangeError } from "../../services/rconClient";

const ERROR_MESSAGES: Record<string, string> = {
  not_found: "Не нашёл вас в вайтлисте по привязке аккаунта -- укажи параметр `текущий_ник` (ник, под которым тебя приняли).",
  not_whitelisted: "Вы ещё не в вайтлисте -- дождитесь принятия заявки.",
  same_nickname: "Этот никнейм уже указан у вас в вайтлисте.",
  nickname_taken: "Этот никнейм уже занят другим игроком.",
  bad_nickname: "Некорректный никнейм.",
  nickname_owned_by_other: "Этот текущий ник уже привязан к другому Discord-аккаунту.",
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
    )
    .addStringOption((opt) =>
      opt
        .setName("текущий_ник")
        .setDescription("Ник, под которым сейчас в вайтлисте (если бот не находит вас по аккаунту)")
        .setRequired(false)
        .setMinLength(3)
        .setMaxLength(16),
    ),
  execute: async (interaction) => {
    const newNickname = interaction.options.getString("nickname", true).trim();
    const currentNickname = interaction.options.getString("текущий_ник")?.trim();

    if (!/^[A-Za-z0-9_]{3,16}$/.test(newNickname)) {
      await interaction.reply({
        content: "Никнейм может содержать только латинские буквы, цифры и `_`, от 3 до 16 символов.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    try {
      const { oldNickname } = await rconWhitelistChangeNick(interaction.user.id, newNickname, currentNickname);

      const member = await interaction.guild?.members.fetch(interaction.user.id).catch(() => null);
      await member?.setNickname(newNickname).catch(err =>
        console.error("[ChangeNick] Не удалось сменить ник в Discord:", err)
      );

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
