import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Vote, Trash2, Eye, EyeOff, Lock, Plus } from 'lucide-react'
import { apiFetch, getDiscordUser } from '../api'
import { getRoleStyle } from '../roleColors'
import './AdminPage.css'

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })
}

function formatDateTime(iso) {
  return new Date(iso).toLocaleString('ru-RU', { day: 'numeric', month: 'long', hour: '2-digit', minute: '2-digit' })
}

export default function AdminPage() {
  const navigate = useNavigate()
  const [checking, setChecking] = useState(true)
  const [allowed, setAllowed] = useState(false)

  const [roles, setRoles] = useState([])
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [searching, setSearching] = useState(false)

  const [selected, setSelected] = useState(null) // nickname
  const [playerRoles, setPlayerRoles] = useState(null)
  const [roleToAdd, setRoleToAdd] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  const [subscription, setSubscription] = useState(null)
  const [subBusy, setSubBusy] = useState(false)
  const [subError, setSubError] = useState('')

  const [cards, setCards] = useState(null)
  const [cardBusy, setCardBusy] = useState(false)
  const [cardError, setCardError] = useState('')
  const [newCardLabel, setNewCardLabel] = useState('')

  const [polls, setPolls] = useState(null)
  const [pollError, setPollError] = useState('')
  const [pollBusy, setPollBusy] = useState(false)
  const [newTitle, setNewTitle] = useState('')
  const [newCandidates, setNewCandidates] = useState(['', ''])
  const [newDeadline, setNewDeadline] = useState('')
  const [newVisible, setNewVisible] = useState(true)

  useEffect(() => {
    if (!getDiscordUser()) { navigate('/cabinet'); return }
    apiFetch('/web/me')
      .then(me => {
        if (!me.linked || !me.is_admin) { setAllowed(false); return }
        setAllowed(true)
        apiFetch('/web/admin/roles').then(setRoles).catch(() => {})
        loadPolls()
      })
      .catch(() => setAllowed(false))
      .finally(() => setChecking(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function loadPolls() {
    apiFetch('/web/admin/polls').then(setPolls).catch(e => setPollError(e.message))
  }

  function updateCandidate(i, value) {
    setNewCandidates(cs => cs.map((c, idx) => idx === i ? value : c))
  }

  function addCandidateField() {
    setNewCandidates(cs => [...cs, ''])
  }

  function removeCandidateField(i) {
    setNewCandidates(cs => cs.filter((_, idx) => idx !== i))
  }

  async function createPoll() {
    const title = newTitle.trim()
    const candidates = newCandidates.map(c => c.trim()).filter(Boolean)
    if (!title) { setPollError('Укажи тему голосования'); return }
    if (candidates.length < 2) { setPollError('Нужно минимум 2 кандидата'); return }

    setPollBusy(true)
    setPollError('')
    try {
      await apiFetch('/web/admin/polls', {
        method: 'POST',
        body: JSON.stringify({
          title,
          candidates,
          deadline: newDeadline || null,
          visible: newVisible,
        }),
      })
      setNewTitle('')
      setNewCandidates(['', ''])
      setNewDeadline('')
      setNewVisible(true)
      loadPolls()
    } catch (e) {
      setPollError(e.message)
    } finally {
      setPollBusy(false)
    }
  }

  async function togglePollVisible(poll) {
    setPollBusy(true)
    try {
      await apiFetch(`/web/admin/polls/${poll.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ visible: !poll.visible }),
      })
      loadPolls()
    } catch (e) {
      setPollError(e.message)
    } finally {
      setPollBusy(false)
    }
  }

  async function closePollNow(pollId) {
    setPollBusy(true)
    try {
      await apiFetch(`/web/admin/polls/${pollId}/close`, { method: 'POST' })
      loadPolls()
    } catch (e) {
      setPollError(e.message)
    } finally {
      setPollBusy(false)
    }
  }

  async function deletePoll(pollId) {
    setPollBusy(true)
    try {
      await apiFetch(`/web/admin/polls/${pollId}`, { method: 'DELETE' })
      loadPolls()
    } catch (e) {
      setPollError(e.message)
    } finally {
      setPollBusy(false)
    }
  }

  function search(q) {
    setQuery(q)
    if (!q.trim()) { setResults([]); return }
    setSearching(true)
    apiFetch(`/web/admin/players?q=${encodeURIComponent(q.trim())}`)
      .then(setResults)
      .catch(() => setResults([]))
      .finally(() => setSearching(false))
  }

  function openPlayer(nickname) {
    setSelected(nickname)
    setPlayerRoles(null)
    setError('')
    apiFetch(`/web/admin/player/${encodeURIComponent(nickname)}/roles`)
      .then(d => setPlayerRoles(d.roles))
      .catch(e => setError(e.message))
    loadSubscription(nickname)
    loadCards(nickname)
  }

  function loadSubscription(nickname) {
    setSubscription(null)
    setSubError('')
    apiFetch(`/web/admin/player/${encodeURIComponent(nickname)}/subscription`)
      .then(setSubscription)
      .catch(e => setSubError(e.message))
  }

  function loadCards(nickname) {
    setCards(null)
    setCardError('')
    apiFetch(`/web/admin/player/${encodeURIComponent(nickname)}/cards`)
      .then(setCards)
      .catch(e => setCardError(e.message))
  }

  async function createCard() {
    if (!selected) return
    setCardBusy(true)
    setCardError('')
    try {
      await apiFetch(`/web/admin/player/${encodeURIComponent(selected)}/cards`, {
        method: 'POST',
        body: JSON.stringify({ label: newCardLabel.trim() }),
      })
      setNewCardLabel('')
      loadCards(selected)
    } catch (e) {
      setCardError(e.message)
    } finally {
      setCardBusy(false)
    }
  }

  async function grantSubscription(months, forever) {
    if (!selected) return
    setSubBusy(true)
    setSubError('')
    try {
      await apiFetch(`/web/admin/player/${encodeURIComponent(selected)}/subscription`, {
        method: 'POST',
        body: JSON.stringify({ months, forever }),
      })
      loadSubscription(selected)
    } catch (e) {
      setSubError(e.message)
    } finally {
      setSubBusy(false)
    }
  }

  async function revokeSubscription() {
    if (!selected) return
    setSubBusy(true)
    setSubError('')
    try {
      await apiFetch(`/web/admin/player/${encodeURIComponent(selected)}/subscription`, { method: 'DELETE' })
      loadSubscription(selected)
    } catch (e) {
      setSubError(e.message)
    } finally {
      setSubBusy(false)
    }
  }

  async function grantRole() {
    if (!roleToAdd || !selected) return
    setBusy(true)
    setError('')
    try {
      await apiFetch(`/web/admin/player/${encodeURIComponent(selected)}/roles`, {
        method: 'POST',
        body: JSON.stringify({ role_name: roleToAdd }),
      })
      setRoleToAdd('')
      openPlayer(selected)
    } catch (e) {
      setError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function revokeRole(role) {
    setBusy(true)
    setError('')
    try {
      await apiFetch(`/web/admin/player/${encodeURIComponent(selected)}/roles/${encodeURIComponent(role)}`, {
        method: 'DELETE',
      })
      openPlayer(selected)
    } catch (e) {
      setError(e.message)
    } finally {
      setBusy(false)
    }
  }

  if (checking) {
    return (
      <div className="admin-page page-fade">
        <div className="admin-loading"><div className="spinner" /></div>
      </div>
    )
  }

  if (!allowed) {
    return (
      <div className="admin-page page-fade">
        <div className="admin-denied">
          <div className="admin-denied-title">Нет доступа</div>
          <div className="admin-denied-desc">Эта страница только для администраторов.</div>
        </div>
      </div>
    )
  }

  const availableToAdd = roles.filter(r => !playerRoles?.includes(r))

  return (
    <div className="admin-page page-fade">
      <h1 className="page-title">Админ-<em>панель</em></h1>

      <div className="admin-layout">
        <div className="admin-search-col">
          <input
            type="text"
            className="admin-search"
            placeholder="Поиск игрока по нику..."
            value={query}
            onChange={e => search(e.target.value)}
          />
          <div className="admin-results">
            {searching && <div className="admin-empty">Поиск...</div>}
            {!searching && query.trim() && results.length === 0 && (
              <div className="admin-empty">Никого не найдено</div>
            )}
            {results.map(p => (
              <div
                key={p.nickname}
                className={`admin-result-row ${selected === p.nickname ? 'active' : ''}`}
                onClick={() => openPlayer(p.nickname)}
              >
                <span className="admin-result-name">{p.nickname}</span>
                {!p.has_linked_account && <span className="admin-result-badge">не привязан</span>}
              </div>
            ))}
          </div>
        </div>

        <div className="admin-detail-col">
          {!selected && (
            <div className="admin-empty admin-empty-big">Выбери игрока слева</div>
          )}

          {selected && (
            <div className="admin-detail-card">
              <div className="admin-detail-title">{selected}</div>

              {error && <div className="admin-error">{error}</div>}

              {playerRoles === null ? (
                <div className="admin-empty">Загрузка...</div>
              ) : (
                <>
                  <div className="admin-section-label">Текущие роли</div>
                  {playerRoles.length === 0 ? (
                    <div className="admin-empty">Ролей нет</div>
                  ) : (
                    <div className="admin-roles-list">
                      {playerRoles.map(r => {
                        const style = getRoleStyle(r)
                        return (
                        <div
                          key={r}
                          className="admin-role-chip"
                          style={{ color: style.color, background: style.bg, borderColor: style.color }}
                        >
                          {r}
                          <button disabled={busy} onClick={() => revokeRole(r)} title="Снять роль">×</button>
                        </div>
                        )
                      })}
                    </div>
                  )}

                  <div className="admin-section-label">Выдать роль</div>
                  <div className="admin-grant-row">
                    <select value={roleToAdd} onChange={e => setRoleToAdd(e.target.value)}>
                      <option value="">Выбери роль...</option>
                      {availableToAdd.map(r => (
                        <option key={r} value={r}>{r}</option>
                      ))}
                    </select>
                    <button className="btn btn-primary" disabled={!roleToAdd || busy} onClick={grantRole}>
                      Выдать
                    </button>
                  </div>

                  <div className="admin-section-label">IchoPlus</div>
                  {subError && <div className="admin-error">{subError}</div>}
                  {subscription === null ? (
                    <div className="admin-empty">Загрузка...</div>
                  ) : (
                    <>
                      <div className={`admin-sub-status ${subscription.active ? 'active' : ''}`}>
                        {subscription.active
                          ? (subscription.forever
                              ? 'Активна — навсегда'
                              : `Активна до ${formatDate(subscription.expires_at)}`)
                          : 'Не активна'}
                      </div>
                      <div className="admin-sub-actions">
                        <button className="admin-sub-btn" disabled={subBusy} onClick={() => grantSubscription(1, false)}>+1 месяц</button>
                        <button className="admin-sub-btn" disabled={subBusy} onClick={() => grantSubscription(2, false)}>+2 месяца</button>
                        <button className="admin-sub-btn" disabled={subBusy} onClick={() => grantSubscription(3, false)}>+3 месяца</button>
                        <button className="admin-sub-btn forever" disabled={subBusy} onClick={() => grantSubscription(null, true)}>Навсегда</button>
                        {subscription.active && (
                          <button className="admin-sub-btn revoke" disabled={subBusy} onClick={revokeSubscription}>Снять</button>
                        )}
                      </div>
                    </>
                  )}

                  <div className="admin-section-label">Карты</div>
                  {cardError && <div className="admin-error">{cardError}</div>}
                  {cards === null ? (
                    <div className="admin-empty">Загрузка...</div>
                  ) : (
                    <>
                      <div className="admin-roles-list">
                        {cards.map(c => (
                          <div key={c.id} className="admin-role-chip">
                            {c.label} · {c.balance}₽
                          </div>
                        ))}
                      </div>
                      <div className="admin-grant-row" style={{ marginTop: 10 }}>
                        <input
                          type="text"
                          placeholder="Название карты (необязательно)"
                          value={newCardLabel}
                          onChange={e => setNewCardLabel(e.target.value)}
                          maxLength={40}
                        />
                        <button className="btn btn-primary" disabled={cardBusy} onClick={createCard}>
                          Создать карту
                        </button>
                      </div>
                    </>
                  )}
                </>
              )}
            </div>
          )}
        </div>
      </div>

      <div className="admin-polls-section">
        <div className="admin-section-header">
          <Vote size={18} strokeWidth={1.75} />
          <h2>Голосования</h2>
        </div>

        {pollError && <div className="admin-error">{pollError}</div>}

        <div className="admin-poll-create">
          <input
            type="text"
            className="admin-search"
            placeholder="Тема голосования"
            value={newTitle}
            onChange={e => setNewTitle(e.target.value)}
          />

          <div className="admin-poll-cand-inputs">
            {newCandidates.map((c, i) => (
              <div className="admin-poll-cand-input-row" key={i}>
                <input
                  type="text"
                  placeholder={`Кандидат ${i + 1} (ник)`}
                  value={c}
                  onChange={e => updateCandidate(i, e.target.value)}
                />
                {newCandidates.length > 2 && (
                  <button type="button" onClick={() => removeCandidateField(i)} title="Убрать">×</button>
                )}
              </div>
            ))}
            <button type="button" className="admin-poll-add-cand" onClick={addCandidateField}>
              <Plus size={14} /> Кандидат
            </button>
          </div>

          <div className="admin-poll-create-row">
            <label className="admin-poll-deadline">
              <span>Дедлайн (необязательно)</span>
              <input
                type="datetime-local"
                value={newDeadline}
                onChange={e => setNewDeadline(e.target.value)}
              />
            </label>
            <label className="admin-poll-visible-toggle">
              <input type="checkbox" checked={newVisible} onChange={e => setNewVisible(e.target.checked)} />
              Показывать на сайте
            </label>
          </div>

          <button className="btn btn-primary" disabled={pollBusy} onClick={createPoll}>
            Создать голосование
          </button>
        </div>

        <div className="admin-poll-list">
          {polls === null && <div className="admin-empty">Загрузка...</div>}
          {polls !== null && polls.length === 0 && (
            <div className="admin-empty admin-empty-big">Голосований пока нет</div>
          )}
          {polls?.map(poll => (
            <div className="admin-poll-card" key={poll.id}>
              <div className="admin-poll-head">
                <div className="admin-poll-title">{poll.title}</div>
                <div className="admin-poll-badges">
                  {!poll.visible && <span className="admin-poll-badge hidden">Скрыт</span>}
                  <span className={`admin-poll-badge ${poll.is_closed ? 'closed' : 'open'}`}>
                    {poll.is_closed ? 'Закрыт' : 'Открыт'}
                  </span>
                </div>
              </div>
              <div className="admin-poll-meta">
                {poll.deadline ? `До ${formatDateTime(poll.deadline)}` : 'Без дедлайна'} · {poll.total_votes} голосов
                {poll.created_by && ` · создал ${poll.created_by}`}
              </div>

              <div className="admin-poll-candidates">
                {poll.candidates.map(c => (
                  <div className="admin-poll-cand" key={c.id}>
                    <div className="admin-poll-cand-bar" style={{ width: `${Math.max(c.percent, c.votes ? 3 : 0)}%` }} />
                    <span className="admin-poll-cand-name">{c.nickname}</span>
                    <span className="admin-poll-cand-pct">{c.votes} ({c.percent}%)</span>
                  </div>
                ))}
              </div>

              <div className="admin-poll-actions">
                <button disabled={pollBusy} onClick={() => togglePollVisible(poll)}>
                  {poll.visible ? <><EyeOff size={13} /> Скрыть</> : <><Eye size={13} /> Показать</>}
                </button>
                {!poll.is_closed && (
                  <button disabled={pollBusy} onClick={() => closePollNow(poll.id)}>
                    <Lock size={13} /> Закрыть сейчас
                  </button>
                )}
                <button className="danger" disabled={pollBusy} onClick={() => deletePoll(poll.id)}>
                  <Trash2 size={13} /> Удалить
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
