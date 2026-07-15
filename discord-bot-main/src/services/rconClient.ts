import { whitelistAdd } from "./backendClient";

const WINGS_URL     = process.env.WINGS_URL     ?? "http://65.109.82.139:8080";
const WINGS_TOKEN   = process.env.WINGS_TOKEN   ?? "";
const VELOCITY_UUID = process.env.VELOCITY_UUID ?? "3a8e6555-2478-468b-8510-662dabede282";

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
  // Бэкенд — источник истины для вайтлиста, пишем в него ВСЕГДА и в первую очередь.
  await whitelistAdd({
    minecraftName,
    discordUserId,
    moderatorDiscordUserId,
    reason: reason ?? null,
  });
  console.log(`[Whitelist] Backend: ${minecraftName} / discord:${discordUserId}`);

  // Пуш в консоль Velocity — необязательный (best-effort): если Wings недоступен
  // или не настроен, вайтлист уже сработал через бэкенд, ошибку тут не считаем фатальной.
  if (!WINGS_TOKEN) return;
  try {
    await sendVelocityCommand(`vwhitelist add ${minecraftName}`);
    console.log(`[Whitelist] vwhitelist add ${minecraftName}`);
  } catch (e) {
    console.warn(`[Whitelist] Wings push failed (не критично, бэкенд уже обновлён): ${e}`);
  }
}
