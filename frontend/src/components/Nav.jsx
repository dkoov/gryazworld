import { NavLink, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { getDiscordUser, clearDiscordUser, getAvatarUrl, apiFetch } from '../api'
import DiscordIcon from './DiscordIcon'
import './Nav.css'

export default function Nav() {
  const navigate = useNavigate()
  const [user, setUser] = useState(getDiscordUser())
  const [online, setOnline] = useState(0)
  const [mcNick, setMcNick] = useState(null)
  const [menuOpen, setMenuOpen] = useState(false)

  useEffect(() => {
    const handler = () => setUser(getDiscordUser())
    window.addEventListener('auth-change', handler)
    return () => window.removeEventListener('auth-change', handler)
  }, [])

  useEffect(() => {
    if (!user) return
    apiFetch(`/web/profile/${user.id}`)
      .then(p => { if (p.nickname) setMcNick(p.nickname) })
      .catch(() => {})
  }, [user])

  useEffect(() => {
    const load = () => {
      apiFetch('/web/server-stats')
        .then(d => setOnline(d.online))
        .catch(() => {})
    }
    load()
    const id = setInterval(load, 30000)
    return () => clearInterval(id)
  }, [])

  function logout() {
    clearDiscordUser()
    setUser(null)
    setMcNick(null)
    window.dispatchEvent(new Event('auth-change'))
  }

  return (
    <>
    <nav className="nav">
      <div className="nav-logo" onClick={() => navigate('/')}>
        Gryaz<span>World</span>
      </div>

      {/* Десктоп ссылки */}
      <ul className="nav-links">
        <li><NavLink to="/">Главная</NavLink></li>
        <li><NavLink to="/access">Проходка</NavLink></li>
        <li><NavLink to="/shop">Магазин</NavLink></li>
        <li><NavLink to="/wiki">Wiki</NavLink></li>
        <li><NavLink to="/stats">Статистика</NavLink></li>
        <li><NavLink to="/communities">Общины</NavLink></li>
        <li><NavLink to="/map">Карта</NavLink></li>
      </ul>

      {/* Десктоп правая часть */}
      <div className="nav-right">
        <div className="status-pill">
          <span className="status-dot" />
          <span>{online} онлайн</span>
        </div>

        {user ? (
          <div className="user-info">
            <div className="user-link" onClick={() => navigate('/cabinet')}>
              <img src={getAvatarUrl(user)} alt="" className="user-avatar" />
              <span className="user-name">{mcNick || user.global_name || user.username}</span>
            </div>
            <button className="btn btn-ghost" onClick={logout}>Выйти</button>
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
            <li><NavLink to="/access">Проходка</NavLink></li>
            <li><NavLink to="/wiki">Wiki</NavLink></li>
            <li><NavLink to="/stats">Статистика</NavLink></li>
            <li><NavLink to="/communities">Общины</NavLink></li>
            <li><NavLink to="/map">Карта</NavLink></li>
          </ul>
          {user && (
            <button className="btn btn-ghost nav-mobile-logout" onClick={logout}>Выйти</button>
          )}
        </div>
      )}
    </>
  )
}
