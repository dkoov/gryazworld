import { useEffect, useRef, useState } from 'react'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import './DeadlinePicker.css'

const WEEKDAYS = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс']
const MONTHS = ['Января', 'Февраля', 'Марта', 'Апреля', 'Мая', 'Июня', 'Июля', 'Августа', 'Сентября', 'Октября', 'Ноября', 'Декабря']

function pad2(n) { return String(n).padStart(2, '0') }

function buildGrid(viewYear, viewMonth) {
  const first = new Date(viewYear, viewMonth, 1)
  const firstWeekday = (first.getDay() + 6) % 7 // 0=Пн
  const daysInMonth = new Date(viewYear, viewMonth + 1, 0).getDate()
  const daysInPrevMonth = new Date(viewYear, viewMonth, 0).getDate()

  const cells = []
  for (let i = firstWeekday - 1; i >= 0; i--) {
    cells.push({ day: daysInPrevMonth - i, muted: true, monthOffset: -1 })
  }
  for (let d = 1; d <= daysInMonth; d++) {
    cells.push({ day: d, muted: false, monthOffset: 0 })
  }
  while (cells.length % 7 !== 0 || cells.length < 42) {
    const nextIndex = cells.length - firstWeekday - daysInMonth + 1
    cells.push({ day: nextIndex, muted: true, monthOffset: 1 })
    if (cells.length >= 42) break
  }
  return cells
}

export default function DeadlinePicker({ value, onChange, placeholder }) {
  const [open, setOpen] = useState(false)
  const today = new Date()
  const [viewYear, setViewYear] = useState((value ?? today).getFullYear())
  const [viewMonth, setViewMonth] = useState((value ?? today).getMonth())
  const [hh, setHh] = useState(value ? pad2(value.getHours()) : '12')
  const [mm, setMm] = useState(value ? pad2(value.getMinutes()) : '00')
  const wrapRef = useRef(null)

  useEffect(() => {
    function onClickOutside(e) {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) setOpen(false)
    }
    document.addEventListener('mousedown', onClickOutside)
    return () => document.removeEventListener('mousedown', onClickOutside)
  }, [])

  useEffect(() => {
    if (value) {
      setViewYear(value.getFullYear())
      setViewMonth(value.getMonth())
      setHh(pad2(value.getHours()))
      setMm(pad2(value.getMinutes()))
    }
  }, [value])

  function goMonth(delta) {
    let m = viewMonth + delta
    let y = viewYear
    if (m < 0) { m = 11; y -= 1 }
    if (m > 11) { m = 0; y += 1 }
    setViewMonth(m)
    setViewYear(y)
  }

  function pickDay(cell) {
    const targetMonth = viewMonth + cell.monthOffset
    const d = new Date(viewYear, targetMonth, cell.day, Number(hh) || 0, Number(mm) || 0)
    onChange(d)
    if (cell.monthOffset !== 0) {
      setViewMonth(d.getMonth())
      setViewYear(d.getFullYear())
    }
  }

  function applyTime(nextHh, nextMm) {
    if (!value) return
    const d = new Date(value)
    d.setHours(Number(nextHh) || 0, Number(nextMm) || 0)
    onChange(d)
  }

  function onHhChange(e) {
    const v = e.target.value.replace(/\D/g, '').slice(0, 2)
    setHh(v)
    if (v.length === 2) applyTime(Math.min(23, Number(v)), mm)
  }

  function onMmChange(e) {
    const v = e.target.value.replace(/\D/g, '').slice(0, 2)
    setMm(v)
    if (v.length === 2) applyTime(hh, Math.min(59, Number(v)))
  }

  const cells = buildGrid(viewYear, viewMonth)
  const label = value
    ? `${pad2(value.getDate())}.${pad2(value.getMonth() + 1)}.${value.getFullYear()} ${pad2(value.getHours())}:${pad2(value.getMinutes())}`
    : (placeholder || 'Не выбрано')

  return (
    <div className="dlp-wrap" ref={wrapRef}>
      <button type="button" className={`dlp-trigger${value ? '' : ' dlp-trigger-empty'}`} onClick={() => setOpen(o => !o)}>
        {label}
      </button>
      {value && (
        <button type="button" className="dlp-clear" title="Очистить" onClick={() => onChange(null)}>×</button>
      )}
      {open && (
        <div className="dlp-panel">
          <div className="dlp-header">
            <button type="button" className="dlp-nav" onClick={() => goMonth(-1)}><ChevronLeft size={16} /></button>
            <span className="dlp-month-label">{MONTHS[viewMonth]} {viewYear}</span>
            <button type="button" className="dlp-nav" onClick={() => goMonth(1)}><ChevronRight size={16} /></button>
          </div>
          <div className="dlp-weekdays">
            {WEEKDAYS.map(w => <span key={w}>{w}</span>)}
          </div>
          <div className="dlp-grid">
            {cells.map((cell, i) => {
              const isSelected = value
                && cell.monthOffset === 0
                && value.getDate() === cell.day
                && value.getMonth() === viewMonth
                && value.getFullYear() === viewYear
              const isToday = !cell.muted
                && today.getDate() === cell.day
                && today.getMonth() === viewMonth
                && today.getFullYear() === viewYear
              return (
                <button
                  type="button"
                  key={i}
                  className={`dlp-day${cell.muted ? ' dlp-day-muted' : ''}${isSelected ? ' dlp-day-selected' : ''}${isToday ? ' dlp-day-today' : ''}`}
                  onClick={() => pickDay(cell)}
                >
                  {cell.day}
                </button>
              )
            })}
          </div>
          <div className="dlp-time-row">
            <span className="dlp-time-label">Время</span>
            <input className="dlp-time-input" value={hh} onChange={onHhChange} inputMode="numeric" maxLength={2} placeholder="ЧЧ" />
            <span className="dlp-time-colon">:</span>
            <input className="dlp-time-input" value={mm} onChange={onMmChange} inputMode="numeric" maxLength={2} placeholder="ММ" />
          </div>
          <div className="dlp-actions">
            <button type="button" className="dlp-action-ghost" onClick={() => { onChange(null); setOpen(false) }}>Очистить</button>
            <button type="button" className="dlp-action-primary" onClick={() => setOpen(false)}>Готово</button>
          </div>
        </div>
      )}
    </div>
  )
}
