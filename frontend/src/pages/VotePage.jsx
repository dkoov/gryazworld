import { useEffect, useState } from 'react'
import { ArrowLeft, Check, Clock, Trophy, Users } from 'lucide-react'
import { apiFetch, getDiscordUser } from '../api'
import './VotePage.css'

const COLORS = ['#8b5cf6', '#3b82f6', '#22c55e', '#f59e0b', '#ef4444', '#ec4899', '#14b8a6', '#a3a3a3']

function mcHead(nickname, size = 40) {
  return `https://mc-heads.net/avatar/${encodeURIComponent(nickname)}/${size}`
}

function formatDateTime(iso) {
  return new Date(iso).toLocaleString('ru-RU', { day: 'numeric', month: 'long', hour: '2-digit', minute: '2-digit' })
}

function DonutChart({ candidates, size = 168 }) {
  const total = candidates.reduce((s, c) => s + c.votes, 0)
  let cursor = 0
  const stops = candidates.map((c, i) => {
    const frac = total ? c.votes / total : 0
    const start = cursor * 360
    cursor += frac
    const end = cursor * 360
    return `${COLORS[i % COLORS.length]} ${start}deg ${end}deg`
  })
  const gradient = total ? `conic-gradient(${stops.join(', ')})` : 'conic-gradient(var(--border2) 0deg 360deg)'

  return (
    <div className="vote-donut" style={{ width: size, height: size }}>
      <div className="vote-donut-ring" style={{ background: gradient }} />
      <div className="vote-donut-hole">
        <span className="vote-donut-total">{total}</span>
        <small>{total === 1 ? 'голос' : 'голосов'}</small>
      </div>
    </div>
  )
}

export default function VotePage() {
  const [polls, setPolls] = useState(null)
  const [error, setError] = useState('')
  const [selectedId, setSelectedId] = useState(null)
  const [detail, setDetail] = useState(null)
  const [voting, setVoting] = useState(false)
  const loggedIn = !!getDiscordUser()

  useEffect(() => {
    apiFetch('/web/polls').then(setPolls).catch(e => setError(e.message))
  }, [])

  useEffect(() => {
    if (selectedId == null) { setDetail(null); return }
    apiFetch(`/web/polls/${selectedId}`).then(setDetail).catch(e => setError(e.message))
  }, [selectedId])

  function refreshDetail() {
    if (selectedId == null) return
    apiFetch(`/web/polls/${selectedId}`).then(setDetail).catch(e => setError(e.message))
  }

  async function castVote(candidateId) {
    if (!detail) return
    setVoting(true)
    setError('')
    try {
      await apiFetch(`/web/polls/${detail.id}/vote`, {
        method: 'POST',
        body: JSON.stringify({ candidate_id: candidateId }),
      })
      refreshDetail()
      apiFetch('/web/polls').then(setPolls).catch(() => {})
    } catch (e) {
      setError(e.message)
    } finally {
      setVoting(false)
    }
  }

  if (selectedId != null) {
    return (
      <section className="section page-fade vote-page">
        <button className="vote-back" onClick={() => setSelectedId(null)}>
          <ArrowLeft size={15} /> Все голосования
        </button>

        {!detail && <div className="vote-empty">Загрузка...</div>}

        {detail && (
          <div className="vote-detail">
            <div className="vote-detail-head">
              <h2>{detail.title}</h2>
              <div className="vote-detail-meta">
                <span className={`vote-badge ${detail.is_open ? 'open' : 'closed'}`}>
                  {detail.is_open ? 'Идёт голосование' : 'Завершено'}
                </span>
                {detail.deadline && (
                  <span className="vote-meta-item"><Clock size={13} /> до {formatDateTime(detail.deadline)}</span>
                )}
                <span className="vote-meta-item"><Users size={13} /> {detail.total_votes} голосов</span>
              </div>
            </div>

            {error && <div className="vote-error">{error}</div>}

            <div className="vote-detail-body">
              <DonutChart candidates={detail.candidates} />

              <div className="vote-candidates">
                {[...detail.candidates].sort((a, b) => b.votes - a.votes).map((c, i) => {
                  const origIndex = detail.candidates.findIndex(x => x.id === c.id)
                  const isVoted = detail.voted_candidate_id === c.id
                  const isWinner = !detail.is_open && detail.total_votes > 0 && i === 0
                  const canVote = detail.is_open && detail.voted_candidate_id == null

                  return (
                    <div className={`vote-cand-row ${isVoted ? 'voted' : ''}`} key={c.id}>
                      <div className="vote-cand-bar" style={{ width: `${c.percent}%`, background: COLORS[origIndex % COLORS.length] }} />
                      <img className="vote-cand-avatar" src={mcHead(c.nickname)} alt="" />
                      <div className="vote-cand-name">
                        {c.nickname}
                        {isWinner && <Trophy size={13} className="vote-cand-winner-icon" />}
                        {isVoted && <Check size={13} className="vote-cand-voted-icon" />}
                      </div>
                      <div className="vote-cand-stats">{c.votes} · {c.percent}%</div>
                      {canVote && (
                        <button
                          className="vote-cand-btn"
                          disabled={voting || !loggedIn}
                          onClick={() => castVote(c.id)}
                        >
                          Голосовать
                        </button>
                      )}
                    </div>
                  )
                })}
              </div>
            </div>

            {!loggedIn && detail.is_open && (
              <div className="vote-login-hint">Войди через Discord в личном кабинете, чтобы проголосовать.</div>
            )}
            {loggedIn && detail.is_open && detail.voted_candidate_id != null && (
              <div className="vote-voted-hint"><Check size={14} /> Ты уже проголосовал в этом опросе.</div>
            )}
          </div>
        )}
      </section>
    )
  }

  return (
    <section className="section page-fade vote-page">
      <div className="section-label">Голосование</div>
      <h1 className="page-title">Голосования <em>сообщества</em></h1>

      {error && <div className="vote-error">{error}</div>}
      {polls === null && <div className="vote-empty">Загрузка...</div>}
      {polls !== null && polls.length === 0 && (
        <div className="vote-empty vote-empty-big">Сейчас нет активных голосований.</div>
      )}

      <div className="vote-grid">
        {polls?.map(poll => (
          <div className="vote-card" key={poll.id} onClick={() => setSelectedId(poll.id)}>
            <div className="vote-card-head">
              <span className={`vote-badge ${poll.is_open ? 'open' : 'closed'}`}>
                {poll.is_open ? 'Идёт' : 'Завершено'}
              </span>
              {poll.voted_candidate_id != null && <span className="vote-badge voted"><Check size={11} /> Голос учтён</span>}
            </div>
            <div className="vote-card-title">{poll.title}</div>
            <div className="vote-card-cands">
              {poll.candidates.slice(0, 4).map(c => (
                <img key={c.id} className="vote-card-avatar" src={mcHead(c.nickname, 32)} alt="" title={c.nickname} />
              ))}
              {poll.candidates.length > 4 && <span className="vote-card-more">+{poll.candidates.length - 4}</span>}
            </div>
            <div className="vote-card-footer">
              <span><Users size={12} /> {poll.total_votes}</span>
              {poll.deadline && <span><Clock size={12} /> до {formatDateTime(poll.deadline)}</span>}
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}
