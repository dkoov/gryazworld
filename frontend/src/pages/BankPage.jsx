import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Crown, Gem, ArrowRight, Receipt, ImagePlus, X, Lock, Siren } from 'lucide-react'
import { apiFetch, getDiscordUser } from '../api'
import Modal from '../components/Modal'
import PlayerNicknameInput from '../components/PlayerNicknameInput'
import './BankPage.css'

const CLOUDINARY_URL = 'https://api.cloudinary.com/v1_1/dzwhmfizw/image/upload'
const CLOUDINARY_PRESET = 'gryazworld'

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
  invoice_payment: 'Оплата счёта',
}

function formatDateTime(iso) {
  return new Date(iso).toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' })
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

  const [transferMode, setTransferMode] = useState('transfer') // transfer | invoice
  const [transferNick, setTransferNick] = useState('')
  const [transferAmount, setTransferAmount] = useState('')
  const [transferComment, setTransferComment] = useState('')
  const [transferFromId, setTransferFromId] = useState(null)
  const [fromDropdownOpen, setFromDropdownOpen] = useState(false)
  const fromDropdownRef = useRef(null)

  const [invoices, setInvoices] = useState([])
  const [invoiceBusyId, setInvoiceBusyId] = useState(null)

  const [isIchoPlus, setIsIchoPlus] = useState(false)

  const [canGiveFines, setCanGiveFines] = useState(false)
  const [fineBusy, setFineBusy] = useState(false)
  const [fineFormError, setFineFormError] = useState('')
  const [fineNick, setFineNick] = useState('')
  const [fineAmount, setFineAmount] = useState('')
  const [fineReason, setFineReason] = useState('')
  const [fineComment, setFineComment] = useState('')

  const [editLabel, setEditLabel] = useState('')
  const [editHideBalance, setEditHideBalance] = useState(false)
  const [editImageUrl, setEditImageUrl] = useState('')
  const [imageUploading, setImageUploading] = useState(false)
  const imageInputRef = useRef(null)

  const [accessInfo, setAccessInfo] = useState(null)
  const [accessNick, setAccessNick] = useState('')

  const [newCardLabel, setNewCardLabel] = useState('')

  const activeAccount = accounts.find(a => a.id === activeId) || null
  const transferFromAccount = accounts.find(a => a.id === transferFromId) || activeAccount
  const animatedBalance = useCountUp(activeAccount?.balance ?? 0)

  useEffect(() => {
    function onClickOutside(e) {
      if (fromDropdownRef.current && !fromDropdownRef.current.contains(e.target)) setFromDropdownOpen(false)
    }
    document.addEventListener('mousedown', onClickOutside)
    return () => document.removeEventListener('mousedown', onClickOutside)
  }, [])

  function loadAccounts() {
    return apiFetch('/web/bank/accounts').then(list => {
      setAccounts(list)
      setActiveId(prev => (list.some(a => a.id === prev) ? prev : (list[0]?.id ?? null)))
      return list
    })
  }

  function loadInvoices() {
    apiFetch('/web/bank/invoices').then(setInvoices).catch(() => {})
  }

  useEffect(() => {
    if (!getDiscordUser()) { navigate('/cabinet'); return }
    setLoading(true)
    loadAccounts()
      .then(() => setLoadError(''))
      .catch(e => setLoadError(e.message))
      .finally(() => setLoading(false))
    loadInvoices()
    apiFetch('/web/bank/permissions')
      .then(p => setIsIchoPlus(!!p.is_ichoplus))
      .catch(() => {})
    apiFetch('/web/court/permissions')
      .then(p => setCanGiveFines(!!p.can_review))
      .catch(() => {})
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

  function openTransfer(mode = 'transfer') {
    setTransferMode(mode)
    setTransferNick(''); setTransferAmount(''); setTransferComment('')
    setTransferFromId(activeId)
    setFromDropdownOpen(false)
    setModal('transfer')
    setFormError('')
  }

  function openEdit() {
    if (!activeAccount) return
    setEditLabel(activeAccount.label || '')
    setEditHideBalance(activeAccount.hide_balance)
    setEditImageUrl(activeAccount.image_url || '')
    setModal('edit')
    setFormError('')
  }

  function openFine() {
    setFineNick(''); setFineAmount(''); setFineReason(''); setFineComment('')
    setFineFormError('')
    setModal('fine')
  }

  async function submitFine() {
    setFineFormError('')
    const amt = Number(fineAmount)
    if (!fineNick.trim()) { setFineFormError('Введи ник игрока'); return }
    if (!amt || amt <= 0) { setFineFormError('Введи сумму'); return }
    if (!fineReason.trim()) { setFineFormError('Введи причину'); return }
    setFineBusy(true)
    try {
      await apiFetch('/web/court/fines', {
        method: 'POST',
        body: JSON.stringify({
          nickname: fineNick.trim(),
          amount: amt,
          reason: fineReason.trim(),
          comment: fineComment.trim(),
        }),
      })
      closeModal()
    } catch (e) {
      setFineFormError(e.message)
    } finally {
      setFineBusy(false)
    }
  }

  async function handleImagePick(e) {
    const file = e.target.files?.[0]
    e.target.value = ''
    if (!file || !isIchoPlus) return
    setImageUploading(true)
    setFormError('')
    try {
      const fd = new FormData()
      fd.append('file', file)
      fd.append('upload_preset', CLOUDINARY_PRESET)
      const res = await fetch(CLOUDINARY_URL, { method: 'POST', body: fd })
      const data = await res.json()
      if (data.secure_url) setEditImageUrl(data.secure_url)
      else throw new Error(data.error?.message || 'Ошибка загрузки')
    } catch (err) {
      setFormError('Ошибка: ' + err.message)
    } finally {
      setImageUploading(false)
    }
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
    const fromId = transferFromAccount?.id
    setBusy(true)
    try {
      if (transferMode === 'invoice') {
        await apiFetch(`/web/bank/accounts/${fromId}/invoices`, {
          method: 'POST',
          body: JSON.stringify({ debtor_nickname: transferNick.trim(), amount: amt, comment: transferComment.trim() }),
        })
        loadInvoices()
      } else {
        await apiFetch(`/web/bank/accounts/${fromId}/transfer`, {
          method: 'POST',
          body: JSON.stringify({ to_nickname: transferNick.trim(), amount: amt, comment: transferComment.trim() }),
        })
        loadAccounts()
        loadTxs(fromId)
      }
      closeModal()
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function payInvoice(id) {
    setInvoiceBusyId(id)
    try {
      await apiFetch(`/web/bank/invoices/${id}/pay`, { method: 'POST' })
      loadInvoices()
      loadAccounts()
      if (activeId) loadTxs(activeId)
    } catch (e) {
      alert(e.message)
    } finally {
      setInvoiceBusyId(null)
    }
  }

  async function declineInvoice(id) {
    setInvoiceBusyId(id)
    try {
      await apiFetch(`/web/bank/invoices/${id}/decline`, { method: 'POST' })
      loadInvoices()
    } catch (e) {
      alert(e.message)
    } finally {
      setInvoiceBusyId(null)
    }
  }

  async function cancelInvoice(id) {
    setInvoiceBusyId(id)
    try {
      await apiFetch(`/web/bank/invoices/${id}`, { method: 'DELETE' })
      loadInvoices()
    } catch (e) {
      alert(e.message)
    } finally {
      setInvoiceBusyId(null)
    }
  }

  async function submitEdit() {
    setFormError('')
    setBusy(true)
    try {
      await apiFetch(`/web/bank/accounts/${activeAccount.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ label: editLabel.trim(), hide_balance: editHideBalance, image_url: editImageUrl }),
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
            <div
              className={`bank-card ${activeAccount.image_url ? 'has-image' : ''}`}
              style={activeAccount.image_url ? { backgroundImage: `url(${activeAccount.image_url})` } : undefined}
            >
              <div className="bank-card-top">
                <div>{activeAccount.label || (activeAccount.is_primary ? 'Основная карта' : 'Карта')}</div>
                <div>I-Bank</div>
              </div>
              <div className="bank-card-bottom">
                <div className="bank-card-holder">{holderName?.toUpperCase()}</div>
                <div className="bank-card-balance">
                  <Gem size={13} />
                  {activeAccount.hide_balance ? ' ••••' : ` ${animatedBalance}`}
                </div>
              </div>
            </div>
          )}

          <div className="bank-actions">
            <div className="bank-action-row" onClick={() => openTransfer('transfer')}>
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

          {canGiveFines && (
            <div className="bank-fine-tile" onClick={openFine}>
              <span className="bank-fine-tile-icon"><Siren size={16} /></span>
              <div className="bank-fine-tile-info">
                <div className="bank-fine-tile-title">Штраф</div>
                <div className="bank-fine-tile-desc">Выдать штраф игроку</div>
              </div>
            </div>
          )}

          <div className="bank-other-cards">
            <div className="bank-other-title">Другие карты</div>
            {otherAccounts.length === 0 && (
              <div className="bank-other-empty">Пока только одна карта</div>
            )}
            {otherAccounts.map(a => (
              <div key={a.id} className="bank-other-row" onClick={() => setActiveId(a.id)}>
                <div
                  className={`bank-other-preview ${a.is_primary ? 'primary' : ''} ${a.image_url ? 'has-image' : ''}`}
                  style={a.image_url ? { backgroundImage: `url(${a.image_url})` } : undefined}
                />
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
          {invoices.length > 0 && (
            <div className="bank-invoices-block">
              <div className="bank-history-title bank-invoices-title">Счета</div>
              <div className="bank-invoices-list">
                {invoices.map((inv, i) => (
                  <div
                    key={inv.id}
                    className={`bank-invoice-row status-${inv.status}`}
                    style={{ animationDelay: `${i * 0.04}s` }}
                  >
                    <img className="bank-tx-avatar" src={mcHead(inv.counterparty || '?', 64)} alt="" />
                    <div className="bank-invoice-info">
                      <div className="bank-tx-name">
                        {inv.outgoing ? `Счёт для ${inv.counterparty}` : `Счёт от ${inv.counterparty}`}
                        {inv.account_label ? ` — ${inv.account_label}` : ''}
                      </div>
                      {inv.comment && <div className="bank-tx-comment">{inv.comment}</div>}
                      <div className="bank-invoice-meta">
                        {formatDateTime(inv.created_at)}
                        {inv.status === 'paid' && <span className="bank-invoice-status paid">оплачен</span>}
                        {inv.status === 'declined' && <span className="bank-invoice-status declined">отклонён</span>}
                        {inv.status === 'cancelled' && <span className="bank-invoice-status declined">отменён</span>}
                        {inv.status === 'pending' && !inv.outgoing && <span className="bank-invoice-status pending">ожидает оплаты</span>}
                        {inv.status === 'pending' && inv.outgoing && <span className="bank-invoice-status pending">выставлен</span>}
                      </div>
                    </div>
                    <div className="bank-invoice-right">
                      <div className="bank-tx-amount"><Gem size={12} className="bank-tx-amount-icon" /> {Math.round(inv.amount)}</div>
                      {inv.status === 'pending' && !inv.outgoing && (
                        <div className="bank-invoice-actions">
                          <button
                            className="bank-invoice-pay"
                            disabled={invoiceBusyId === inv.id}
                            onClick={() => payInvoice(inv.id)}
                          >
                            Оплатить
                          </button>
                          <button
                            className="bank-invoice-decline"
                            disabled={invoiceBusyId === inv.id}
                            onClick={() => declineInvoice(inv.id)}
                          >
                            Отклонить
                          </button>
                        </div>
                      )}
                      {inv.status === 'pending' && inv.outgoing && (
                        <div className="bank-invoice-actions">
                          <button
                            className="bank-invoice-decline"
                            disabled={invoiceBusyId === inv.id}
                            onClick={() => cancelInvoice(inv.id)}
                          >
                            Удалить счёт
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

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

      <Modal open={modal === 'transfer'} onClose={closeModal} wide>
        <div className="bt-tabs">
          <div
            className={`bt-tab ${transferMode === 'transfer' ? 'active' : ''}`}
            onClick={() => { setTransferMode('transfer'); setFormError('') }}
          >
            Перевести
          </div>
          <div
            className={`bt-tab ${transferMode === 'invoice' ? 'active' : ''}`}
            onClick={() => { setTransferMode('invoice'); setFormError('') }}
          >
            <Receipt size={13} className="bt-tab-icon" /> Выставить счёт
          </div>
        </div>

        {transferFromAccount && (() => {
          const cardEl = (
            <div
              className={`bt-preview-card ${transferFromAccount.image_url ? 'has-image' : ''}`}
              style={transferFromAccount.image_url ? { backgroundImage: `url(${transferFromAccount.image_url})` } : undefined}
            >
              <div className="bank-card-top">
                <span>{transferFromAccount.label || (transferFromAccount.is_primary ? 'Основная карта' : 'Карта')}</span>
                <span>I-Bank</span>
              </div>
              <div className="bank-card-bottom">
                <span>{transferFromAccount.owner_nickname}</span>
                <span className="bank-card-balance"><Gem size={12} /> {Math.round(transferFromAccount.balance)}</span>
              </div>
            </div>
          )
          const targetEl = (
            <div className={`bt-preview-target ${transferNick.trim() ? 'filled' : ''}`}>
              {transferNick.trim() ? (
                <>
                  <img src={mcHead(transferNick, 40)} alt="" />
                  <span>{transferNick}</span>
                </>
              ) : (
                <span className="bt-preview-placeholder">Игрок не выбран</span>
              )}
            </div>
          )
          const isInvoice = transferMode === 'invoice'
          return (
            <div className="bt-preview-row" key={transferMode}>
              {isInvoice ? targetEl : cardEl}
              <div className={`bt-preview-arrow ${isInvoice ? 'flipped' : ''}`}>
                <ArrowRight size={18} className="bt-preview-arrow-icon" />
              </div>
              {isInvoice ? cardEl : targetEl}
            </div>
          )
        })()}

        <div className="bt-select-row" ref={fromDropdownRef}>
          <div
            className="bt-select-main"
            onClick={() => accounts.length > 1 && setFromDropdownOpen(v => !v)}
          >
            <img className="bt-select-avatar" src={mcHead(transferFromAccount?.owner_nickname || '?', 40)} alt="" />
            <div className="bt-select-info">
              <div className="bt-select-name">{transferFromAccount?.owner_nickname}</div>
              <div className="bt-select-sub">
                {transferFromAccount?.label || (transferFromAccount?.is_primary ? 'Основная карта' : 'Карта')}
              </div>
            </div>
            {accounts.length > 1 && <span className={`bt-select-chevron ${fromDropdownOpen ? 'open' : ''}`}>▾</span>}
          </div>
          {fromDropdownOpen && (
            <div className="bt-select-dropdown">
              {accounts.map(a => (
                <div
                  key={a.id}
                  className="bt-select-option"
                  onMouseDown={() => { setTransferFromId(a.id); setFromDropdownOpen(false) }}
                >
                  <img src={mcHead(a.owner_nickname, 28)} alt="" />
                  <span>{a.label || (a.is_primary ? 'Основная карта' : 'Карта')}</span>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="inp-group">
          <PlayerNicknameInput
            value={transferNick}
            onChange={setTransferNick}
            placeholder={transferMode === 'invoice' ? 'Кто оплатит счёт' : 'Ник игрока'}
          />
        </div>

        <div className="inp-group">
          <div className="bank-amount-input-wrap right">
            <input
              className="bank-amount-input right"
              type="number"
              value={transferAmount}
              onChange={e => setTransferAmount(e.target.value)}
              placeholder="Сумма"
            />
            <span className="bank-amount-icon right"><Gem size={14} /></span>
          </div>
        </div>

        <div className="inp-group">
          <textarea
            value={transferComment}
            onChange={e => setTransferComment(e.target.value.slice(0, 350))}
            placeholder="Комментарий"
            rows={3}
          />
          <div className="bt-char-count">Символов {transferComment.length}/350</div>
        </div>

        {formError && <p className="bank-transfer-error">{formError}</p>}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginTop: 14 }}>
          <button className="btn btn-ghost" onClick={closeModal}>Отменить</button>
          <button className="btn btn-primary" disabled={busy || !transferNick.trim() || !transferAmount} onClick={submitTransfer}>
            {busy
              ? 'Отправка...'
              : <>{transferMode === 'invoice' ? 'Выставить' : 'Перевести'} <Gem size={13} className="bank-btn-diamond" /> {transferAmount || 0}</>}
          </button>
        </div>
      </Modal>

      <Modal open={modal === 'edit'} onClose={closeModal}>
        <h2>Редактировать карту</h2>
        {activeAccount && (
          <div
            className={`bank-card bank-edit-card-preview ${editImageUrl ? 'has-image' : ''}`}
            style={editImageUrl ? { backgroundImage: `url(${editImageUrl})` } : undefined}
          >
            <div className="bank-card-top">
              <span>{editLabel || (activeAccount.is_primary ? 'Основная карта' : 'Карта')}</span>
              <span>I-Bank</span>
            </div>
            <div className="bank-card-bottom">
              <span>{holderName?.toUpperCase()}</span>
              <span className="bank-card-balance"><Gem size={12} /> {Math.round(activeAccount.balance)}</span>
            </div>
          </div>
        )}
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

        <div className="bank-image-row">
          <div className="bank-image-row-label">
            Картинка карты
            {!isIchoPlus && <span className="bank-image-plus-badge"><Lock size={11} /> IchoPlus</span>}
          </div>
          <input
            ref={imageInputRef}
            type="file"
            accept="image/*"
            style={{ display: 'none' }}
            onChange={handleImagePick}
          />
          {isIchoPlus ? (
            <div className="bank-image-actions">
              <button
                type="button"
                className="btn btn-outline bank-image-pick-btn"
                disabled={imageUploading}
                onClick={() => imageInputRef.current?.click()}
              >
                <ImagePlus size={14} /> {imageUploading ? 'Загрузка...' : editImageUrl ? 'Заменить' : 'Загрузить'}
              </button>
              {editImageUrl && (
                <button
                  type="button"
                  className="btn btn-ghost bank-image-clear-btn"
                  disabled={imageUploading}
                  onClick={() => setEditImageUrl('')}
                >
                  <X size={14} /> Убрать
                </button>
              )}
            </div>
          ) : (
            <div className="bank-image-locked">Своя картинка карты доступна подписчикам IchoPlus</div>
          )}
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
                <Crown size={15} className="bank-owner-crown" />
                <span>{accessInfo.owner}</span>
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

      <Modal open={modal === 'fine'} onClose={closeModal} wide>
        <h2><Siren size={18} className="bank-fine-modal-icon" /> Выдать штраф</h2>

        <div className="bt-preview-row">
          <div className="bank-fine-preview-icon"><Siren size={22} /></div>
          <div className="bt-preview-arrow flipped">
            <ArrowRight size={18} className="bt-preview-arrow-icon" />
          </div>
          <div className={`bt-preview-target ${fineNick.trim() ? 'filled' : ''}`}>
            {fineNick.trim() ? (
              <>
                <img src={mcHead(fineNick, 40)} alt="" />
                <span>{fineNick}</span>
              </>
            ) : (
              <span className="bt-preview-placeholder">Игрок не выбран</span>
            )}
          </div>
        </div>

        <div className="inp-group">
          <PlayerNicknameInput
            value={fineNick}
            onChange={setFineNick}
            placeholder="Ник игрока..."
          />
        </div>
        <div className="inp-group">
          <label>Причина</label>
          <input type="text" value={fineReason} onChange={e => setFineReason(e.target.value)} maxLength={200} />
        </div>
        <div className="inp-group">
          <div className="bank-amount-input-wrap right">
            <input
              className="bank-amount-input right"
              type="number"
              value={fineAmount}
              onChange={e => setFineAmount(e.target.value)}
              placeholder="Сумма штрафа"
            />
            <span className="bank-amount-icon right"><Gem size={14} /></span>
          </div>
        </div>
        <div className="inp-group">
          <textarea
            value={fineComment}
            onChange={e => setFineComment(e.target.value.slice(0, 350))}
            placeholder="Комментарий (необязательно)"
            rows={3}
          />
          <div className="bt-char-count">Символов {fineComment.length}/350</div>
        </div>
        {fineFormError && <p className="bank-transfer-error">{fineFormError}</p>}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginTop: 16 }}>
          <button className="btn btn-ghost" onClick={closeModal}>Отменить</button>
          <button className="bank-fine-submit-btn" disabled={fineBusy} onClick={submitFine}>
            {fineBusy
              ? 'Отправка...'
              : <>Оштрафовать <Gem size={13} className="bank-btn-diamond" /> {fineAmount || 0}</>}
          </button>
        </div>
      </Modal>
    </div>
  )
}
