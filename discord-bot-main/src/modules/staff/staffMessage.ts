import {
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
  ActionRowBuilder,
  StringSelectMenuBuilder,
  MessageFlags,
  MediaGalleryBuilder,
  MediaGalleryItemBuilder,
} from "discord.js";
import { ExtendedClient } from "../../structures/Client";
import { sendOrEdit } from "../../services/channelMessage";

export async function sendStaffMessage(client: ExtendedClient): Promise<void> {
  const channelId = process.env.STAFF_CHANNEL_ID;
  if (!channelId) return;

  const bannerUrl =
    process.env.STAFF_BANNER_URL ||
    "https://cdn.discordapp.com/attachments/1470518080756121745/1471628043578507516/Slide_16_9_-_3.png?ex=698f9feb&is=698e4e6b&hm=3bf922f3fb2f5662a87c236e2861278848bea7306ecbc663c1f22d1f936316ee&";

  const select = new StringSelectMenuBuilder()
    .setCustomId("open_staff_select")
    .setPlaceholder("Выберите должность")
    .addOptions(
      { label: "Заявка на модератора", value: "staff_moderator", emoji: "🛡️" },
      { label: "Заявка на медиа",      value: "staff_media",     emoji: "🎬" },
    );

  const container = new ContainerBuilder()
    .setAccentColor(0x7e4dd9)
    .addMediaGalleryComponents(
      new MediaGalleryBuilder().addItems(
        new MediaGalleryItemBuilder().setURL(bannerUrl)
      )
    )
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        "## Набор в команду\nХочешь стать частью нашей команды? Подай заявку на одну из доступных позиций.\nМы рассмотрим её в кратчайшие сроки."
      )
    )
    .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
    .addActionRowComponents(
      new ActionRowBuilder<StringSelectMenuBuilder>().addComponents(select)
    );

  await sendOrEdit(client, channelId, {
    components: [container],
    flags: MessageFlags.IsComponentsV2,
  });
}
