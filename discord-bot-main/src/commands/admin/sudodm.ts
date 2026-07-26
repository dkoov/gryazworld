import {
  SlashCommandBuilder,
  MessageFlags,
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
  GuildMember,
} from "discord.js";
import { Command } from "../../types";

const SUDO_ROLE_ID = "1470512352943407330";

const command: Command = {
  data: new SlashCommandBuilder()
    .setName("sudodm")
    .setDescription("Разослать сообщение в ЛС выбранному игроку или всем игрокам (только роль _sudo)")
    .addStringOption((opt) =>
      opt.setName("message").setDescription("Текст сообщения").setRequired(true).setMaxLength(1500),
    )
    .addUserOption((opt) =>
      opt.setName("target").setDescription("Конкретный игрок (если не указан — используйте all)").setRequired(false),
    )
    .addBooleanOption((opt) =>
      opt.setName("all").setDescription("Отправить всем с ролью Игрок").setRequired(false),
    ),
  execute: async (interaction) => {
    const member = interaction.member as GuildMember;
    if (!member.roles.cache.has(SUDO_ROLE_ID)) {
      await interaction.reply({ content: "Недостаточно прав.", flags: MessageFlags.Ephemeral });
      return;
    }

    const message = interaction.options.getString("message", true);
    const target = interaction.options.getUser("target");
    const all = interaction.options.getBoolean("all") ?? false;

    if (!target && !all) {
      await interaction.reply({
        content: "Укажите либо `target`, либо `all: true`.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }
    if (target && all) {
      await interaction.reply({
        content: "Укажите что-то одно: `target` ИЛИ `all`, не оба сразу.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    const playerRoleId = process.env.ACCEPTED_ROLE_ID;
    if (all && !playerRoleId) {
      await interaction.reply({ content: "ACCEPTED_ROLE_ID не настроен.", flags: MessageFlags.Ephemeral });
      return;
    }

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const buildDm = () =>
      new ContainerBuilder()
        .addTextDisplayComponents(
          new TextDisplayBuilder().setContent(`## Сообщение от администрации Ichorix`),
        )
        .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
        .addTextDisplayComponents(new TextDisplayBuilder().setContent(message));

    let sent = 0;
    let failed = 0;

    if (target) {
      const ok = await target
        .send({ components: [buildDm()], flags: MessageFlags.IsComponentsV2 })
        .then(() => true)
        .catch(() => false);
      ok ? sent++ : failed++;
    } else {
      const guild = interaction.guild!;
      const members = await guild.members.fetch();
      const targets = members.filter((m) => m.roles.cache.has(playerRoleId!) && !m.user.bot);
      for (const [, m] of targets) {
        const ok = await m
          .send({ components: [buildDm()], flags: MessageFlags.IsComponentsV2 })
          .then(() => true)
          .catch(() => false);
        ok ? sent++ : failed++;
        // Пауза между отправками, чтобы не упереться в рейт-лимит Discord на большой рассылке.
        await new Promise((r) => setTimeout(r, 300));
      }
    }

    await interaction.editReply({ content: `Готово. Отправлено: ${sent}, не удалось: ${failed}.` });
  },
};

export default command;
