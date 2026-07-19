import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Crown, Gem } from 'lucide-react'
import { apiFetch } from '../api'
import './IchorbecsPage.css'

function mcHead(nickname, size = 64) {
  return `https://mc-heads.net/avatar/${encodeURIComponent(nickname)}/${size}`
}

function fmtBalance(n) {
  return Math.round(n).toLocaleString('ru-RU')
}

const MEDAL = { 1: '#ffd700', 2: '#c0c0c0', 3: '#cd7f32' }

export default function IchorbecsPage() {
  const [players, setPlayers] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    apiFetch('/web/ichorbecs')
      .then(setPlayers)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  return (
    <section className="section page-fade">
      <div className="section-label">Рейтинг</div>

      <div className="ichorbecs-hero">
        <div className="ichorbecs-hero-icon"><Crown size={28} /></div>
        <div>
          <h2>Ichorbecs</h2>
          <p>Самые богатые игроки Ichorix — по сумме баланса всех карт.</p>
        </div>
      </div>

      {loading && <div className="ichorbecs-empty">Загрузка...</div>}
      {!loading && players.length === 0 && (
        <div className="ichorbecs-empty">Пока никто не накопил достаточно алмазов.</div>
      )}

      <div className="ichorbecs-list">
        {players.map((p) => (
          <Link to={`/player/${encodeURIComponent(p.nickname)}`} key={p.nickname} className={`ichorbecs-row rank-${p.rank}`}>
            <div className="ichorbecs-rank" style={MEDAL[p.rank] ? { color: MEDAL[p.rank] } : undefined}>
              {MEDAL[p.rank] ? <Crown size={16} /> : `#${p.rank}`}
            </div>
            <img className="ichorbecs-avatar" src={mcHead(p.nickname, 48)} alt="" />
            <div className="ichorbecs-name">{p.nickname}</div>
            <div className="ichorbecs-balance">
              <Gem size={14} className="ichorbecs-gem" /> {fmtBalance(p.balance)}
            </div>
          </Link>
        ))}
      </div>
    </section>
  )
}
