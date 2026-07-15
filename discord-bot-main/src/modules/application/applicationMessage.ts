import {
  ContainerBuilder,
  TextDisplayBuilder,
  SeparatorBuilder,
  ActionRowBuilder,
  ButtonBuilder,
  ButtonStyle,
  MessageFlags,
  MediaGalleryBuilder,
  MediaGalleryItemBuilder,
} from "discord.js";
import { ExtendedClient } from "../../structures/Client";
import { sendOrEdit } from "../../services/channelMessage";

export async function sendApplicationMessage(
  client: ExtendedClient,
): Promise<void> {
  const channelId = process.env.APPLICATION_CHANNEL_ID;
  if (!channelId) return;

  const container = new ContainerBuilder()
    .addMediaGalleryComponents(
      new MediaGalleryBuilder().addItems(
        new MediaGalleryItemBuilder().setURL(
          "https://cdn.discordapp.com/attachments/1470518080756121745/1471550919937102101/Slide_16_9_-_12.png?ex=698f5817&is=698e0697&hm=2d5292f6e35f23ac811c490e32a8da0ad8483d53072be21fc12f8ad507f9ceb2&"
        )
      )
    )
    .addTextDisplayComponents(
      new TextDisplayBuilder().setContent(
        ` **Ичорикс** - уникальный ванильный Майнкрафт сервер c своей дружелюбной атмосферой и активным сообществом. Мы стремимся создать место, где игроки могут наслаждаться ванильным геймплеем.
## Основные преимущества:
- Уникальные фишки и ивенты
- Администрация, которая всегда на связи
- Активное и дружелюбное сообщество 
## Как присоединиться:
Попасть можна через заполнение заявки или покупки проходки.`,
      ),
    )
    .addActionRowComponents(
      new ActionRowBuilder<ButtonBuilder>().addComponents(
        new ButtonBuilder()
          .setLabel("Заполнить заявку")
          .setStyle(ButtonStyle.Primary)
          .setCustomId("open_application_modal"),
        new ButtonBuilder()
          .setLabel("Купить проходку")
          .setStyle(ButtonStyle.Link)
          .setURL("https://store.ichorix.cc/"),
      ),
    );

  await sendOrEdit(client, channelId, {
    components: [container],
    flags: MessageFlags.IsComponentsV2,
  });
}
