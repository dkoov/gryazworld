import { useState, lazy, Suspense } from 'react'
import { useNavigate } from 'react-router-dom'
import { Clock, Calendar, CalendarDays, Flame } from 'lucide-react'
import DiscordIcon from './DiscordIcon'
import TwitchIcon from './TwitchIcon'
import { getRoleStyle } from '../roleColors'
import './PlayerProfileView.css'

const SkinViewer = lazy(() => import('./SkinViewer'))

const SERVER_NAMES = {
  lobby: 'Лобби',
  gamegraz: 'Мир построек',
  farmserv: 'Мир ферм',
  bingo: 'Бинго',
}
function serverLabel(id) {
  return SERVER_NAMES[id] || id
}

const DAY_MS = 86400000
const MONTH_LABELS = ['Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек']
const MONTH_FULL = ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь']

function formatHeatmapDate(dayKey) {
  const d = new Date(dayKey + 'T00:00:00Z')
  return `${MONTH_FULL[d.getUTCMonth()]} ${String(d.getUTCDate()).padStart(2, '0')}, ${d.getUTCFullYear()}`
}

function formatHours(seconds) {
  return (seconds / 3600).toFixed(1)
}

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

/**
 * Общий вид профиля игрока — используется и на публичной странице /player/:nickname,
 * и в личном кабинете (там же данные из /web/me, но профиль строится идентично).
 */
