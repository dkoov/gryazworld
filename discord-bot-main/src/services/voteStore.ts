import fs from "fs";
import path from "path";

const DB_PATH = path.join(process.cwd(), "data", "votes.json");

export interface VoteRecord {
  voterId: string;
  targetId: string;
  createdAt: number;
}

function ensureDir() {
  const dir = path.dirname(DB_PATH);
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

function readAll(): VoteRecord[] {
  ensureDir();
  if (!fs.existsSync(DB_PATH)) return [];
  const raw = fs.readFileSync(DB_PATH, "utf-8");
  try {
    return JSON.parse(raw);
  } catch {
    return [];
  }
}

function writeAll(votes: VoteRecord[]) {
  ensureDir();
  fs.writeFileSync(DB_PATH, JSON.stringify(votes, null, 2));
}

/** Каждый голосующий может держать только один активный голос -- возвращает его текущего кандидата (или null). */
export function getVoteTarget(voterId: string): string | null {
  const record = readAll().find((v) => v.voterId === voterId);
  return record ? record.targetId : null;
}

export function countVotes(targetId: string): number {
  return readAll().filter((v) => v.targetId === targetId).length;
}

/**
 * Заменяет голос voterId на targetId, снимая прошлый голос (если был) с прошлого кандидата.
 * Возвращает прошлого кандидата и актуальные счётчики после замены.
 */
export function setVote(
  voterId: string,
  targetId: string
): { previousTargetId: string | null; previousCount: number; newCount: number } {
  const votes = readAll();
  const existingIndex = votes.findIndex((v) => v.voterId === voterId);
  const previousTargetId = existingIndex >= 0 ? votes[existingIndex].targetId : null;
  if (existingIndex >= 0) votes.splice(existingIndex, 1);
  votes.push({ voterId, targetId, createdAt: Math.floor(Date.now() / 1000) });
  writeAll(votes);

  const previousCount = previousTargetId ? votes.filter((v) => v.targetId === previousTargetId).length : 0;
  const newCount = votes.filter((v) => v.targetId === targetId).length;
  return { previousTargetId, previousCount, newCount };
}
