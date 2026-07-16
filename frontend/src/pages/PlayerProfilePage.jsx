import { useEffect, useState, useCallback } from 'react'
import { useParams } from 'react-router-dom'
import { apiFetch, getDiscordUser, getSessionToken } from '../api'
import './PlayerProfilePage.css'

function formatHours(seconds) {
  return (seconds / 3600).toFixed(1)
}

const DAY_MS = 86400000
const MONTH_LABELS = ['Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек']

function levelFor(seconds) {
  if (!seconds) return 0
  if (seconds < 30 * 60) return 1
  if (seconds < 2 * 3600) return 2
  if (seconds < 5 * 3600) return 3
  return 4
}

function buildHeatmapWeeks(heatmap) {
  const byDay = new Map(heatmap.map(h => [h.day, h.seconds]))
  const today = new Date()
  today.setUTCHours(0, 0, 0, 0)
  const start = new Date(today.getTime() - 363 * DAY_MS)
  // align to the previous Monday so weeks are full columns
  const startDow = (start.getUTCDay() + 6) % 7
  start.setTime(start.getTime() - startDow * DAY_MS)

  const weeks = []
  const monthMarks = []
  let cursor = new Date(start)
  let weekIdx = 0
  let lastMonth = -1
  while (cursor <= today) {
    const week = []
    for (let d = 0; d < 7; d++) {
      const key = cursor.toISOString().slice(0, 10)
      week.push({ day: key, seconds: byDay.get(key) || 0, future: cursor > today })
      if (d === 0 && cursor.getUTCMonth() !== lastMonth) {
        lastMonth = cursor.getUTCMonth()
        monthMarks.push({ weekIdx, label: MONTH_LABELS[lastMonth] })
      }
      cursor = new Date(cursor.getTime() + DAY_MS)
    }
    weeks.push(week)
    weekIdx++
  }
  return { weeks, monthMarks }
}

function timeAgo(iso) {
  const diff = Date.now() - new Date(iso).getTime()
  const day = 86400000
  if (diff < day) return 'сегодня'
  if (diff < 2 * day) return 'вчера'
  const days = Math.floor(diff / day)
  if (days < 30) return `${days} дн. назад`
  const months = Math.floor(days / 30)
  if (months < 12) return `${months} мес. назад`
  const years = Math.floor(months / 12)
  return `${years} г. назад`
}

