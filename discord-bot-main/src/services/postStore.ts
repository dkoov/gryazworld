import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const DATA_PATH = path.join(__dirname, "..", "..", "data", "posts.json");

export interface PostSection {
  key: string;
  title: string;
  body: string;
  bannerFile?: string;
}

export interface PostDef {
  channelId: string;
  sections: PostSection[];
}

type PostsFile = Record<string, PostDef>;

function loadPosts(): PostsFile {
  if (!fs.existsSync(DATA_PATH)) return {};
  return JSON.parse(fs.readFileSync(DATA_PATH, "utf-8"));
}

function savePosts(data: PostsFile): void {
  const dir = path.dirname(DATA_PATH);
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
  fs.writeFileSync(DATA_PATH, JSON.stringify(data, null, 2));
}

export function getPost(name: string): PostDef | undefined {
  return loadPosts()[name];
}

export function listPosts(): Array<{ name: string } & PostDef> {
  const posts = loadPosts();
  return Object.entries(posts).map(([name, def]) => ({ name, ...def }));
}

export function ensurePost(name: string, channelId?: string): PostDef {
  const posts = loadPosts();
  if (posts[name]) return posts[name];

  if (!channelId) {
    throw new Error(
      `Поста "${name}" ещё не существует — укажите канал (channel), чтобы его создать.`
    );
  }

  posts[name] = { channelId, sections: [] };
  savePosts(posts);
  return posts[name];
}

export function upsertSection(
  name: string,
  channelId: string | undefined,
  section: PostSection
): PostDef {
  const posts = loadPosts();
  let post = posts[name];

  if (!post) {
    if (!channelId) {
      throw new Error(
        `Поста "${name}" ещё не существует — укажите канал (channel), чтобы его создать.`
      );
    }
    post = { channelId, sections: [] };
    posts[name] = post;
  }

  const existingIndex = post.sections.findIndex((s) => s.key === section.key);
  if (existingIndex >= 0) {
    post.sections[existingIndex] = {
      ...post.sections[existingIndex],
      ...section,
    };
  } else {
    post.sections.push(section);
  }

  savePosts(posts);
  return post;
}

export function removeSection(name: string, key: string): PostSection | undefined {
  const posts = loadPosts();
  const post = posts[name];
  if (!post) return undefined;

  const index = post.sections.findIndex((s) => s.key === key);
  if (index < 0) return undefined;

  const [removed] = post.sections.splice(index, 1);
  savePosts(posts);
  return removed;
}

export function deletePost(name: string): PostDef | undefined {
  const posts = loadPosts();
  const post = posts[name];
  if (!post) return undefined;
  delete posts[name];
  savePosts(posts);
  return post;
}
