import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch, getDiscordUser } from '../api'
import Modal from '../components/Modal'
import './BankPage.css'

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
  const [nickname, setNickname] = useState(null)
  const [balance, setBalance] = useState(null)
  const [txs, setTxs] = useState([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [search, setSearch] = useState('')
  const [transferOpen, setTransferOpen] = useState(false)
  const [toNick, setToNick] = useState('')
  const [amount, setAmount] = useState('')
  const [comment, setComment] = useState('')
  const [sending, setSending] = useState(false)
  const [formError, setFormError] = useState('')

  const animatedBalance = useCountUp(balance ?? 0)

  function load() {
    setLoading(true)
    apiFetch('/web/bank/me')
      .then(d => { setNickname(d.nickname); setBalance(d.balance); setLoadError('') })
      .catch(e => setLoadError(e.message))
      .finally(() => setLoading(false))
    apiFetch('/web/bank/transactions').then(setTxs).catch(() => {})
  }

  useEffect(() => {
    if (!getDiscordUser()) { navigate('/cabinet'); return }
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function submitTransfer() {
    setFormError('')
    const amt = Number(amount)
    if (!toNick.trim()) { setFormError('Введи ник игрока'); return }
    if (!amt || amt <= 0) { setFormError('Введи сумму'); return }
    setSending(true)
    try {
      await apiFetch('/web/bank/transfer', {
        method: 'POST',
        body: JSON.stringify({ to_nickname: toNick.trim(), amount: amt, comment: comment.trim() }),
      })
      setTransferOpen(false)
      setToNick(''); setAmount(''); setComment('')
      load()
    } catch (e) {
      setFormError(e.message)
    } finally {
      setSending(false)
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
          <div className="bank-page-error-desc">
            {loadError === 'Failed to fetch' ? 'Не удалось загрузить данные счёта. Привяжи Minecraft-ник в личном кабинете.' : loadError}
          </div>
          <button className="btn btn-primary" onClick={() => navigate('/cabinet')}>В личный кабинет</button>
        </div>
      </div>
    )
  }

  let rowIdx = 0

  return (
    <div className="bank-page page-fade">
      <h1 className="page-title">Личный <em>Банк</em></h1>

      <div className="bank-layout">
        <div className="bank-left">
          <div className="bank-card">
            <div className="bank-card-top">
              <div>{nickname}</div>
              <div>I-Bank</div>
            </div>
            <div className="bank-card-bottom">
              <div className="bank-card-holder">{nickname?.toUpperCase()}</div>
              <div className="bank-card-balance">◆ {animatedBalance}</div>
            </div>
          </div>

          <div className="bank-actions">
            <div className="bank-action-row" onClick={() => setTransferOpen(true)}>
              <span className="bank-action-icon">⇄</span>Перевод денег
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
                        <div className="bank-tx-avatar" />
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

      <Modal open={transferOpen} onClose={() => setTransferOpen(false)}>
        <h2>Перевести деньги</h2>
        <div className="inp-group">
          <label>Ник игрока</label>
          <input type="text" value={toNick} onChange={e => setToNick(e.target.value)} placeholder="Введите ник игрока" />
        </div>
        <div className="inp-group">
          <label>Сумма</label>
          <input type="number" value={amount} onChange={e => setAmount(e.target.value)} placeholder="0" />
        </div>
        <div className="inp-group">
          <label>Комментарий</label>
          <textarea value={comment} onChange={e => setComment(e.target.value)} placeholder="Необязательно" rows={3} />
        </div>
        {formError && <p className="bank-transfer-error">{formError}</p>}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginTop: 16 }}>
          <button className="btn btn-ghost" onClick={() => setTransferOpen(false)}>Отменить</button>
          <button className="btn btn-primary" disabled={sending} onClick={submitTransfer}>
            {sending ? 'Отправка...' : `Перевести ◆ ${amount || 0}`}
          </button>
        </div>
      </Modal>
    </div>
  )
}
