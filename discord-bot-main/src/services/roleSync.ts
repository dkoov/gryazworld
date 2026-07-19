import { ExtendedClient } from "../structures/Client";

const BACKEND_URL = process.env.BACKEND_INTERNAL_URL ?? "http://backend:8000";
const API_KEY = process.env.INTERNAL_API_KEY ?? "";
const GUILD_ID = process.env.GUILD_ID ?? "";
const SYNC_INTERVAL_MS = 3 * 60 * 1000;

/** Minecraft-роль (cs_roles.name) -> Discord-роль. Owner намеренно не синкается. */
const MC_TO_DISCORD_ROLE: Record<string, string> = {
  Banker: "1523054711392043008", // Банкир
  Helper: "1518943489142952068", // Хелпер
  IchoPlus: "1470758280510050327", // IchoPlus
  Keeper: "1523055065336909955", // Строитель
  "Куратор": "1470756818354438281", // Куратор
  Moderator: "1470756763522437171", // Модератор
  Police: "1523054974450401441", // СБИ
  "Судья": "1522969933850611752", // Судья
};

const MANAGED_ROLE_IDS = new Set(Object.values(MC_TO_DISCORD_ROLE));

interface RoleSyncEntry {
  discord_id: string;
  roles: string[];
}

async function fetchRoleSyncData(): Promise<RoleSyncEntry[]> {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), 10_000);
  try {
    const res = await fetch(`${BACKEND_URL}/internal/discord/role-sync`, {
      headers: { "x-api-key": API_KEY },
      signal: controller.signal,
    });
    if (!res.ok) throw new Error(`backend ${res.status}`);
    return (await res.json()) as RoleSyncEntry[];
  } finally {
    clearTimeout(timer);
  }
}

export async function syncRoles(client: ExtendedClient): Promise<void> {
  if (!API_KEY || !GUILD_ID) return;

  let entries: RoleSyncEntry[];
  try {
    entries = await fetchRoleSyncData();
  } catch (e) {
    console.error("[RoleSync] не удалось получить данные с бэкенда:", e);
    return;
  }

  const guild = client.guilds.cache.get(GUILD_ID) ?? (await client.guilds.fetch(GUILD_ID).catch(() => null));
  if (!guild) {
    console.warn(`[RoleSync] гильдия ${GUILD_ID} не найдена`);
    return;
  }

  for (const entry of entries) {
    const shouldHave = new Set(
      entry.roles.map((r) => MC_TO_DISCORD_ROLE[r]).filter((id): id is string => Boolean(id))
    );

    let member;
    try {
      member = guild.members.cache.get(entry.discord_id) ?? (await guild.members.fetch(entry.discord_id));
    } catch {
      continue; // игрок покинул сервер или ещё не заходил -- пропускаем
    }

    const has = new Set(member.roles.cache.filter((r) => MANAGED_ROLE_IDS.has(r.id)).map((r) => r.id));

    for (const roleId of shouldHave) {
      if (!has.has(roleId)) {
        await member.roles.add(roleId).catch((e) =>
          console.error(`[RoleSync] не удалось выдать роль ${roleId} игроку ${entry.discord_id}:`, e.message)
        );
      }
    }
    for (const roleId of has) {
      if (!shouldHave.has(roleId)) {
        await member.roles.remove(roleId).catch((e) =>
          console.error(`[RoleSync] не удалось снять роль ${roleId} у игрока ${entry.discord_id}:`, e.message)
        );
      }
    }
  }
}

export function startRoleSync(client: ExtendedClient): void {
  syncRoles(client).catch((e) => console.error("[RoleSync] ошибка:", e));
  setInterval(() => {
    syncRoles(client).catch((e) => console.error("[RoleSync] ошибка:", e));
  }, SYNC_INTERVAL_MS);
}
