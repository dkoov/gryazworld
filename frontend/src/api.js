const API_BASE = import.meta.env.VITE_API_URL || ''

export async function apiFetch(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({}))
    throw new Error(err.detail || `HTTP ${res.status}`)
  }
  return res.json()
}

export function getDiscordUser() {
  const stored = localStorage.getItem('discord_user')
  if (!stored) return null
  try {
    return JSON.parse(stored)
  } catch {
    return null
  }
}

export function setDiscordUser(user) {
  localStorage.setItem('discord_user', JSON.stringify(user))
}

export function clearDiscordUser() {
  localStorage.removeItem('discord_user')
}

export function getAvatarUrl(user) {
  if (!user) return ''
  return user.avatar
    ? `https://cdn.discordapp.com/avatars/${user.id}/${user.avatar}.png?size=128`
    : `https://cdn.discordapp.com/embed/avatars/${Number(BigInt(user.id) % 6n)}.png`
}

export const DISCORD_CLIENT_ID = import.meta.env.VITE_DISCORD_CLIENT_ID || ''
export const DISCORD_REDIRECT_URI = import.meta.env.VITE_REDIRECT_URI || 'https://gryazworld.ru/cabinet'

export async function createPayment(items) {
  const user = getDiscordUser()
  if (!user) throw new Error('Нужно войти через Discord')
  return apiFetch('/web/payments/create', {
    method: 'POST',
    body: JSON.stringify({ discord_id: user.id, items }),
  })
}
