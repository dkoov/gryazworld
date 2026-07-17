const BACKEND_URL = process.env.BACKEND_INTERNAL_URL ?? "http://backend:8000";
const PLUGIN_SECRET = process.env.PLUGIN_SECRET ?? "";

interface CreateClaimBody {
  plaintiff_discord_id: string;
  defendant_text: string;
  subject: string;
  description: string;
  thread_url?: string;
}

/** Регистрирует поданный иск в базе сайта, чтобы Судья/админ могли рассмотреть его на сайте. */
export async function createClaim(body: CreateClaimBody): Promise<void> {
  const res = await fetch(`${BACKEND_URL}/mc/claims`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Plugin-Secret": PLUGIN_SECRET,
    },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`Backend ${res.status}: ${text}`);
  }
}
