import { MessageFlags } from "discord.js";
import { Modal } from "../../types";
import { upsertSection } from "../../services/postStore";
import { takePendingBanner } from "../../services/pendingBanner";
import { renderPost } from "../../modules/post/postMessage";
import { ExtendedClient } from "../../structures/Client";

const PREFIX = "post_section:";

const modal: Modal = {
  customId: PREFIX,
  prefix: true,
  execute: async (interaction) => {
    const rest = interaction.customId.slice(PREFIX.length);
    const [action, channelIdRaw, name, key] = rest.split(":");
    const channelId = channelIdRaw === "-" ? undefined : channelIdRaw;

    await interaction.deferReply({ flags: MessageFlags.Ephemeral });

    const title = interaction.fields.getTextInputValue("title").trim();
    const body = interaction.fields.getTextInputValue("body");

    try {
      const bannerFile = takePendingBanner(interaction.user.id, name, key);

      upsertSection(name, channelId, {
        key,
        title,
        body,
        ...(bannerFile ? { bannerFile } : {}),
      });

      await renderPost(interaction.client as ExtendedClient, name);

      await interaction.editReply({
        content:
          action === "add"
            ? `Раздел \`${key}\` поста \`${name}\` создан и опубликован.`
            : `Раздел \`${key}\` поста \`${name}\` обновлён.`,
      });
    } catch (err) {
      console.error("[postSectionModal] Ошибка:", err);
      await interaction.editReply({
        content: `Не удалось сохранить раздел: ${err instanceof Error ? err.message : "неизвестная ошибка"}`,
      });
    }
  },
};

export default modal;
