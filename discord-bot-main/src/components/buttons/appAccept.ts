import { MessageFlags, GuildMember, ContainerBuilder, TextDisplayBuilder, SeparatorBuilder, MediaGalleryBuilder, MediaGalleryItemBuilder, ThreadChannel } from "discord.js";
import { Button } from "../../types";
import { getApplicationByThread, removeApplication } from "../../services/database";
import { logApplication } from "../../services/applicationLog";
import { rconWhitelistAdd } from "../../services/rconClient";

const MOD_ROLE_IDS = [
  "1470512352943407330",
  "1470761608228634767",
  "1518725041653551214",
];

const button: Button = {
  customId: "app_accept_",
  prefix: true,
  execute: async (interaction) => {
    const member = interaction.member as GuildMember;

    const hasModRole = MOD_ROLE_IDS.some(id => member.roles.cache.has(id));
    if (!hasModRole) {
      await interaction.reply({
        content: "Только модерация может принимать/отклонять заявки. Ожидайте, пока модерация рассмотрит заявку.",
        flags: MessageFlags.Ephemeral,
      });
      return;
    }

    const container = new ContainerBuilder()
      .setAccentColor(0x4CAF50)
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `### Поздравляем! Ваша заявка была принята.`
        ),
      )
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `Для конформтной игры на сервере, пожалуйста, ознакомьтесь с https://discord.com/channels/1470509467450736775/1470516578620543097 если у вас возникнут вопросы, не стесняйтесь обращаться в <#1519003894955638794>.`
        ),
      )
      .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `## Версия сервера:
\`\`\`1.21.11\`\`\``
        ),
      )
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `## IP сервера:
\`\`\`play.ichorix.cc\`\`\`
-# Java edition`
        ),
      )
      .addMediaGalleryComponents(
        new MediaGalleryBuilder().addItems(
          new MediaGalleryItemBuilder().setURL(
            "https://cdn.discordapp.com/attachments/1470518080756121745/1471567188036026581/2.png?ex=698f673e&is=698e15be&hm=3403cb986174525e7967ab090da02c206c26700e711db3c86767af0036b24f36&"
          )
        )
      )




    const threadId = interaction.channel?.id;
    if (!threadId) return;

    const app = getApplicationByThread(threadId);

    if (!app) {
      return;
    }

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const guild = interaction.guild!;
    const applicant = await guild.members.fetch(app.userId).catch(() => null);

    if (applicant) {
      const acceptedRoleId = process.env.ACCEPTED_ROLE_ID;
      if (acceptedRoleId) {
        const roleExists = guild.roles.cache.has(acceptedRoleId);
        console.log(`[AppAccept] Выдача роли: roleId=${acceptedRoleId} существует=${roleExists} userId=${app.userId}`);
        await applicant.roles.add(acceptedRoleId).catch(err =>
          console.error(`[AppAccept] Ошибка выдачи роли ${acceptedRoleId}:`, err)
        );
      } else {
        console.warn("[AppAccept] ACCEPTED_ROLE_ID не задан в .env");
      }

      await applicant.setNickname(app.nickname).catch(err =>
        console.error(`[AppAccept] Ошибка смены ника:`, err)
      );

      try {
        await rconWhitelistAdd(app.nickname, app.userId, interaction.user.id);
      } catch (err) {
        console.error("[RCON] Failed to whitelist add:", err);
      }

      await applicant.send({
        components: [container],
        flags: MessageFlags.IsComponentsV2,
      }).catch(() => null);
    }

    removeApplication(threadId);

    const thread = interaction.channel;
    await logApplication(interaction.client, app, thread as ThreadChannel, "accepted", interaction.user.id, interaction.user.username);

    if (thread?.isThread()) {
      await thread.delete().catch(() => null);
    }
  },
};

export default button;
