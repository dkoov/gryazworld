import { useEffect, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { Users, Boxes, Sprout, Clock, Check, Copy } from 'lucide-react'
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
    .sort((a, b) => (b.is_online - a.is_online) || (b.total_seconds - a.total_seconds))

  function copyIp() {
    navigator.clipboard.writeText('ichorix.ru').then(() => {
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    })
  }

  return (
    <section className="section page-fade">
      <div className="section-label">Статистика</div>

      <div className="stats-hero">
        <div className="server-avatar">
          <img src="/logo.png" alt="Ichorix" />
        </div>
        <div className="server-info">
          <h2>Ichorix</h2>
          <div className="ip-box">
            <span className="ip-text">ichorix.ru</span>
            <button className={`ip-copy ${copied ? 'copied' : ''}`} onClick={copyIp}>
              {copied ? <Check size={13} /> : <Copy size={13} />}
              {copied ? 'Скопировано' : 'Копировать'}
            </button>
          </div>
        </div>
      </div>

      <div className="server-cards">
        <div className="server-cell" style={{ animationDelay: '0.02s' }}>
          <div className="server-cell-icon total"><Users size={18} /></div>
          <div className="server-cell-body">
            <div className="server-cell-name">Всего онлайн</div>
            <div className="server-cell-online">
              <span className="online-dot" />{serverStats.online ?? 0}
            </div>
          </div>
        </div>
        <div className="server-cell" style={{ animationDelay: '0.08s' }}>
          <div className="server-cell-icon builds"><Boxes size={18} /></div>
          <div className="server-cell-body">
            <div className="server-cell-name">Мир построек</div>
            <div className="server-cell-online">
              <span className="online-dot" />{serverStats.servers?.gamegraz?.online ?? 0}
            </div>
          </div>
        </div>
        <div className="server-cell" style={{ animationDelay: '0.14s' }}>
          <div className="server-cell-icon farms"><Sprout size={18} /></div>
          <div className="server-cell-body">
            <div className="server-cell-name">Мир ферм</div>
            <div className="server-cell-online">
              <span className="online-dot" />{serverStats.servers?.farmserv?.online ?? 0}
            </div>
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
        <div className="server-tabs">
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

      <div className="players-grid">
        {filtered.length === 0 ? (
          <div className="no-players">
            {tab === 'online' ? 'Никого нет онлайн' : 'Нет данных'}
          </div>
        ) : (
          filtered.map((p, i) => (
            <PlayerCard key={p.nickname} player={p} delay={Math.min(i, 24) * 0.03} />
          ))
        )}
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

function PlayerCard({ player, delay }) {
  const skinUrl = `https://mc-heads.net/avatar/${player.nickname}/64`

  return (
    <Link
      to={`/player/${encodeURIComponent(player.nickname)}`}
      className={`player-card ${player.is_online ? 'online' : ''}`}
      style={{ animationDelay: `${delay}s` }}
    >
      <img
        src={skinUrl}
        alt={player.nickname}
        className="player-skin"
        onError={e => { e.target.src = 'https://mc-heads.net/avatar/Steve/64' }}
      />
      <div className="player-card-body">
        <div className="player-name">
          {player.is_online && <span className="online-dot" />}
          {player.nickname}
          {player.is_online && player.server && (
            <span className="player-server">{player.server}</span>
          )}
        </div>
        <div className="player-hours">
          <Clock size={11} className="player-hours-icon" />
          <strong>{formatTime(player.total_seconds)}</strong>
        </div>
      </div>
    </Link>
  )
}
