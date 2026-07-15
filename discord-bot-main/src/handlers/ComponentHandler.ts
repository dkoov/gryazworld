import { ExtendedClient } from "../structures/Client.js";
import { Button, Modal, SelectMenu } from "../types/index.js";
import path from "path";
import fs from "fs";
import { fileURLToPath, pathToFileURL } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const runtimeExt = path.extname(__filename) || ".ts";

async function loadFromDir<T extends { customId: string }>(
  dirPath: string
): Promise<T[]> {
  const items: T[] = [];

  if (!fs.existsSync(dirPath)) return items;

  const entries = fs.readdirSync(dirPath, { withFileTypes: true });

  for (const entry of entries) {
    const fullPath = path.join(dirPath, entry.name);

    if (entry.isDirectory()) {
      const nested = await loadFromDir<T>(fullPath);
      items.push(...nested);
      continue;
    }

    if (!entry.name.endsWith(runtimeExt)) continue;

    const mod = await import(pathToFileURL(fullPath).href);
    const item: T = mod.default;

    if (item?.customId && typeof (item as any).execute === "function") {
      items.push(item);
    }
  }

  return items;
}

export async function loadComponents(client: ExtendedClient): Promise<void> {
  const componentsPath = path.join(__dirname, "..", "components");

  const buttons = await loadFromDir<Button>(
    path.join(componentsPath, "buttons")
  );
  for (const btn of buttons) {
    if (client.buttons.has(btn.customId)) {
      console.warn(`[Components] Duplicate button customId: ${btn.customId}`);
    }
    client.buttons.set(btn.customId, btn);
  }

  const modals = await loadFromDir<Modal>(
    path.join(componentsPath, "modals")
  );
  for (const modal of modals) {
    if (client.modals.has(modal.customId)) {
      console.warn(`[Components] Duplicate modal customId: ${modal.customId}`);
    }
    client.modals.set(modal.customId, modal);
  }

  const selectMenus = await loadFromDir<SelectMenu>(
    path.join(componentsPath, "selectMenus")
  );
  for (const menu of selectMenus) {
    if (client.selectMenus.has(menu.customId)) {
      console.warn(`[Components] Duplicate select customId: ${menu.customId}`);
    }
    client.selectMenus.set(menu.customId, menu);
  }
}
