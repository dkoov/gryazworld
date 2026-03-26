import { Link } from 'react-router-dom'
import './Footer.css'

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-logo">Gryaz<span>World</span></div>
      <div className="footer-links">
        <Link to="/access">Проходка</Link>
        <Link to="/wiki">Wiki</Link>
        <Link to="/stats">Статистика</Link>
        <Link to="/communities">Общины</Link>
      </div>
      <div className="footer-copy">&copy; 2025 GryazWorld</div>
    </footer>
  )
}
