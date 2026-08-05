import {
  SlashCommandBuilder,
  PermissionFlagsBits,
  MessageFlags,
  ChannelType,
  ModalBuilder,
  TextInputBuilder,
  TextInputStyle,
  LabelBuilder,
} from "discord.js";
import { Command } from "../../types";
import { ExtendedClient } from "../../structures/Client";
import {
  getPost,
  listPosts,
  removeSection as removeSectionFromStore,
  deletePost,
  upsertSection,
} from "../../services/postStore";
import { saveBannerAttachment } from "../../services/bannerFile";
import { setPendingBanner } from "../../services/pendingBanner";
import { renderPost, deleteSectionMessage } from "../../modules/post/postMessage";
import { PRAVILA_SECTIONS } from "../../modules/post/pravilaSeed";

const SLUG_RE = /^[a-zA-Zа-яА-Я0-9_.-]{1,32}$/;

function validateSlug(value: string, label: string): string | null {
  if (!SLUG_RE.test(value)) {
    return `\`${label}\` может содержать только буквы, цифры, "-", "_", "." (без пробелов), максимум 32 символа.`;
  }
  return null;
}

function buildSectionModal(opts: {
  action: "add" | "edit";
  channelId?: string;
  name: string;
  key: string;
  existingTitle?: string;
  existingBody?: string;
}): ModalBuilder {
  const titleInput = new TextInputBuilder()
    .setCustomId("title")
    .setStyle(TextInputStyle.Short)
    .setMaxLength(150)
    .setRequired(true);
  if (opts.existingTitle) titleInput.setValue(opts.existingTitle);

  const bodyInput = new TextInputBuilder()
    .setCustomId("body")
    .setStyle(TextInputStyle.Paragraph)
    .setMaxLength(4000)
    .setRequired(true);
  if (opts.existingBody) bodyInput.setValue(opts.existingBody);

  const customId = `post_section:${opts.action}:${opts.channelId ?? "-"}:${opts.name}:${opts.key}`;

  return new ModalBuilder()
    .setCustomId(customId)
    .setTitle(opts.action === "add" ? "Новый раздел" : "Редактирование раздела")
    .addLabelComponents(
      new LabelBuilder().setLabel("Заголовок").setTextInputComponent(titleInput),
      new LabelBuilder().setLabel("Текст (Markdown)").setTextInputComponent(bodyInput)
    );
}

