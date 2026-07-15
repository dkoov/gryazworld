import {
  ChatInputCommandInteraction,
  ButtonInteraction,
  ModalSubmitInteraction,
  StringSelectMenuInteraction,
  SlashCommandBuilder,
  SlashCommandSubcommandsOnlyBuilder,
  SlashCommandOptionsOnlyBuilder,
  Collection,
  ClientEvents,
} from "discord.js";
import { ExtendedClient } from "../structures/Client";

export interface Command {
  data:
    | SlashCommandBuilder
    | Omit<SlashCommandBuilder, "addSubcommandGroup" | "addSubcommand">
    | SlashCommandSubcommandsOnlyBuilder
    | SlashCommandOptionsOnlyBuilder;
  aliases?: string[];
  cooldown?: number;
  execute: (interaction: ChatInputCommandInteraction) => Promise<void>;
}

export interface Button {
  customId: string;
  prefix?: boolean;
  execute: (interaction: ButtonInteraction) => Promise<void>;
}

export interface Modal {
  customId: string;
  prefix?: boolean;
  execute: (interaction: ModalSubmitInteraction) => Promise<void>;
}

export interface SelectMenu {
  customId: string;
  execute: (interaction: StringSelectMenuInteraction) => Promise<void>;
}

export interface Event<K extends keyof ClientEvents = keyof ClientEvents> {
  name: K;
  once?: boolean;
  execute: (client: ExtendedClient, ...args: ClientEvents[K]) => Promise<void>;
}

declare module "discord.js" {
  interface Client {
    commands: Collection<string, Command>;
    buttons: Collection<string, Button>;
    modals: Collection<string, Modal>;
    selectMenus: Collection<string, SelectMenu>;
    cooldowns: Collection<string, Collection<string, number>>;
  }
}
