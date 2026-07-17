import fs from "fs";
import path from "path";

const DB_PATH = path.join(process.cwd(), "data", "fine_messages.json");

interface FineMessageRef {
  fineId: number;
  channelId: string;
  messageId: string;
}

function ensureDir() {
  const dir = path.dirname(DB_PATH);
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

function readAll(): FineMessageRef[] {
  ensureDir();
  if (!fs.existsSync(DB_PATH)) return [];
  const raw = fs.readFileSync(DB_PATH, "utf-8");
  try {
    return JSON.parse(raw);
  } catch {
    return [];
  }
}

function writeAll(refs: FineMessageRef[]) {
  ensureDir();
  fs.writeFileSync(DB_PATH, JSON.stringify(refs, null, 2));
}

/** Запоминаем, в каком сообщении СБИ-канала висит кнопка "Подтвердить оплату" для этого штрафа. */
export function recordFineMessage(fineId: number, channelId: string, messageId: string): void {
  const refs = readAll().filter((r) => r.fineId !== fineId);
  refs.push({ fineId, channelId, messageId });
  writeAll(refs);
}

export function getFineMessage(fineId: number): FineMessageRef | undefined {
  return readAll().find((r) => r.fineId === fineId);
}

export function removeFineMessage(fineId: number): void {
  writeAll(readAll().filter((r) => r.fineId !== fineId));
}
