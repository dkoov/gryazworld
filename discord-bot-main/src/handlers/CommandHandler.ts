import { REST, Routes, SlashCommandBuilder } from "discord.js";
import { ExtendedClient } from "../structures/Client.js";
import { Command } from "../types/index.js";
import path from "path";
import fs from "fs";
import { fileURLToPath, pathToFileURL } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const runtimeExt = path.extname(__filename) || ".ts";

export async function loadCommands(client: ExtendedClient): Promise<void> {
  const commandsPath = path.join(__dirname, "..", "commands");
  const categories = fs.readdirSync(commandsPath);

  for (const category of categories) {
    const categoryPath = path.join(commandsPath, category);
    const stat = fs.statSync(categoryPath);

    if (!stat.isDirectory()) continue;

    const commandFiles = fs
      .readdirSync(categoryPath)
      .filter((file) => file.endsWith(runtimeExt));

    for (const file of commandFiles) {
      const filePath = path.join(categoryPath, file);
      const commandModule = await import(pathToFileURL(filePath).href);
      const command: Command = commandModule.default;

      if (command?.data && typeof command?.execute === "function") {
        client.commands.set(command.data.name, command);

        if (command.aliases) {
          for (const alias of command.aliases) {
            const aliasData = new SlashCommandBuilder()
              .setName(alias)
              .setDescription(command.data.description);

            const json = command.data.toJSON() as any;
            if ("dm_permission" in json) {
              aliasData.setDMPermission(json.dm_permission);
            }

            const aliasCommand: Command = {
              ...command,
              data: aliasData,
            };

            client.commands.set(alias, aliasCommand);
          }
        }
      }
    }
  }
}

export async function registerCommands(client: ExtendedClient): Promise<void> {
  const rest = new REST({ version: "10" }).setToken(process.env.TOKEN!);

  const guildCommands: any[] = [];
  const globalCommands: any[] = [];

  for (const cmd of client.commands.values()) {
    const json = cmd.data.toJSON();
    if ("dm_permission" in json && json.dm_permission === true) {
      globalCommands.push(json);
    } else {
      guildCommands.push(json);
    }
  }

  if (process.env.GUILD_ID && guildCommands.length) {
    await rest.put(
      Routes.applicationGuildCommands(
        process.env.CLIENT_ID!,
        process.env.GUILD_ID!
      ),
      { body: guildCommands }
    );
  }

  if (globalCommands.length) {
    await rest.put(Routes.applicationCommands(process.env.CLIENT_ID!), {
      body: globalCommands,
    });
  }
}
