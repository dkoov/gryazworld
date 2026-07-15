import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { Zap, ShieldCheck, Users, Calendar, Clock } from 'lucide-react'
import { createPayment, getDiscordUser } from '../api'
import './AccessPage.css'

export default function AccessPage() {
  return (
    <section className="section">
      <div className="section-label">Проходка</div>
      <div className="section-title">Выберите тариф</div>
      <p className="section-sub">Проходка даёт доступ к серверу. Никаких доп. преимуществ — только игра на равных.</p>

      <div className="notice">
        <strong>Важно:</strong> для покупки необходимо войти через Discord и указать ник в Minecraft. После оплаты доступ выдаётся автоматически.
      </div>

      <div className="access-hero-grid">
        <div className="access-hero-left">
          <h2 className="access-hero-title">Один тариф. Полный доступ. Никакой возни.</h2>
          <p className="access-hero-sub">
            Мы не разделяем игроков на «донат» и «остальных». Одна проходка — одна цена — весь сезон.
          </p>
          <ul className="access-benefits">
            <li className="access-benefit">
              <Zap className="access-benefit-ico" size={20} strokeWidth={1.8} />
              <span>Мгновенная выдача доступа после оплаты</span>
            </li>
            <li className="access-benefit">
              <ShieldCheck className="access-benefit-ico" size={20} strokeWidth={1.8} />
              <span>Проверенные игроки — минимум гриферов</span>
            </li>
            <li className="access-benefit">
              <Users className="access-benefit-ico" size={20} strokeWidth={1.8} />
              <span>Дружелюбное сообщество</span>
            </li>
            <li className="access-benefit">
              <Calendar className="access-benefit-ico" size={20} strokeWidth={1.8} />
              <span>Весь сезон без доплат</span>
            </li>
          </ul>
          <p className="access-hero-note">
            Проходка не даёт преимуществ. Это просто плата за то, чтобы играть на честном сервере.
          </p>
        </div>

        <div className="access-hero-right">
          <div className="access-single-card">
            <PriceCard sku="access_seasonal" name="Сезонная проходка" price="259" period="на весь сезон" featured features={['Полный доступ к серверу', 'Роль «Игрок» в Discord', 'Белый список на сервере', 'Java Edition']} />
          </div>
        </div>
      </div>

      <div style={{ marginTop: 60 }}>
        <div className="section-label">FAQ</div>
        <div className="section-title" style={{ fontSize: 22, marginBottom: 24 }}>Частые вопросы</div>
        <div className="faq-list">
          <FaqItem q="Нужна лицензия Minecraft?" a="Нет, можно играть с пиратской версии Java Edition. Официальная лицензия тоже подходит." />
          <FaqItem q="Как быстро дают доступ после оплаты?" a="Автоматически, в течение нескольких секунд. Система сама добавляет ник в белый список и выдаёт роль в Discord." />
          <FaqItem q="Можно вернуть деньги?" a="Да, если наиграли меньше 2 часов и прошло не более 3 дней — напишите администратору в Discord." />
          <FaqItem q="Зачем платить за доступ?" a="Платный доступ отсеивает гриферов. Сервер существует на деньги от проходок, а не донат-привилегии." />
        </div>
      </div>
    </section>
  )
}

function useCountUp(target, duration = 800) {
  const [value, setValue] = useState(0)
  const ref = useRef(null)
  const startedRef = useRef(false)

  useEffect(() => {
    const el = ref.current
    if (!el) return
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && !startedRef.current) {
          startedRef.current = true
          const start = performance.now()
          const animate = (now) => {
            const elapsed = now - start
            if (elapsed >= duration) {
              setValue(target)
              return
            }
            const progress = elapsed / duration
            const easeOut = 1 - Math.pow(1 - progress, 3)
            setValue(Math.floor(target * easeOut))
            requestAnimationFrame(animate)
          }
          requestAnimationFrame(animate)
        }
      },
      { threshold: 0.3 }
    )
    observer.observe(el)
    return () => observer.disconnect()
  }, [target, duration])

  return [value, ref]
}

function PriceCard({ sku, name, price, period, features, featured, badge }) {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const numericPrice = featured ? Number(price) : 0
  const [animatedPrice, priceRef] = useCountUp(numericPrice, 800)

  async function handleBuy() {
    if (!getDiscordUser()) {
      navigate('/cabinet')
      return
    }
    setLoading(true)
    try {
      const { confirmation_url } = await createPayment([{ sku, qty: 1 }])
      window.location.href = confirmation_url
    } catch (e) {
      alert(e.message || 'Не удалось создать платёж')
      setLoading(false)
    }
  }

  return (
    <div className={`pcard ${featured ? 'featured' : ''}`}>
      <div className="pcard-badge-area">
        {badge && <div className="pbadge">{badge}</div>}
      </div>
      <div className="pname">{name}</div>
      <div className="access-price-anchor">
        <s className="access-old-price">499₽</s>
        <span className="access-discount">-48%</span>
      </div>
      <div className="pprice" ref={priceRef}>{featured ? animatedPrice : price}<span> &#8381;</span></div>
      <div className="pperiod">{period}</div>
      <div className="access-urgency">
        <Clock className="access-urgency-ico" size={14} strokeWidth={1.8} />
        <span>Стартовая цена сезона — вырастет 1 августа</span>
      </div>
      <ul className="pfeats">
        {features.map((f, i) => <li key={i}>{f}</li>)}
      </ul>
      <div className="access-guarantee">
        <ShieldCheck className="access-guarantee-ico" size={20} strokeWidth={1.8} />
        <div className="access-guarantee-text">
          <div className="access-guarantee-line1">Возврат 100% в первые 2 часа</div>
          <div className="access-guarantee-line2">Не понравилось — вернём деньги без вопросов</div>
        </div>
      </div>
      <button
        className={`btn ${featured ? 'btn-primary access-pulse-btn' : 'btn-outline'}`}
        onClick={handleBuy}
        disabled={loading}
      >
        {loading ? 'Переход…' : 'КУПИТЬ'}
      </button>
    </div>
  )
}

function FaqItem({ q, a }) {
  const [open, setOpen] = useState(false)
  return (
    <div className={`faq-item ${open ? 'open' : ''}`}>
      <button className="faq-q" onClick={() => setOpen(!open)}>
        {q}
        <i className="faq-icon">+</i>
      </button>
      <div className="faq-a"><p>{a}</p></div>
    </div>
  )
}
