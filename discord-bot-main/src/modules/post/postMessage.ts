import {
  ContainerBuilder,
  TextDisplayBuilder,
  MediaGalleryBuilder,
  MediaGalleryItemBuilder,
  MessageFlags,
  MessageCreateOptions,
  MessageEditOptions,
  AttachmentBuilder,
} from "discord.js";
import { ExtendedClient } from "../../structures/Client";
import { sendOrEditSequence, deleteStoredSequenceMessage } from "../../services/channelMessage";
import { getPost, listPosts, PostSection } from "../../services/postStore";
import { readBannerFile } from "../../services/bannerFile";

const ACCENT_COLOR = 0x7e4dd9;

function sequenceKey(postName: string, sectionKey: string): string {
  return `${postName}:${sectionKey}`;
}

function buildSectionPayload(
  section: PostSection
): MessageCreateOptions & MessageEditOptions {
  const container = new ContainerBuilder().setAccentColor(ACCENT_COLOR);

  const files: AttachmentBuilder[] = [];
  if (section.bannerFile) {
    const buffer = readBannerFile(section.bannerFile);
    if (buffer) {
      files.push(new AttachmentBuilder(buffer, { name: section.bannerFile }));
      container.addMediaGalleryComponents(
        new MediaGalleryBuilder().addItems(
          new MediaGalleryItemBuilder().setURL(`attachment://${section.bannerFile}`)
        )
      );
    }
  }

  const content = section.title
    ? `## ${section.title}\n\n${section.body}`
    : section.body;

  container.addTextDisplayComponents(new TextDisplayBuilder().setContent(content));

  return {
    components: [container],
    flags: MessageFlags.IsComponentsV2,
    files,
    // Без этого при edit() discord.js оставляет старые вложения и баннер
    // дублировался бы с каждым редактированием раздела.
    attachments: [],
  };
}

export async function renderPost(client: ExtendedClient, name: string): Promise<void> {
  const post = getPost(name);
  if (!post) return;

  const sections = post.sections.map((section) => ({
    key: sequenceKey(name, section.key),
    payload: buildSectionPayload(section),
  }));

  await sendOrEditSequence(client, post.channelId, sections);
}

export async function renderAllPosts(client: ExtendedClient): Promise<void> {
  for (const post of listPosts()) {
    try {
      await renderPost(client, post.name);
    } catch (err) {
      console.error(`[Post] Не удалось отрисовать пост "${post.name}":`, err);
    }
  }
}

export async function deleteSectionMessage(
  client: ExtendedClient,
  channelId: string,
  postName: string,
  sectionKey: string
): Promise<void> {
  await deleteStoredSequenceMessage(client, channelId, sequenceKey(postName, sectionKey));
}
