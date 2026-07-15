import { ExtendedClient } from "./structures/Client";
import dotenv from "dotenv";
import fs from "fs";
import path from "path";
dotenv.config();

const LOCK_PATH = path.join(process.cwd(), ".bot.lock");

function acquireLock(): void {
  try {
    const fd = fs.openSync(LOCK_PATH, "wx");
    fs.writeFileSync(fd, String(process.pid));
    fs.closeSync(fd);
  } catch {
    try {
      const existing = fs.readFileSync(LOCK_PATH, "utf-8").trim();
      console.error(`[Startup] Bot already running (pid=${existing || "unknown"}). Exiting duplicate process ${process.pid}.`);
    } catch {
      console.error(`[Startup] Bot lock exists. Exiting duplicate process ${process.pid}.`);
    }
    process.exit(1);
  }
}

function releaseLock(): void {
  try {
    if (fs.existsSync(LOCK_PATH)) fs.unlinkSync(LOCK_PATH);
  } catch {
    // Ignore lock cleanup errors on shutdown.
  }
}

acquireLock();
const client = new ExtendedClient();

process.on("exit", releaseLock);
process.on("SIGINT", () => {
  releaseLock();
  process.exit(0);
});
process.on("SIGTERM", () => {
  releaseLock();
  process.exit(0);
});

process.on("unhandledRejection", (error) => {
  console.error("[UnhandledRejection]", error);
});

process.on("uncaughtException", (error) => {
  console.error("[UncaughtException]", error);
});

client.start(process.env.TOKEN!);
