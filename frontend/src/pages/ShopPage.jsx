import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createPayment, getDiscordUser } from '../api'
import './AccessPage.css'

export default function ShopPage() {
  return (
    <section className="section">
      <div className="section-label">Магазин</div>
      <div className="section-title">Услуги и подписки</div>
      <p className="section-sub">Дополнительные товары для уже зарегистрированных игроков.</p>

      <div className="notice">
        <strong>Важно:</strong> покупки доступны только после входа через Discord и привязки Minecraft-ника.
      </div>

      <div className="pricing-grid pricing-grid-3">
        <PriceCard
          sku="unban"
          name="Разбан"
          price="1000"
          period="единоразово"
          features={['Снятие бана с аккаунта', 'Восстановление доступа к серверу']}
        />
        <PriceCard
          sku="unmute"
          name="Размут"
          price="300"
          period="единоразово"
          features={['Снятие мута с аккаунта', 'Восстановление доступа к чату']}
        />
        <PriceCard
          sku="unwarn"
          name="Разварн"
          price="50"
          period="единоразово"
          features={['Снятие варна с аккаунта']}
        />
      </div>
    </section>
  )
}

function PriceCard({ sku, name, price, period, features, featured, badge }) {
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
    <div className={`pcard ${featured ? 'featured' : ''}`}>
      <div className="pcard-badge-area">
        {badge && <div className="pbadge">{badge}</div>}
      </div>
      <div className="pname">{name}</div>
      <div className="pprice">{price}<span> &#8381;</span></div>
      <div className="pperiod">{period}</div>
      <ul className="pfeats">
        {features.map((f, i) => <li key={i}>{f}</li>)}
      </ul>
      <button
        className={`btn ${featured ? 'btn-primary' : 'btn-outline'}`}
        onClick={handleBuy}
        disabled={loading}
      >
        {loading ? 'Переход…' : 'Купить'}
      </button>
    </div>
  )
}
