import React, { useContext, useEffect, useMemo, useRef, useState } from 'react'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { AuthContext } from '../../context/AuthContext'
import CommandPalette from '../system/CommandPalette'
import {
  Bell,
  BarChart3,
  Boxes,
  Building2,
  ChevronDown,
  ClipboardList,
  FileText,
  LogOut,
  ScanLine,
  Search,
  Settings,
  User,
} from 'lucide-react'

const PATH_LABELS = {
  '/app': 'Overview',
  '/app/products': 'Products',
  '/app/inventory': 'Inventory',
  '/app/warehouses': 'Warehouses',
  '/app/analytics': 'Analytics',
  '/app/scanner': 'Scanner',
  '/app/pdf-upload': 'PDF Upload',
}

function Breadcrumbs({ pathname }) {
  const crumbs = pathname.split('/').filter(Boolean)

  if (crumbs[0] !== 'app') {
    return null
  }

  const parts = crumbs.map((_, index) => `/${crumbs.slice(0, index + 1).join('/')}`)

  return (
    <nav className="flex items-center gap-2 text-xs text-[var(--text-muted)]">
      {parts.map((path, index) => {
        const isLast = index === parts.length - 1
        const label = PATH_LABELS[path] || 'Page'

        return (
          <React.Fragment key={path}>
            {!isLast ? (
              <Link to={path} className="transition hover:text-[var(--text-primary)]">
                {label}
              </Link>
            ) : (
              <span className="text-[var(--text-secondary)]">{label}</span>
            )}
            {!isLast && <span>/</span>}
          </React.Fragment>
        )
      })}
    </nav>
  )
}

