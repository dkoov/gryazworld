import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { apiFetch, getDiscordUser } from '../api'
import './CommunityPage.css'

export default function CommunityPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [comm, setComm] = useState(null)
  const [members, setMembers] = useState([])
  const user = getDiscordUser()

  useEffect(() => {
    apiFetch('/web/communities').then(list => {
      const c = list.find(x => x.id === Number(id))
      if (c) setComm(c)
    }).catch(() => {})

    apiFetch(`/web/communities/${id}/members`).then(setMembers).catch(() => {})
  }, [id])

  if (!comm) {
    return (
      <section className="section">
        <p style={{ color: 'var(--muted)' }}>Загрузка...</p>
      </section>
    )
  }

  const isOwner = user && user.id === comm.owner_discord_id

  async function joinCommunity() {
    if (!user) return navigate('/cabinet')
    try {
      await apiFetch(`/web/communities/${id}/join`, {
        method: 'POST',
        body: JSON.stringify({ discord_id: user.id }),
      })
      window.location.reload()
    } catch (e) {
      alert(e.message)
    }
  }

  return (
    <section className="section">
      <button className="btn btn-ghost" onClick={() => navigate('/communities')} style={{ marginBottom: 24 }}>
        &larr; Все общины
      </button>

      {/* Banner */}
      <div className="cp-banner">
        {comm.banner_url ? <img src={comm.banner_url} alt="" /> : <div className="cp-banner-placeholder" />}
      </div>

      {comm.is_recruiting !== 0 && (
        <div className="cp-recruit-badge">ИДЕТ НАБОР</div>
      )}

      <div className="cp-header">
        <h1>{comm.name}</h1>
        <div className="cp-meta">
          <span>{comm.member_count} участников</span>
          {comm.tag && <span>{comm.tag}</span>}
          {comm.discord_url && <span>Есть Discord сервер</span>}
        </div>
      </div>

      {comm.description && <p className="cp-desc">{comm.description}</p>}

      {/* Info blocks */}
      {comm.info_blocks && comm.info_blocks.length > 0 && (
        <div className="cp-info-blocks">
          {comm.info_blocks.map((block, i) => (
            <div key={i} className="cp-info-block">
              <p>{block.content}</p>
            </div>
          ))}
        </div>
      )}

      {/* Images gallery */}
      {comm.images && comm.images.length > 0 && (
        <div className="cp-gallery">
          {comm.images.map((url, i) => (
            <img key={i} src={url} alt="" className="cp-gallery-img" />
          ))}
        </div>
      )}

      {/* Links */}
      <div className="cp-links">
        {comm.banner_url && <a href={comm.banner_url} target="_blank" rel="noreferrer">Баннер</a>}
        {comm.discord_url && <a href={comm.discord_url} target="_blank" rel="noreferrer">Discord сервер</a>}
      </div>

      {/* Members */}
      <div className="cp-section">
        <h3>Участники ({members.length})</h3>
        <div className="cp-members">
          {members.map(m => (
            <div key={m.discord_id || m.nickname} className="cp-member">
              <img
                src={`https://mc-heads.net/avatar/${m.nickname}/32`}
                alt=""
                onError={e => { e.target.src = 'https://mc-heads.net/avatar/Steve/32' }}
              />
              <span className={m.role !== 'member' ? 'role-highlight' : ''}>
                {m.role === 'owner' ? '\uD83D\uDC51 ' : m.role === 'deputy' ? '\u2B50 ' : ''}
                {m.nickname}
              </span>
            </div>
          ))}
        </div>
      </div>

      {!isOwner && (
        <button className="btn btn-primary" style={{ marginTop: 24 }} onClick={joinCommunity}>
          Вступить в общину
        </button>
      )}
    </section>
  )
}
