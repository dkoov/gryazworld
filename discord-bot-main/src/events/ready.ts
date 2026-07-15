import { ActivityType } from "discord.js";
import { Event } from "../types";
import { registerCommands } from "../handlers/CommandHandler";
import { sendApplicationMessage } from "../modules/application/applicationMessage";
import { sendSupportMessage } from "../modules/support/supportMessage";
import { sendStaffMessage } from "../modules/staff/staffMessage";

const event: Event<"clientReady"> = {
  name: "clientReady",
  once: true,
  execute: async (client) => {
    const startTime = Date.now();
    console.log(
      `[Runtime] pid=${process.pid} host=${process.env.HOSTNAME ?? "unknown"} shard=${client.shard?.ids.join(",") ?? "0"}`
    );
    console.log(`${client.user?.tag} онлайн`);
    await registerCommands(client);
    const endTime = Date.now();
    console.log(
      `Регнул ${client.commands.size} команд за ${(endTime - startTime) / 1000}с.`
    );

    const updatePresence = () => {
      const members = client.guilds.cache.reduce(
        (acc, guild) => acc + guild.memberCount,
        0
      );
      client.user?.setPresence({
        activities: [
          {
            name: "Vibecoding",
            type: ActivityType.Playing,
            state: `вместе с ${members} участниками`,
          },
        ],
        status: "idle",
      });
    };

    updatePresence();
    setInterval(updatePresence, 60_000);

    await sendApplicationMessage(client);
    await sendSupportMessage(client);
    await sendStaffMessage(client);
  },
};

export default event;
