import fs from "fs";
import path from "path";

const DB_PATH = path.join(process.cwd(), "data", "staff_applications.json");

export interface StaffApplication {
  userId: string;
  threadId: string;
  type: "moderator" | "media";
  nickname: string;
  age: string;
  experience?: string;
  why?: string;
  time?: string;
  links?: string;
  contentExp?: string;
  createdAt: number;
}

function ensureDir() {
  const dir = path.dirname(DB_PATH);
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

function readAll(): StaffApplication[] {
  ensureDir();
  if (!fs.existsSync(DB_PATH)) return [];
  try {
    return JSON.parse(fs.readFileSync(DB_PATH, "utf-8"));
  } catch {
    return [];
  }
}

function writeAll(apps: StaffApplication[]) {
  ensureDir();
  fs.writeFileSync(DB_PATH, JSON.stringify(apps, null, 2));
}

export function createStaffApplication(
  data: Omit<StaffApplication, "createdAt">
): StaffApplication {
  const apps = readAll();
  const app: StaffApplication = { ...data, createdAt: Math.floor(Date.now() / 1000) };
  apps.push(app);
  writeAll(apps);
  return app;
}

export function getStaffApplicationByThread(threadId: string): StaffApplication | undefined {
  return readAll().find((a) => a.threadId === threadId);
}

export function getStaffApplicationByUser(userId: string): StaffApplication | undefined {
  return readAll().find((a) => a.userId === userId);
}

export function removeStaffApplication(threadId: string): StaffApplication | undefined {
  const apps = readAll();
  const idx = apps.findIndex((a) => a.threadId === threadId);
  if (idx === -1) return undefined;
  const [removed] = apps.splice(idx, 1);
  writeAll(apps);
  return removed;
}
