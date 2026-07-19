import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import HomePage from './pages/HomePage'
import ShopPage from './pages/ShopPage'
import WikiPage from './pages/WikiPage'
import StatsPage from './pages/StatsPage'
import IchorbecsPage from './pages/IchorbecsPage'
import VotePage from './pages/VotePage'
import CommunitiesPage from './pages/CommunitiesPage'
import CommunityPage from './pages/CommunityPage'
import MapPage from './pages/MapPage'
import CabinetPage from './pages/CabinetPage'
import BankPage from './pages/BankPage'
import CourtPage from './pages/CourtPage'
import AdminPage from './pages/AdminPage'
import MessengerPage from './pages/MessengerPage'
import PlayerProfilePage from './pages/PlayerProfilePage'
import OfertaPage from './pages/OfertaPage'
import PaymentReturnPage from './pages/PaymentReturnPage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/access" element={<Navigate to="/shop" replace />} />
        <Route path="/shop" element={<ShopPage />} />
        <Route path="/wiki" element={<WikiPage />} />
        <Route path="/stats" element={<StatsPage />} />
        <Route path="/ichorbecs" element={<IchorbecsPage />} />
        <Route path="/vote" element={<VotePage />} />
        {/* HIDDEN: до решения команды — общины скрыты, роуты редиректят на главную.
            Компоненты CommunitiesPage/CommunityPage оставлены рабочими в кодовой базе.
        <Route path="/communities" element={<CommunitiesPage />} />
        <Route path="/community/:id" element={<CommunityPage />} />
        <Route path="/c/:slug" element={<CommunityPage />} />
        */}
        <Route path="/communities" element={<Navigate to="/" replace />} />
        <Route path="/community/:id" element={<Navigate to="/" replace />} />
        <Route path="/c/:slug" element={<Navigate to="/" replace />} />
        <Route path="/map" element={<MapPage />} />
        <Route path="/cabinet" element={<CabinetPage />} />
        <Route path="/bank" element={<BankPage />} />
        <Route path="/court" element={<CourtPage />} />
        <Route path="/admin" element={<AdminPage />} />
        <Route path="/messenger" element={<MessengerPage />} />
        <Route path="/player/:nickname" element={<PlayerProfilePage />} />
        <Route path="/oferta" element={<OfertaPage />} />
        <Route path="/payment/return" element={<PaymentReturnPage />} />
      </Route>
    </Routes>
  )
}
