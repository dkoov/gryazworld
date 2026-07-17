import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Scale, ExternalLink, Gem } from 'lucide-react'
import { apiFetch, getDiscordUser } from '../api'
import Modal from '../components/Modal'
import PlayerNicknameInput from '../components/PlayerNicknameInput'
import './CourtPage.css'

function mcHead(nickname, size = 64) {
  return `https://mc-heads.net/avatar/${encodeURIComponent(nickname)}/${size}`
}

function formatDateTime(iso) {
  const d = new Date(iso)
  return `${d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' })}, ${d.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })}`
}

function ClaimRow({ c, i, onReview }) {
  return (
    <div className={`court-row status-${c.status}`} style={{ animationDelay: `${i * 0.04}s` }}>
      <img className="court-avatar" src={mcHead(c.defendant || '?', 64)} alt="" />
      <div className="court-row-info">
        <div className="court-row-subject">{c.subject}</div>
        <div className="court-row-parties">
          {c.plaintiff || 'Discord'} против {c.defendant}
          {!c.defendant_resolved && ' (не привязан)'}
        </div>
        <div className="court-row-meta">
          {formatDateTime(c.created_at)}
          {c.status === 'pending' && <span className="court-status pending">на рассмотрении</span>}
          {c.status === 'approved' && <span className="court-status approved">оштрафован</span>}
          {c.status === 'dismissed' && <span className="court-status dismissed">отклонён</span>}
          {c.thread_url && (
            <a href={c.thread_url} target="_blank" rel="noreferrer" className="court-thread-link">
              <ExternalLink size={11} /> тред
            </a>
          )}
        </div>
      </div>
      <div className="court-row-right">
        {c.status === 'pending' ? (
          <button className="court-review-btn" onClick={() => onReview(c)}>Рассмотреть</button>
        ) : (
          c.resolved_by && <div className="court-resolved-by">{c.resolved_by}</div>
        )}
      </div>
    </div>
  )
}

