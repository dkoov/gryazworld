import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { Zap, ShieldCheck, Users, Calendar, Clock, Check } from 'lucide-react'
import { createPayment, getDiscordUser } from '../api'
import './ShopPage.css'

const faqs = [
  {
    q: 'Нужна лицензия Minecraft?',
    a: 'Нет, можно играть с пиратской версии Java Edition. Официальная лицензия тоже подходит.',
  },
  {
    q: 'Как быстро дают доступ после оплаты?',
    a: 'Автоматически, в течение нескольких секунд. Система сама добавляет ник в белый список и выдаёт роль в Discord.',
  },
  {
    q: 'Можно вернуть деньги?',
    a: 'Да, если наиграли меньше 2 часов — напишите администратору в Discord.',
  },
  {
    q: 'Зачем платить за доступ?',
    a: 'Платный доступ отсеивает гриферов. Сервер существует на деньги от проходок, а не донат-привилегии.',
  },
]

const accessBenefits = [
  { icon: Zap, text: 'Мгновенная выдача доступа после оплаты' },
  { icon: ShieldCheck, text: 'Проверенные игроки — минимум гриферов' },
  { icon: Users, text: 'Дружелюбное сообщество' },
  { icon: Calendar, text: 'Весь сезон без доната' },
]

export default function ShopPage() {
  return (
    <section className="section shop-section">
      <div className="shop-inner">
        <div className="shop-header">
          <div className="section-label">Магазин</div>
          <div className="section-title">Услуги и подписки</div>
          <p className="shop-sub">Дополнительные товары для уже зарегистрированных игроков.</p>
        </div>

        <div className="shop-notice">
          <strong>Важно:</strong> покупки доступны только после входа через Discord и привязки Minecraft-ника.
        </div>

        <div className="shop-hero">
          <div className="shop-hero-left">
            <h2 className="shop-hero-title">Один тариф. Полный доступ. Никаких возни.</h2>
            <p className="shop-hero-sub">
              Мы не разделяем игроков на "випов" и "смертных". Одна проходка — один доступ — весь сезон.
            </p>
            <ul className="shop-benefits">
              {accessBenefits.map(({ icon: Icon, text }, i) => (
                <li key={i} className="shop-benefit">
                  <Icon className="shop-benefit-ico" size={20} strokeWidth={1.8} />
                  <span>{text}</span>
                </li>
              ))}
            </ul>
            <p className="shop-hero-note">
              Проходка не даёт преимуществ. Это просто плата за то, чтобы играть на честном сервере.
            </p>
          </div>

          <div className="shop-hero-right">
            <PriceCard
              sku="access_seasonal"
              name="Сезонная проходка"
              price="259"
              oldPrice="499"
              discount="−48%"
              period="на весь сезон"
              featured
              badge="Сезонный хит"
              features={['Полный доступ к серверу', 'Роль «Игрок» в Discord', 'Белый список на сервере', 'Java Edition']}
            />
          </div>
        </div>

        <div className="shop-divider" />

        <div className="shop-section-header">
          <div className="section-label">Подписка</div>
          <div className="section-title">IchoPlus</div>
          <p className="section-sub">Дополнительные возможности и статус для активных игроков.</p>
        </div>

        <div className="shop-subs-grid">
          <PriceCard
            sku="ichoplus_1m"
            name="IchoPlus 1 месяц"
            price="199"
            period="на 30 дней"
            featured
            features={['Роль IchoPlus в Discord', 'Цветной ник в чате', 'Расширенные команды']}
          />
          <div className="shop-subs-side">
            <CompactPriceCard
              sku="ichoplus_2m"
              name="IchoPlus 2 месяца"
              price="349"
              period="на 60 дней"
              perMonth="≈175₽/мес"
            />
            <CompactPriceCard
              sku="ichoplus_3m"
              name="IchoPlus 3 месяца"
              price="449"
              period="на 90 дней"
              badge="выгода"
              perMonth="≈150₽/мес"
            />
          </div>
        </div>

        <div className="shop-divider" />

        <div className="shop-services-grid">
          <PriceCard
            sku="unban"
            name="Разбан"
            price="599"
            period="без обнуления"
            note="Выдача сразу после оплаты"
            features={['Снятие бана с аккаунта', 'Восстановление доступа к серверу']}
          />
          <PriceCard
            sku="unmute"
            name="Размут"
            price="199"
            period="без обнуления"
            note="Выдача сразу после оплаты"
            features={['Снятие мута', 'Восстановление доступа к чату']}
          />
          <PriceCard
            sku="unwarn"
            name="Разварн"
            price="49"
            period="без обнуления"
            note="Выдача сразу после оплаты"
            features={['Снятие одного варна']}
          />
        </div>

        <div className="shop-divider" />

        <div className="shop-section-header">
          <div className="section-label">FAQ</div>
          <div className="section-title">Частые вопросы</div>
        </div>

        <div className="shop-faq-list">
          {faqs.map(({ q, a }, i) => (
            <FaqItem key={i} q={q} a={a} />
          ))}
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

function PriceCard({ sku, name, price, oldPrice, discount, period, features, featured, badge, note }) {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const numericPrice = sku === 'access_seasonal' ? Number(price) : 0
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
    <div className={`shop-card ${featured ? 'featured' : ''}`}>
      <div className="shop-card-top">
        <div className="shop-card-badge-area">
          {badge && <div className="shop-badge">{badge}</div>}
        </div>
        <div className="shop-card-name">{name}</div>
        {oldPrice && discount && (
          <div className="shop-price-anchor">
            <s className="shop-old-price">{oldPrice}₽</s>
            <span className="shop-discount">{discount}</span>
          </div>
        )}
        <div className="shop-card-price" ref={priceRef}>{sku === 'access_seasonal' ? animatedPrice : price}<span>₽</span></div>
        <div className="shop-card-period">{period}{note && <span className="shop-card-note">{note}</span>}</div>
        {sku === 'access_seasonal' && (
          <div className="shop-urgency">
            <Clock className="shop-urgency-ico" size={14} strokeWidth={1.8} />
            <span>Стартовая цена сезона — вырастет 1 августа</span>
          </div>
        )}
      </div>

      {sku === 'access_seasonal' && (
        <div className="shop-guarantee">
          <ShieldCheck className="shop-guarantee-ico" size={20} strokeWidth={1.8} />
          <div className="shop-guarantee-text">
            <div className="shop-guarantee-line1">Возврат 100% в первые 2 часа</div>
            <div className="shop-guarantee-line2">Не понравилось — вернём деньги без вопросов</div>
          </div>
        </div>
      )}

      <ul className="shop-card-features">
        {features.map((f, i) => (
          <li key={i}><Check size={16} strokeWidth={2.5} />{f}</li>
        ))}
      </ul>

      <button
        className={`btn ${featured && sku === 'access_seasonal' ? 'btn-primary shop-pulse-btn' : featured ? 'btn-primary' : 'btn-outline'}`}
        onClick={handleBuy}
        disabled={loading}
      >
        {loading ? 'Переход…' : 'Купить'}
      </button>
    </div>
  )
}

function CompactPriceCard({ sku, name, price, period, badge, perMonth }) {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)

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
    <div className="shop-compact-card">
      <div className="shop-compact-main">
        <div className="shop-compact-header">
          <span className="shop-compact-name">{name}</span>
          {badge && <span className="shop-badge small">{badge}</span>}
        </div>
        <div className="shop-compact-price">{price}<span>₽</span></div>
        <div className="shop-compact-period">{period}{perMonth && <span className="shop-compact-per-month">{perMonth}</span>}</div>
      </div>
      <button
        className="btn btn-outline"
        onClick={handleBuy}
        disabled={loading}
      >
        {loading ? 'Переход…' : 'Купить'}
      </button>
    </div>
  )
}

function FaqItem({ q, a }) {
  const [open, setOpen] = useState(false)
  return (
    <div className={`shop-faq-item ${open ? 'open' : ''}`}>
      <button className="shop-faq-q" onClick={() => setOpen(!open)}>
        {q}
        <span className="shop-faq-icon">+</span>
      </button>
      <div className="shop-faq-a"><p>{a}</p></div>
    </div>
  )
}
