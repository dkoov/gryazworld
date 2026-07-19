import { SlashCommandBuilder, MessageFlags, EmbedBuilder, TextChannel } from "discord.js";
import { Command } from "../../types";
import { hasVoted, countVotes, addVote } from "../../services/voteStore";

const VOTES_REQUIRED = Number(process.env.VOTES_REQUIRED ?? "4");

const command: Command = {
  data: new SlashCommandBuilder()
    .setName("vote")
    .setDescription("Проголосовать за игрока в Парламент")
    .addUserOption((opt) =>
      opt.setName("user").setDescription("За кого голосуем").setRequired(true)
    ),
  execute: async (interaction) => {
    const target = interaction.options.getUser("user", true);
    const voter = interaction.user;

    if (target.id === voter.id) {
      await interaction.reply({ content: "Нельзя голосовать за самого себя.", flags: MessageFlags.Ephemeral });
      return;
    }
    if (target.bot) {
      await interaction.reply({ content: "Нельзя голосовать за бота.", flags: MessageFlags.Ephemeral });
      return;
    }

    const before = countVotes(target.id);
    if (before >= VOTES_REQUIRED) {
      await interaction.reply({
        content: `<@${target.id}> уже набрал(а) максимум голосов (${VOTES_REQUIRED}).`,
        flags: MessageFlags.Ephemeral,
      });
      return;
    }
    if (hasVoted(voter.id, target.id)) {
      await interaction.reply({ content: "Вы уже голосовали за этого игрока.", flags: MessageFlags.Ephemeral });
      return;
    }

    const count = addVote(voter.id, target.id);

    await interaction.reply({
      content: `Голос за <@${target.id}> засчитан (${count}/${VOTES_REQUIRED}).`,
      flags: MessageFlags.Ephemeral,
    });

    const voteChannelId = process.env.VOTE_CHANNEL_ID;
    const voteChannel = voteChannelId
      ? ((interaction.guild?.channels.cache.get(voteChannelId) as TextChannel | undefined) ??
        (await interaction.guild?.channels.fetch(voteChannelId).catch(() => null)) as TextChannel | null)
      : null;

    const embed = new EmbedBuilder()
      .setColor(0x8b5cf6)
      .setDescription(`<@${voter.id}> проголосовал(а) за <@${target.id}>`)
      .addFields({ name: "Голосов", value: `${count}/${VOTES_REQUIRED}`, inline: true })
      .setTimestamp();

    await voteChannel?.send({ embeds: [embed] }).catch((e: unknown) =>
      console.error("[Vote] не удалось отправить уведомление о голосе:", e)
    );

    if (count >= VOTES_REQUIRED) {
      const parliamentRoleId = process.env.PARLIAMENT_ROLE_ID;
      const member = await interaction.guild?.members.fetch(target.id).catch(() => null);

      if (parliamentRoleId && member) {
        await member.roles.add(parliamentRoleId).catch((e: unknown) =>
          console.error("[Vote] не удалось выдать роль Парламента:", e)
        );
      }

      const grantedEmbed = new EmbedBuilder()
        .setColor(0xffd700)
        .setTitle("🏛️ Новый член Парламента!")
        .setDescription(`<@${target.id}> набрал(а) ${VOTES_REQUIRED} голосов и получает роль Парламента.`)
        .setTimestamp();

      await voteChannel?.send({ embeds: [grantedEmbed] }).catch(() => null);
    }
  },
};

export default command;
