import { useState } from 'react'
import './MapPage.css'

const MAPS = [
  { id: 'gamegraz', label: 'Мир построек', src: '/map/gamegraz/' },
  { id: 'farmgame', label: 'Мир ферм',     src: '/map/farmgame/' },
]

export default function MapPage() {
  const [active, setActive] = useState('gamegraz')
  const [v] = useState(() => Date.now())
  const current = MAPS.find(m => m.id === active)

  return (
    <div className="map-page">
      <div className="map-tabs">
        {MAPS.map(m => (
          <button
            key={m.id}
            className={`map-tab-btn${active === m.id ? ' active' : ''}`}
            onClick={() => setActive(m.id)}
          >
            {m.label}
          </button>
        ))}
      </div>

      <iframe
        key={current.src}
        src={`${current.src}?v=${v}`}
        title={current.label}
        className="map-iframe-full"
        allowFullScreen
      />
    </div>
  )
}
