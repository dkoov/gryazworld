import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ShieldCheck } from 'lucide-react'
import { createPayment, getDiscordUser } from '../api'
import './AccessPage.css'
import './ShopPage.css'

const SERVICES = [
  { sku: 'unban', name: 'Разбан', price: '599', desc: 'Снятие бана с аккаунта и восстановление доступа к серверу.' },
  { sku: 'unmute', name: 'Размут', price: '199', desc: 'Снятие мута с аккаунта и восстановление доступа к чату.' },
  { sku: 'unwarn', name: 'Разварн', price: '49', desc: 'Снятие варна с аккаунта.' },
]

const PASS_FEATURES = ['Полный доступ к серверу', 'Роль «Игрок» в Discord', 'Белый список на сервере', 'Java Edition']
const PLUS_FEATURES = ['Роль IchoPlus в Discord', 'Цветной ник в чате', 'Расширенные команды']

const SUBS = [
  { key: '1', sku: 'ichoplus_1m', label: 'IchoPlus 1 месяц', days: 'на 30 дней', price: '159' },
  { key: '2', sku: 'ichoplus_2m', label: 'IchoPlus 2 месяца', days: 'на 60 дней', price: '399' },
  { key: '3', sku: 'ichoplus_3m', label: 'IchoPlus 3 месяца', days: 'на 90 дней', price: '699', badge: 'Выгодно' },
]

const FAQS = [
  { q: 'Нужна лицензия Minecraft?', a: 'Нет, можно играть с пиратской версии Java Edition. Официальная лицензия тоже подходит.' },
  { q: 'Как быстро дают доступ после оплаты?', a: 'Автоматически, в течение нескольких секунд. Система сама добавляет ник в белый список и выдаёт роль в Discord.' },
  { q: 'Можно вернуть деньги?', a: 'Да, если наиграли меньше 2 часов и прошло не более 3 дней — напишите администратору в Discord.' },
  { q: 'Зачем платить за доступ?', a: 'Платный доступ отсеивает гриферов. Сервер существует на деньги от проходок, а не донат-привилегии.' },
]

async function buy(sku, navigate, setLoadingSku) {
  if (!getDiscordUser()) {
    navigate('/cabinet')
    return
  }
  setLoadingSku(sku)
  try {
    const { confirmation_url } = await createPayment([{ sku, qty: 1 }])
    window.location.href = confirmation_url
  } catch (e) {
    alert(e.message || 'Не удалось создать платёж')
    setLoadingSku(null)
  }
}

