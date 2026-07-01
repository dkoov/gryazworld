import { Client, Collection, GatewayIntentBits, Partials } from "discord.js";
import { Command, Button, Modal, SelectMenu } from "../types";
import { loadCommands } from "../handlers/CommandHandler";
import { loadEvents } from "../handlers/EventHandler";
import { loadComponents } from "../handlers/ComponentHandler";

export class ExtendedClient extends Client {
  commands: Collection<string, Command> = new Collection();
  buttons: Collection<string, Button> = new Collection();
  modals: Collection<string, Modal> = new Collection();
  selectMenus: Collection<string, SelectMenu> = new Collection();
  cooldowns: Collection<string, Collection<string, number>> = new Collection();

  constructor() {
    super({
      intents: [
        GatewayIntentBits.Guilds,
        GatewayIntentBits.GuildMessages,
        GatewayIntentBits.MessageContent,
        GatewayIntentBits.GuildMembers,
        GatewayIntentBits.DirectMessages,
      ],
      partials: [Partials.Channel, Partials.Message],
    });
  }

  async start(token: string): Promise<void> {
    await loadCommands(this);
    await loadComponents(this);
    await loadEvents(this);
    await this.login(token);
  }
}
