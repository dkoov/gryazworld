import { Event } from "../types";
import {
  ChannelType,
} from "discord.js";

let channelIds: string[] = [];

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
