import { useEffect, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { apiFetch } from '../api'
import './StatsPage.css'

export default function StatsPage() {
  const [players, setPlayers] = useState([])
  const [serverStats, setServerStats] = useState({ online: 0, servers: {} })
  const [tab, setTab] = useState('all')
  const [serverTab, setServerTab] = useState('all')
  const [search, setSearch] = useState('')
  const [copied, setCopied] = useState(false)

  const load = useCallback(() => {
    apiFetch('/web/stats').then(setPlayers).catch(() => {})
    apiFetch('/web/server-stats').then(setServerStats).catch(() => {})
  }, [])

  useEffect(() => {
    load()
    const id = setInterval(load, 30000)
    return () => clearInterval(id)
  }, [load])

  const serverNames = [...new Set(players.map(p => p.server).filter(Boolean))]

  const filtered = players
    .filter(p => tab === 'online' ? p.is_online : true)
    .filter(p => serverTab === 'all' || p.server === serverTab)
    .filter(p => p.nickname.toLowerCase().includes(search.toLowerCase()))

  function copyIp() {
    navigator.clipboard.writeText('play.ichorix.cc').then(() => {
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    })
  }

  return (
    <section className="section stats-section">
      <div className="stats-inner">
        <div className="section-label">Статистика</div>

        <div className="stats-hero">
          <div className="stats-server-avatar">
            <img src="/logo-circle.png" alt="Ichorix" />
          </div>
          <div className="stats-server-info">
            <h1 className="stats-server-title">Ichorix</h1>
            <div className="stats-ip-box">
              <span className="stats-ip-text">play.ichorix.cc</span>
              <button className={`stats-ip-copy ${copied ? 'copied' : ''}`} onClick={copyIp}>
                {copied ? 'Скопировано' : 'Копировать'}
              </button>
            </div>
          </div>
        </div>

        <div className="stats-server-cards">
          <div className="stats-server-cell">
            <div className="stats-server-cell-name">Всего онлайн</div>
            <div className="stats-server-cell-online">
              <span className="stats-online-dot" />{serverStats.online ?? 0}
            </div>
          </div>
          <div className="stats-server-cell">
            <div className="stats-server-cell-name">Мир построек</div>
            <div className="stats-server-cell-online">
              <span className="stats-online-dot" />{serverStats.servers?.gamegraz?.online ?? 0}
            </div>
          </div>
          <div className="stats-server-cell">
            <div className="stats-server-cell-name">Мир ферм</div>
            <div className="stats-server-cell-online">
              <span className="stats-online-dot" />{serverStats.servers?.farmserv?.online ?? 0}
            </div>
          </div>
        </div>

        <div className="stats-controls">
          <div className="stats-tabs">
            <button className={tab === 'all' ? 'active' : ''} onClick={() => setTab('all')}>
              Все игроки
            </button>
            <button className={tab === 'online' ? 'active' : ''} onClick={() => setTab('online')}>
              Онлайн
            </button>
          </div>
          <input
            type="text"
            placeholder="Поиск по нику..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="stats-search"
          />
        </div>

        {serverNames.length > 0 && (
          <div className="stats-server-tabs">
            <button className={serverTab === 'all' ? 'active' : ''} onClick={() => setServerTab('all')}>
              Все серверы
            </button>
            {serverNames.map(name => (
              <button
                key={name}
                className={serverTab === name ? 'active' : ''}
                onClick={() => setServerTab(name)}
              >
                {name}
              </button>
            ))}
          </div>
        )}

        <div className="stats-players-grid">
          {filtered.length === 0 ? (
            <div className="stats-no-players">
              {tab === 'online' ? 'Никого нет онлайн' : 'Нет данных'}
            </div>
          ) : (
            filtered.map(p => (
              <PlayerCard key={p.nickname} player={p} />
            ))
          )}
        </div>
      </div>
    </section>
  )
}

function formatTime(total_seconds) {
  const s = total_seconds || 0
  if (s < 3600) {
    const mins = Math.floor(s / 60)
    return `${mins} мин.`
  }
  return `${(s / 3600).toFixed(1)} ч.`
}

function PlayerCard({ player }) {
  const skinUrl = `https://mc-heads.net/avatar/${player.nickname}/64`

  return (
    <Link to={`/player/${encodeURIComponent(player.nickname)}`} className="stats-player-card">
      <img
        src={skinUrl}
        alt={player.nickname}
        className="stats-player-skin"
        onError={e => { e.target.src = 'https://mc-heads.net/avatar/Steve/64' }}
      />
      <div>
        <div className="stats-player-name">
          {player.is_online && <span className="stats-online-dot" />}
          {player.nickname}
          {player.is_online && player.server && (
            <span className="stats-player-server">{player.server}</span>
          )}
        </div>
        <div className="stats-player-hours">
          Наиграл: <strong>{formatTime(player.total_seconds)}</strong>
        </div>
      </div>
    </Link>
  )
}
