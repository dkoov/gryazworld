import { useEffect, useState, useCallback } from 'react'
import { useParams } from 'react-router-dom'
import { apiFetch, getDiscordUser, getSessionToken } from '../api'
import PlayerProfileView from '../components/PlayerProfileView'

export default function PlayerProfilePage() {
  const { nickname } = useParams()
  const [profile, setProfile] = useState(null)
  const [notFound, setNotFound] = useState(false)
  const [liking, setLiking] = useState(false)
  const me = getDiscordUser()

  const load = useCallback(() => {
    apiFetch(`/web/player/${encodeURIComponent(nickname)}`)
      .then(p => { setProfile(p); setNotFound(false) })
      .catch(() => setNotFound(true))
  }, [nickname])

  useEffect(() => { load() }, [load])

  async function toggleLike() {
    if (!getSessionToken()) {
      window.location.href = '/cabinet'
      return
    }
    setLiking(true)
    try {
      const res = await apiFetch(`/web/player/${encodeURIComponent(nickname)}/like`, { method: 'POST' })
      setProfile(p => ({ ...p, liked_by_me: res.liked, likes_count: res.likes_count }))
    } catch (e) {
      // тихо игнорируем — например, попытка лайкнуть самого себя
    } finally {
      setLiking(false)
    }
  }

  if (notFound) {
    return (
      <section className="section">
        <div className="pv-card pv-empty-inline">Игрок «{nickname}» не найден</div>
      </section>
    )
  }

  if (!profile) {
    return (
      <section className="section">
        <div className="pv-card pv-empty-inline">Загрузка...</div>
      </section>
    )
  }

  const isSelf = me && profile.discord_id === me.id

  return (
    <section className="section">
      <PlayerProfileView profile={profile} isSelf={isSelf} liking={liking} onToggleLike={toggleLike} showSkinViewer={true} />
    </section>
  )
}
