import { Event } from "../types";
import { Collection, MessageFlags } from "discord.js";
import { hostname } from "os";

const seenInteractions = new Map<string, number>();
const hostId = process.env.HOSTNAME || process.env.COMPUTERNAME || hostname();

function markInteractionSeen(interactionId: string): boolean {
  const now = Date.now();
  const isDuplicate = seenInteractions.has(interactionId);
  seenInteractions.set(interactionId, now);

  if (seenInteractions.size > 5000) {
    const cutoff = now - 10 * 60 * 1000;
    for (const [id, ts] of seenInteractions) {
      if (ts < cutoff) seenInteractions.delete(id);
    }
  }

  return isDuplicate;
}

const event: Event<"interactionCreate"> = {
  name: "interactionCreate",
  once: false,
  execute: async (client, interaction) => {
    const routeId =
      (interaction as { customId?: string; commandName?: string }).customId ||
      (interaction as { commandName?: string }).commandName ||
      "unknown";
    const ageMs = Date.now() - interaction.createdTimestamp;

    if (ageMs < -1000 || ageMs > 60_000) {
      console.warn(
        `[InteractionClockSkew] id=${interaction.id} route=${routeId} ageMs=${ageMs} createdTs=${interaction.createdTimestamp} nowTs=${Date.now()} host=${hostId} pid=${process.pid}`
      );
    }

    if (markInteractionSeen(interaction.id)) {
      console.warn(
        `[InteractionDuplicate] id=${interaction.id} route=${routeId} type=${interaction.type} host=${hostId} pid=${process.pid}`
      );
      return;
    }

    if (ageMs > 2500) {
      console.warn(
        `[InteractionSlowAck] id=${interaction.id} route=${routeId} ageMs=${ageMs} type=${interaction.type} host=${hostId} pid=${process.pid}`
      );
    }

    try {
      if (interaction.isButton()) {
        const button =
          client.buttons.get(interaction.customId) ||
          client.buttons.find(
            (b) => !!b.prefix && interaction.customId.startsWith(b.customId)
          );
        if (button) await button.execute(interaction);
        return;
      }

      if (interaction.isModalSubmit()) {
        const modal =
          client.modals.get(interaction.customId) ||
          client.modals.find(
            (m) => !!m.prefix && interaction.customId.startsWith(m.customId)
          );
        if (modal) await modal.execute(interaction);
        return;
      }

      if (interaction.isStringSelectMenu()) {
        const menu = client.selectMenus.get(interaction.customId);
        if (menu) await menu.execute(interaction);
        return;
      }

      if (!interaction.isChatInputCommand()) return;

      const command = client.commands.get(interaction.commandName);
      if (!command) return;

      if (command.cooldown) {
        if (!client.cooldowns.has(command.data.name)) {
          client.cooldowns.set(command.data.name, new Collection());
        }

        const timestamps = client.cooldowns.get(command.data.name)!;
        const cooldownAmount = command.cooldown * 1000;
        const now = Date.now();

        if (timestamps.has(interaction.user.id)) {
          const expirationTime =
            timestamps.get(interaction.user.id)! + cooldownAmount;

          if (now < expirationTime) {
            const timeLeft = (expirationTime - now) / 1000;
            await interaction.reply({
              content: `Подождите ${timeLeft.toFixed(
                1
              )} секунд, прежде чем использовать команду \`${
                command.data.name
              }\`.`,
              flags: MessageFlags.Ephemeral,
            });
            return;
          }
        }

        timestamps.set(interaction.user.id, now);
        setTimeout(
          () => timestamps.delete(interaction.user.id),
          cooldownAmount
        );
      }

      await command.execute(interaction);
    } catch (error) {
      const err = error as { code?: number; message?: string };
      const state = interaction as { replied?: boolean; deferred?: boolean };
      if (err?.code === 10062) {
        console.error(
          `[InteractionExpired] id=${interaction.id} route=${routeId} ageMs=${ageMs} replied=${state.replied ?? false} deferred=${state.deferred ?? false} host=${hostId} pid=${process.pid}`
        );
      } else {
        console.error(`Error executing interaction:`, error);
      }

      if (interaction.isRepliable() && !interaction.replied && !interaction.deferred) {
        await interaction.reply({
          content: 'Произошла ошибка при выполнении этой команды!',
          flags: MessageFlags.Ephemeral
        }).catch(() => {});
      }
    }
  },
};

export default event;
