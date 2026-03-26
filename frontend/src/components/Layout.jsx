import { Outlet } from 'react-router-dom'
import Nav from './Nav'
import Footer from './Footer'

export default function Layout() {
  return (
    <>
      <Nav />
      <main style={{ paddingTop: 58, minHeight: '100vh' }}>
        <Outlet />
      </main>
      <Footer />
    </>
  )
}
