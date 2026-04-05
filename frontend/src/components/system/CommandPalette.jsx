import React, { useMemo, useState } from 'react'
import { Box, FileText, LineChart, ScanLine, Search } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

const entries = [
  { group: 'Products', label: 'Open Products', hint: '/app/products', to: '/app/products', icon: Box },
  { group: 'Products', label: 'Open Inventory', hint: '/app/inventory', to: '/app/inventory', icon: Box },
  { group: 'Actions', label: 'Open Scanner', hint: '/app/scanner', to: '/app/scanner', icon: ScanLine },
  { group: 'Actions', label: 'Open PDF Upload', hint: '/app/pdf-upload', to: '/app/pdf-upload', icon: FileText },
  { group: 'Analytics', label: 'Open Analytics', hint: '/app/analytics', to: '/app/analytics', icon: LineChart },
]

export default function CommandPalette({ open, onClose }) {
  const [query, setQuery] = useState('')
  const [activeIndex, setActiveIndex] = useState(0)
  const navigate = useNavigate()

  const filtered = useMemo(() => {
    const list = entries.filter((item) => {
      const q = query.trim().toLowerCase()
      if (!q) {
        return true
      }
      return `${item.label} ${item.hint} ${item.group}`.toLowerCase().includes(q)
    })

    return list
  }, [query])

  const grouped = useMemo(() => {
    const groups = new Map()
    filtered.forEach((item) => {
      if (!groups.has(item.group)) {
        groups.set(item.group, [])
      }
      groups.get(item.group).push(item)
    })
    return [...groups.entries()]
  }, [filtered])

  React.useEffect(() => {
    if (!open) {
      setQuery('')
      setActiveIndex(0)
    }
  }, [open])

  React.useEffect(() => {
    if (!open) {
      return undefined
    }

    const onKey = (event) => {
      if (event.key === 'ArrowDown') {
        event.preventDefault()
        setActiveIndex((prev) => (filtered.length ? (prev + 1) % filtered.length : 0))
      }

      if (event.key === 'ArrowUp') {
        event.preventDefault()
        setActiveIndex((prev) => (filtered.length ? (prev - 1 + filtered.length) % filtered.length : 0))
      }

      if (event.key === 'Enter' && filtered[activeIndex]) {
        event.preventDefault()
        navigate(filtered[activeIndex].to)
        onClose()
      }

      if (event.key === 'Escape') {
        event.preventDefault()
        onClose()
      }
    }

    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [activeIndex, filtered, navigate, onClose, open])

  if (!open) {
    return null
  }

  let rowIndex = -1

  return (
    <div className="fixed inset-0 z-[90] grid place-items-start bg-black/70 px-4 pt-[12vh] backdrop-blur-md" onClick={onClose}>
      <div
        className="w-full max-w-[560px] overflow-hidden rounded-xl border border-[var(--border-bright)] bg-[var(--bg-overlay)] shadow-[var(--shadow-lg)]"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="relative border-b border-[var(--border-dim)] px-4 py-3">
          <Search className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-[var(--text-muted)]" />
          <input
            autoFocus
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            className="w-full bg-transparent pl-7 text-xl text-[var(--text-primary)] outline-none placeholder:text-[var(--text-muted)]"
            placeholder="Search products, routes, actions"
          />
        </div>

        <div className="max-h-[360px] overflow-y-auto p-2">
          {grouped.length === 0 ? (
            <div className="p-3 text-sm text-[var(--text-muted)]">No results.</div>
          ) : (
            grouped.map(([group, items]) => (
              <div key={group} className="mb-3">
                <p className="px-2 py-1 text-[10px] font-semibold uppercase tracking-[0.1em] text-[var(--text-muted)]">
                  {group}
                </p>
                <div className="space-y-1">
                  {items.map((item) => {
                    rowIndex += 1
                    const current = rowIndex
                    const active = current === activeIndex
                    const Icon = item.icon
                    return (
                      <button
                        key={item.to}
                        className={`flex h-10 w-full items-center justify-between rounded-md px-2.5 text-left text-sm transition ${
                          active
                            ? 'bg-[var(--accent-dim)] text-[var(--text-primary)]'
                            : 'text-[var(--text-secondary)] hover:bg-white/[0.03]'
                        }`}
                        onMouseEnter={() => setActiveIndex(current)}
                        onClick={() => {
                          navigate(item.to)
                          onClose()
                        }}
                      >
                        <span className="flex items-center gap-2">
                          <Icon size={14} />
                          {item.label}
                        </span>
                        <span className="mono text-xs text-[var(--text-muted)]">{item.hint}</span>
                      </button>
                    )
                  })}
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  )
}
