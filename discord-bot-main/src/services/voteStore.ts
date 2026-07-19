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

export function hasVoted(voterId: string, targetId: string): boolean {
  return readAll().some((v) => v.voterId === voterId && v.targetId === targetId);
}

export function countVotes(targetId: string): number {
  return readAll().filter((v) => v.targetId === targetId).length;
}

/** Записывает голос и возвращает новое количество голосов за targetId. */
export function addVote(voterId: string, targetId: string): number {
  const votes = readAll();
  votes.push({ voterId, targetId, createdAt: Math.floor(Date.now() / 1000) });
  writeAll(votes);
  return votes.filter((v) => v.targetId === targetId).length;
}