export default function ShopPage() {
  const navigate = useNavigate()
  const [loadingSku, setLoadingSku] = useState(null)
  const [showOtherSubs, setShowOtherSubs] = useState(false)
  const [faqOpen, setFaqOpen] = useState({})

  const highlighted = SUBS.find(s => s.badge) || SUBS[0]
  const others = SUBS.filter(s => s.key !== highlighted.key)

  return (
    <section className="section shop-page">
      <div className="shop-header">
        <div className="section-label">Магазин</div>
        <div className="section-title">Услуги и подписки</div>
        <p className="section-sub" style={{ margin: '0 auto 0' }}>Дополнительные товары для уже зарегистрированных игроков.</p>
      </div>

      <div className="notice shop-notice">
        <strong>Важно:</strong> покупки доступны только после входа через Discord и привязки Minecraft-ника.
      </div>

      <div className="shop-block">
        <div className="shop-block-label">Сезонная проходка</div>
        <div className="pcard featured shop-pass-card">
          <div className="access-price-anchor">
            <s className="access-old-price">499₽</s>
            <span className="access-discount">-48%</span>
          </div>
          <div className="pprice">259<span> ₽</span></div>
          <div className="pperiod">на весь сезон</div>
          <ul className="pfeats pfeats-inline">
            {PASS_FEATURES.map((f, i) => <li key={i}>{f}</li>)}
          </ul>
          <div className="access-guarantee">
            <ShieldCheck className="access-guarantee-ico" size={18} strokeWidth={1.8} />
            <div className="access-guarantee-text">
              <div className="access-guarantee-line1">Возврат 100% в первые 2 часа</div>
            </div>
          </div>
          <button
            className="btn btn-primary"
            disabled={loadingSku === 'access_seasonal'}
            onClick={() => buy('access_seasonal', navigate, setLoadingSku)}
          >
            {loadingSku === 'access_seasonal' ? 'Переход…' : 'Купить'}
          </button>
        </div>
      </div>

      <div className="shop-block">
        <div className="shop-block-label">Подписка IchoPlus</div>
        <div className="shop-subs-grid">
          <div className="pcard featured shop-sub-highlighted">
            <div className="pcard-badge-area">
              {highlighted.badge && <div className="pbadge">{highlighted.badge}</div>}
            </div>
            <div className="pname">{highlighted.label}</div>
            <div className="pprice">{highlighted.price}<span> ₽</span></div>
            <div className="pperiod">{highlighted.days}</div>
            <ul className="pfeats">
              {PLUS_FEATURES.map((f, i) => <li key={i}>{f}</li>)}
            </ul>
            <button
              className="btn btn-primary"
              disabled={loadingSku === highlighted.sku}
              onClick={() => buy(highlighted.sku, navigate, setLoadingSku)}
            >
              {loadingSku === highlighted.sku ? 'Переход…' : 'Купить'}
            </button>
          </div>

          <div className="shop-sub-others-col">
            <button className="shop-toggle-others" onClick={() => setShowOtherSubs(v => !v)}>
              {showOtherSubs ? 'Скрыть другие сроки' : `Показать другие сроки (${others.length})`}
            </button>
            {showOtherSubs && others.map(s => (
              <div key={s.key} className="shop-sub-other-card">
                <div className="shop-sub-other-top">
                  <div className="shop-sub-other-name">{s.label}</div>
                  {s.badge && <div className="pbadge" style={{ fontSize: 8 }}>{s.badge}</div>}
                </div>
                <div className="shop-sub-other-days">{s.days}</div>
                <div className="shop-sub-other-bottom">
                  <div className="shop-sub-other-price">{s.price}₽</div>
                  <button
                    className="btn btn-outline shop-sub-other-btn"
                    disabled={loadingSku === s.sku}
                    onClick={() => buy(s.sku, navigate, setLoadingSku)}
                  >
                    {loadingSku === s.sku ? '...' : 'Купить'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="shop-block">
        <div className="shop-services-list">
          {SERVICES.map((s, i) => (
            <div key={s.sku} className={`shop-service-row ${i < SERVICES.length - 1 ? 'bordered' : ''}`}>
              <div>
                <div className="shop-service-name">{s.name}</div>
                <div className="shop-service-desc">{s.desc}</div>
              </div>
              <div className="shop-service-right">
                <div className="shop-service-price-block">
                  <div className="shop-service-price">{s.price}<span> ₽</span></div>
                  <div className="shop-service-once">единоразово</div>
                </div>
                <button
                  className="btn btn-outline"
                  disabled={loadingSku === s.sku}
                  onClick={() => buy(s.sku, navigate, setLoadingSku)}
                >
                  {loadingSku === s.sku ? '...' : 'Купить'}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="shop-block shop-faq-block">
        <div className="section-label">FAQ</div>
        <div className="section-title" style={{ fontSize: 22, marginBottom: 20 }}>Частые вопросы</div>
        <div className="faq-list">
          {FAQS.map((item, i) => (
            <div key={i} className={`faq-item ${faqOpen[i] ? 'open' : ''}`}>
              <button className="faq-q" onClick={() => setFaqOpen(o => ({ ...o, [i]: !o[i] }))}>
                {item.q}
                <i className="faq-icon">+</i>
              </button>
              <div className="faq-a"><p>{item.a}</p></div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
