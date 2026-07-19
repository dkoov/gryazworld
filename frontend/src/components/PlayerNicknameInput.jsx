import { useEffect, useRef, useState } from 'react'
import { apiFetch } from '../api'
import './PlayerNicknameInput.css'

export default function PlayerNicknameInput({ value, onChange, placeholder, onSubmit, className, autoFocus }) {
  const [suggestions, setSuggestions] = useState([])
  const [open, setOpen] = useState(false)
  const wrapRef = useRef(null)
  const debounceRef = useRef(null)

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current)
    const q = value.trim()
    if (q.length < 2) { setSuggestions([]); return undefined }
    debounceRef.current = setTimeout(() => {
      apiFetch(`/web/players/search?q=${encodeURIComponent(q)}`)
        .then(setSuggestions)
        .catch(() => setSuggestions([]))
    }, 200)
    return () => clearTimeout(debounceRef.current)
  }, [value])

  useEffect(() => {
    function onClickOutside(e) {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) setOpen(false)
    }
    document.addEventListener('mousedown', onClickOutside)
    return () => document.removeEventListener('mousedown', onClickOutside)
  }, [])

  function pick(nick) {
    onChange(nick)
    setOpen(false)
    setSuggestions([])
  }

  const showList = open && suggestions.length > 0 && suggestions[0]?.toLowerCase() !== value.trim().toLowerCase()

  return (
    <div className="pni-wrap" ref={wrapRef}>
      <input
        type="text"
        className={className}
        value={value}
        placeholder={placeholder}
        autoFocus={autoFocus}
        onChange={e => { onChange(e.target.value); setOpen(true) }}
        onFocus={() => setOpen(true)}
        onKeyDown={e => { if (e.key === 'Enter' && onSubmit) onSubmit() }}
      />
      {showList && (
        <div className="pni-suggestions">
          {suggestions.map(n => (
            <div key={n} className="pni-suggestion" onMouseDown={() => pick(n)}>
              <img className="pni-suggestion-avatar" src={`https://mc-heads.net/avatar/${encodeURIComponent(n)}/32`} alt="" />
              {n}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
