import { useEffect, useState } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { apiFetch } from '../api'

const TERMINAL_STATUSES = ['paid', 'canceled', 'failed', 'refunded']
const POLL_INTERVAL_MS = 2000
const MAX_ATTEMPTS = 15 // 15 * 2с = 30с

export default function PaymentReturnPage() {
  const [params] = useSearchParams()
  const orderId = params.get('order')
  const [order, setOrder] = useState(null)
  const [error, setError] = useState(null)
  const [timedOut, setTimedOut] = useState(false)

  useEffect(() => {
    if (!orderId) return
    let cancelled = false
    let attempts = 0

    const tick = async () => {
      if (cancelled) return
      try {
        const data = await apiFetch(`/web/orders/${orderId}`)
        if (cancelled) return
        setOrder(data)
        if (TERMINAL_STATUSES.includes(data.status)) return
        attempts += 1
        if (attempts >= MAX_ATTEMPTS) {
          setTimedOut(true)
          return
        }
        setTimeout(tick, POLL_INTERVAL_MS)
      } catch (e) {
        if (!cancelled) setError(e.message)
      }
    }

    tick()
    return () => { cancelled = true }
  }, [orderId])

  if (!orderId) {
    return (
      <section className="section">
        <div className="section-label">Оплата</div>
        <div className="section-title">Нет идентификатора заказа</div>
      </section>
    )
  }

  if (error) {
    return (
      <section className="section">
        <div className="section-label">Оплата</div>
        <div className="section-title">Ошибка</div>
        <p className="section-sub">{error}</p>
      </section>
    )
  }

  let title = 'Обрабатывается…'
  let sub = 'Подождите, проверяем статус оплаты.'
  if (order?.status === 'paid') {
    title = 'Оплачено'
    sub = 'Товар будет выдан автоматически в ближайшие минуты.'
  } else if (order?.status === 'canceled') {
    title = 'Платёж отменён'
    sub = 'Если это ошибка — попробуйте ещё раз.'
  } else if (order?.status === 'failed') {
    title = 'Ошибка оплаты'
    sub = 'Не удалось провести платёж. Попробуйте ещё раз.'
  } else if (order?.status === 'refunded') {
    title = 'Возврат средств'
    sub = 'По этому заказу произведён возврат.'
  } else if (timedOut) {
    title = 'Платёж ещё обрабатывается'
    sub = 'Это может занять пару минут. Обновите страницу позже или загляните в личный кабинет.'
  }

  return (
    <section className="section">
      <div className="section-label">Оплата</div>
      <div className="section-title">{title}</div>
      <p className="section-sub">{sub}</p>
      <div style={{ marginTop: 32, display: 'flex', gap: 12, flexWrap: 'wrap' }}>
        <Link className="btn btn-primary" to="/cabinet">Личный кабинет</Link>
        <Link className="btn btn-outline" to="/shop">В магазин</Link>
      </div>
    </section>
  )
}
