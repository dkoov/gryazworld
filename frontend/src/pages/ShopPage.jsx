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
          name="Разбан"
          price="1000"
          period="единоразово"
          features={['Снятие бана с аккаунта', 'Восстановление доступа к серверу']}
        />
        <PriceCard
          name="Размут"
          price="300"
          period="единоразово"
          features={['Снятие мута с аккаунта', 'Восстановление доступа к чату']}
        />
        <PriceCard
          name="Подписка Plus"
          price="300"
          period="в месяц"
          featured
          badge="Новинка"
          features={[]}
        />
      </div>
    </section>
  )
}

function PriceCard({ name, price, period, features, featured, badge }) {
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
      <button className={`btn ${featured ? 'btn-primary' : 'btn-outline'}`}>
        Купить
      </button>
    </div>
  )
}
