import { useEffect, useState } from 'react'
import { apiFetch } from '../api'
import Modal from './Modal'
import './BankTab.css'

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })
}

const TX_LABELS = {
  deposit: 'Пополнение баланса',
  transfer: 'Перевод',
  fine_payment: 'Оплата штрафа',
}

export default function BankTab({ nickname }) {
  const [balance, setBalance] = useState(null)
  const [txs, setTxs] = useState([])
  const [transferOpen, setTransferOpen] = useState(false)
  const [toNick, setToNick] = useState('')
  const [amount, setAmount] = useState('')
  const [comment, setComment] = useState('')
  const [sending, setSending] = useState(false)
  const [error, setError] = useState('')

  function load() {
    apiFetch('/web/bank/me').then(d => setBalance(d.balance)).catch(() => {})
    apiFetch('/web/bank/transactions').then(setTxs).catch(() => {})
  }

  useEffect(() => { load() }, [])

  async function submitTransfer() {
    setError('')
    const amt = Number(amount)
    if (!toNick.trim()) { setError('Введи ник игрока'); return }
    if (!amt || amt <= 0) { setError('Введи сумму'); return }
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
      setError(e.message)
    } finally {
      setSending(false)
    }
  }

  // группировка по дате (день)
  const groups = []
  for (const t of txs) {
    const day = formatDate(t.created_at)
    let g = groups.find(g => g.date === day)
    if (!g) { g = { date: day, items: [] }; groups.push(g) }
    g.items.push(t)
  }

  return (
    <div className="bank-tab">
      <div className="bank-left">
        <div className="bank-card">
          <div className="bank-card-top">
            <div>{nickname}</div>
            <div>I-Bank</div>
          </div>
          <div className="bank-card-bottom">
            <div className="bank-card-holder">{nickname?.toUpperCase()}</div>
            <div className="bank-card-balance">◆ {balance === null ? '—' : Math.round(balance)}</div>
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
          <div className="bank-history-count">{txs.length}</div>
        </div>

        {groups.length === 0 ? (
          <div className="bank-empty">Пока нет операций</div>
        ) : (
          groups.map(g => (
            <div key={g.date} className="bank-group">
              <div className="bank-group-date">{g.date}</div>
              {g.items.map(t => (
                <div key={t.id} className="bank-tx-row">
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
              ))}
            </div>
          ))
        )}
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
        {error && <p className="bank-transfer-error">{error}</p>}
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
