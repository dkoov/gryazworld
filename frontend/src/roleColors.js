const ROLE_STYLES = {
  'owner':          { color: '#ef4444', bg: 'rgba(239, 68, 68, 0.14)' },
  'администратор':  { color: '#f97316', bg: 'rgba(249, 115, 22, 0.14)' },
  'moderator':      { color: '#60a5fa', bg: 'rgba(96, 165, 250, 0.14)' },
  'судья':          { color: '#fbbf24', bg: 'rgba(251, 191, 36, 0.14)' },
  'police':         { color: '#38bdf8', bg: 'rgba(56, 189, 248, 0.14)' },
  'banker':         { color: '#4ade80', bg: 'rgba(74, 222, 128, 0.14)' },
  'keeper':         { color: '#2dd4bf', bg: 'rgba(45, 212, 191, 0.14)' },
  'helper':         { color: '#a3e635', bg: 'rgba(163, 230, 53, 0.14)' },
  'ichoplus':       { color: '#e879f9', bg: 'rgba(232, 121, 249, 0.14)' },
}

const DEFAULT_ROLE_STYLE = { color: '#c4b5fd', bg: 'rgba(139, 92, 246, 0.15)' }

export function getRoleStyle(role) {
  return ROLE_STYLES[(role || '').toLowerCase()] || DEFAULT_ROLE_STYLE
}
