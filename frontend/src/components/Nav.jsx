import { NavLink, useNavigate } from 'react-router-dom'
import { useEffect, useState, useRef } from 'react'
import { Landmark, MessageCircle, Users, Scale } from 'lucide-react'
import { getDiscordUser, clearDiscordUser, clearSessionToken, getAvatarUrl, apiFetch } from '../api'
import DiscordIcon from './DiscordIcon'
import Modal from './Modal'
import './Nav.css'

const THEME_KEY = 'ichorix_theme'

function getTheme() {
  return localStorage.getItem(THEME_KEY) || 'dark'
}

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem(THEME_KEY, theme)
}

export default function Nav() {
  const navigate = useNavigate()
  const [user, setUser] = useState(getDiscordUser())
  const [mcNick, setMcNick] = useState(null)
  const [isAdmin, setIsAdmin] = useState(false)
  const [canReviewClaims, setCanReviewClaims] = useState(false)
  const [unreadMsgs, setUnreadMsgs] = useState(0)
  const [menuOpen, setMenuOpen] = useState(false)
  const [userMenuOpen, setUserMenuOpen] = useState(false)
  const [theme, setTheme] = useState(getTheme())
  const [comingSoon, setComingSoon] = useState(null) // null | { title, desc }
  const menuRef = useRef(null)

  useEffect(() => { applyTheme(theme) }, [theme])

  useEffect(() => {
    const handler = () => setUser(getDiscordUser())
    window.addEventListener('auth-change', handler)
    return () => window.removeEventListener('auth-change', handler)
  }, [])

  useEffect(() => {
    if (!user) return
    apiFetch('/web/me')
      .then(p => { if (p.nickname) setMcNick(p.nickname); setIsAdmin(!!p.is_admin) })
      .catch(() => {})
    apiFetch('/web/court/permissions')
      .then(p => setCanReviewClaims(!!p.can_review))
      .catch(() => {})
  }, [user])

  useEffect(() => {
    if (!user) { setUnreadMsgs(0); return }
    const load = () => {
      apiFetch('/web/messenger/unread-count')
        .then(d => setUnreadMsgs(d.count || 0))
        .catch(() => {})
    }
    load()
    const id = setInterval(load, 20000)
    return () => clearInterval(id)
  }, [user])

  useEffect(() => {
    function onClickOutside(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) setUserMenuOpen(false)
    }
    document.addEventListener('mousedown', onClickOutside)
    return () => document.removeEventListener('mousedown', onClickOutside)
  }, [])

  function logout() {
    clearSessionToken()
    clearDiscordUser()
    setUser(null)
    setMcNick(null)
    setUserMenuOpen(false)
    window.dispatchEvent(new Event('auth-change'))
  }

  function openComingSoon(title, desc) {
    setComingSoon({ title, desc })
  }

  return (
    <>
    <nav className="nav">
      <div className="nav-logo" onClick={() => navigate('/')}>
        <img src="/logo.png" alt="Ichorix" />
      </div>

      {/* Десктоп ссылки */}
      <ul className="nav-links">
        <li><NavLink to="/">Главная</NavLink></li>
        <li><NavLink to="/shop">Магазин</NavLink></li>
        <li><NavLink to="/wiki">Wiki</NavLink></li>
        <li><NavLink to="/stats">Статистика</NavLink></li>
        {/* HIDDEN: до решения команды
        <li><NavLink to="/communities">Общины</NavLink></li>
        */}
        <li><NavLink to="/map">Карта</NavLink></li>
      </ul>

      {/* Десктоп правая часть */}
      <div className="nav-right">
        {user && (
          <>
            <button className="nav-feature-btn" title="Банк" onClick={() => navigate('/bank')}>
              <Landmark size={18} strokeWidth={1.75} />
            </button>
            {canReviewClaims && (
              <button className="nav-feature-btn" title="Суд" onClick={() => navigate('/court')}>
                <Scale size={18} strokeWidth={1.75} />
              </button>
            )}
            <button className="nav-feature-btn" title="Сообщения" onClick={() => navigate('/messenger')}>
              <MessageCircle size={18} strokeWidth={1.75} />
              {unreadMsgs > 0 && <span className="nav-feature-badge" />}
            </button>
            <button
              className="nav-feature-btn"
              title="Общины"
              onClick={() => openComingSoon('Общины', 'Раздел общин пока в разработке.')}
            >
              <Users size={18} strokeWidth={1.75} />
            </button>
          </>
        )}

        {user ? (
          <div className="user-menu-wrap" ref={menuRef}>
            <div className="user-link" onClick={() => setUserMenuOpen(o => !o)}>
              <img src={getAvatarUrl(user)} alt="" className="user-avatar" />
              <span className={`user-chevron ${userMenuOpen ? 'open' : ''}`}>▾</span>
            </div>

            {userMenuOpen && (
              <div className="user-dropdown">
                <div className="user-dropdown-head">
                  <img src={getAvatarUrl(user)} alt="" className="user-dropdown-avatar" />
                  <div>
                    <div className="user-dropdown-name">{mcNick || user.global_name || user.username}</div>
                    <div className="user-dropdown-link" onClick={() => { setUserMenuOpen(false); navigate('/cabinet') }}>Открыть профиль →</div>
                  </div>
                </div>
                <div className="user-dropdown-row">
                  <span>Тёмная тема</span>
                  <div
                    className={`theme-switch ${theme === 'dark' ? 'on' : ''}`}
                    onClick={() => setTheme(t => t === 'dark' ? 'light' : 'dark')}
                  >
                    <div className="theme-switch-thumb" />
                  </div>
                </div>
                <div className="user-dropdown-item" onClick={() => navigate('/shop')}>Магазин</div>
                {isAdmin && (
                  <div className="user-dropdown-item" onClick={() => { setUserMenuOpen(false); navigate('/admin') }}>Админ-панель</div>
                )}
                <div className="user-dropdown-item danger" onClick={logout}>Выйти из аккаунта</div>
              </div>
            )}
          </div>
        ) : (
          <button className="btn btn-discord" onClick={() => navigate('/cabinet')}>
            <DiscordIcon size={18} />
            Войти
          </button>
        )}
      </div>

      {/* Мобильная кнопка бургер */}
      <button className="nav-burger" onClick={() => setMenuOpen(o => !o)}>
        <span /><span /><span />
      </button>

    </nav>

      {/* Мобильное меню — вне <nav> чтобы не наследовать stacking context */}
      {menuOpen && (
        <div className="nav-mobile" onClick={() => setMenuOpen(false)}>
          <div className="nav-mobile-top">
            {!user && (
              <button className="btn btn-discord" onClick={() => navigate('/cabinet')}>
                <DiscordIcon size={18} />
                Авторизоваться
              </button>
            )}
          </div>
          <ul className="nav-mobile-links">
            {user && (
              <li>
                <NavLink to="/cabinet" className="nav-mobile-cabinet">
                  Личный кабинет
                </NavLink>
              </li>
            )}
            <li><NavLink to="/">Главная</NavLink></li>
            <li><NavLink to="/shop">Магазин</NavLink></li>
            <li><NavLink to="/wiki">Wiki</NavLink></li>
            <li><NavLink to="/stats">Статистика</NavLink></li>
            {/* HIDDEN: до решения команды
            <li><NavLink to="/communities">Общины</NavLink></li>
            */}
            <li><NavLink to="/map">Карта</NavLink></li>
          </ul>
          {user && (
            <button className="btn btn-ghost nav-mobile-logout" onClick={logout}>Выйти</button>
          )}
        </div>
      )}

      {/* Мессенджер / Общины — заглушка "скоро" */}
      <Modal open={!!comingSoon} onClose={() => setComingSoon(null)}>
        <h2>{comingSoon?.title}</h2>
        <p>{comingSoon?.desc}</p>
      </Modal>
    </>
  )
}
