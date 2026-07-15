import fs from "fs";
import path from "path";

const DB_PATH = path.join(process.cwd(), "data", "applications.json");

export interface Application {
  userId: string;
  threadId: string;
  nickname: string;
  age: string;
  about: string;
  reason: string;
  source: string;
  createdAt: number;
}

function ensureDir() {
  const dir = path.dirname(DB_PATH);
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

function readAll(): Application[] {
  ensureDir();
  if (!fs.existsSync(DB_PATH)) return [];
  const raw = fs.readFileSync(DB_PATH, "utf-8");
  try {
    return JSON.parse(raw);
  } catch {
    return [];
  }
}

function writeAll(apps: Application[]) {
  ensureDir();
  fs.writeFileSync(DB_PATH, JSON.stringify(apps, null, 2));
}

export function createApplication(data: Omit<Application, "createdAt">): Application {
  const apps = readAll();
  const app: Application = { ...data, createdAt: Math.floor(Date.now() / 1000) };
  apps.push(app);
  writeAll(apps);
  return app;
}

export function getApplicationByThread(threadId: string): Application | undefined {
  return readAll().find((a) => a.threadId === threadId);
}

export function getApplicationByUser(userId: string): Application | undefined {
  return readAll().find((a) => a.userId === userId);
}

export function removeApplication(threadId: string): Application | undefined {
  const apps = readAll();
  const idx = apps.findIndex((a) => a.threadId === threadId);
  if (idx === -1) return undefined;
  const [removed] = apps.splice(idx, 1);
  writeAll(apps);
  return removed;
}
