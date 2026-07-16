const BACKEND_URL = process.env.BACKEND_INTERNAL_URL ?? "http://backend:8000";
const PLUGIN_SECRET = process.env.PLUGIN_SECRET ?? "";

export async function relayChatToMinecraft(username: string, message: string): Promise<void> {
  const res = await fetch(`${BACKEND_URL}/mc/chat/discord-relay`, {
    method: "POST",
    headers: { "Content-Type": "application/json", "X-Plugin-Secret": PLUGIN_SECRET },
    body: JSON.stringify({ username, message }),
  });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`Backend ${res.status}: ${text}`);
  }
}
