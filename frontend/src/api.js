const API_BASE = import.meta.env.VITE_API_URL || ''

const SESSION_TOKEN_KEY = 'gw_session_token'
const PENDING_RETURN_KEY = 'gw_pending_return_to'
const OAUTH_NONCE_KEY = 'gw_oauth_nonce'
const OAUTH_IN_FLIGHT_KEY = 'gw_oauth_in_flight'

export function getSessionToken() {
  return localStorage.getItem(SESSION_TOKEN_KEY)
}

export function setSessionToken(token) {
  if (token) localStorage.setItem(SESSION_TOKEN_KEY, token)
}

export function clearSessionToken() {
  localStorage.removeItem(SESSION_TOKEN_KEY)
}

function b64urlEncode(str) {
  return btoa(unescape(encodeURIComponent(str))).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

function b64urlDecode(str) {
  const pad = str.length % 4 ? '='.repeat(4 - (str.length % 4)) : ''
  const b64 = str.replace(/-/g, '+').replace(/_/g, '/') + pad
  try { return decodeURIComponent(escape(atob(b64))) } catch { return null }
}

export function buildOauthState(returnTo) {
  const nonce = (crypto.randomUUID && crypto.randomUUID()) || String(Date.now()) + Math.random()
  sessionStorage.setItem(OAUTH_NONCE_KEY, nonce)
  return b64urlEncode(JSON.stringify({ n: nonce, r: returnTo || '/cabinet' }))
}

export function parseOauthState(state) {
  if (!state) return null
  const raw = b64urlDecode(state)
  if (!raw) return null
  try { return JSON.parse(raw) } catch { return null }
}

export function verifyOauthNonce(nonce) {
  const saved = sessionStorage.getItem(OAUTH_NONCE_KEY)
  sessionStorage.removeItem(OAUTH_NONCE_KEY)
  return !!saved && saved === nonce
}

export function consumePendingReturn() {
  const v = sessionStorage.getItem(PENDING_RETURN_KEY)
  sessionStorage.removeItem(PENDING_RETURN_KEY)
  return v
}

export function markOauthInFlight() {
  sessionStorage.setItem(OAUTH_IN_FLIGHT_KEY, '1')
}

export function isOauthInFlight() {
  return sessionStorage.getItem(OAUTH_IN_FLIGHT_KEY) === '1'
}

export function clearOauthInFlight() {
  sessionStorage.removeItem(OAUTH_IN_FLIGHT_KEY)
}

export function redirectToDiscordOauth(returnTo) {
  const state = buildOauthState(returnTo)
  markOauthInFlight()
  const url = new URL('https://discord.com/api/oauth2/authorize')
  url.searchParams.set('client_id', DISCORD_CLIENT_ID)
  url.searchParams.set('redirect_uri', DISCORD_REDIRECT_URI)
  url.searchParams.set('response_type', 'code')
  url.searchParams.set('scope', 'identify')
  url.searchParams.set('state', state)
  window.location.href = url.toString()
}

function handleUnauthorized() {
  // Один pending_return_to → один OAuth. Если уже летим (oauth_in_flight=1)
  // и снова словили 401 — значит после OAuth бэк опять не принимает токен
  // (битый SESSION_SECRET и т.п.). Не зацикливаемся: чистим всё и оставляем
  // юзера на /cabinet.
  clearSessionToken()
  clearDiscordUser()
  window.dispatchEvent(new Event('auth-change'))

  if (isOauthInFlight()) {
    clearOauthInFlight()
    sessionStorage.removeItem(PENDING_RETURN_KEY)
    if (window.location.pathname !== '/cabinet') {
      window.location.href = '/cabinet'
    }
    return
  }

  const returnTo = window.location.pathname + window.location.search
  if (window.location.pathname !== '/cabinet') {
    sessionStorage.setItem(PENDING_RETURN_KEY, returnTo)
    window.location.href = '/cabinet'
  }
}

export async function apiFetch(path, options = {}) {
  const token = getSessionToken()
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  if (token && path.startsWith('/web/')) {
    headers['Authorization'] = `Bearer ${token}`
  }
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (res.status === 401) {
    handleUnauthorized()
    const err = await res.json().catch(() => ({}))
    throw new Error(err.detail || 'HTTP 401')
  }
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
export const DISCORD_REDIRECT_URI = import.meta.env.VITE_REDIRECT_URI || 'https://ichorix.cc/cabinet'

export async function createPayment(items) {
  if (!getSessionToken()) throw new Error('Нужно войти через Discord')
  return apiFetch('/web/payments/create', {
    method: 'POST',
    body: JSON.stringify({ items }),
  })
}
