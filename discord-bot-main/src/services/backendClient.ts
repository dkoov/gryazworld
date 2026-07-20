const BACKEND_URL = process.env.BACKEND_INTERNAL_URL ?? "http://backend:3000";
const API_KEY = process.env.INTERNAL_API_KEY ?? "";

const TIMEOUT_MS = 5_000;

interface WhitelistAddBody {
  minecraftName: string;
  moderatorDiscordUserId: string;
  discordUserId?: string | null;
  reason?: string | null;
}

interface WhitelistRemoveBody {
  minecraftName: string;
}

interface BackendResponse {
  ok?: boolean;
  minecraftName?: string;
  error?: string;
}

interface WhitelistCheckResponse {
  minecraftName: string;
  whitelisted: boolean;
  addedAt?: string;
  addedBy?: string;
}

interface ChangeNicknameBody {
  discordUserId: string;
  newNickname: string;
}

interface ChangeNicknameResponse {
  ok: boolean;
  oldNickname?: string;
  newNickname?: string;
  error?: string;
}

async function request<T>(
  method: string,
  path: string,
  body?: unknown,
): Promise<T> {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), TIMEOUT_MS);

  try {
    const res = await fetch(`${BACKEND_URL}${path}`, {
      method,
      headers: {
        "content-type": "application/json",
        "x-api-key": API_KEY,
      },
      body: body ? JSON.stringify(body) : undefined,
      signal: controller.signal,
    });

    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new Error(`Backend ${res.status}: ${text}`);
    }

    return (await res.json()) as T;
  } catch (err: any) {
    if (err.name === "AbortError") {
      throw new Error("Backend request timed out");
    }
    throw err;
  } finally {
    clearTimeout(timer);
  }
}

export async function whitelistAdd(body: WhitelistAddBody): Promise<BackendResponse> {
  return request<BackendResponse>("POST", "/internal/whitelist/add", body);
}

export async function whitelistRemove(body: WhitelistRemoveBody): Promise<BackendResponse> {
  return request<BackendResponse>("POST", "/internal/whitelist/remove", body);
}

export async function whitelistCheck(minecraftName: string): Promise<WhitelistCheckResponse> {
  return request<WhitelistCheckResponse>(
    "GET",
    `/internal/whitelist/check/${encodeURIComponent(minecraftName)}`,
  );
}

export async function whitelistChangeNickname(body: ChangeNicknameBody): Promise<ChangeNicknameResponse> {
  return request<ChangeNicknameResponse>("POST", "/internal/whitelist/changenick", body);
}