export default function PlayerProfilePage() {
  const { nickname } = useParams()
  const [profile, setProfile] = useState(null)
  const [notFound, setNotFound] = useState(false)
  const [liking, setLiking] = useState(false)
  const me = getDiscordUser()

  const load = useCallback(() => {
    apiFetch(`/web/player/${encodeURIComponent(nickname)}`)
      .then(p => { setProfile(p); setNotFound(false) })
      .catch(() => setNotFound(true))
  }, [nickname])

  useEffect(() => { load() }, [load])

  async function toggleLike() {
    if (!getSessionToken()) {
      window.location.href = '/cabinet'
      return
    }
    setLiking(true)
    try {
      const res = await apiFetch(`/web/player/${encodeURIComponent(nickname)}/like`, { method: 'POST' })
      setProfile(p => ({ ...p, liked_by_me: res.liked, likes_count: res.likes_count }))
    } catch (e) {
      // тихо игнорируем — например, попытка лайкнуть самого себя
    } finally {
      setLiking(false)
    }
  }

  if (notFound) {
    return (
      <section className="section">
        <div className="pp-card pp-empty">Игрок «{nickname}» не найден</div>
      </section>
    )
  }

  if (!profile) {
    return (
      <section className="section">
        <div className="pp-card pp-empty">Загрузка...</div>
      </section>
    )
  }

  const isSelf = me && profile.discord_id === me.id
  const { weeks, monthMarks } = buildHeatmapWeeks(profile.heatmap || [])
  const monthLabelByWeek = new Map(monthMarks.map(m => [m.weekIdx, m.label]))

  return (
    <section className="section pp-page">
      <div className="pp-card pp-header">
        <div className="pp-skin-box">
          <iframe
            className="pp-skin-frame"
            title="Minecraft skin"
            src={`https://vzge.me/embed/full/${encodeURIComponent(profile.nickname)}`}
            frameBorder="0"
          />
        </div>
        <div className="pp-header-info">
          <div className="pp-name-row">
            <span className={`pp-online-dot ${profile.is_online ? 'is-online' : ''}`} />
            <h1 className="pp-name">{profile.nickname}</h1>
          </div>
          <div className="pp-status-text">
            {profile.is_online ? `На сервере (${profile.server})` : 'Не в сети'}
          </div>
          <div className="pp-badges">
            {profile.discord_id && (
              <a
                className="pp-badge pp-badge-discord"
                href={`https://discord.com/users/${profile.discord_id}`}
                target="_blank" rel="noreferrer"
              >
                Discord
              </a>
            )}
          </div>
          {!isSelf && (
            <button
              className={`pp-like-btn ${profile.liked_by_me ? 'liked' : ''}`}
              disabled={liking}
              onClick={toggleLike}
            >
              {profile.liked_by_me ? '♥' : '♡'} {profile.likes_count}
            </button>
          )}
          {isSelf && <div className="pp-likes-self">♥ {profile.likes_count}</div>}
        </div>
      </div>

      <div className="pp-card">
        <div className="pp-section-title">Подписка</div>
        <div className="pp-sub-row">
          <div>
            <div className="pp-sub-name">IchoPlus</div>
            {profile.subscription ? (
              <div className="pp-sub-status pp-sub-active">
                Активна до {new Date(profile.subscription.expires_at).toLocaleDateString('ru-RU')}
              </div>
            ) : (
              <div className="pp-sub-status">Не куплена</div>
            )}
          </div>
          {!profile.subscription && isSelf && (
            <a href="/shop" className="pp-sub-buy">Купить подписку</a>
          )}
        </div>
      </div>

      <div className="pp-card">
        <div className="pp-section-title">Статистика</div>
        <div className="pp-stats-grid">
          <div className="pp-stat">
            <div className="pp-stat-label">Наиграно</div>
            <div className="pp-stat-value">{formatHours(profile.total_seconds)} ч.</div>
          </div>
          <div className="pp-stat">
            <div className="pp-stat-label">Месяц</div>
            <div className="pp-stat-value">{formatHours(profile.playtime_month)} ч.</div>
          </div>
          <div className="pp-stat">
            <div className="pp-stat-label">Неделя</div>
            <div className="pp-stat-value">{formatHours(profile.playtime_week)} ч.</div>
          </div>
          <div className="pp-stat">
            <div className="pp-stat-label">Сегодня</div>
            <div className="pp-stat-value">{formatHours(profile.playtime_today)} ч.</div>
          </div>
        </div>
      </div>

      <div className="pp-card">
        <div className="pp-section-title">Активность за год</div>
        <div className="pp-heatmap-scroll">
          <div className="pp-heatmap">
            <div className="pp-heatmap-months">
              {weeks.map((_, wi) => (
                <span key={wi} className="pp-heatmap-month-cell">{monthLabelByWeek.get(wi) || ''}</span>
              ))}
            </div>
            <div className="pp-heatmap-grid">
              {weeks.map((week, wi) => (
                <div key={wi} className="pp-heatmap-week">
                  {week.map(d => (
                    <div
                      key={d.day}
                      className={`pp-heatmap-cell ${d.future ? 'pp-heatmap-future' : `pp-lvl-${levelFor(d.seconds)}`}`}
                      title={d.future ? '' : `${d.day}: ${(d.seconds / 3600).toFixed(1)} ч.`}
                    />
                  ))}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      <div className="pp-card">
        <div className="pp-section-title">Персонажи{profile.characters ? ` (${profile.characters.length})` : ''}</div>
        {profile.characters === null ? (
          <div className="pp-empty-inline">Игровой сервер сейчас недоступен</div>
        ) : profile.characters.length === 0 ? (
          <div className="pp-empty-inline">Персонажей нет</div>
        ) : (
          <div className="pp-characters-list">
            {profile.characters.map(c => (
              <div key={c.char_id} className={`pp-character-row ${c.is_active ? 'is-active' : ''}`}>
                <span className="pp-character-idx">#{c.char_id}</span>
                <span className="pp-character-name">{c.name}</span>
                {c.is_active && <span className="pp-character-active">выбран</span>}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="pp-card">
        <div className="pp-section-title">Предупреждения ({profile.warns.length})</div>
        {profile.warns.length === 0 ? (
          <div className="pp-empty-inline">Предупреждений нет</div>
        ) : (
          <div className="pp-warns-list">
            {profile.warns.map((w, i) => (
              <div key={i} className="pp-warn-row">
                <div className="pp-warn-reason">{w.reason}</div>
                <div className="pp-warn-meta">
                  Выдал <span>{w.issued_by}</span> · {timeAgo(w.created_at)}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="pp-card">
        <div className="pp-section-title">Общины ({profile.communities.length})</div>
        {profile.communities.length === 0 ? (
          <div className="pp-empty-inline">Не состоит ни в одной общине</div>
        ) : (
          <div className="pp-communities-grid">
            {profile.communities.map(c => (
              <div key={c.id} className="pp-community-card">
                <span className="pp-community-icon">{c.icon}</span>
                <div>
                  <div className="pp-community-name">{c.name}</div>
                  <div className="pp-community-meta">{c.member_count} участников</div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  )
}
