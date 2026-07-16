import { useState } from 'react'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import { categories, articles } from './wikiData'
import './WikiPage.css'

const DEFAULT_ID = articles[0]?.id

// Renderers for markdown elements that need light tweaks (no CSS changes):
// responsive images and in-page navigation for internal /wiki/ links.
function makeComponents(go) {
  return {
    img: ({ node, alt, ...props }) => (
      <img {...props} alt={alt || ''} loading="lazy" />
    ),
    a: ({ node, href = '', children, ...props }) => {
      if (href.startsWith('/wiki/')) {
        const target = href.slice('/wiki/'.length)
        const found = articles.find(a => a.id === target)
        if (found) {
          return (
            <a
              href={href}
              onClick={(e) => { e.preventDefault(); go(found.id) }}
              {...props}
            >
              {children}
            </a>
          )
        }
      }
      const external = /^https?:\/\//.test(href)
      return (
        <a href={href} {...(external ? { target: '_blank', rel: 'noreferrer' } : {})} {...props}>
          {children}
        </a>
      )
    },
  }
}

export default function WikiPage() {
  const [active, setActive] = useState(DEFAULT_ID)
  const article = articles.find(a => a.id === active) || articles[0]
  const components = makeComponents(setActive)

  return (
    <section className="section wiki-section">
      <div className="wiki-header">
        <div className="section-label">Wiki</div>
        <h1 className="wiki-title">База знаний</h1>
        <p className="wiki-lead">Всё о механиках и особенностях сервера Ichorix.</p>
      </div>

      <div className="wiki-layout">
        <div className="wiki-sidebar">
          {categories.map(cat => {
            const items = articles.filter(a => a.category === cat.slug)
            if (items.length === 0) return null
            return (
              <div key={cat.slug}>
                <div className="wiki-sidebar-title">{cat.title}</div>
                {items.map(a => (
                  <button
                    key={a.id}
                    className={`wiki-nav-item ${active === a.id ? 'active' : ''}`}
                    onClick={() => setActive(a.id)}
                  >
                    {a.title}
                  </button>
                ))}
              </div>
            )
          })}
        </div>

        <div className="wiki-content">
          {article && (
            <div className="wiki-article">
              <div className="wiki-tag">
                {categories.find(c => c.slug === article.category)?.title || 'Wiki'}
              </div>
              <h1>{article.title}</h1>
              <ReactMarkdown remarkPlugins={[remarkGfm]} components={components}>
                {article.content}
              </ReactMarkdown>
            </div>
          )}
        </div>
      </div>
    </section>
  )
}
