import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import HomePage from './pages/HomePage'
import AccessPage from './pages/AccessPage'
import ShopPage from './pages/ShopPage'
import WikiPage from './pages/WikiPage'
import StatsPage from './pages/StatsPage'
import CommunitiesPage from './pages/CommunitiesPage'
import CommunityPage from './pages/CommunityPage'
import MapPage from './pages/MapPage'
import CabinetPage from './pages/CabinetPage'
import OfertaPage from './pages/OfertaPage'
import PaymentReturnPage from './pages/PaymentReturnPage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/access" element={<AccessPage />} />
        <Route path="/shop" element={<ShopPage />} />
        <Route path="/wiki" element={<WikiPage />} />
        <Route path="/stats" element={<StatsPage />} />
        <Route path="/communities" element={<CommunitiesPage />} />
        <Route path="/community/:id" element={<CommunityPage />} />
        <Route path="/c/:slug" element={<CommunityPage />} />
        <Route path="/map" element={<MapPage />} />
        <Route path="/cabinet" element={<CabinetPage />} />
        <Route path="/oferta" element={<OfertaPage />} />
        <Route path="/payment/return" element={<PaymentReturnPage />} />
      </Route>
    </Routes>
  )
}
