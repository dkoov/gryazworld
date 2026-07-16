import {
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
  ActionRowBuilder,
  ButtonBuilder,
  ButtonStyle,
  MessageFlags,
} from "discord.js";
import { ExtendedClient } from "../../structures/Client";
import { sendOrEdit } from "../../services/channelMessage";

export async function sendCourtMessage(client: ExtendedClient): Promise<void> {
  const channelId = process.env.COURT_CHANNEL_ID;
  if (!channelId) return;

  const button = new ButtonBuilder()
    .setCustomId("open_court_claim")
    .setLabel("Подать иск")
    .setEmoji("⚖️")
    .setStyle(ButtonStyle.Primary);

  const container = new ContainerBuilder()
    .setAccentColor(0xe8d26a)
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(`## Суд Ichorix RP`)
    )
    .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        `Если считаете, что ваши права были нарушены другим игроком — подайте иск.\nБудет создан приватный тред с вами и Верховным Судьёй.`
      )
    )
    .addSeparatorComponents(new SeparatorBuilder().setDivider(true))
    .addActionRowComponents(
      new ActionRowBuilder<ButtonBuilder>().addComponents(button)
    );

  await sendOrEdit(client, channelId, {
    components: [container],
    flags: MessageFlags.IsComponentsV2,
  });
}
