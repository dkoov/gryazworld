import { useEffect, useState, useRef, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { apiFetch, getDiscordUser } from '../api'
import Modal from '../components/Modal'
import './CommunityPage.css'

const CLOUDINARY_URL = 'https://api.cloudinary.com/v1_1/dzwhmfizw/image/upload'
const CLOUDINARY_PRESET = 'gryazworld'

export default function CommunityPage() {
  const { id, slug } = useParams()
  const navigate = useNavigate()
  const [comm, setComm] = useState(null)
  const [members, setMembers] = useState([])
  const [memberSearch, setMemberSearch] = useState('')
  const user = getDiscordUser()

  // Carousel
  const [carouselIdx, setCarouselIdx] = useState(0)

  // Edit modal
  const [editOpen, setEditOpen] = useState(false)
  const [editForm, setEditForm] = useState({})
  const [bannerUploading, setBannerUploading] = useState(false)

  // Images modal
  const [imagesOpen, setImagesOpen] = useState(false)
  const [existingImages, setExistingImages] = useState([])
  const [newImageFiles, setNewImageFiles] = useState([])
  const [imagesUploading, setImagesUploading] = useState(false)

  // Context menu
  const [menuState, setMenuState] = useState(null)

  const loadData = useCallback(async () => {
    try {
      let c
      if (slug) {
        c = await apiFetch(`/web/community-by-slug/${slug}`)
      } else {
        const list = await apiFetch('/web/communities')
        c = list.find(x => x.id === Number(id))
      }
      if (c) {
        setComm(c)
        const m = await apiFetch(`/web/communities/${c.id}/members`)
        setMembers(m)
      } else {
        navigate('/communities')
      }
    } catch {
      navigate('/communities')
    }
  }, [id, slug, navigate])

  useEffect(() => { loadData() }, [loadData])

  // Close context menu on outside click
  useEffect(() => {
    if (!menuState) return
    const close = () => setMenuState(null)
    setTimeout(() => document.addEventListener('click', close, { once: true }), 0)
    return () => document.removeEventListener('click', close)
  }, [menuState])

  if (!comm) {
    return <section className="section"><p style={{ color: 'var(--muted)' }}>Загрузка...</p></section>
  }

  const isOwner = user && user.id === comm.owner_discord_id
  const currentMember = members.find(m => m.discord_id === user?.id)
  const currentRole = currentMember?.role || 'member'
  const isMember = !!currentMember
  const isDeputy = currentRole === 'deputy'

  // ─── Actions ──────────────────────────────────────────────────────────────

  async function joinCommunity() {
    if (!user) return navigate('/cabinet')
    try {
      await apiFetch(`/web/communities/${comm.id}/join`, {
        method: 'POST',
        body: JSON.stringify({ discord_id: user.id }),
      })
      await loadData()
    } catch (e) {
      if (e.message.includes('приватная') || e.message.includes('закрыта')) {
        alert('Община приватная. Вступление только по приглашению.')
      } else {
        alert(e.message)
      }
    }
  }

  async function leaveCommunity() {
    if (!confirm('Покинуть общину?')) return
    try {
      await apiFetch(`/web/communities/${comm.id}/leave`, {
        method: 'POST',
        body: JSON.stringify({ discord_id: user.id }),
      })
      await loadData()
    } catch (e) { alert(e.message) }
  }

  async function deleteCommunity() {
    if (!confirm('Удалить общину навсегда? Это действие необратимо!')) return
    if (!confirm('Вы уверены? Все участники будут исключены.')) return
    try {
      await apiFetch(`/web/communities/${comm.id}`, {
        method: 'DELETE',
        body: JSON.stringify({ discord_id: user.id }),
      })
      navigate('/communities')
    } catch (e) { alert(e.message) }
  }

  async function toggleRecruit() {
    const newVal = comm.is_recruiting ? 0 : 1
    try {
      await apiFetch(`/web/communities/${comm.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ discord_id: user.id, is_recruiting: newVal }),
      })
      await loadData()
    } catch (e) { alert(e.message) }
  }

  async function togglePrivate() {
    const newVal = comm.is_private ? 0 : 1
    try {
      await apiFetch(`/web/communities/${comm.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ discord_id: user.id, is_private: newVal }),
      })
      await loadData()
    } catch (e) { alert(e.message) }
  }

  async function kickMember(targetDiscordId, nickname) {
    if (!confirm(`Исключить ${nickname} из общины?`)) return
    try {
      await apiFetch(`/web/communities/${comm.id}/kick`, {
        method: 'POST',
        body: JSON.stringify({ discord_id: user.id, target_discord_id: targetDiscordId }),
      })
      await loadData()
    } catch (e) { alert(e.message) }
  }

  async function setMemberRole(targetDiscordId, role) {
    try {
      await apiFetch(`/web/communities/${comm.id}/set-role`, {
        method: 'POST',
        body: JSON.stringify({ discord_id: user.id, target_discord_id: targetDiscordId, role }),
      })
      await loadData()
    } catch (e) { alert(e.message) }
  }

  // ─── Info blocks ──────────────────────────────────────────────────────────

  async function addInfoBlock() {
    const content = prompt('Введите текст блока:')
    if (!content?.trim()) return
    const updated = [...(comm.info_blocks || []), { title: '', content: content.trim() }]
    try {
      await apiFetch(`/web/communities/${comm.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ discord_id: user.id, info_blocks: updated }),
      })
      await loadData()
    } catch (e) { alert(e.message) }
  }

  async function removeInfoBlock(index) {
    const updated = [...(comm.info_blocks || [])]
    updated.splice(index, 1)
    try {
      await apiFetch(`/web/communities/${comm.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ discord_id: user.id, info_blocks: updated }),
      })
      await loadData()
    } catch (e) { alert(e.message) }
  }

  // ─── Edit modal ───────────────────────────────────────────────────────────

  function openEdit() {
    setEditForm({
      name: comm.name || '',
      slug: comm.slug || '',
      discord_url: comm.discord_url || '',
      description: comm.description || '',
      banner_url: comm.banner_url || '',
    })
    setEditOpen(true)
  }

  async function saveEdit() {
    try {
      await apiFetch(`/web/communities/${comm.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ discord_id: user.id, ...editForm }),
      })
      setEditOpen(false)
      await loadData()
    } catch (e) { alert(e.message) }
  }

  async function uploadBannerFile(e) {
    const file = e.target.files[0]
    if (!file) return
    setBannerUploading(true)
    try {
      const fd = new FormData()
      fd.append('file', file)
      fd.append('upload_preset', CLOUDINARY_PRESET)
      const res = await fetch(CLOUDINARY_URL, { method: 'POST', body: fd })
      const data = await res.json()
      if (data.secure_url) {
        setEditForm(f => ({ ...f, banner_url: data.secure_url }))
      } else {
        throw new Error(data.error?.message || 'Ошибка загрузки')
      }
    } catch (err) { alert('Ошибка: ' + err.message) }
    finally { setBannerUploading(false) }
  }

  // ─── Images modal ─────────────────────────────────────────────────────────

  function openImages() {
    setExistingImages([...(comm.images || [])])
    setNewImageFiles([])
    setImagesOpen(true)
  }

  function handleImageFiles(e) {
    const available = 3 - existingImages.length - newImageFiles.length
    const files = Array.from(e.target.files).slice(0, available)
    setNewImageFiles(prev => [...prev, ...files])
    e.target.value = ''
  }

  async function applyImages() {
    setImagesUploading(true)
    try {
      const uploadedUrls = []
      for (const file of newImageFiles) {
        const fd = new FormData()
        fd.append('file', file)
        fd.append('upload_preset', CLOUDINARY_PRESET)
        const res = await fetch(CLOUDINARY_URL, { method: 'POST', body: fd })
        const data = await res.json()
        if (data.secure_url) uploadedUrls.push(data.secure_url)
        else throw new Error(data.error?.message || 'Ошибка загрузки')
      }
      const allImages = [...existingImages, ...uploadedUrls]
      await apiFetch(`/web/communities/${comm.id}`, {
        method: 'PATCH',
        body: JSON.stringify({ discord_id: user.id, images: allImages }),
      })
      setImagesOpen(false)
      await loadData()
    } catch (e) { alert('Ошибка: ' + e.message) }
    finally { setImagesUploading(false) }
  }

  // ─── Context menu ─────────────────────────────────────────────────────────

  function openMemberMenu(e, m) {
    e.stopPropagation()
    const canKick = (isOwner || isDeputy) && m.role !== 'owner' && !(isDeputy && m.role === 'deputy')
    const canSetRole = isOwner && m.role !== 'owner'
    if (!canKick && !canSetRole) return
    setMenuState({ x: e.clientX, y: e.clientY, member: m, canKick, canSetRole })
  }

  // ─── Filtered members ─────────────────────────────────────────────────────

  const filteredMembers = memberSearch
    ? members.filter(m => m.nickname.toLowerCase().includes(memberSearch.toLowerCase()))
    : members

  // ─── Carousel helpers ─────────────────────────────────────────────────────

  const images = comm.images || []

  // ─── Render ───────────────────────────────────────────────────────────────

  return (
    <section className="section">
      <div className="cp-layout">
        {/* Breadcrumbs */}
        <div className="cp-breadcrumbs" style={{ gridColumn: '1 / -1' }}>
          <span className="cp-breadcrumb-link" onClick={() => navigate('/communities')}>Общины</span>
          <span className="cp-breadcrumb-sep">&rsaquo;</span>
          <span>{comm.name}</span>
        </div>

        {/* ─── LEFT COLUMN ──────────────────────────────────────────── */}
        <div className="cp-left">

          {/* Header */}
          <div className="cp-header-row">
            <div className="cp-mc-banner">
              {comm.banner_url ? (
                <img src={comm.banner_url} alt="" onError={e => { e.target.style.display = 'none'; e.target.nextSibling.style.display = 'block' }} />
              ) : null}
              <div className="cp-mc-banner-placeholder" style={{ display: comm.banner_url ? 'none' : 'block' }} />
            </div>
            <div className="cp-header-info">
              {comm.is_recruiting !== 0 && (
                <span className="cp-recruit-badge">● Идёт набор</span>
              )}
              <h1 className="cp-title">{comm.name}</h1>
              {comm.description && <div className="cp-desc">{comm.description}</div>}
              <div className="cp-meta">
                <span>{comm.member_count} участников</span>
                {comm.tag && <span> · {comm.tag}</span>}
                {comm.discord_url && <span> · Есть Discord сервер</span>}
              </div>
            </div>
          </div>

          {/* Carousel gallery */}
          {images.length > 0 && (
            <div className="cp-carousel">
              <div className="cp-carousel-track" style={{ transform: `translateX(-${carouselIdx * 100}%)` }}>
                {images.map((url, i) => (
                  <img key={i} src={url} alt="" className="cp-carousel-img" />
                ))}
              </div>
              {images.length > 1 && (
                <>
                  <button className="cp-carousel-btn cp-carousel-prev" onClick={() => setCarouselIdx((carouselIdx - 1 + images.length) % images.length)}>&lsaquo;</button>
                  <button className="cp-carousel-btn cp-carousel-next" onClick={() => setCarouselIdx((carouselIdx + 1) % images.length)}>&rsaquo;</button>
                  <div className="cp-carousel-dots">
                    {images.map((_, i) => (
                      <div key={i} className={`cp-carousel-dot ${i === carouselIdx ? 'active' : ''}`} onClick={() => setCarouselIdx(i)} />
                    ))}
                  </div>
                </>
              )}
            </div>
          )}

          {/* Info blocks */}
          {comm.info_blocks && comm.info_blocks.length > 0 && (
            <div className="cp-info-blocks">
              {comm.info_blocks.map((block, i) => (
                <div key={i} className="cp-info-block">
                  {isOwner && (
                    <button className="cp-info-block-delete" onClick={() => removeInfoBlock(i)} title="Удалить блок">🗑</button>
                  )}
                  <p>{block.content}</p>
                </div>
              ))}
            </div>
          )}

          {isOwner && (!comm.info_blocks || comm.info_blocks.length === 0) && (
            <button className="cp-add-block-btn" onClick={addInfoBlock}>
              + Нажмите, чтобы добавить блок с информацией
            </button>
          )}
        </div>

        {/* ─── RIGHT COLUMN ─────────────────────────────────────────── */}
        <div className="cp-right">

          {/* Management panel (owner only) */}
          {isOwner && (
            <div className="cp-panel">
              <div className="cp-panel-title">УПРАВЛЕНИЕ</div>
              <button className="cp-panel-btn" onClick={openEdit}>✏️ Редактировать</button>
              <button className="cp-panel-btn" onClick={openImages}>🖼 Изменить картинки</button>
              <div className="cp-panel-toggle-row">
                <div className="cp-panel-toggle-label">⭕ Набор в общину</div>
                <div className={`cp-toggle ${comm.is_recruiting ? 'on' : ''}`} onClick={toggleRecruit}>
                  <div className="cp-toggle-dot" />
                </div>
              </div>
              <div className="cp-panel-toggle-row">
                <div className="cp-panel-toggle-label">🔒 Приватная</div>
                <div className={`cp-toggle private ${comm.is_private ? 'on' : ''}`} onClick={togglePrivate}>
                  <div className="cp-toggle-dot" />
                </div>
              </div>
            </div>
          )}

          {/* Links */}
          <div className="cp-panel">
            <div className="cp-panel-title">ССЫЛКИ</div>
            <div className="cp-links-inner">
              {comm.banner_url && <a href={comm.banner_url} target="_blank" rel="noreferrer">🖼 Баннер ↗</a>}
              {comm.discord_url && <a href={comm.discord_url} target="_blank" rel="noreferrer">💬 Discord сервер ↗</a>}
              {!comm.banner_url && !comm.discord_url && <div className="cp-no-data">Ссылок нет</div>}
            </div>
          </div>

          {/* Members */}
          <div className="cp-panel">
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
              <div className="cp-panel-title" style={{ margin: 0 }}>УЧАСТНИКИ</div>
              <span className="cp-member-count-badge">{members.length}</span>
            </div>
            <input
              type="text"
              className="cp-member-search"
              placeholder="Поиск..."
              value={memberSearch}
              onChange={e => setMemberSearch(e.target.value)}
            />
            <div className="cp-members-list">
              {filteredMembers.map(m => {
                const canInteract = (isOwner || isDeputy) && m.role !== 'owner' && !(isDeputy && m.role === 'deputy')
                const roleIcon = m.role === 'owner' ? '👑' : m.role === 'deputy' ? '⭐' : ''
                const roleColor = m.role === 'owner' ? '#c8a84b' : m.role === 'deputy' ? '#a0c4ff' : 'var(--text)'
                return (
                  <div
                    key={m.discord_id}
                    className={`cp-member-item ${canInteract || (isOwner && m.role !== 'owner') ? 'clickable' : ''}`}
                    onClick={e => openMemberMenu(e, m)}
                  >
                    <img
                      src={`https://mc-heads.net/avatar/${m.nickname}/32`}
                      alt=""
                      onError={e => { e.target.src = 'https://mc-heads.net/avatar/Steve/32' }}
                    />
                    <span
                      className="cp-member-name"
                      style={{ fontWeight: m.role !== 'member' ? 600 : 400, color: roleColor }}
                    >
                      {roleIcon} {m.nickname}
                    </span>
                    {m.discord_id === user?.id && m.role !== 'owner' && (
                      <span className="cp-leave-x" onClick={e => { e.stopPropagation(); leaveCommunity() }}>✕</span>
                    )}
                  </div>
                )
              })}
              {filteredMembers.length === 0 && <div className="cp-no-data">Участников не найдено</div>}
            </div>
            {!isOwner && (
              <button className="cp-join-btn" onClick={joinCommunity}>
                {isMember ? 'Вы уже в общине' : 'Вступить'}
              </button>
            )}
          </div>
        </div>
      </div>

      {/* ─── Context Menu ────────────────────────────────────────────── */}
      {menuState && (
        <div className="cp-context-menu" style={{ left: menuState.x, top: menuState.y }}>
          <div className="cp-context-menu-header">{menuState.member.nickname}</div>
          {menuState.canSetRole && (
            <div
              className="cp-context-menu-item"
              onClick={() => { setMemberRole(menuState.member.discord_id, menuState.member.role === 'deputy' ? 'member' : 'deputy'); setMenuState(null) }}
            >
              ⭐ {menuState.member.role === 'deputy' ? 'Снять заместителя' : 'Сделать заместителем'}
            </div>
          )}
          {menuState.canKick && (
            <div
              className="cp-context-menu-item danger"
              onClick={() => { kickMember(menuState.member.discord_id, menuState.member.nickname); setMenuState(null) }}
            >
              🚫 Исключить
            </div>
          )}
        </div>
      )}

      {/* ─── Edit Modal ──────────────────────────────────────────────── */}
      <Modal open={editOpen} onClose={() => setEditOpen(false)} wide>
        <h2>Редактировать общину</h2>
        <div className="inp-group">
          <label>Название общины</label>
          <input type="text" value={editForm.name || ''} onChange={e => setEditForm(f => ({ ...f, name: e.target.value }))} maxLength={40} />
        </div>
        <div className="inp-group">
          <label>Ссылка на общину</label>
          <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
            <span style={{ color: 'var(--muted)', fontSize: 13, whiteSpace: 'nowrap' }}>gryazworld.ru/c/</span>
            <input
              type="text"
              value={editForm.slug || ''}
              onChange={e => setEditForm(f => ({ ...f, slug: e.target.value.toLowerCase().replace(/[^a-z0-9-]/g, '') }))}
              placeholder="moya-obshina"
              maxLength={30}
              style={{ flex: 1 }}
            />
          </div>
          <div style={{ fontSize: 11, color: 'var(--muted)', marginTop: 4 }}>Только латиница, цифры и дефис</div>
        </div>
        <div className="inp-group">
          <label>Discord сервер</label>
          <input type="text" value={editForm.discord_url || ''} onChange={e => setEditForm(f => ({ ...f, discord_url: e.target.value }))} placeholder="https://discord.gg/..." />
        </div>
        <div className="inp-group">
          <label>Описание</label>
          <textarea
            value={editForm.description || ''}
            onChange={e => setEditForm(f => ({ ...f, description: e.target.value }))}
            rows={3}
            maxLength={100}
          />
          <div style={{ fontSize: 11, color: 'var(--muted)', marginTop: 4 }}>{(editForm.description || '').length}/100</div>
        </div>
        <div className="inp-group">
          <label>Баннер общины</label>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            <input
              type="text"
              value={editForm.banner_url || ''}
              onChange={e => setEditForm(f => ({ ...f, banner_url: e.target.value }))}
              placeholder="URL баннера"
              style={{ flex: 1 }}
            />
            <label className="cp-upload-btn">
              {bannerUploading ? 'Загрузка...' : '📁 Загрузить'}
              <input type="file" accept="image/*" style={{ display: 'none' }} onChange={uploadBannerFile} disabled={bannerUploading} />
            </label>
          </div>
          {editForm.banner_url && (
            <div style={{ marginTop: 8 }}>
              <img src={editForm.banner_url} alt="" style={{ maxWidth: 100, borderRadius: 6 }} />
            </div>
          )}
          <div style={{ fontSize: 11, color: 'var(--muted)', marginTop: 4 }}>Загрузите картинку или вставьте прямую ссылку на изображение</div>
        </div>
        <div style={{ display: 'flex', gap: 8, marginTop: 16, justifyContent: 'flex-end' }}>
          <button className="btn btn-ghost" onClick={() => setEditOpen(false)}>Отменить</button>
          <button className="btn btn-primary" onClick={saveEdit}>Сохранить</button>
        </div>
        <div className="cp-delete-section">
          <button className="cp-delete-btn" onClick={deleteCommunity}>Удалить общину</button>
        </div>
      </Modal>

      {/* ─── Images Modal ────────────────────────────────────────────── */}
      <Modal open={imagesOpen} onClose={() => setImagesOpen(false)}>
        <h2>Изменить картинки</h2>

        {existingImages.length + newImageFiles.length < 3 ? (
          <label className="cp-images-dropzone">
            <div style={{ fontSize: 15, fontWeight: 600, marginBottom: 4 }}>Добавить картинки</div>
            <div style={{ fontSize: 12 }}>Нажмите, чтобы выбрать файл</div>
            <input type="file" accept="image/*" multiple style={{ display: 'none' }} onChange={handleImageFiles} />
          </label>
        ) : (
          <div style={{ fontSize: 12, color: '#e05c5c', marginBottom: 8 }}>Максимум 3 изображения</div>
        )}

        <div style={{ fontSize: 12, color: '#e05c5c', marginBottom: 12 }}>
          Запрещено использовать NSFW изображения, минимальный размер картинки 619x364
        </div>

        <div className="cp-images-list">
          {existingImages.map((url, i) => (
            <div key={'e' + i} className="cp-image-item">
              <img src={url} alt="" />
              <span className="cp-image-name">{url.split('/').pop().split('?')[0]}</span>
              <button onClick={() => setExistingImages(prev => prev.filter((_, j) => j !== i))}>🗑</button>
            </div>
          ))}
          {newImageFiles.map((f, i) => (
            <div key={'n' + i} className="cp-image-item new">
              <img src={URL.createObjectURL(f)} alt="" />
              <span className="cp-image-name">{f.name}</span>
              <button onClick={() => setNewImageFiles(prev => prev.filter((_, j) => j !== i))}>🗑</button>
            </div>
          ))}
          {existingImages.length === 0 && newImageFiles.length === 0 && (
            <div className="cp-no-data" style={{ textAlign: 'center', padding: 16 }}>Нет изображений</div>
          )}
        </div>

        <div style={{ display: 'flex', gap: 8, marginTop: 16, justifyContent: 'flex-end' }}>
          <button className="btn btn-ghost" onClick={() => setImagesOpen(false)}>Отменить</button>
          <button className="btn btn-primary" onClick={applyImages} disabled={imagesUploading}>
            {imagesUploading ? 'Загрузка...' : 'Применить'}
          </button>
        </div>
      </Modal>
    </section>
  )
}
