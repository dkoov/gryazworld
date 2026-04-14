import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch } from '../api'
import './HomePage.css'

export default function HomePage() {
  const navigate = useNavigate()
  const [online, setOnline] = useState(0)
  const [servers, setServers] = useState({})

  useEffect(() => {
    apiFetch('/web/server-stats').then(d => {
      setOnline(d.online)
      setServers(d.servers || {})
    }).catch(() => {})
  }, [])

  return (
    <>
      {/* Hero */}
      <div className="hero">
        <div className="hero-bg" />
        <div className="hero-content">
          <div className="hero-tag">Ванильный сервер &middot; Java Edition &middot; Minecraft</div>
          <h1>
            <span className="l1">Выживание без</span>
            <span className="l2">компромиссов</span>
          </h1>
          <p className="hero-desc">
            Приватный Minecraft-сервер с акцентом на честную игру, живое сообщество и реальную экономику. Никаких донат-привилегий.
          </p>
          <div className="hero-actions">
            <button className="btn btn-primary" onClick={() => navigate('/access')}>Купить проходку</button>
            <a href="#about" className="btn btn-outline">О сервере</a>
          </div>
          <div className="hero-stats">
            <div className="hstat">
              <span className="hstat-v">{online}</span>
              <span className="hstat-l">Онлайн</span>
              {Object.keys(servers).length > 0 && (
                <span className="hstat-sub">
                  {Object.entries(servers).map(([name, s]) => `${name}: ${s.online}`).join(' \u00b7 ')}
                </span>
              )}
            </div>
            <div className="hstat-div" />
            <div className="hstat"><span className="hstat-v">Ваниль</span><span className="hstat-l">Без доната</span></div>
            <div className="hstat-div" />
            <div className="hstat"><span className="hstat-v">Java</span><span className="hstat-l">Edition</span></div>
          </div>
        </div>
      </div>

      {/* About */}
      <section className="section section-alt" id="about">
        <div className="section-label">О сервере</div>
        <div className="section-title">Честная игра.<br />Живое сообщество.</div>
        <p className="section-sub">GryazWorld — приватный ванильный сервер, где каждый начинает с нуля. Никаких привилегий за деньги.</p>
        <div className="card-grid">
          <Card icon="grid" title="Чистая ваниль" desc="Никаких лишних плагинов. Классический Minecraft таким, каким он должен быть." />
          <Card icon="users" title="Живое сообщество" desc="Discord с активными игроками, совместные стройки, торговля и события." />
          <Card icon="lock" title="Без донат-привилегий" desc="Проходка даёт только доступ. Никаких нечестных преимуществ." />
        </div>
      </section>

      {/* Features */}
      <section className="section">
        <div className="section-label">Особенности</div>
        <div className="section-title">Что вас ждёт</div>
        <p className="section-sub">Сервер настроен под комфортную долгосрочную игру.</p>
        <div className="feat-grid">
          <Feature num="01" title="Белый список" desc="Доступ только через проходку. Отсеивает гриферов и случайных людей." />
          <Feature num="02" title="Discord-интеграция" desc="При покупке проходки автоматически получаете роль в нашем Discord." />
          <Feature num="03" title="Общины" desc="Создавайте объединения игроков, стройте города, общайтесь вместе." />
          <Feature num="04" title="Карта сервера" desc="Интерактивная карта мира в реальном времени прямо на сайте." />
        </div>
      </section>

      {/* Steps */}
      <section className="section section-alt">
        <div className="section-label">Как попасть</div>
        <div className="section-title">Три шага до игры</div>
        <div className="steps-grid">
          <Step num="01" title="Войдите через Discord" desc="Нажмите «Войти» в шапке. Никаких паролей." />
          <Step num="02" title="Укажите ник" desc="Введите ваш игровой никнейм для белого списка." />
          <Step num="03" title="Купите проходку" desc="После оплаты — роль в Discord и доступ к серверу." />
          <Step num="04" title="Играйте" desc="Адрес сервера придёт в Discord сразу после покупки." />
        </div>
      </section>
    </>
  )
}

function Card({ title, desc }) {
  return (
    <div className="card">
      <div className="card-icon">
        <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="var(--accent)" strokeWidth="1.5">
          <rect x="2" y="2" width="9" height="9" /><rect x="13" y="2" width="9" height="9" />
          <rect x="2" y="13" width="9" height="9" /><rect x="13" y="13" width="9" height="9" />
        </svg>
      </div>
      <h3>{title}</h3>
      <p>{desc}</p>
    </div>
  )
}

function Feature({ num, title, desc }) {
  return (
    <div className="feat">
      <span className="feat-num">{num}</span>
      <div className="feat-body">
        <h4>{title}</h4>
        <p>{desc}</p>
      </div>
    </div>
  )
}

function Step({ num, title, desc }) {
  return (
    <div className="step">
      <div className="step-num">{num}</div>
      <h4>{title}</h4>
      <p>{desc}</p>
    </div>
  )
}
