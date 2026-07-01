import { whitelistAdd } from "./backendClient";

const WINGS_URL     = process.env.WINGS_URL     ?? "";
const WINGS_TOKEN   = process.env.WINGS_TOKEN   ?? "";
const VELOCITY_UUID = process.env.VELOCITY_UUID ?? "";

async function sendVelocityCommand(command: string): Promise<void> {
  const res = await fetch(`${WINGS_URL}/api/servers/${VELOCITY_UUID}/commands`, {
    method:  "POST",
    headers: {
      "Authorization":  `Bearer ${WINGS_TOKEN}`,
      "Content-Type":   "application/json",
    },
    body: JSON.stringify({ commands: [command] }),
  });
  if (res.status !== 204 && !res.ok) {
    const body = await res.text().catch(() => "");
    throw new Error(`Wings API ${res.status}: ${body}`);
  }
}

export async function rconWhitelistAdd(
  minecraftName:          string,
  discordUserId:          string,
  moderatorDiscordUserId: string,
  reason?:                string | null,
): Promise<void> {
  await sendVelocityCommand(`vwhitelist add ${minecraftName}`);
  console.log(`[Whitelist] vwhitelist add ${minecraftName}`);

  await whitelistAdd({
    minecraftName,
    discordUserId,
    moderatorDiscordUserId,
    reason: reason ?? null,
  });
  console.log(`[Whitelist] Backend: ${minecraftName} / discord:${discordUserId}`);
}

