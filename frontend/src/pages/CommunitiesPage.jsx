import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch, getDiscordUser } from '../api'
import Modal from '../components/Modal'
import './CommunitiesPage.css'

export default function CommunitiesPage() {
  const navigate = useNavigate()
  const [communities, setCommunities] = useState([])
  const [tab, setTab] = useState('all')
  const [search, setSearch] = useState('')
  const [createOpen, setCreateOpen] = useState(false)
  const [newName, setNewName] = useState('')

  useEffect(() => {
    loadCommunities()
  }, [])

  function loadCommunities() {
    apiFetch('/web/communities').then(setCommunities).catch(() => {})
  }

  const user = getDiscordUser()

  let list = communities
  if (tab === 'my') {
    if (!user) list = []
    else list = list.filter(c => c.owner_discord_id === user.id || (c.members_discord_ids && c.members_discord_ids.includes(user.id)))
  }
  if (search) {
    const q = search.toLowerCase()
    list = list.filter(c => c.name.toLowerCase().includes(q) || (c.description || '').toLowerCase().includes(q))
  }
  list = [...list].sort((a, b) => {
    const ar = a.is_recruiting !== 0 ? 1 : 0
    const br = b.is_recruiting !== 0 ? 1 : 0
    if (ar !== br) return br - ar
    return (b.total_hours || 0) - (a.total_hours || 0)
  })

  async function createCommunity() {
    if (!user) return
    if (!newName.trim()) return
    try {
      await apiFetch('/web/communities', {
        method: 'POST',
        body: JSON.stringify({ name: newName.trim(), discord_id: user.id }),
      })
      setCreateOpen(false)
      setNewName('')
      loadCommunities()
    } catch (e) {
      alert(e.message)
    }
  }

  async function joinCommunity(id) {
    if (!user) return navigate('/cabinet')
    try {
      await apiFetch(`/web/communities/${id}/join`, {
        method: 'POST',
        body: JSON.stringify({ discord_id: user.id }),
      })
      loadCommunities()
    } catch (e) {
      alert(e.message)
    }
  }

  return (
    <section className="section">
      <div className="section-label">Общины</div>
      <div className="section-title">Объединения игроков</div>
      <p className="section-sub">Вступайте в существующие или создавайте свои общины. Нужна активная проходка.</p>

      <div className="comm-toolbar">
        <div className="comm-count">
          <h2>Общины</h2>
          <span className="badge">{communities.length}</span>
        </div>
        <button className="btn btn-primary" onClick={() => user ? setCreateOpen(true) : navigate('/cabinet')}>
          + Создать общину
        </button>
      </div>

      <div className="comm-tabs">
        <button className={tab === 'all' ? 'active' : ''} onClick={() => setTab('all')}>Все</button>
        <button className={tab === 'my' ? 'active' : ''} onClick={() => setTab('my')}>Мои общины</button>
      </div>

      <input
        type="text"
        className="comm-search"
        placeholder="Поиск..."
        value={search}
        onChange={e => setSearch(e.target.value)}
      />

      <div className="comm-grid">
        {list.length === 0 ? (
          <div className="no-players" style={{ gridColumn: '1 / -1' }}>
            {tab === 'my' && !user ? 'Войдите через Discord чтобы увидеть свои общины' : 'Ничего не найдено'}
          </div>
        ) : (
          list.map(c => (
            <div
              key={c.id}
              className={`comm-card ${c.is_recruiting ? 'recruiting' : ''}`}
              onClick={() => navigate(`/community/${c.id}`)}
            >
              <div className="comm-banner">
                {c.banner_url && <img src={c.banner_url} alt="" />}
              </div>
              {c.is_recruiting !== 0 && <div className="comm-recruit-badge">ИДЕТ НАБОР</div>}
              <div className="comm-body">
                <div className="comm-name">{c.name}</div>
                <span className={`comm-type ${c.is_private ? 'private' : 'public'}`}>
                  {c.is_private ? 'Приватная' : 'Открытая'}
                </span>
                {c.description && <div className="comm-desc">{c.description}</div>}
                <div className="comm-stats-info">
                  <div>{c.member_count} участников</div>
                  <div>{c.total_hours || 0} ч. наиграно</div>
                </div>
                <div className="comm-actions" onClick={e => e.stopPropagation()}>
                  <button className="btn btn-outline" style={{ fontSize: 12, padding: '5px 12px' }} onClick={() => joinCommunity(c.id)}>
                    Вступить
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      <Modal open={createOpen} onClose={() => setCreateOpen(false)}>
        <h2>Создать общину</h2>
        <div className="inp-group">
          <label>Название общины</label>
          <input type="text" value={newName} onChange={e => setNewName(e.target.value)} placeholder="Северный форт" maxLength={40} />
        </div>
        <div style={{ display: 'flex', gap: 8, marginTop: 16 }}>
          <button className="btn btn-outline" style={{ flex: 1, justifyContent: 'center' }} onClick={() => setCreateOpen(false)}>Назад</button>
          <button className="btn btn-primary" style={{ flex: 1, justifyContent: 'center' }} onClick={createCommunity}>Создать</button>
        </div>
      </Modal>
    </section>
  )
}
