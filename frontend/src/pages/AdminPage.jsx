import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch, getDiscordUser } from '../api'
import { getRoleStyle } from '../roleColors'
import './AdminPage.css'

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })
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

  useEffect(() => {
    if (!getDiscordUser()) { navigate('/cabinet'); return }
    apiFetch('/web/me')
      .then(me => {
        if (!me.linked || !me.is_admin) { setAllowed(false); return }
        setAllowed(true)
        apiFetch('/web/admin/roles').then(setRoles).catch(() => {})
      })
      .catch(() => setAllowed(false))
      .finally(() => setChecking(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

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
  }

  function loadSubscription(nickname) {
    setSubscription(null)
    setSubError('')
    apiFetch(`/web/admin/player/${encodeURIComponent(nickname)}/subscription`)
      .then(setSubscription)
      .catch(e => setSubError(e.message))
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
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
