import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Crown, Gem, Trophy } from 'lucide-react'
import { apiFetch } from '../api'
import './IchorbecsPage.css'

function mcHead(nickname, size = 64) {
  return `https://mc-heads.net/avatar/${encodeURIComponent(nickname)}/${size}`
}

function fmtBalance(n) {
  return Math.round(n).toLocaleString('ru-RU')
}

const MEDAL = { 1: '#ffd700', 2: '#c0c0c0', 3: '#cd7f32' }
const PODIUM_ORDER = [1, 0, 2] // 2nd, 1st, 3rd — классическое расположение подиума

export default function IchorbecsPage() {
  const [players, setPlayers] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    apiFetch('/web/ichorbecs')
      .then(setPlayers)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const top3 = players.slice(0, 3)
  const rest = players.slice(3)
  const maxBalance = players[0]?.balance || 1
  const totalWealth = players.reduce((sum, p) => sum + p.balance, 0)

  return (
    <section className="section page-fade">
      <div className="section-label">Рейтинг</div>

      <div className="ichorbecs-hero">
        <div className="ichorbecs-hero-icon"><Crown size={28} /></div>
        <div>
          <h2>Ichorbecs</h2>
          <p>Самые богатые игроки Ichorix — по сумме баланса всех карт.</p>
        </div>
        {!loading && players.length > 0 && (
          <div className="ichorbecs-total">
            <span className="ichorbecs-total-label">Общее состояние топа</span>
            <span className="ichorbecs-total-value"><Gem size={15} /> {fmtBalance(totalWealth)}</span>
          </div>
        )}
      </div>

      {loading && <div className="ichorbecs-empty">Загрузка...</div>}
      {!loading && players.length === 0 && (
        <div className="ichorbecs-empty">Пока никто не накопил достаточно алмазов.</div>
      )}

      {top3.length > 0 && (
        <div className="ichorbecs-podium">
          {PODIUM_ORDER.filter((i) => top3[i]).map((i) => {
            const p = top3[i]
            return (
              <Link to={`/player/${encodeURIComponent(p.nickname)}`} key={p.nickname} className={`podium-card podium-${p.rank}`}>
                <div className="podium-crown"><Trophy size={p.rank === 1 ? 22 : 16} /></div>
                <img className="podium-avatar" src={mcHead(p.nickname, 80)} alt="" />
                <div className="podium-name">{p.nickname}</div>
                <div className="podium-balance"><Gem size={13} /> {fmtBalance(p.balance)}</div>
                <div className="podium-stand">#{p.rank}</div>
              </Link>
            )
          })}
        </div>
      )}

      <div className="ichorbecs-list">
        {rest.map((p) => (
          <Link to={`/player/${encodeURIComponent(p.nickname)}`} key={p.nickname} className="ichorbecs-row">
            <div
              className="ichorbecs-row-bar"
              style={{ width: `${Math.max(4, (p.balance / maxBalance) * 100)}%` }}
            />
            <div className="ichorbecs-rank">#{p.rank}</div>
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
