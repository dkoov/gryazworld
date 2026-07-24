import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Siren, Gem, Clock, X } from 'lucide-react'
import {
  apiFetch,
  getDiscordUser,
  setDiscordUser,
  clearDiscordUser,
  setSessionToken,
  clearSessionToken,
  parseOauthState,
  verifyOauthNonce,
  consumePendingReturn,
  clearOauthInFlight,
  redirectToDiscordOauth,
  redirectToTwitchOauth,
} from '../api'
import DiscordIcon from '../components/DiscordIcon'
import TwitchIcon from '../components/TwitchIcon'
import PlayerProfileView from '../components/PlayerProfileView'
import './CabinetPage.css'

function fineDeadlineInfo(deadline) {
  if (!deadline) return { text: 'Без срока', urgent: false }
  const diff = new Date(deadline) - new Date()
  if (diff <= 0) return { text: 'Срок истёк', urgent: true }
  const hours = Math.floor(diff / 1000 / 60 / 60)
  const minutes = Math.floor((diff / 1000 / 60) % 60)
  const text = hours > 0 ? `Осталось: ${hours} ч. ${minutes} мин.` : `Осталось: ${minutes} мин.`
  return { text, urgent: hours < 3 }
}

export default function CabinetPage() {
  const navigate = useNavigate()
  const [screen, setScreen] = useState('login') // login | loading | link | profile
  const [user, setUser] = useState(null)
  const [account, setAccount] = useState(null) // /web/me: fines, linked
  const [richProfile, setRichProfile] = useState(null) // /web/player/{nick}: shared view data
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

  async function linkTwitchCode(code) {
    return apiFetch('/web/twitch/link', {
      method: 'POST',
      body: JSON.stringify({ code }),
    })
  }

  async function loadAccount() {
    return apiFetch('/web/me')
  }

  async function loadRichProfile(nickname) {
    return apiFetch(`/web/player/${encodeURIComponent(nickname)}`)
  }

  async function loadEverything() {
    const acc = await loadAccount()
    setAccount(acc)
    if (acc.linked) {
      const rich = await loadRichProfile(acc.nickname)
      setRichProfile(rich)
    }
    return acc
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

        if (parsed.p === 'twitch') {
          setScreen('loading')
          try {
            const res = await linkTwitchCode(code)
            addAlert(`Twitch привязан: ${res.twitch_username}`, 'success')
          } catch (e) {
            addAlert(e.message)
          } finally {
            clearOauthInFlight()
            try {
              const acc = await loadEverything()
              setScreen(acc.linked ? 'profile' : 'login')
            } catch {
              setScreen('login')
            }
          }
          return
        }

        setScreen('loading')
        try {
          const res = await exchangeCode(code)
          setSessionToken(res.token)
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

          const acc = await loadEverything()
          setScreen(acc.linked ? 'profile' : 'link')

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

      const pendingReturn = consumePendingReturn()
      const stored = getDiscordUser()

      if (stored) {
        setScreen('loading')
        try {
          setUser(stored)
          const acc = await loadEverything()
          setScreen(acc.linked ? 'profile' : 'link')
        } catch (e) {
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
      await loadEverything()
      setScreen('profile')
      window.dispatchEvent(new Event('auth-change'))
    } catch (e) {
      setLinkError(e.message)
    } finally {
      setLinking(false)
    }
  }

  function linkTwitch() {
    redirectToTwitchOauth('/cabinet')
  }

  async function unlinkTwitch() {
    try {
      await apiFetch('/web/twitch/link', { method: 'DELETE' })
      addAlert('Twitch отвязан', 'info')
      await loadEverything()
    } catch (e) {
      addAlert(e.message)
    }
  }

  function logout() {
    clearSessionToken()
    clearDiscordUser()
    setUser(null)
    setAccount(null)
    setRichProfile(null)
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
      await loadEverything()
    } catch (e) { alert(e.message) }
  }

  return (
    <div className="cabinet page-fade">
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
      {screen === 'profile' && user && richProfile && (
        <div className="cab-profile-wrap">
          <PlayerProfileView
            profile={richProfile}
            isSelf={true}
            showSkinViewer={true}
            extraActions={
              <div className="cab-extra">
                <button className="btn btn-danger cab-logout-btn" onClick={logout}>Выйти</button>
              </div>
            }
          />

          <div className="pv-card cab-twitch-card">
            <div className="pv-section-title">
              <TwitchIcon size={14} color="#9146FF" />
              Twitch
            </div>
            {account?.twitch_username ? (
              <div className="cab-twitch-linked">
                <a
                  href={`https://twitch.tv/${account.twitch_username}`}
                  target="_blank"
                  rel="noreferrer"
                  className="cab-twitch-name"
                >
                  {account.twitch_username}
                </a>
                <button className="btn btn-danger cab-twitch-unlink" onClick={unlinkTwitch} title="Отвязать">
                  <X size={14} />
                </button>
              </div>
            ) : (
              <button className="btn btn-outline" onClick={linkTwitch}>
                <TwitchIcon size={16} color="#9146FF" />
                Привязать Twitch
              </button>
            )}
          </div>

          {account?.active_fines?.length > 0 && (
            <div className="pv-card cab-fines-card">
              <div className="pv-section-title cab-fines-title">
                <Siren size={14} className="cab-fines-title-icon" />
                Активные штрафы
                <span className="cab-fines-count">{account.active_fines.length}</span>
              </div>
              <div className="fines-list">
                {account.active_fines.map((f, i) => {
                  const deadline = fineDeadlineInfo(f.deadline)
                  return (
                    <div key={f.id} className="fine-item" style={{ animationDelay: `${i * 0.05}s` }}>
                      <div className="fine-icon"><Siren size={16} /></div>
                      <div className="fine-main">
                        <div className="fine-reason">{f.reason}</div>
                        <div className={`fine-meta ${deadline.urgent ? 'urgent' : ''}`}>
                          <Clock size={11} className="fine-meta-icon" />
                          {deadline.text}
                        </div>
                      </div>
                      <div className="fine-right">
                        <div className="fine-amount"><Gem size={13} /> {Math.round(f.amount)}</div>
                        <button className="fine-pay-btn" onClick={() => payFine(f.id)}>Оплатить</button>
                      </div>
                    </div>
                  )
                })}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