const command: Command = {
  data: new SlashCommandBuilder()
    .setName("post")
    .setDescription("Управление оформленными сообщениями бота (правила, новости и т.д.)")
    .setDefaultMemberPermissions(PermissionFlagsBits.Administrator)
    .addSubcommand((sub) =>
      sub
        .setName("add-section")
        .setDescription("Добавить новый раздел в пост")
        .addStringOption((o) =>
          o.setName("name").setDescription("Имя поста, напр. pravila").setRequired(true)
        )
        .addStringOption((o) =>
          o.setName("key").setDescription("Ключ раздела, напр. 2.1").setRequired(true)
        )
        .addChannelOption((o) =>
          o
            .setName("channel")
            .setDescription("Канал (нужен только при создании нового поста)")
            .addChannelTypes(ChannelType.GuildText)
            .setRequired(false)
        )
        .addAttachmentOption((o) =>
          o.setName("banner").setDescription("Баннер сверху раздела (картинка)").setRequired(false)
        )
    )
    .addSubcommand((sub) =>
      sub
        .setName("edit-section")
        .setDescription("Отредактировать существующий раздел")
        .addStringOption((o) =>
          o.setName("name").setDescription("Имя поста").setRequired(true)
        )
        .addStringOption((o) =>
          o.setName("key").setDescription("Ключ раздела").setRequired(true)
        )
        .addAttachmentOption((o) =>
          o
            .setName("banner")
            .setDescription("Новый баннер (если не указать — старый останется)")
            .setRequired(false)
        )
    )
    .addSubcommand((sub) =>
      sub
        .setName("remove-section")
        .setDescription("Удалить раздел из поста")
        .addStringOption((o) =>
          o.setName("name").setDescription("Имя поста").setRequired(true)
        )
        .addStringOption((o) =>
          o.setName("key").setDescription("Ключ раздела").setRequired(true)
        )
    )
    .addSubcommand((sub) =>
      sub.setName("list").setDescription("Список всех постов и их разделов")
    )
    .addSubcommand((sub) =>
      sub
        .setName("resend")
        .setDescription("Переотправить/обновить все сообщения поста")
        .addStringOption((o) =>
          o.setName("name").setDescription("Имя поста").setRequired(true)
        )
    )
    .addSubcommand((sub) =>
      sub
        .setName("seed-pravila")
        .setDescription("Первичная публикация правил Ичорикса (готовый текст)")
        .addChannelOption((o) =>
          o
            .setName("channel")
            .setDescription("Канал для правил")
            .addChannelTypes(ChannelType.GuildText)
            .setRequired(true)
        )
        .addAttachmentOption((o) =>
          o.setName("banner").setDescription("Баннер для вступительного раздела").setRequired(false)
        )
    ),

  execute: async (interaction) => {
    const sub = interaction.options.getSubcommand();
    const client = interaction.client as ExtendedClient;

    if (sub === "add-section" || sub === "edit-section") {
      const name = interaction.options.getString("name", true).trim();
      const key = interaction.options.getString("key", true).trim();
      const channel = interaction.options.getChannel("channel");
      const banner = interaction.options.getAttachment("banner");

      const nameError = validateSlug(name, "name");
      const keyError = validateSlug(key, "key");
      if (nameError || keyError) {
        await interaction.reply({
          content: [nameError, keyError].filter(Boolean).join("\n"),
          flags: MessageFlags.Ephemeral,
        });
        return;
      }

      const existingPost = getPost(name);
      const existingSection = existingPost?.sections.find((s) => s.key === key);

      if (sub === "add-section" && !existingPost && !channel) {
        await interaction.reply({
          content: `Поста \`${name}\` ещё нет — укажите параметр \`channel\`, чтобы его создать.`,
          flags: MessageFlags.Ephemeral,
        });
        return;
      }

      if (sub === "edit-section" && !existingSection) {
        await interaction.reply({
          content: `Раздел \`${key}\` в посте \`${name}\` не найден. Используйте \`/post add-section\`, чтобы его создать.`,
          flags: MessageFlags.Ephemeral,
        });
        return;
      }

      if (banner) {
        try {
          const fileName = await saveBannerAttachment(name, key, banner);
          setPendingBanner(interaction.user.id, name, key, fileName);
        } catch (err) {
          await interaction.reply({
            content: `Не удалось сохранить баннер: ${err instanceof Error ? err.message : "неизвестная ошибка"}`,
            flags: MessageFlags.Ephemeral,
          });
          return;
        }
      }

      const modal = buildSectionModal({
        action: sub === "add-section" ? "add" : "edit",
        channelId: existingPost?.channelId ?? (channel?.id as string | undefined),
        name,
        key,
        existingTitle: existingSection?.title,
        existingBody: existingSection?.body,
      });

      await interaction.showModal(modal);
      return;
    }

    if (sub === "remove-section") {
      await interaction.deferReply({ flags: MessageFlags.Ephemeral });
      const name = interaction.options.getString("name", true).trim();
      const key = interaction.options.getString("key", true).trim();

      const post = getPost(name);
      if (!post) {
        await interaction.editReply({ content: `Пост \`${name}\` не найден.` });
        return;
      }

      const removed = removeSectionFromStore(name, key);
      if (!removed) {
        await interaction.editReply({ content: `Раздел \`${key}\` в посте \`${name}\` не найден.` });
        return;
      }

      await deleteSectionMessage(client, post.channelId, name, key);

      if (getPost(name)?.sections.length === 0) {
        deletePost(name);
      }

      await interaction.editReply({ content: `Раздел \`${key}\` удалён из поста \`${name}\`.` });
      return;
    }

    if (sub === "list") {
      await interaction.deferReply({ flags: MessageFlags.Ephemeral });
      const posts = listPosts();

      if (posts.length === 0) {
        await interaction.editReply({ content: "Постов пока нет." });
        return;
      }

      const lines = posts.map((p) => {
        const sectionsList = p.sections
          .map((s) => `  • \`${s.key}\` — ${s.title || "(без заголовка)"}`)
          .join("\n");
        return `**${p.name}** — <#${p.channelId}>\n${sectionsList || "  (нет разделов)"}`;
      });

      await interaction.editReply({ content: lines.join("\n\n").slice(0, 1900) });
      return;
    }

    if (sub === "resend") {
      await interaction.deferReply({ flags: MessageFlags.Ephemeral });
      const name = interaction.options.getString("name", true).trim();

      if (!getPost(name)) {
        await interaction.editReply({ content: `Пост \`${name}\` не найден.` });
        return;
      }

      await renderPost(client, name);
      await interaction.editReply({ content: `Пост \`${name}\` обновлён.` });
      return;
    }

    if (sub === "seed-pravila") {
      await interaction.deferReply({ flags: MessageFlags.Ephemeral });
      const channel = interaction.options.getChannel("channel", true);
      const banner = interaction.options.getAttachment("banner");

      let introBannerFile: string | undefined;
      if (banner) {
        try {
          introBannerFile = await saveBannerAttachment("pravila", PRAVILA_SECTIONS[0].key, banner);
        } catch (err) {
          await interaction.editReply({
            content: `Не удалось сохранить баннер: ${err instanceof Error ? err.message : "неизвестная ошибка"}`,
          });
          return;
        }
      }

      for (const section of PRAVILA_SECTIONS) {
        upsertSection("pravila", channel.id, {
          ...section,
          ...(section === PRAVILA_SECTIONS[0] && introBannerFile ? { bannerFile: introBannerFile } : {}),
        });
      }

      await renderPost(client, "pravila");

      await interaction.editReply({
        content: `Правила опубликованы в <#${channel.id}> (${PRAVILA_SECTIONS.length} сообщений). Дальше редактируйте через \`/post edit-section name:pravila key:<ключ>\`.`,
      });
      return;
    }
  },
};

export default command;
