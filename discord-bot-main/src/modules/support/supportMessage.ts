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

export const supportCategories = [
  { label: "Проблема", value: "problem", emoji: "🐛", description: "Сообщить о проблеме или баге" },
  { label: "Жалоба", value: "complaint", emoji: "📢", description: "Пожаловаться на игрока" },
  { label: "Вопрос", value: "question", emoji: "❓", description: "Задать вопрос по серверу" },
  { label: "Другое", value: "other", emoji: "📋", description: "Другой тип обращения" },
];

export function getCategoryLabel(value: string): string {
  return supportCategories.find((c) => c.value === value)?.label ?? value;
}

export function getCategoryEmoji(value: string): string {
  return supportCategories.find((c) => c.value === value)?.emoji ?? "📋";
}

export async function sendSupportMessage(client: ExtendedClient): Promise<void> {
  const channelId = process.env.SUPPORT_CHANNEL_ID;
  if (!channelId) return;

  const select = new StringSelectMenuBuilder()
    .setCustomId("open_support_select")
    .setPlaceholder("Выберите тип обращения")
    .addOptions(
      supportCategories.map((c) => ({
        label: c.label,
        value: c.value,
        emoji: c.emoji,
        description: c.description,
      }))
    );

  const container = new ContainerBuilder()
    .addMediaGalleryComponents(
        new MediaGalleryBuilder().addItems(
            new MediaGalleryItemBuilder().setURL(
                "https://cdn.discordapp.com/attachments/1470518080756121745/1471628043578507516/Slide_16_9_-_3.png?ex=698f9feb&is=698e4e6b&hm=3bf922f3fb2f5662a87c236e2861278848bea7306ecbc663c1f22d1f936316ee&"
            )
        )
    )
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        `## Поддержка\nЕсли у вас возникли вопросы или проблемы - создайте тикет. \nНаша команда рассмотрит ваш тикет в кратчайшие сроки.`
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
