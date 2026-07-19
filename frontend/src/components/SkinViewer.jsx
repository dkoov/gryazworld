import { useEffect, useRef, useState } from 'react'
import * as skinview3d from 'skinview3d'
import { apiFetch } from '../api'
import './SkinViewer.css'

const POSES = [
  { key: 'idle', label: 'Стоя', make: () => new skinview3d.IdleAnimation() },
  { key: 'walk', label: 'Ходьба', make: () => new skinview3d.WalkingAnimation() },
  { key: 'run', label: 'Бег', make: () => new skinview3d.RunningAnimation() },
  { key: 'crouch', label: 'Присед', make: () => new skinview3d.CrouchAnimation() },
  { key: 'wave', label: 'Взмах', make: () => new skinview3d.WaveAnimation() },
]

const FALLBACK_SKIN = nickname => `https://mc-heads.net/skin/${encodeURIComponent(nickname)}`

export default function SkinViewer({ nickname }) {
  const canvasRef = useRef(null)
  const viewerRef = useRef(null)
  const [pose, setPose] = useState('idle')
  const [autoRotate, setAutoRotate] = useState(false)
  const [resolved, setResolved] = useState(null) // { skinUrl, model, custom }

  useEffect(() => {
    let cancelled = false
    apiFetch(`/web/skin/${encodeURIComponent(nickname)}`)
      .then(meta => {
        if (cancelled) return
        setResolved(
          meta.hasCustomSkin
            ? { skinUrl: meta.textureUrl, model: meta.skinModel === 'slim' ? 'slim' : 'default', custom: true }
            : { skinUrl: FALLBACK_SKIN(nickname), model: 'auto-detect', custom: false }
        )
      })
      .catch(() => {
        if (!cancelled) setResolved({ skinUrl: FALLBACK_SKIN(nickname), model: 'auto-detect', custom: false })
      })
    return () => { cancelled = true }
  }, [nickname])

  useEffect(() => {
    if (!canvasRef.current || !resolved) return
    const viewer = new skinview3d.SkinViewer({
      canvas: canvasRef.current,
      width: 230,
      height: 320,
      skin: resolved.skinUrl,
      model: resolved.model,
    })
    viewer.controls.enablePan = false
    viewer.controls.minDistance = 20
    viewer.controls.maxDistance = 90
    viewer.animation = new skinview3d.IdleAnimation()
    viewerRef.current = viewer

    return () => {
      viewer.dispose()
      viewerRef.current = null
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [resolved])

  useEffect(() => {
    if (viewerRef.current) viewerRef.current.autoRotate = autoRotate
  }, [autoRotate])

  function applyPose(key) {
    setPose(key)
    if (!viewerRef.current) return
    const def = POSES.find(p => p.key === key)
    viewerRef.current.animation = def ? def.make() : null
  }

  async function downloadSkin() {
    const skinUrl = resolved?.skinUrl || FALLBACK_SKIN(nickname)
    try {
      const res = await fetch(skinUrl)
      const blob = await res.blob()
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${nickname}_skin.png`
      document.body.appendChild(a)
      a.click()
      a.remove()
      URL.revokeObjectURL(url)
    } catch {
      window.open(skinUrl, '_blank')
    }
  }

  return (
    <div className="skinviewer-card">
      <div className="skinviewer-canvas-wrap">
        <canvas ref={canvasRef} className="skinviewer-canvas" />
        <div className="skinviewer-hint">Крутите мышью</div>
      </div>

      <div className="skinviewer-poses">
        {POSES.map(p => (
          <button
            key={p.key}
            className={`skinviewer-pose-btn ${pose === p.key ? 'active' : ''}`}
            onClick={() => applyPose(p.key)}
          >
            {p.label}
          </button>
        ))}
      </div>

      <div className="skinviewer-actions">
        <button
          className={`skinviewer-action-btn ${autoRotate ? 'active' : ''}`}
          onClick={() => setAutoRotate(v => !v)}
        >
          {autoRotate ? 'Остановить вращение' : 'Автовращение'}
        </button>
        <button className="skinviewer-action-btn skinviewer-download-btn" onClick={downloadSkin}>
          Скачать скин
        </button>
      </div>
    </div>
  )
}
