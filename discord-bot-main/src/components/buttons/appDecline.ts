import { MessageFlags, GuildMember, TextDisplayBuilder, ContainerBuilder, MediaGalleryBuilder, MediaGalleryItemBuilder, ButtonBuilder, ActionRowBuilder, ButtonStyle, ThreadChannel } from "discord.js";
import { Button } from "../../types";
import { getApplicationByThread, removeApplication } from "../../services/database";
import { logApplication } from "../../services/applicationLog";

const MOD_ROLE_IDS = [
  "1470512352943407330",
  "1470761608228634767",
  "1518725041653551214",
];

const button: Button = {
  customId: "app_decline_",
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

    const threadId = interaction.channel?.id;
    if (!threadId) return;

    const app = getApplicationByThread(threadId);

    const container = new ContainerBuilder()
      .setAccentColor(0xF44336)
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `# Увы, к сожалению вы не подходите по критериям.`
        ),
      )
      .addTextDisplayComponents(
        new TextDisplayBuilder().setContent(
          `Но не все потеряно! **Вы** можете приобрести проходку на сервер в нашем магазине **https://shop.ichorix.cc/**`
        )
      )
      .addMediaGalleryComponents(
        new MediaGalleryBuilder().addItems(
          new MediaGalleryItemBuilder().setURL(
            "https://cdn.discordapp.com/attachments/1470518080756121745/1471599528468811937/4de8757a7ee3b9bd.png?ex=698f855c&is=698e33dc&hm=473c282c2b5a1bb284cf7ccbad3933d3fa20d3b1a611b269baea9b3d306388ab&"
          )
        )
      )
      .addActionRowComponents(
        new ActionRowBuilder<ButtonBuilder>().addComponents(
          new ButtonBuilder()
            .setLabel("Купить проходку")
            .setStyle(ButtonStyle.Link)
            .setURL("https://shop.ichorix.cc/"),
        ),
      )

    if (!app) {
      return;
    }

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const guild = interaction.guild!;
    const applicant = await guild.members.fetch(app.userId).catch(() => null);

    if (applicant) {
      const declinedRoleId = process.env.DECLINED_ROLE_ID;
      if (declinedRoleId) {
        await applicant.roles.add(declinedRoleId).catch(() => null);
      }

      await applicant.send({
        components: [container],
        flags: MessageFlags.IsComponentsV2,
      }).catch(() => null);
    }

    removeApplication(threadId);

    const thread = interaction.channel;
    await logApplication(interaction.client, app, thread as ThreadChannel, "declined", interaction.user.id, interaction.user.username);

    if (thread?.isThread()) {
      await thread.delete().catch(() => null);
    }
  },
};

export default button;
