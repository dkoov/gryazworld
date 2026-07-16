import { Event } from "../types";
import {
  ChannelType,
} from "discord.js";
import { relayChatToMinecraft } from "../services/chatRelay";

let channelIds: string[] = [];
const MC_CHAT_CHANNEL_ID = process.env.MC_CHAT_CHANNEL_ID ?? "1504176921833902100";

function loadConfig() {
  const raw = process.env.AUTO_THREAD_CHANNELS;
  if (!raw) return;
  channelIds = raw.split(",").map((id) => id.trim()).filter(Boolean);
}

const event: Event<"messageCreate"> = {
  name: "messageCreate",
  once: false,
  execute: async (_client, message) => {
    if (message.author.bot) return;
    if (!message.guild) return;

    if (message.channelId === MC_CHAT_CHANNEL_ID) {
      const content = message.content.trim();
      if (content) {
        const displayName = message.member?.displayName ?? message.author.username;
        relayChatToMinecraft(displayName, content).catch((e) =>
          console.error("[ChatRelay] не удалось отправить сообщение в игру:", e)
        );
      }
    }

    if (!channelIds.length) loadConfig();
    if (!channelIds.includes(message.channelId)) return;

    for (const emoji of ["❤️", "👍", "👎"]) {
      await message.react(emoji).catch(() => null);
    }

    await message.startThread({
      name: "Обсуждение - " + message.author.username,
      autoArchiveDuration: 1440,
    }).catch(() => null);
  },
};

export default event;
