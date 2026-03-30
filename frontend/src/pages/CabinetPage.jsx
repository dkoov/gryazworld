import { useEffect, useState } from 'react'
import { apiFetch, getDiscordUser, setDiscordUser, clearDiscordUser, getAvatarUrl, DISCORD_CLIENT_ID, DISCORD_REDIRECT_URI } from '../api'
import DiscordIcon from '../components/DiscordIcon'
import './CabinetPage.css'

export default function CabinetPage() {
  const [screen, setScreen] = useState('login') // login | loading | link | profile
  const [user, setUser] = useState(null)
  const [profile, setProfile] = useState(null)
  const [alerts, setAlerts] = useState([])
  const [linkNick, setLinkNick] = useState('')
  const [linking, setLinking] = useState(false)
  const [linkError, setLinkError] = useState('')

  function addAlert(msg, type = 'error') {
    const id = Date.now()
    setAlerts(prev => [...prev, { id, msg, type }])
    setTimeout(() => setAlerts(prev => prev.filter(a => a.id !== id)), 5000)
  }

  async function exchangeCode(code) {
    const res = await apiFetch('/web/discord/token', {
      method: 'POST',
      body: JSON.stringify({ code }),
    })
    return res
  }

  async function loadProfile(discordId) {
    return apiFetch(`/web/profile/${discordId}`)
  }

  useEffect(() => {
    async function init() {
      const params = new URLSearchParams(window.location.search)
      const code = params.get('code')
      const state = params.get('state')

      if (code) {
        window.history.replaceState({}, '', window.location.pathname)

        const savedState = sessionStorage.getItem('oauth_state')
        if (savedState && state !== savedState) {
          addAlert('Ошибка безопасности: state не совпадает')
          setScreen('login')
          return
        }
        sessionStorage.removeItem('oauth_state')

        setScreen('loading')
        try {
          const u = await exchangeCode(code)
          setDiscordUser(u)
          setUser(u)
          window.dispatchEvent(new Event('auth-change'))

          const p = await loadProfile(u.id)
          setProfile(p)
          setScreen(p.linked ? 'profile' : 'link')
        } catch (e) {
          addAlert(e.message)
          setScreen('login')
        }
        return
      }

      const stored = getDiscordUser()
      if (stored) {
        setScreen('loading')
        try {
          setUser(stored)
          const p = await loadProfile(stored.id)
          setProfile(p)
          setScreen(p.linked ? 'profile' : 'link')
        } catch (e) {
          addAlert(e.message)
          setScreen('login')
        }
        return
      }

      setScreen('login')
    }

    init()
  }, [])

  function loginWithDiscord() {
    const state = crypto.randomUUID()
    sessionStorage.setItem('oauth_state', state)
    const url = new URL('https://discord.com/api/oauth2/authorize')
    url.searchParams.set('client_id', DISCORD_CLIENT_ID)
    url.searchParams.set('redirect_uri', DISCORD_REDIRECT_URI)
    url.searchParams.set('response_type', 'code')
    url.searchParams.set('scope', 'identify')
    url.searchParams.set('state', state)
    window.location.href = url.toString()
  }

  async function linkAccount() {
    setLinkError('')
    if (!linkNick.trim()) { setLinkError('Введи Minecraft ник'); return }
    if (!user) { setLinkError('Не авторизован'); return }
    setLinking(true)
    try {
      await apiFetch('/web/link', {
        method: 'POST',
        body: JSON.stringify({ discord_id: user.id, minecraft_nick: linkNick.trim() }),
      })
      addAlert('Аккаунт успешно привязан!', 'success')
      const p = await loadProfile(user.id)
      setProfile(p)
      setScreen('profile')
      window.dispatchEvent(new Event('auth-change'))
    } catch (e) {
      setLinkError(e.message)
    } finally {
      setLinking(false)
    }
  }

  function logout() {
    clearDiscordUser()
    setUser(null)
    setProfile(null)
    setScreen('login')
    window.dispatchEvent(new Event('auth-change'))
    addAlert('Вы вышли из аккаунта', 'info')
  }

  function escHtml(s) {
    const div = document.createElement('div')
    div.textContent = s
    return div.innerHTML
  }

  return (
    <div className="cabinet">
      <div className="alert-box">
        {alerts.map(a => (
          <div key={a.id} className={`alert alert-${a.type}`}>{a.msg}</div>
        ))}
      </div>

      <h1 className="page-title">Личный <em>кабинет</em></h1>

      {/* Login screen */}
      {screen === 'login' && (
        <div className="cab-card cab-login">
          <div className="login-icon">
            <DiscordIcon size={42} color="#5865F2" />
          </div>
          <p className="login-desc">
            Войди через Discord, чтобы получить доступ к личному кабинету —<br />
            статистике, балансу и управлению аккаунтом.
          </p>
          <button className="btn btn-discord" style={{ padding: '14px 32px', fontSize: '0.9rem' }} onClick={loginWithDiscord}>
            <DiscordIcon size={20} />
            Войти через Discord
          </button>
        </div>
      )}

      {/* Loading screen */}
      {screen === 'loading' && (
        <div className="cab-card cab-loading">
          <div className="spinner" />
          <p className="loading-text">Загрузка профиля...</p>
        </div>
      )}

      {/* Link screen */}
      {screen === 'link' && user && (
        <div className="cab-card cab-link-screen">
          <div className="link-screen-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--gold)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
              <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
            </svg>
          </div>
          <h2 className="link-screen-title">Привяжи Minecraft аккаунт</h2>
          <p className="link-screen-desc">
            Введи свой игровой ник, под которым ты играешь<br />
            на сервере и на который будешь покупать проходку
          </p>
          <div className="link-screen-input-wrap">
            <input
              type="text"
              className="link-screen-input"
              placeholder="Твой Minecraft ник"
              maxLength={32}
              value={linkNick}
              onChange={e => { setLinkNick(e.target.value); setLinkError('') }}
              onKeyDown={e => e.key === 'Enter' && linkAccount()}
            />
            <button className="btn btn-primary btn-link-submit" disabled={linking} onClick={linkAccount}>
              {linking ? 'Привязка...' : 'Привязать'}
            </button>
          </div>
          {linkError && <p className="link-screen-error">{linkError}</p>}
        </div>
      )}

      {/* Profile screen */}
      {screen === 'profile' && user && (
        <div className="cab-card cab-profile">
          {/* Discord header */}
          <div className="profile-header">
            <div className="avatar-wrap">
              <img src={getAvatarUrl(user)} alt="Avatar" />
              <div className="discord-badge">
                <DiscordIcon size={12} />
              </div>
            </div>
            <div className="profile-meta">
              <div className="profile-name">{user.global_name || user.username}</div>
              <div className="profile-tag">Discord ID: <span>{user.id}</span></div>
            </div>
          </div>

          <div className="divider" />

          {/* Stats block */}
          {profile && profile.linked && (
            <div className="block-stats">
              <div className="cab-section-title">Профиль на сервере</div>
              <div className="stats-grid-cab">
                <div className="stat-card">
                  <div className="stat-label">Minecraft ник</div>
                  <div className="stat-value" style={{ fontSize: '0.95rem' }}>{profile.nickname}</div>
                </div>
                <div className="stat-card">
                  <div className="stat-label">Часов на сервере</div>
                  <div className="stat-value">{profile.hours}</div>
                  <div className="stat-sub">{profile.minutes} мин.</div>
                </div>
                <div className="stat-card">
                  <div className="stat-label">Баланс</div>
                  <div className="stat-value">{Number(profile.balance).toFixed(1)}</div>
                  <div className="stat-sub">алмазов</div>
                </div>
                <div className="stat-card">
                  <div className="stat-label">Варны</div>
                  <div className={`stat-value ${profile.warns >= 3 ? 'danger' : profile.warns >= 2 ? 'warn' : ''}`}>
                    {profile.warns}
                  </div>
                  <div className="stat-sub">/ 3 до бана</div>
                </div>
              </div>

              <div style={{ marginTop: 28 }}>
                <div className="cab-section-title">Активные штрафы</div>
                <div className="fines-list">
                  {(!profile.active_fines || profile.active_fines.length === 0) ? (
                    <div className="empty-state">Активных штрафов нет</div>
                  ) : (
                    profile.active_fines.map(f => (
                      <div key={f.id} className="fine-item">
                        <div>
                          <div className="fine-reason">{f.reason}</div>
                          <div className="fine-meta">
                            Выдал: {f.issued_by} &middot; Срок: {f.deadline ? new Date(f.deadline).toLocaleDateString('ru-RU') : 'Без срока'}
                          </div>
                        </div>
                        <div className="fine-amount">{f.amount} &#x1F48E;</div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>
          )}

          <div className="divider" />

          <div className="logout-row">
            <button className="btn btn-danger" onClick={logout}>Выйти</button>
          </div>
        </div>
      )}
    </div>
  )
}