export default function AppShell() {
  const location = useLocation()
  const navigate = useNavigate()
  const { user, logout } = useContext(AuthContext)
  const [paletteOpen, setPaletteOpen] = useState(false)
  const [profileOpen, setProfileOpen] = useState(false)
  const [liveTick, setLiveTick] = useState(0)
  const profileRef = useRef(null)

  const role = String(user?.role || 'STAFF').toUpperCase()
  const isAdmin = role === 'ADMIN'

  const navItems = useMemo(
    () => [
      { to: '/app', label: 'Overview', icon: BarChart3, access: 'all' },
      { to: '/app/products', label: 'Products', icon: Boxes, access: 'all' },
      { to: '/app/inventory', label: 'Inventory', icon: ClipboardList, access: 'all' },
      { to: '/app/warehouses', label: 'Warehouses', icon: Building2, access: 'all' },
      { to: '/app/analytics', label: 'Analytics', icon: BarChart3, access: 'admin' },
      { to: '/app/scanner', label: 'Scanner', icon: ScanLine, access: 'all' },
      { to: '/app/pdf-upload', label: 'PDF Upload', icon: FileText, access: 'admin' },
    ],
    []
  )

  const filteredNavItems = useMemo(() => {
    if (isAdmin) {
      return navItems
    }
    return navItems.filter((item) => item.access === 'all')
  }, [isAdmin, navItems])

  const pageTitle = useMemo(() => {
    const current = filteredNavItems.find((item) => location.pathname === item.to)
    return current?.label || 'Overview'
  }, [location.pathname, filteredNavItems])

  useEffect(() => {
    const onKeyDown = (event) => {
      if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') {
        event.preventDefault()
        setPaletteOpen((prev) => !prev)
      }
      if (event.key === '/' && document.activeElement?.tagName !== 'INPUT') {
        event.preventDefault()
        document.getElementById('app-shell-search')?.focus()
      }
    }

    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [])

  useEffect(() => {
    const interval = window.setInterval(() => {
      setLiveTick((value) => value + 1)
    }, 4000)

    return () => window.clearInterval(interval)
  }, [])

  useEffect(() => {
    const onClickOutside = (event) => {
      if (!profileRef.current) {
        return
      }
      if (!profileRef.current.contains(event.target)) {
        setProfileOpen(false)
      }
    }

    document.addEventListener('mousedown', onClickOutside)
    return () => document.removeEventListener('mousedown', onClickOutside)
  }, [])

  return (
    <div className="min-h-screen bg-[var(--bg-base)] page-transition">
      <CommandPalette open={paletteOpen} onClose={() => setPaletteOpen(false)} />
      <div className="grid min-h-screen md:grid-cols-[240px,1fr]">
        <aside className="hidden border-r border-[var(--border-dim)] bg-[var(--bg-surface)] p-4 md:block">
          <div className="sticky top-4">
            <div className="px-2 py-3">
              <p className="brand-wordmark text-xl text-[var(--text-primary)]">
                stock<span className="text-[var(--accent)]">.</span>os
              </p>
              <p className="mt-2 text-[10px] font-semibold uppercase tracking-[0.12em] text-[var(--text-muted)]">
                Inventory Command
              </p>
            </div>

            <p className="px-2 pb-2 pt-4 text-[10px] font-semibold uppercase tracking-[0.1em] text-[var(--text-muted)]">
              Workspace
            </p>

            <nav className="space-y-1">
              {filteredNavItems.map((item) => {
                const Icon = item.icon
                const active = location.pathname === item.to
                return (
                  <Link
                    key={item.to}
                    to={item.to}
                    className={`relative flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium transition ${
                      active
                        ? 'bg-[var(--accent-dim)] text-[var(--accent)]'
                        : 'text-[var(--text-secondary)] hover:bg-white/[0.03] hover:text-[var(--text-primary)]'
                    }`}
                  >
                    {active && <span className="absolute left-0 top-1/2 h-6 w-0.5 -translate-y-1/2 bg-[var(--accent)]" />}
                    <Icon size={16} strokeWidth={1.6} />
                    {item.label}
                  </Link>
                )
              })}
            </nav>

            <div className="mt-6 rounded-lg border border-[var(--border-base)] bg-[var(--bg-elevated)] p-3">
              <div className="flex items-center gap-2">
                <div className="grid h-8 w-8 place-items-center rounded-full bg-[var(--accent)] text-xs font-semibold text-[#08080a]">
                  {(user?.username || 'U').slice(0, 1).toUpperCase()}
                </div>
                <div>
                  <p className="text-sm font-medium text-[var(--text-primary)]">{user?.username || 'User'}</p>
                  <p className="text-[10px] uppercase tracking-[0.08em] text-[var(--text-muted)]">
                    {isAdmin ? 'Admin' : 'Staff'}
                  </p>
                </div>
              </div>

              <button
                onClick={() => {
                  logout()
                  navigate('/login')
                }}
                className="btn-ghost mt-3 w-full justify-start text-xs"
              >
                <LogOut size={14} />
                Logout
              </button>
            </div>
          </div>
        </aside>

        <div className="min-w-0 p-3 md:p-6">
          <header className="mb-4 rounded-lg border border-[var(--border-dim)] bg-[var(--bg-surface)] px-4 py-3">
            <div className="flex items-center justify-between gap-3">
              <div>
                <Breadcrumbs pathname={location.pathname} />
                <p className="mt-1 text-sm font-medium text-[var(--text-primary)]">
                  Welcome back, {user?.username || 'User'}
                </p>
              </div>

              <div className="hidden items-center gap-3 md:flex">
                <label className="relative block w-[320px]">
                  <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[var(--text-muted)]" />
                  <input
                    id="app-shell-search"
                    className="input w-full pl-9 pr-12 text-sm"
                    placeholder="Search products, orders, SKUs..."
                    onFocus={() => setPaletteOpen(true)}
                  />
                  <span className="absolute right-2 top-1/2 -translate-y-1/2 rounded border border-[var(--border-base)] px-1.5 py-0.5 text-[10px] text-[var(--text-disabled)]">
                    CMD+K
                  </span>
                </label>

                <button className="relative rounded-md border border-[var(--border-base)] bg-[var(--bg-elevated)] p-2 text-[var(--text-secondary)] transition hover:text-[var(--text-primary)]">
                  <Bell size={16} />
                  <span className="absolute -right-1 -top-1 grid h-4 min-w-4 place-items-center rounded-full bg-[var(--danger)] px-1 text-[10px] font-semibold text-white">
                    {3 + (liveTick % 3)}
                  </span>
                </button>

                <div className="relative" ref={profileRef}>
                  <button
                    type="button"
                    onClick={() => setProfileOpen((value) => !value)}
                    className="inline-flex items-center gap-2 rounded-md border border-[var(--border-base)] bg-[var(--bg-elevated)] px-2 py-1.5 text-sm text-[var(--text-secondary)] transition hover:text-[var(--text-primary)]"
                  >
                    <span className="grid h-7 w-7 place-items-center rounded-full bg-[var(--accent)] text-xs font-semibold text-[#08080a]">
                      {(user?.username || 'U').slice(0, 1).toUpperCase()}
                    </span>
                    <ChevronDown size={14} />
                  </button>

                  {profileOpen && (
                    <div className="dropdown-popover absolute right-0 top-11 z-20 w-44 rounded-md border border-[var(--border-base)] bg-[var(--bg-surface)] p-1">
                      <button className="flex w-full items-center gap-2 rounded px-2 py-2 text-sm text-[var(--text-secondary)] hover:bg-[var(--bg-elevated)] hover:text-[var(--text-primary)]">
                        <User size={14} />
                        Profile
                      </button>
                      <button className="flex w-full items-center gap-2 rounded px-2 py-2 text-sm text-[var(--text-secondary)] hover:bg-[var(--bg-elevated)] hover:text-[var(--text-primary)]">
                        <Settings size={14} />
                        Preferences
                      </button>
                      <button
                        onClick={() => {
                          logout()
                          navigate('/login')
                        }}
                        className="flex w-full items-center gap-2 rounded px-2 py-2 text-sm text-[var(--danger)] hover:bg-[var(--danger-dim)]"
                      >
                        <LogOut size={14} />
                        Logout
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="mt-3 flex items-center gap-2 rounded-md border border-[var(--border-dim)] bg-[var(--bg-elevated)] px-3 py-2 text-xs text-[var(--text-secondary)] md:hidden">
              <span className="h-2 w-2 animate-pulse rounded-full bg-[var(--accent)]" />
              Live updates enabled for {pageTitle}
            </div>
          </header>

          <motion.main
            key={location.pathname}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.22, ease: 'easeOut' }}
          >
            <Outlet />
          </motion.main>
        </div>
      </div>

      <nav className="fixed inset-x-0 bottom-0 z-40 grid grid-cols-4 border-t border-[var(--border-dim)] bg-[var(--bg-surface)] p-2 md:hidden">
        {filteredNavItems.slice(0, 4).map((item) => {
          const Icon = item.icon
          const active = location.pathname === item.to
          return (
            <Link
              key={item.to}
              to={item.to}
              className={`flex flex-col items-center gap-1 rounded px-1 py-1.5 text-[11px] ${
                active ? 'text-[var(--accent)]' : 'text-[var(--text-secondary)]'
              }`}
            >
              <Icon size={14} />
              {item.label}
            </Link>
          )
        })}
      </nav>
    </div>
  )
}
