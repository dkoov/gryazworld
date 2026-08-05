const pending = new Map<string, { fileName: string; expiresAt: number }>();

const TTL_MS = 10 * 60 * 1000;

function makeKey(userId: string, name: string, sectionKey: string): string {
  return `${userId}:${name}:${sectionKey}`;
}

function cleanup(): void {
  const now = Date.now();
  for (const [key, value] of pending) {
    if (value.expiresAt < now) pending.delete(key);
  }
}

export function setPendingBanner(
  userId: string,
  name: string,
  sectionKey: string,
  fileName: string
): void {
  cleanup();
  pending.set(makeKey(userId, name, sectionKey), {
    fileName,
    expiresAt: Date.now() + TTL_MS,
  });
}

export function takePendingBanner(
  userId: string,
  name: string,
  sectionKey: string
): string | undefined {
  const key = makeKey(userId, name, sectionKey);
  const value = pending.get(key);
  pending.delete(key);
  if (!value || value.expiresAt < Date.now()) return undefined;
  return value.fileName;
}
