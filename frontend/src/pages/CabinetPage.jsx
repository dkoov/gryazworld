import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  apiFetch,
  getDiscordUser,
  setDiscordUser,
  clearDiscordUser,
  getAvatarUrl,
  setSessionToken,
  clearSessionToken,
  parseOauthState,
  verifyOauthNonce,
  consumePendingReturn,
  clearOauthInFlight,
  redirectToDiscordOauth,
} from '../api'
import DiscordIcon from '../components/DiscordIcon'
import './CabinetPage.css'

export default function CabinetPage() {
  const navigate = useNavigate()
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
    return apiFetch('/web/discord/token', {
      method: 'POST',
      body: JSON.stringify({ code }),
    })
  }

  async function loadProfile() {
    return apiFetch('/web/me')
  }

  useEffect(() => {
    async function init() {
      const params = new URLSearchParams(window.location.search)
      const code = params.get('code')
      const stateParam = params.get('state')

      if (code) {
        window.history.replaceState({}, '', window.location.pathname)

        const parsed = parseOauthState(stateParam)
        if (!parsed || !verifyOauthNonce(parsed.n)) {
          clearOauthInFlight()
          addAlert('Ошибка безопасности: state не совпадает')
          setScreen('login')
          return
        }

        setScreen('loading')
        try {
          const res = await exchangeCode(code)
          setSessionToken(res.token)
          // user-объект для UI (avatar, global_name) — без чувствительных полей.
          const u = {
            id: res.id,
            username: res.username,
            avatar: res.avatar,
            global_name: res.global_name,
          }
          setDiscordUser(u)
          setUser(u)
          clearOauthInFlight()
          window.dispatchEvent(new Event('auth-change'))

          const p = await loadProfile()
          setProfile(p)
          setScreen(p.linked ? 'profile' : 'link')

          const returnTo = parsed.r
          if (returnTo && returnTo !== '/cabinet' && returnTo.startsWith('/')) {
            navigate(returnTo, { replace: true })
          }
        } catch (e) {
          clearOauthInFlight()
          addAlert(e.message)
          setScreen('login')
        }
        return
      }

      // Если apiFetch ранее словил 401 на другой странице — нас сюда редиректнули
      // с pending_return_to. Запускаем OAuth и просим Discord вернуть на исходный URL.
      const pendingReturn = consumePendingReturn()
      const stored = getDiscordUser()

      if (stored) {
        setScreen('loading')
        try {
          setUser(stored)
          const p = await loadProfile()
          setProfile(p)
          setScreen(p.linked ? 'profile' : 'link')
        } catch (e) {
          // 401 уже обработан в apiFetch (он сам редиректит). Сюда падают только не-401 ошибки.
          addAlert(e.message)
          setScreen('login')
        }
        return
      }

      if (pendingReturn) {
        redirectToDiscordOauth(pendingReturn)
        return
      }

      setScreen('login')
    }

    init()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function loginWithDiscord() {
    const returnTo = window.location.pathname + window.location.search
    redirectToDiscordOauth(returnTo === '/cabinet' ? '/cabinet' : returnTo)
  }

  async function linkAccount() {
    setLinkError('')
    if (!linkNick.trim()) { setLinkError('Введи Minecraft ник'); return }
    if (!user) { setLinkError('Не авторизован'); return }
    setLinking(true)
    try {
      await apiFetch('/web/link', {
        method: 'POST',
        body: JSON.stringify({ minecraft_nick: linkNick.trim() }),
      })
      addAlert('Аккаунт успешно привязан!', 'success')
      const p = await loadProfile()
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
    clearSessionToken()
    clearDiscordUser()
    setUser(null)
    setProfile(null)
    setScreen('login')
    window.dispatchEvent(new Event('auth-change'))
    addAlert('Вы вышли из аккаунта', 'info')
  }

  async function payFine(fineId) {
    try {
      await apiFetch('/web/pay-fine', {
        method: 'POST',
        body: JSON.stringify({ fine_id: fineId }),
      })
      const p = await loadProfile()
      setProfile(p)
    } catch (e) { alert(e.message) }
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
                  <div className="stat-value">{Math.round(profile.balance)}</div>
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
                        <div className="fine-main">
                          <div className="fine-reason">{f.reason}</div>
                          <div className="fine-meta">
                            {f.deadline ? (() => {
                              const diff = new Date(f.deadline) - new Date()
                              if (diff <= 0) return 'Срок истёк'
                              const hours = Math.floor(diff / 1000 / 60 / 60)
                              const minutes = Math.floor((diff / 1000 / 60) % 60)
                              return hours > 0 ? `Осталось: ${hours} ч. ${minutes} мин.` : `Осталось: ${minutes} мин.`
                            })() : 'Без срока'}
                          </div>
                        </div>
                        <div className="fine-right">
                          <div className="fine-amount">{f.amount} алмазов</div>
                          <button className="fine-pay-btn" onClick={() => payFine(f.id)}>Оплатить</button>
                        </div>
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
