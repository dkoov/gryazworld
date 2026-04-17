import { useState } from 'react'
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

      <div className="pricing-grid pricing-grid-3">
        <PriceCard name="Месячная проходка" price="249" period="на 30 дней" features={['Полный доступ к серверу', 'Роль «Игрок» в Discord', 'Белый список на сервере', 'Java Edition']} />
        <PriceCard name="Проходка на 3 месяца" price="499" period="на 90 дней" featured badge="Выгодно" features={['Полный доступ к серверу', 'Роль «Игрок» в Discord', 'Белый список на сервере', 'Java Edition', 'Экономия против месячной']} />
        <PriceCard name="Сезонная проходка" price="699" period="на весь сезон (9–12 мес.)" features={['Полный доступ к серверу', 'Роль «Игрок» в Discord', 'Белый список на сервере', 'Java Edition', 'Приоритетная поддержка']} />
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
