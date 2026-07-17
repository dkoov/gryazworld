import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch, getDiscordUser } from '../api'
import './MessengerPage.css'

function mcHead(nickname, size = 64) {
  return `https://mc-heads.net/avatar/${encodeURIComponent(nickname)}/${size}`
}

function formatDate(iso) {
  const d = new Date(iso)
  const now = new Date()
  const sameDay = d.toDateString() === now.toDateString()
  if (sameDay) return d.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })
  return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' })
}

function formatTime(iso) {
  return new Date(iso).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })
}

export default function MessengerPage() {
  const navigate = useNavigate()
  const [checking, setChecking] = useState(true)
  const [linked, setLinked] = useState(false)

  const [conversations, setConversations] = useState([])
  const [active, setActive] = useState(null) // nickname
  const [messages, setMessages] = useState([])
  const [newNick, setNewNick] = useState('')
  const [text, setText] = useState('')
  const [sending, setSending] = useState(false)
  const [error, setError] = useState('')
  const threadEndRef = useRef(null)

  useEffect(() => {
    if (!getDiscordUser()) { navigate('/cabinet'); return }
    apiFetch('/web/me')
      .then(me => setLinked(!!me.linked))
      .catch(() => setLinked(false))
      .finally(() => setChecking(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function loadConversations() {
    apiFetch('/web/messenger/conversations').then(setConversations).catch(() => {})
  }

  useEffect(() => {
    if (!linked) return
    loadConversations()
    const id = setInterval(loadConversations, 15000)
    return () => clearInterval(id)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [linked])

  function loadThread(nickname) {
    apiFetch(`/web/messenger/messages/${encodeURIComponent(nickname)}`)
      .then(d => { setMessages(d.messages); setError('') })
      .catch(e => setError(e.message))
  }

  useEffect(() => {
    if (!active) return
    loadThread(active)
    const id = setInterval(() => loadThread(active), 4000)
    return () => clearInterval(id)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [active])

  useEffect(() => {
    threadEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  function openConversation(nickname) {
    setActive(nickname)
    setMessages([])
    setConversations(cs => cs.map(c => c.nickname === nickname ? { ...c, unread: 0 } : c))
  }

  function startNew() {
    const nick = newNick.trim()
    if (!nick) return
    setNewNick('')
    openConversation(nick)
  }

  async function send() {
    const t = text.trim()
    if (!t || !active) return
    setSending(true)
    setError('')
    try {
      const msg = await apiFetch(`/web/messenger/messages/${encodeURIComponent(active)}`, {
        method: 'POST',
        body: JSON.stringify({ text: t }),
      })
      setMessages(m => [...m, msg])
      setText('')
      loadConversations()
    } catch (e) {
      setError(e.message)
    } finally {
      setSending(false)
    }
  }

  if (checking) {
    return (
      <div className="msg-page page-fade">
        <div className="msg-loading"><div className="spinner" /></div>
      </div>
    )
  }

  if (!linked) {
    return (
      <div className="msg-page page-fade">
        <div className="msg-denied">
          <div className="msg-denied-title">Нужен привязанный аккаунт</div>
          <div className="msg-denied-desc">Привяжи Minecraft-ник в личном кабинете, чтобы писать сообщения.</div>
          <button className="btn btn-primary" onClick={() => navigate('/cabinet')}>В личный кабинет</button>
        </div>
      </div>
    )
  }

  return (
    <div className="msg-page page-fade">
      <h1 className="page-title">Мессен<em>джер</em></h1>

      <div className="msg-layout">
        <div className="msg-list-col">
          <div className="msg-new-row">
            <input
              type="text"
              placeholder="Написать игроку..."
              value={newNick}
              onChange={e => setNewNick(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && startNew()}
            />
            <button onClick={startNew}>→</button>
          </div>

          <div className="msg-conversations">
            {conversations.length === 0 && (
              <div className="msg-empty">Пока нет диалогов</div>
            )}
            {conversations.map(c => (
              <div
                key={c.nickname}
                className={`msg-conv-row ${active === c.nickname ? 'active' : ''}`}
                onClick={() => openConversation(c.nickname)}
              >
                <img className="msg-avatar" src={mcHead(c.nickname)} alt="" />
                <div className="msg-conv-info">
                  <div className="msg-conv-top">
                    <span className="msg-conv-name">{c.nickname}</span>
                    <span className="msg-conv-date">{formatDate(c.last_message_at)}</span>
                  </div>
                  <div className="msg-conv-bottom">
                    <span className="msg-conv-preview">{c.outgoing ? 'Вы: ' : ''}{c.last_message}</span>
                    {c.unread > 0 && <span className="msg-conv-unread" />}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="msg-thread-col">
          {!active && (
            <div className="msg-empty msg-empty-big">Выбери диалог или напиши новому игроку</div>
          )}

          {active && (
            <>
              <div className="msg-thread-head">
                <img className="msg-avatar" src={mcHead(active)} alt="" />
                <div className="msg-thread-name">{active}</div>
              </div>

              <div className="msg-thread-body">
                {messages.map(m => (
                  <div key={m.id} className={`msg-row ${m.outgoing ? 'mine' : 'theirs'}`}>
                    <div className="msg-bubble">{m.text}</div>
                    <div className="msg-time">{formatTime(m.created_at)}</div>
                  </div>
                ))}
                <div ref={threadEndRef} />
              </div>

              {error && <div className="msg-error">{error}</div>}

              <div className="msg-input-row">
                <input
                  type="text"
                  placeholder="Напишите сообщение..."
                  value={text}
                  onChange={e => setText(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && send()}
                  maxLength={2000}
                />
                <button disabled={sending || !text.trim()} onClick={send}>➤</button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  )
}
