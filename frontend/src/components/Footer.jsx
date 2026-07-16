import { Link } from 'react-router-dom'
import './Footer.css'

export default function Footer() {
  return (
    <footer className="footer">
      <Link to="/" className="footer-logo">
        <img src="/logo-circle.png" alt="Ichorix" />
      </Link>
      <div className="footer-links">
        <Link to="/shop">Магазин</Link>
        <Link to="/wiki">Wiki</Link>
        <Link to="/stats">Статистика</Link>
        {/* HIDDEN: до решения команды
        <Link to="/communities">Общины</Link>
        */}
        <Link to="/oferta">Оферта</Link>
      </div>
      <div className="footer-copy">&copy; 2025 Ichorix</div>
    </footer>
  )
}
