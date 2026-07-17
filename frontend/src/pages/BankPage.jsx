import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch, getDiscordUser } from '../api'
import Modal from '../components/Modal'
import PlayerNicknameInput from '../components/PlayerNicknameInput'
import './BankPage.css'

function mcHead(nickname, size = 64) {
  return `https://mc-heads.net/avatar/${encodeURIComponent(nickname)}/${size}`
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })
}

const TX_LABELS = {
  deposit: 'Пополнение баланса',
  withdraw: 'Снятие со счёта',
  transfer: 'Перевод',
  fine_payment: 'Оплата штрафа',
}

function useCountUp(target, duration = 700) {
  const [value, setValue] = useState(0)
  const prevTarget = useRef(0)
  useEffect(() => {
    if (target === null || target === undefined) return
    const from = prevTarget.current
    const start = performance.now()
    let raf
    const tick = (now) => {
      const elapsed = now - start
      if (elapsed >= duration) {
        setValue(target)
        prevTarget.current = target
        return
      }
      const progress = elapsed / duration
      const easeOut = 1 - Math.pow(1 - progress, 3)
      setValue(Math.round(from + (target - from) * easeOut))
      raf = requestAnimationFrame(tick)
    }
    raf = requestAnimationFrame(tick)
    return () => cancelAnimationFrame(raf)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [target])
  return value
}

export default function BankPage() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [accounts, setAccounts] = useState([])
  const [activeId, setActiveId] = useState(null)
  const [txs, setTxs] = useState([])
  const [search, setSearch] = useState('')

  const [modal, setModal] = useState(null) // null | transfer | edit | access | newCard
  const [formError, setFormError] = useState('')
  const [busy, setBusy] = useState(false)

  const [transferNick, setTransferNick] = useState('')
  const [transferAmount, setTransferAmount] = useState('')
  const [transferComment, setTransferComment] = useState('')

  const [editLabel, setEditLabel] = useState('')
  const [editHideBalance, setEditHideBalance] = useState(false)

  const [accessInfo, setAccessInfo] = useState(null)
  const [accessNick, setAccessNick] = useState('')

  const [newCardLabel, setNewCardLabel] = useState('')

  const activeAccount = accounts.find(a => a.id === activeId) || null
  const animatedBalance = useCountUp(activeAccount?.balance ?? 0)

  function loadAccounts() {
    return apiFetch('/web/bank/accounts').then(list => {
      setAccounts(list)
      setActiveId(prev => (list.some(a => a.id === prev) ? prev : (list[0]?.id ?? null)))
      return list
    })
  }

  useEffect(() => {
    if (!getDiscordUser()) { navigate('/cabinet'); return }
    setLoading(true)
    loadAccounts()
      .then(() => setLoadError(''))
      .catch(e => setLoadError(e.message))
      .finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function loadTxs(accountId) {
    apiFetch(`/web/bank/accounts/${accountId}/transactions`).then(setTxs).catch(() => setTxs([]))
  }

  useEffect(() => {
    if (activeId) loadTxs(activeId)
    else setTxs([])
  }, [activeId])

  function closeModal() {
    setModal(null)
    setFormError('')
  }

  function openTransfer() {
    setTransferNick(''); setTransferAmount(''); setTransferComment('')
    setModal('transfer')
    setFormError('')
  }

  function openEdit() {
    if (!activeAccount) return
    setEditLabel(activeAccount.label || '')
    setEditHideBalance(activeAccount.hide_balance)
    setModal('edit')
    setFormError('')
  }

  function openAccess() {
    if (!activeAccount) return
    setAccessInfo(null)
    setAccessNick('')
    setModal('access')
    setFormError('')
    apiFetch(`/web/bank/accounts/${activeAccount.id}/access`)
      .then(setAccessInfo)
      .catch(e => setFormError(e.message))
  }

  function openNewCard() {
    setNewCardLabel('')
    setModal('newCard')
    setFormError('')
  }

  async function submitTransfer() {
    setFormError('')
    const amt = Number(transferAmount)
    if (!transferNick.trim()) { setFormError('Введи ник игрока'); return }
    if (!amt || amt <= 0) { setFormError('Введи сумму'); return }
    setBusy(true)
    try {
      await apiFetch(`/web/bank/accounts/${activeAccount.id}/transfer`, {
        method: 'POST',
        body: JSON.stringify({ to_nickname: transferNick.trim(), amount: amt, comment: transferComment.trim() }),
      })
      closeModal()
      loadAccounts()
      loadTxs(activeAccount.id)
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function submitEdit() {
    setFormError('')
    setBusy(true)
    try {
      await apiFetch(`/web/bank/accounts/${activeAccount.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ label: editLabel.trim(), hide_balance: editHideBalance }),
      })
      closeModal()
      loadAccounts()
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function submitDeleteCard() {
    if (!activeAccount || activeAccount.is_primary) return
    setBusy(true)
    setFormError('')
    try {
      await apiFetch(`/web/bank/accounts/${activeAccount.id}`, { method: 'DELETE' })
      closeModal()
      setActiveId(null)
      loadAccounts()
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function submitNewCard() {
    setBusy(true)
    setFormError('')
    try {
      const created = await apiFetch('/web/bank/accounts', {
        method: 'POST',
        body: JSON.stringify({ label: newCardLabel.trim() }),
      })
      closeModal()
      await loadAccounts()
      setActiveId(created.id)
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function submitGrantAccess() {
    if (!accessNick.trim()) return
    setBusy(true)
    setFormError('')
    try {
      await apiFetch(`/web/bank/accounts/${activeAccount.id}/access`, {
        method: 'POST',
        body: JSON.stringify({ nickname: accessNick.trim() }),
      })
      setAccessNick('')
      const info = await apiFetch(`/web/bank/accounts/${activeAccount.id}/access`)
      setAccessInfo(info)
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function revokeAccess(playerId) {
    setBusy(true)
    setFormError('')
    try {
      await apiFetch(`/web/bank/accounts/${activeAccount.id}/access/${playerId}`, { method: 'DELETE' })
      const info = await apiFetch(`/web/bank/accounts/${activeAccount.id}/access`)
      setAccessInfo(info)
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  const q = search.trim().toLowerCase()
  const filtered = q
    ? txs.filter(t =>
        (TX_LABELS[t.type] || t.type).toLowerCase().includes(q) ||
        (t.counterparty || '').toLowerCase().includes(q) ||
        (t.comment || '').toLowerCase().includes(q)
      )
    : txs

  const groups = []
  for (const t of filtered) {
    const day = formatDate(t.created_at)
    let g = groups.find(g => g.date === day)
    if (!g) { g = { date: day, items: [] }; groups.push(g) }
    g.items.push(t)
  }

  if (loading) {
    return (
      <div className="bank-page page-fade">
        <div className="bank-loading"><div className="spinner" /></div>
      </div>
    )
  }

  if (loadError) {
    return (
      <div className="bank-page page-fade">
        <div className="bank-page-error">
          <div className="bank-page-error-title">Банк недоступен</div>
          <div className="bank-page-error-desc">{loadError}</div>
          <button className="btn btn-primary" onClick={() => navigate('/cabinet')}>В личный кабинет</button>
        </div>
      </div>
    )
  }

  if (accounts.length === 0) {
    return (
      <div className="bank-page page-fade">
        <h1 className="page-title">Личный <em>Банк</em></h1>
        <div className="bank-page-error">
          <div className="bank-page-error-title">У тебя ещё нет счёта</div>
          <div className="bank-page-error-desc">
            Банковский счёт открывает банкир в игре — команда <code>/bank</code> у него в меню.
            Обратись к любому игроку с ролью «Банкир».
          </div>
        </div>
      </div>
    )
  }

  const otherAccounts = accounts.filter(a => a.id !== activeId)
  const holderName = activeAccount?.owner_nickname || ''
  let rowIdx = 0

  return (
    <div className="bank-page page-fade">
      <h1 className="page-title">Личный <em>Банк</em></h1>

      <div className="bank-layout">
        <div className="bank-left">
          {activeAccount && (
            <div className="bank-card">
              <div className="bank-card-top">
                <div>{activeAccount.label || (activeAccount.is_primary ? 'Основная карта' : 'Карта')}</div>
                <div>I-Bank</div>
              </div>
              <div className="bank-card-bottom">
                <div className="bank-card-holder">{holderName?.toUpperCase()}</div>
                <div className="bank-card-balance">
                  {activeAccount.hide_balance ? '◆ ••••' : `◆ ${animatedBalance}`}
                </div>
              </div>
            </div>
          )}

          <div className="bank-actions">
            <div className="bank-action-row" onClick={openTransfer}>
              <span className="bank-action-icon">⇄</span>Перевод денег
            </div>
            {activeAccount?.is_owner && (
              <>
                <div className="bank-action-row" onClick={openEdit}>
                  <span className="bank-action-icon">✎</span>Редактировать
                </div>
                <div className="bank-action-row" onClick={openAccess}>
                  <span className="bank-action-icon">⚭</span>Настройки доступа
                </div>
              </>
            )}
          </div>

          <div className="bank-other-cards">
            <div className="bank-other-title">Другие карты</div>
            {otherAccounts.length === 0 && (
              <div className="bank-other-empty">Пока только одна карта</div>
            )}
            {otherAccounts.map(a => (
              <div key={a.id} className="bank-other-row" onClick={() => setActiveId(a.id)}>
                <div className={`bank-other-preview ${a.is_primary ? 'primary' : ''}`} />
                <div className="bank-other-info">
                  <div className="bank-other-name">{a.label || (a.is_primary ? 'Основная карта' : 'Карта')}</div>
                  <div className="bank-other-owner">{a.is_owner ? '' : a.owner_nickname}</div>
                </div>
              </div>
            ))}
            <div className="bank-other-row bank-other-add" onClick={openNewCard}>
              <div className="bank-other-preview add">+</div>
              <div className="bank-other-info">
                <div className="bank-other-name">Новая карта</div>
              </div>
            </div>
          </div>
        </div>

        <div className="bank-right">
          <div className="bank-history-head">
            <div className="bank-history-title">История платежей</div>
            <div className="bank-history-count">{filtered.length}</div>
          </div>

          <input
            type="text"
            className="bank-search"
            placeholder="Поиск..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />

          {groups.length === 0 ? (
            <div className="bank-empty">{q ? 'Ничего не найдено' : 'Пока нет операций'}</div>
          ) : (
            groups.map(g => (
              <div key={g.date} className="bank-group">
                <div className="bank-group-date">{g.date}</div>
                {g.items.map(t => {
                  const delay = (rowIdx++) * 0.035
                  return (
                    <div key={t.id} className="bank-tx-row" style={{ animationDelay: `${delay}s` }}>
                      <div className="bank-tx-info">
                        <img className="bank-tx-avatar" src={mcHead(t.counterparty || holderName || '?', 64)} alt="" />
                        <div>
                          <div className="bank-tx-name">
                            {TX_LABELS[t.type] || t.type}{t.counterparty ? ` — ${t.counterparty}` : ''}
                          </div>
                          {t.comment && <div className="bank-tx-comment">{t.comment}</div>}
                        </div>
                      </div>
                      <div className={`bank-tx-amount ${t.outgoing ? 'out' : 'in'}`}>
                        {t.outgoing ? '−' : '+'} {Math.round(t.amount)}
                      </div>
                    </div>
                  )
                })}
              </div>
            ))
          )}
        </div>
      </div>

      <Modal open={modal === 'transfer'} onClose={closeModal}>
        <div className="bank-transfer-title">
          <span className="bank-transfer-title-icon">◆</span>
          <h2>Перевод денег</h2>
        </div>

        <div className="bank-transfer-row">
          <div className="inp-group bank-transfer-nick">
            <label>Ник игрока</label>
            <PlayerNicknameInput value={transferNick} onChange={setTransferNick} placeholder="Введите ник" />
          </div>
          <div className="inp-group bank-transfer-sum">
            <label>Сумма</label>
            <div className="bank-amount-input-wrap">
              <span className="bank-amount-icon">◆</span>
              <input
                className="bank-amount-input"
                type="number"
                value={transferAmount}
                onChange={e => setTransferAmount(e.target.value)}
                placeholder="0"
              />
            </div>
          </div>
        </div>

        <div className="inp-group">
          <input
            type="text"
            className="bank-transfer-comment"
            value={transferComment}
            onChange={e => setTransferComment(e.target.value)}
            placeholder="Комментарий (необязательно)"
          />
        </div>

        {formError && <p className="bank-transfer-error">{formError}</p>}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginTop: 14 }}>
          <button className="btn btn-ghost" onClick={closeModal}>Отменить</button>
          <button className="btn btn-primary" disabled={busy} onClick={submitTransfer}>
            {busy ? 'Отправка...' : <>Перевести <span className="bank-btn-diamond">◆</span> {transferAmount || 0}</>}
          </button>
        </div>
      </Modal>

      <Modal open={modal === 'edit'} onClose={closeModal}>
        <h2>Редактировать карту</h2>
        <div className="inp-group">
          <label>Название карты</label>
          <input type="text" value={editLabel} onChange={e => setEditLabel(e.target.value)} placeholder="Основная карта" maxLength={40} />
        </div>
        <div className="bank-toggle-row">
          <span>Скрыть баланс</span>
          <div className={`bank-toggle ${editHideBalance ? 'on' : ''}`} onClick={() => setEditHideBalance(v => !v)}>
            <div className="bank-toggle-thumb" />
          </div>
        </div>
        {formError && <p className="bank-transfer-error">{formError}</p>}
        {!activeAccount?.is_primary && (
          <button className="bank-delete-card-btn" disabled={busy} onClick={submitDeleteCard}>
            🗑 Удалить карту
          </button>
        )}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginTop: 16 }}>
          <button className="btn btn-ghost" onClick={closeModal}>Отменить</button>
          <button className="btn btn-primary" disabled={busy} onClick={submitEdit}>Сохранить</button>
        </div>
      </Modal>

      <Modal open={modal === 'access'} onClose={closeModal}>
        <h2>Настройки доступа</h2>
        <div className="inp-group bank-access-add-row">
          <PlayerNicknameInput
            value={accessNick}
            onChange={setAccessNick}
            placeholder="Ник игрока..."
            onSubmit={submitGrantAccess}
          />
          <button className="btn btn-outline" disabled={busy || !accessNick.trim()} onClick={submitGrantAccess}>
            Добавить
          </button>
        </div>
        {formError && <p className="bank-transfer-error">{formError}</p>}
        {accessInfo === null ? (
          <div className="bank-empty">Загрузка...</div>
        ) : (
          <div className="bank-access-list">
            <div className="bank-access-row">
              <div className="bank-access-info">
                <img className="bank-access-avatar" src={mcHead(accessInfo.owner || '?', 48)} alt="" />
                <span>👑 {accessInfo.owner}</span>
              </div>
            </div>
            {accessInfo.members.map(m => (
              <div key={m.player_id} className="bank-access-row">
                <div className="bank-access-info">
                  <img className="bank-access-avatar" src={mcHead(m.nickname, 48)} alt="" />
                  <span>{m.nickname}</span>
                </div>
                <button disabled={busy} onClick={() => revokeAccess(m.player_id)} title="Убрать доступ">🗑</button>
              </div>
            ))}
          </div>
        )}
        <button className="btn btn-primary" style={{ width: '100%', marginTop: 18 }} onClick={closeModal}>Готово</button>
      </Modal>

      <Modal open={modal === 'newCard'} onClose={closeModal}>
        <h2>Новая карта</h2>
        <div className="inp-group">
          <label>Название карты</label>
          <input
            type="text"
            value={newCardLabel}
            onChange={e => setNewCardLabel(e.target.value)}
            placeholder="Например, Резервная карта"
            maxLength={40}
          />
        </div>
        {formError && <p className="bank-transfer-error">{formError}</p>}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginTop: 16 }}>
          <button className="btn btn-ghost" onClick={closeModal}>Отменить</button>
          <button className="btn btn-primary" disabled={busy} onClick={submitNewCard}>Создать</button>
        </div>
      </Modal>
    </div>
  )
}
