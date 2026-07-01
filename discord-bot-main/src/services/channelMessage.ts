import {
  TextChannel,
  MessageCreateOptions,
  MessageEditOptions,
} from "discord.js";
import { ExtendedClient } from "../structures/Client.js";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const DATA_PATH = path.join(__dirname, "..", "..", "data", "messages.json");

function loadMessages(): Record<string, string> {
  if (!fs.existsSync(DATA_PATH)) return {};
  return JSON.parse(fs.readFileSync(DATA_PATH, "utf-8"));
}

function saveMessages(data: Record<string, string>): void {
  const dir = path.dirname(DATA_PATH);
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
  fs.writeFileSync(DATA_PATH, JSON.stringify(data, null, 2));
}

export async function sendOrEditSequence(
  client: ExtendedClient,
  channelId: string,
  sections: Array<{ key: string; payload: MessageCreateOptions & MessageEditOptions }>
): Promise<Array<{ key: string; messageId: string }>> {
  const channel = client.channels.cache.get(channelId) as TextChannel;
  if (!channel) return [];

  const stored = loadMessages();
  const results: Array<{ key: string; messageId: string }> = [];

  for (const { key, payload } of sections) {
    const storeKey = `${channelId}:${key}`;
    const messageId = stored[storeKey];

    if (messageId) {
      const existing = await channel.messages.fetch(messageId).catch(() => null);
      if (existing) {
        await existing.edit(payload);
        results.push({ key, messageId: existing.id });
        continue;
      }
    }

    const sent = await channel.send(payload);
    stored[storeKey] = sent.id;
    results.push({ key, messageId: sent.id });
  }

  saveMessages(stored);
  return results;
}

export async function sendOrEdit(
  client: ExtendedClient,
  channelId: string,
  payload: MessageCreateOptions & MessageEditOptions
): Promise<void> {
  const channel = client.channels.cache.get(channelId) as TextChannel;
  if (!channel) return;

  const stored = loadMessages();
  const messageId = stored[channelId];


  if (messageId) {
    const existing = await channel.messages.fetch(messageId).catch(() => null);
    if (existing) {
      await existing.edit(payload);
      return;
    }
  }

  const sent = await channel.send(payload);
  stored[channelId] = sent.id;
  saveMessages(stored);
}