export default function CourtPage() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [allowed, setAllowed] = useState(false)
  const [claims, setClaims] = useState([])

  const [claimModal, setClaimModal] = useState(null)
  const [busy, setBusy] = useState(false)
  const [formError, setFormError] = useState('')
  const [amount, setAmount] = useState('')
  const [reason, setReason] = useState('')
  const [comment, setComment] = useState('')
  const [defendantNick, setDefendantNick] = useState('')

  useEffect(() => {
    if (!getDiscordUser()) { navigate('/cabinet'); return }
    apiFetch('/web/court/permissions')
      .then(p => {
        setAllowed(!!p.can_review)
        if (p.can_review) loadClaims()
      })
      .catch(() => setAllowed(false))
      .finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function loadClaims() {
    apiFetch('/web/court/claims').then(setClaims).catch(() => {})
  }

  function openReview(claim) {
    setClaimModal(claim)
    setAmount('')
    setReason(claim.subject || '')
    setComment('')
    setDefendantNick(claim.defendant_resolved ? '' : claim.defendant || '')
    setFormError('')
  }

  function closeReview() {
    setClaimModal(null)
    setFormError('')
  }

  async function submitApprove() {
    if (!claimModal) return
    setFormError('')
    const amt = Number(amount)
    if (!amt || amt <= 0) { setFormError('Введи сумму штрафа'); return }
    if (!reason.trim()) { setFormError('Введи причину'); return }
    setBusy(true)
    try {
      await apiFetch(`/web/court/claims/${claimModal.id}/approve`, {
        method: 'POST',
        body: JSON.stringify({
          defendant_nickname: defendantNick.trim() || undefined,
          amount: amt,
          reason: reason.trim(),
          comment: comment.trim(),
        }),
      })
      closeReview()
      loadClaims()
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function submitDismiss() {
    if (!claimModal) return
    setFormError('')
    setBusy(true)
    try {
      await apiFetch(`/web/court/claims/${claimModal.id}/dismiss`, {
        method: 'POST',
        body: JSON.stringify({ comment: comment.trim() }),
      })
      closeReview()
      loadClaims()
    } catch (e) {
      setFormError(e.message)
    } finally {
      setBusy(false)
    }
  }

  if (loading) {
    return (
      <div className="court-page page-fade">
        <div className="court-loading"><div className="spinner" /></div>
      </div>
    )
  }

  if (!allowed) {
    return (
      <div className="court-page page-fade">
        <h1 className="page-title">Суд</h1>
        <div className="court-denied">
          <div className="court-denied-title">Нет доступа</div>
          <div className="court-denied-desc">Рассматривать иски могут только Судья и администраторы.</div>
          <button className="btn btn-primary" onClick={() => navigate('/')}>На главную</button>
        </div>
      </div>
    )
  }

  const pending = claims.filter(c => c.status === 'pending')
  const resolved = claims.filter(c => c.status !== 'pending')

  return (
    <div className="court-page page-fade">
      <h1 className="page-title">Суд<em>ебные иски</em></h1>

      {claims.length === 0 ? (
        <div className="court-empty">Пока нет поданных исков</div>
      ) : (
        <>
          {pending.length > 0 && (
            <div className="court-section">
              <div className="court-section-title">
                На рассмотрении <span className="court-count">{pending.length}</span>
              </div>
              <div className="court-list">
                {pending.map((c, i) => <ClaimRow key={c.id} c={c} i={i} onReview={openReview} />)}
              </div>
            </div>
          )}
          {resolved.length > 0 && (
            <div className="court-section">
              <div className="court-section-title">Рассмотренные</div>
              <div className="court-list">
                {resolved.map((c, i) => <ClaimRow key={c.id} c={c} i={i} />)}
              </div>
            </div>
          )}
        </>
      )}

      <Modal open={!!claimModal} onClose={closeReview} wide>
        <h2>Рассмотрение иска</h2>
        {claimModal && (
          <>
            <div className="court-detail">
              <div className="court-detail-row">
                <span className="court-detail-label">Истец</span>
                <span>{claimModal.plaintiff || 'не привязан на сайте'}</span>
              </div>
              <div className="court-detail-row">
                <span className="court-detail-label">Ответчик</span>
                <span>{claimModal.defendant}{!claimModal.defendant_resolved && ' (не найден по нику)'}</span>
              </div>
              <div className="court-detail-row">
                <span className="court-detail-label">Суть</span>
                <span>{claimModal.subject}</span>
              </div>
              <div className="court-description">{claimModal.description}</div>
              {claimModal.thread_url && (
                <a href={claimModal.thread_url} target="_blank" rel="noreferrer" className="court-thread-link">
                  <ExternalLink size={12} /> Открыть тред в Discord
                </a>
              )}
            </div>

            {!claimModal.defendant_resolved && (
              <div className="inp-group">
                <label>Ник ответчика на сайте</label>
                <PlayerNicknameInput
                  value={defendantNick}
                  onChange={setDefendantNick}
                  placeholder="Ник игрока..."
                />
              </div>
            )}

            <div className="inp-group">
              <label>Причина штрафа</label>
              <input type="text" value={reason} onChange={e => setReason(e.target.value)} maxLength={200} />
            </div>

            <div className="inp-group">
              <div className="court-amount-input-wrap">
                <input
                  className="court-amount-input"
                  type="number"
                  value={amount}
                  onChange={e => setAmount(e.target.value)}
                  placeholder="Сумма штрафа"
                />
                <span className="court-amount-icon"><Gem size={14} /></span>
              </div>
            </div>

            <div className="inp-group">
              <textarea
                value={comment}
                onChange={e => setComment(e.target.value.slice(0, 350))}
                placeholder="Комментарий (необязательно)"
                rows={3}
              />
              <div className="court-char-count">Символов {comment.length}/350</div>
            </div>

            {formError && <p className="court-error">{formError}</p>}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginTop: 14 }}>
              <button className="btn btn-ghost" onClick={closeReview}>Закрыть</button>
              <button className="court-dismiss-btn" disabled={busy} onClick={submitDismiss}>Отклонить иск</button>
              <button className="btn btn-primary" disabled={busy} onClick={submitApprove}>Оштрафовать</button>
            </div>
          </>
        )}
      </Modal>
    </div>
  )
}
