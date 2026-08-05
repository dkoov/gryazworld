import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import { Attachment } from "discord.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const BANNERS_DIR = path.join(__dirname, "..", "..", "data", "banners");

function extFromAttachment(attachment: Attachment): string {
  const fromName = path.extname(attachment.name || "").replace(".", "");
  if (fromName) return fromName.toLowerCase();

  const type = attachment.contentType || "";
  if (type.includes("png")) return "png";
  if (type.includes("jpeg") || type.includes("jpg")) return "jpg";
  if (type.includes("webp")) return "webp";
  if (type.includes("gif")) return "gif";
  return "png";
}

/** Скачивает вложение и сохраняет на диск, чтобы не зависеть от протухающих CDN-ссылок Discord. */
export async function saveBannerAttachment(
  postName: string,
  sectionKey: string,
  attachment: Attachment
): Promise<string> {
  if (!fs.existsSync(BANNERS_DIR)) fs.mkdirSync(BANNERS_DIR, { recursive: true });

  const ext = extFromAttachment(attachment);
  const safeName = `${postName}__${sectionKey}`.replace(/[^a-zA-Zа-яА-Я0-9_.-]/g, "_");
  const fileName = `${safeName}.${ext}`;
  const filePath = path.join(BANNERS_DIR, fileName);

  const res = await fetch(attachment.url);
  if (!res.ok) {
    throw new Error(`Не удалось скачать баннер: HTTP ${res.status}`);
  }
  const buffer = Buffer.from(await res.arrayBuffer());
  fs.writeFileSync(filePath, buffer);

  return fileName;
}

export function readBannerFile(fileName: string): Buffer | undefined {
  const filePath = path.join(BANNERS_DIR, fileName);
  if (!fs.existsSync(filePath)) return undefined;
  return fs.readFileSync(filePath);
}

export function deleteBannerFile(fileName: string): void {
  const filePath = path.join(BANNERS_DIR, fileName);
  if (fs.existsSync(filePath)) fs.unlinkSync(filePath);
}