export default function PlayerProfileView({ profile, isSelf, onToggleLike, liking, extraActions, showSkinViewer, onLinkTwitch, onUnlinkTwitch }) {
  const navigate = useNavigate()
  const [showAllChars, setShowAllChars] = useState(false)

  const characters = profile.characters
  const shownChars = characters ? (showAllChars ? characters : characters.slice(0, 3)) : []
  const baseRoles = profile.roles && profile.roles.length > 0 ? profile.roles : [profile.role_name || 'Игрок']
  const roles = profile.is_admin
    ? ['Администратор', ...baseRoles.filter(r => r.toLowerCase() !== 'администратор')]
    : baseRoles

  return (
    <div className="pv-layout">
      <aside className="pv-sidebar">
        {showSkinViewer ? (
          <Suspense fallback={<div className="pv-avatar-card skinviewer-loading">Загрузка 3D-модели...</div>}>
            <SkinViewer nickname={profile.nickname} />
          </Suspense>
        ) : (
          <div className="pv-avatar-card">
            <img
              className="pv-avatar-img"
              src={`https://mc-heads.net/body/${encodeURIComponent(profile.nickname)}/300`}
              alt={profile.nickname}
            />
          </div>
        )}

        {!isSelf && (
          <button
            className="pv-message-btn"
            onClick={() => navigate(`/messenger?to=${encodeURIComponent(profile.nickname)}`)}
          >
            Написать сообщение
          </button>
        )}

        {(isSelf || profile.discord_id) && (
          <div className="pv-social-list">
            {profile.discord_id ? (
              <a className="pv-social-row pv-social-discord" href={`https://discord.com/users/${profile.discord_id}`} target="_blank" rel="noreferrer">
                <span className="pv-social-icon"><DiscordIcon size={16} color="#5865F2" /></span>
                <span>{profile.nickname}</span>
              </a>
            ) : isSelf && (
              <div className="pv-social-row pv-social-inactive">
                <span className="pv-social-icon"><DiscordIcon size={16} color="currentColor" /></span>
                <span>Подключить Discord</span>
              </div>
            )}
            {profile.twitch_username ? (
              <div className="pv-social-row pv-social-twitch">
                <a
                  className="pv-social-link"
                  href={`https://twitch.tv/${profile.twitch_username}`}
                  target="_blank"
                  rel="noreferrer"
                >
                  <span className="pv-social-icon"><TwitchIcon size={16} color="#9146FF" /></span>
                  <span>{profile.twitch_username}</span>
                </a>
                {isSelf && onUnlinkTwitch && (
                  <button className="pv-social-unlink" onClick={onUnlinkTwitch} title="Отвязать">×</button>
                )}
              </div>
            ) : isSelf && (
              <div className="pv-social-row pv-social-inactive pv-social-clickable" onClick={onLinkTwitch}>
                <span className="pv-social-icon"><TwitchIcon size={16} color="currentColor" /></span>
                <span>Подключить Twitch</span>
              </div>
            )}
          </div>
        )}

        {extraActions}
      </aside>

      <main className="pv-main">
        <div className="pv-header">
          <div>
            <div className="pv-name-row">
              <span className={`pv-online-dot ${profile.is_online ? 'is-online' : ''}`} />
              <h1 className="pv-name">{profile.nickname}</h1>
            </div>
            <div className="pv-status-text">
              {profile.is_online ? `На сервере (${profile.server})` : 'Не в сети'}
            </div>
          </div>
          {isSelf ? (
            <div className="pv-likes-self">♥ {profile.likes_count}</div>
          ) : (
            <button className={`pv-like-btn ${profile.liked_by_me ? 'liked' : ''}`} disabled={liking} onClick={onToggleLike}>
              {profile.liked_by_me ? '♥' : '♡'} {profile.likes_count}
            </button>
          )}
        </div>

        <div className="pv-role-row">
          {roles.map(r => {
            const style = getRoleStyle(r)
            return (
              <span
                key={r}
                className="pv-role-badge"
                style={{ color: style.color, background: style.bg, borderColor: style.color }}
              >
                {r}
              </span>
            )
          })}
        </div>

        <div className="pv-card">
          <div className="pv-section-title">Статистика</div>
          <div className="pv-stats-grid">
            <div className="pv-stat-cell" style={{ animationDelay: '0.02s' }}>
              <div className="pv-stat-icon total"><Clock size={15} /></div>
              <div className="pv-stat-body">
                <div className="pv-stat-label">Всего</div>
                <div className="pv-stat-value">{formatHours(profile.total_seconds)} ч.</div>
              </div>
            </div>
            <div className="pv-stat-cell" style={{ animationDelay: '0.06s' }}>
              <div className="pv-stat-icon month"><Calendar size={15} /></div>
              <div className="pv-stat-body">
                <div className="pv-stat-label">Месяц</div>
                <div className="pv-stat-value">{formatHours(profile.playtime_month)} ч.</div>
              </div>
            </div>
            <div className="pv-stat-cell" style={{ animationDelay: '0.1s' }}>
              <div className="pv-stat-icon week"><CalendarDays size={15} /></div>
              <div className="pv-stat-body">
                <div className="pv-stat-label">Неделя</div>
                <div className="pv-stat-value">{formatHours(profile.playtime_week)} ч.</div>
              </div>
            </div>
            <div className="pv-stat-cell" style={{ animationDelay: '0.14s' }}>
              <div className="pv-stat-icon today"><Flame size={15} /></div>
              <div className="pv-stat-body">
                <div className="pv-stat-label">Сегодня</div>
                <div className="pv-stat-value">{formatHours(profile.playtime_today)} ч.</div>
              </div>
            </div>
          </div>

          {profile.playtime_by_server && Object.keys(profile.playtime_by_server).length > 0 && (
            <div className="pv-server-breakdown">
              {Object.entries(profile.playtime_by_server)
                .sort((a, b) => b[1] - a[1])
                .map(([server, seconds]) => {
                  const max = Math.max(...Object.values(profile.playtime_by_server))
                  const pct = max > 0 ? Math.round((seconds / max) * 100) : 0
                  return (
                    <div className="pv-server-row" key={server}>
                      <span className="pv-server-row-name">{serverLabel(server)}</span>
                      <div className="pv-server-row-bar">
                        <div className="pv-server-row-fill" style={{ width: `${pct}%` }} />
                      </div>
                      <span className="pv-server-row-hours">{formatHours(seconds)} ч.</span>
                    </div>
                  )
                })}
            </div>
          )}

          <Heatmap heatmap={profile.heatmap || []} />
        </div>

        <div className="pv-card">
          <div className="pv-sub-row">
            <div>
              <div className="pv-sub-name">IchoPlus</div>
              {profile.subscription ? (
                <div className="pv-sub-status pv-sub-active">
                  Активна до {new Date(profile.subscription.expires_at).toLocaleDateString('ru-RU')}
                </div>
              ) : (
                <div className="pv-sub-status">Не куплена</div>
              )}
            </div>
            {!profile.subscription && isSelf && (
              <a href="/shop" className="pv-sub-buy">Купить подписку</a>
            )}
          </div>
        </div>

        <div className="pv-card">
          <div className="pv-section-title">Персонажи{characters ? ` (${characters.length})` : ''}</div>
          {characters === null ? (
            <div className="pv-empty-inline">Игровой сервер сейчас недоступен</div>
          ) : characters.length === 0 ? (
            <div className="pv-empty-inline">Персонажей нет</div>
          ) : (
            <>
              <div className="pv-characters-list">
                {shownChars.map(c => (
                  <div key={c.char_id} className={`pv-character-row ${c.is_active ? 'is-active' : ''}`}>
                    <span className="pv-character-idx">#{c.char_id}</span>
                    <span className="pv-character-name">{c.name}</span>
                    {c.is_active && <span className="pv-character-active">выбран</span>}
                  </div>
                ))}
              </div>
              {characters.length > 3 && (
                <button className="pv-show-more" onClick={() => setShowAllChars(v => !v)}>
                  {showAllChars ? 'Скрыть' : 'Показать больше...'}
                </button>
              )}
            </>
          )}
        </div>

        <div className="pv-card">
          <div className="pv-section-title">Предупреждения ({profile.warns.length})</div>
          {profile.warns.length === 0 ? (
            <div className="pv-empty-inline">Предупреждений нет</div>
          ) : (
            <div className="pv-warns-list">
              {profile.warns.map((w, i) => (
                <div key={i} className="pv-warn-row">
                  <div className="pv-warn-reason">{w.reason}</div>
                  <div className="pv-warn-meta">
                    Выдал <span>{w.issued_by}</span> · {timeAgo(w.created_at)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="pv-card">
          <div className="pv-section-title">Общины</div>
          <div className="pv-empty-inline">Раздел общин пока в разработке</div>
        </div>
      </main>
    </div>
  )
}

function Heatmap({ heatmap }) {
  const { weeks, monthMarks } = buildHeatmapWeeks(heatmap)
  const monthLabelByWeek = new Map(monthMarks.map(m => [m.weekIdx, m.label]))
  return (
    <div className="pv-heatmap-block">
      <div className="pv-heatmap-scroll">
        <div className="pv-heatmap">
          <div className="pv-heatmap-months">
            {weeks.map((_, wi) => (
              <span key={wi} className="pv-heatmap-month-cell">{monthLabelByWeek.get(wi) || ''}</span>
            ))}
          </div>
          <div className="pv-heatmap-grid">
            {weeks.map((week, wi) => (
              <div key={wi} className="pv-heatmap-week">
                {week.map(d => (
                  <div key={d.day} className="pv-heatmap-cell-wrap">
                    <div className={`pv-heatmap-cell ${d.future ? 'pv-heatmap-future' : `pv-lvl-${levelFor(d.seconds)}`}`} />
                    {!d.future && (
                      <div className="pv-heatmap-tooltip">
                        <div className="pv-heatmap-tooltip-date">{formatHeatmapDate(d.day)}</div>
                        <div className="pv-heatmap-tooltip-hours">{(d.seconds / 3600).toFixed(1)} ч.</div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className="pv-heatmap-legend">
        <span>Меньше</span>
        <span className="pv-heatmap-cell pv-lvl-0" />
        <span className="pv-heatmap-cell pv-lvl-1" />
        <span className="pv-heatmap-cell pv-lvl-2" />
        <span className="pv-heatmap-cell pv-lvl-3" />
        <span className="pv-heatmap-cell pv-lvl-4" />
        <span>Больше</span>
      </div>
    </div>
  )
}
