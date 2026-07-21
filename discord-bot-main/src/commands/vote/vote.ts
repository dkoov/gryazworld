import { SlashCommandBuilder, MessageFlags, EmbedBuilder, TextChannel } from "discord.js";
import { Command } from "../../types";
import { getVoteTarget, setVote, countVotes } from "../../services/voteStore";

const VOTES_REQUIRED = Number(process.env.VOTES_REQUIRED ?? "4");
const MIN_PLAYTIME_HOURS = Number(process.env.PARLIAMENT_MIN_PLAYTIME_HOURS ?? "10");
const MIN_PLAYTIME_SECONDS = MIN_PLAYTIME_HOURS * 3600;
const BACKEND_URL = process.env.BACKEND_INTERNAL_URL ?? "http://backend:8000";
const API_KEY = process.env.INTERNAL_API_KEY ?? "";

async function getPlaytimeSeconds(discordId: string): Promise<number> {
  try {
    const res = await fetch(`${BACKEND_URL}/internal/playtime/${discordId}`, {
      headers: { "x-api-key": API_KEY },
    });
    if (!res.ok) return 0;
    const data = (await res.json()) as { seconds?: number };
    return Number(data.seconds ?? 0);
  } catch (e) {
    console.error("[Vote] не удалось получить наигранное время:", e);
    return 0;
  }
}

function fmtHours(seconds: number): string {
  return (seconds / 3600).toFixed(1);
}

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

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const [voterPlaytime, targetPlaytime] = await Promise.all([
      getPlaytimeSeconds(voter.id),
      getPlaytimeSeconds(target.id),
    ]);

    if (voterPlaytime < MIN_PLAYTIME_SECONDS) {
      await interaction.editReply({
        content: `Голосовать могут только игроки с наигранным временем от ${MIN_PLAYTIME_HOURS} ч. на сервере. У вас: ${fmtHours(voterPlaytime)} ч.`,
      });
      return;
    }
    if (targetPlaytime < MIN_PLAYTIME_SECONDS) {
      await interaction.editReply({
        content: `<@${target.id}> ещё не наиграл(а) ${MIN_PLAYTIME_HOURS} ч. на сервере (сейчас: ${fmtHours(targetPlaytime)} ч) и не может быть избран(а) в Парламент.`,
      });
      return;
    }

    const currentTarget = getVoteTarget(voter.id);
    if (currentTarget === target.id) {
      await interaction.editReply({ content: "Вы уже голосуете за этого игрока." });
      return;
    }

    const beforeCount = countVotes(target.id);
    if (beforeCount >= VOTES_REQUIRED) {
      await interaction.editReply({
        content: `<@${target.id}> уже набрал(а) максимум голосов (${VOTES_REQUIRED}).`,
      });
      return;
    }

    const { previousTargetId, previousCount, newCount } = setVote(voter.id, target.id);

    await interaction.editReply({
      content:
        `Голос за <@${target.id}> засчитан (${newCount}/${VOTES_REQUIRED}).` +
        (previousTargetId ? ` Голос снят с <@${previousTargetId}>.` : ""),
    });

    const voteChannelId = process.env.VOTE_CHANNEL_ID;
    const voteChannel = voteChannelId
      ? ((interaction.guild?.channels.cache.get(voteChannelId) as TextChannel | undefined) ??
        ((await interaction.guild?.channels.fetch(voteChannelId).catch(() => null)) as TextChannel | null))
      : null;

    const embed = new EmbedBuilder()
      .setColor(0x8b5cf6)
      .setDescription(
        `<@${voter.id}> проголосовал(а) за <@${target.id}>` +
          (previousTargetId ? ` (ранее голосовал(а) за <@${previousTargetId}>)` : "")
      )
      .addFields({ name: "Голосов", value: `${newCount}/${VOTES_REQUIRED}`, inline: true })
      .setTimestamp();

    await voteChannel
      ?.send({ embeds: [embed] })
      .catch((e: unknown) => console.error("[Vote] не удалось отправить уведомление о голосе:", e));

    const parliamentRoleId = process.env.PARLIAMENT_ROLE_ID;

    // Новый кандидат набрал кворум -- выдаём роль Парламента.
    if (newCount >= VOTES_REQUIRED) {
      const member = await interaction.guild?.members.fetch(target.id).catch(() => null);

      if (parliamentRoleId && member) {
        await member.roles
          .add(parliamentRoleId)
          .catch((e: unknown) => console.error("[Vote] не удалось выдать роль Парламента:", e));
      }

      const grantedEmbed = new EmbedBuilder()
        .setColor(0xffd700)
        .setTitle("🏛️ Новый член Парламента!")
        .setDescription(`<@${target.id}> набрал(а) ${VOTES_REQUIRED} голосов и получает роль Парламента.`)
        .setTimestamp();

      await voteChannel?.send({ embeds: [grantedEmbed] }).catch(() => null);
    }

    // Прошлый кандидат потерял голос и упал ниже кворума -- снимаем роль Парламента сразу.
    if (previousTargetId && previousCount < VOTES_REQUIRED) {
      const prevMember = await interaction.guild?.members.fetch(previousTargetId).catch(() => null);

      if (parliamentRoleId && prevMember?.roles.cache.has(parliamentRoleId)) {
        await prevMember.roles
          .remove(parliamentRoleId)
          .catch((e: unknown) => console.error("[Vote] не удалось снять роль Парламента:", e));

        const removedEmbed = new EmbedBuilder()
          .setColor(0x95a5a6)
          .setTitle("Член Парламента выбыл")
          .setDescription(
            `<@${previousTargetId}> потерял(а) голос и выбывает из Парламента (${previousCount}/${VOTES_REQUIRED}).`
          )
          .setTimestamp();

        await voteChannel?.send({ embeds: [removedEmbed] }).catch(() => null);
      }
    }
  },
};

export default command;
