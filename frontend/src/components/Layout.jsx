import { Outlet, useLocation } from 'react-router-dom'
import Nav from './Nav'
import Footer from './Footer'

export default function Layout() {
  const location = useLocation()
  return (
    <>
      <Nav />
      <main style={{ paddingTop: 62, minHeight: '100vh' }}>
        <div key={location.pathname} className="page-fade">
          <Outlet />
        </div>
      </main>
      <Footer />
    </>
  )
}
