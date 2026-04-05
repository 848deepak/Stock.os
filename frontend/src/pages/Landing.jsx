import React from 'react'
import { Link } from 'react-router-dom'
import {
  ArrowRight,
  Check,
  ChevronDown,
  Menu,
  PlayCircle,
  ShieldCheck,
  Sparkles,
  X,
} from 'lucide-react'

const trustedLogos = ['NorthGrid', 'Fulfillly', 'DepotOne', 'CargoMint', 'Forge Retail', 'Novora']

const problemPoints = [
  'Stockouts appear after customer complaints',
  'Teams update inventory in disconnected sheets',
  'Purchase decisions rely on stale reports',
]

const solutionPoints = [
  'Live alerts trigger before shelves go empty',
  'Barcode + PDF inputs sync into one system',
  'Dashboards show movement and margin in real time',
]

const featureDemos = [
  {
    title: 'Barcode-to-stock in under a second',
    copy: 'Scan from camera or handheld device and instantly reflect quantity changes across locations.',
    metric: '48ms sync latency',
  },
  {
    title: 'Invoice PDFs become structured products',
    copy: 'Upload supplier docs and auto-map SKU, quantity, and landed cost with review confidence.',
    metric: '99.2% extraction precision',
  },
  {
    title: 'Inventory intelligence for every role',
    copy: 'Admins get control, operators get focus. Everyone sees what they need with less noise.',
    metric: '3.4x faster decisions',
  },
]

const showcaseTabs = {
  analytics: {
    label: 'Analytics',
    blurb: 'Revenue, velocity, and risk, synced every minute.',
    cards: [
      { title: 'Revenue Velocity', value: '+18.4%', tone: 'text-[var(--success)]' },
      { title: 'Low Stock SKUs', value: '14', tone: 'text-[var(--warning)]' },
      { title: 'Stock Accuracy', value: '99.1%', tone: 'text-[var(--accent)]' },
    ],
  },
  inventory: {
    label: 'Inventory',
    blurb: 'Track quantity, reserve, and transfer across all warehouses.',
    cards: [
      { title: 'Total SKUs', value: '12,842', tone: 'text-[var(--text-primary)]' },
      { title: 'Reserved Units', value: '1,408', tone: 'text-[var(--info)]' },
      { title: 'In Transit', value: '842', tone: 'text-[var(--accent)]' },
    ],
  },
  reports: {
    label: 'Reports',
    blurb: 'Instant weekly summaries for procurement and finance.',
    cards: [
      { title: 'Purchase Savings', value: 'INR 2.8L', tone: 'text-[var(--success)]' },
      { title: 'Dead Stock', value: '4.2%', tone: 'text-[var(--danger)]' },
      { title: 'Forecast Confidence', value: '93%', tone: 'text-[var(--accent)]' },
    ],
  },
}

const testimonials = [
  {
    quote:
      'We replaced three spreadsheets, two chat groups, and one weekly panic meeting. stock.os paid for itself in 10 days.',
    name: 'Riya Mehta',
    role: 'Operations Lead, NorthGrid',
  },
  {
    quote:
      'The UI feels premium, but the real win is speed. Purchase planning now happens in minutes, not Mondays.',
    name: 'Karan Sethi',
    role: 'Supply Manager, Forge Retail',
  },
  {
    quote:
      'Our warehouse team adopted it in one shift because the interface only shows what matters for the task.',
    name: 'Aarav Kulkarni',
    role: 'COO, DepotOne',
  },
]

const pricing = {
  monthly: [
    { name: 'Starter', price: 'Free', subtitle: 'For single-location teams', cta: 'Get Started' },
    { name: 'Growth', price: 'INR 2,499', subtitle: 'For scaling inventory operations', cta: 'Start 14-day trial', popular: true },
    { name: 'Scale', price: 'Custom', subtitle: 'For multi-region enterprises', cta: 'Talk to sales' },
  ],
  yearly: [
    { name: 'Starter', price: 'Free', subtitle: 'For single-location teams', cta: 'Get Started' },
    { name: 'Growth', price: 'INR 1,999', subtitle: 'Billed yearly, save 20%', cta: 'Start 14-day trial', popular: true },
    { name: 'Scale', price: 'Custom', subtitle: 'Dedicated success + SLA', cta: 'Talk to sales' },
  ],
}

const faqs = [
  {
    question: 'Can I onboard multiple warehouses?',
    answer: 'Yes. You can create and manage unlimited locations on Growth and Scale plans with transfer visibility in one dashboard.',
  },
  {
    question: 'How fast is setup for a new team?',
    answer: 'Most teams go live in under 30 minutes by importing CSV, scanning SKUs, or uploading a supplier PDF.',
  },
  {
    question: 'Does the product support role-based access?',
    answer: 'Yes. Admins, managers, and operators can each see a focused interface tuned to responsibilities.',
  },
]

export default function Landing() {
  const [mobileOpen, setMobileOpen] = React.useState(false)
  const [scrolled, setScrolled] = React.useState(false)
  const [activeTab, setActiveTab] = React.useState('analytics')
  const [billingCycle, setBillingCycle] = React.useState('monthly')
  const [openFaq, setOpenFaq] = React.useState(0)
  const [heroPulse, setHeroPulse] = React.useState(0)
  const [parallax, setParallax] = React.useState({ x: 0, y: 0 })

  React.useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 56)
    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  React.useEffect(() => {
    const interval = window.setInterval(() => {
      setHeroPulse((value) => value + 1)
    }, 2400)

    return () => window.clearInterval(interval)
  }, [])

  const onHeroPointerMove = (event) => {
    const rect = event.currentTarget.getBoundingClientRect()
    const x = (event.clientX - rect.left - rect.width / 2) / rect.width
    const y = (event.clientY - rect.top - rect.height / 2) / rect.height
    setParallax({ x: x * 22, y: y * 22 })
  }

  const onHeroPointerLeave = () => {
    setParallax({ x: 0, y: 0 })
  }

  const currentPricing = pricing[billingCycle]
  const currentShowcase = showcaseTabs[activeTab]

  return (
    <div className="min-h-screen bg-[var(--bg-void)] dot-grid page-transition">
      <header
        className={`fixed inset-x-0 top-0 z-50 transition ${
          scrolled
            ? 'border-b border-[var(--border-dim)] bg-[rgba(10,10,15,0.82)] backdrop-blur-[20px]'
            : 'bg-transparent'
        }`}
      >
        <div className="app-container flex h-20 items-center justify-between">
          <p className="brand-wordmark text-xl text-[var(--text-primary)]">
            stock<span className="text-[var(--accent)]">.</span>os
          </p>

          <nav className="hidden items-center gap-7 md:flex">
            {['Product', 'Features', 'Pricing', 'FAQ'].map((label) => (
              <a
                key={label}
                href={`#${label.toLowerCase()}`}
                className="group relative text-sm font-medium text-[var(--text-secondary)] transition hover:text-[var(--text-primary)]"
              >
                {label}
                <span className="absolute -bottom-1 left-0 h-px w-full origin-left scale-x-0 bg-[var(--accent)] transition-transform duration-200 group-hover:scale-x-100" />
              </a>
            ))}
          </nav>

          <div className="hidden items-center gap-2 md:flex">
            <Link to="/login" className="btn-ghost" data-magnetic="true">
              Sign In
            </Link>
            <Link to="/register" className="btn-primary" data-magnetic="true">
              Get Started
            </Link>
          </div>

          <button
            className="rounded-md border border-[var(--border-base)] p-2 text-[var(--text-secondary)] md:hidden"
            onClick={() => setMobileOpen((value) => !value)}
            aria-label="Toggle navigation"
          >
            {mobileOpen ? <X size={18} /> : <Menu size={18} />}
          </button>
        </div>

        {mobileOpen && (
          <div className="border-t border-[var(--border-dim)] bg-[rgba(15,15,18,0.95)] p-5 md:hidden">
            <div className="space-y-4">
              {['Product', 'Features', 'Pricing', 'FAQ'].map((label) => (
                <a
                  key={label}
                  href={`#${label.toLowerCase()}`}
                  className="block text-lg text-[var(--text-secondary)]"
                  onClick={() => setMobileOpen(false)}
                >
                  {label}
                </a>
              ))}
              <div className="flex gap-2">
                <Link to="/login" className="btn-ghost flex-1">
                  Sign In
                </Link>
                <Link to="/register" className="btn-primary flex-1">
                  Get Started
                </Link>
              </div>
            </div>
          </div>
        )}
      </header>

      <section
        id="product"
        className="app-container relative grid min-h-screen items-center gap-12 pb-14 pt-28 lg:grid-cols-[1.02fr,0.98fr]"
        onMouseMove={onHeroPointerMove}
        onMouseLeave={onHeroPointerLeave}
      >
        <div className="reveal-on-scroll">
          <span className="inline-flex items-center gap-2 rounded-full border border-[var(--border-bright)] bg-[var(--accent-dim)] px-3 py-1 text-xs font-medium text-[var(--accent)]">
            <Sparkles size={14} /> Built for high-volume inventory teams
          </span>
          <h1 className="display mt-6 max-w-[640px] text-5xl font-extrabold leading-[0.95] text-[var(--text-primary)] md:text-7xl">
            Ship faster with
            <br />
            inventory that stays
            <span className="text-[var(--accent)]"> in sync.</span>
          </h1>

          <p className="mt-5 max-w-[520px] text-lg leading-relaxed text-[var(--text-secondary)]">
            Real-time stock visibility, smart procurement signals, and workflow automation for teams that cannot afford operational drift.
          </p>

          <div className="mt-8 flex flex-wrap items-center gap-3 md:gap-4">
            <Link to="/register" className="btn-primary h-12 px-6 text-[15px]" data-magnetic="true">
              Get Started <ArrowRight size={16} />
            </Link>
            <Link to="/login" className="btn-secondary h-12 px-6 text-[15px]" data-magnetic="true">
              <PlayCircle size={16} /> View Demo
            </Link>
          </div>

          <div className="mt-6 flex flex-wrap items-center gap-4 text-xs text-[var(--text-muted)]">
            <span className="inline-flex items-center gap-1">
              <Check size={12} /> 14-day trial
            </span>
            <span className="inline-flex items-center gap-1">
              <ShieldCheck size={12} /> Enterprise-grade security
            </span>
            <span className="inline-flex items-center gap-1">
              <Check size={12} /> Trusted by 10,000+ users
            </span>
          </div>
        </div>

        <div className="reveal-on-scroll">
          <div
            className="glass-card interactive-surface overflow-hidden rounded-2xl p-5"
            style={{ transform: `translate3d(${parallax.x}px, ${parallax.y}px, 0)` }}
          >
            <div className="flex items-center justify-between rounded-xl border border-[var(--border-dim)] bg-[rgba(8,8,10,0.7)] px-4 py-3">
              <div>
                <p className="text-xs text-[var(--text-muted)]">Live Operations Board</p>
                <p className="text-sm font-semibold text-[var(--text-primary)]">Realtime Inventory Overview</p>
              </div>
              <span className="inline-flex items-center gap-1 rounded-full bg-[var(--accent-dim)] px-2 py-1 text-[11px] text-[var(--accent)]">
                <span className="h-2 w-2 animate-pulse rounded-full bg-[var(--accent)]" />
                Live
              </span>
            </div>

            <div className="mt-4 grid gap-3 sm:grid-cols-3">
              <div className="rounded-xl border border-[var(--border-dim)] bg-[var(--bg-elevated)] p-3">
                <p className="text-xs text-[var(--text-muted)]">Active SKUs</p>
                <p className="display mt-1 text-2xl font-bold" data-countup={5400 + (heroPulse % 4) * 42}>
                  0
                </p>
              </div>
              <div className="rounded-xl border border-[var(--border-dim)] bg-[var(--bg-elevated)] p-3">
                <p className="text-xs text-[var(--text-muted)]">Critical Alerts</p>
                <p className="display mt-1 text-2xl font-bold text-[var(--warning)]" data-countup={18 + (heroPulse % 3)}>
                  0
                </p>
              </div>
              <div className="rounded-xl border border-[var(--border-dim)] bg-[var(--bg-elevated)] p-3">
                <p className="text-xs text-[var(--text-muted)]">Fulfillment SLA</p>
                <p className="display mt-1 text-2xl font-bold text-[var(--success)]">99.4%</p>
              </div>
            </div>

            <div className="mt-4 rounded-xl border border-[var(--border-dim)] bg-[rgba(8,8,10,0.6)] p-3">
              <div className="mono grid grid-cols-[1fr,90px,90px] border-b border-[var(--border-dim)] px-1 pb-2 text-[11px] text-[var(--text-muted)]">
                <span>Product</span>
                <span>Qty</span>
                <span>Status</span>
              </div>
              <div className="mono mt-2 space-y-2 text-[12px] text-[var(--text-secondary)]">
                <div className="grid grid-cols-[1fr,90px,90px]">
                  <span>SKU-9031</span>
                  <span>{146 + (heroPulse % 6)}</span>
                  <span className="text-[var(--success)]">Stable</span>
                </div>
                <div className="grid grid-cols-[1fr,90px,90px]">
                  <span>SKU-1193</span>
                  <span>{4 + (heroPulse % 2)}</span>
                  <span className="text-[var(--danger)]">Low</span>
                </div>
                <div className="grid grid-cols-[1fr,90px,90px]">
                  <span>SKU-4410</span>
                  <span>{54 + (heroPulse % 4)}</span>
                  <span className="text-[var(--accent)]">Restocking</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="border-y border-[var(--border-dim)] bg-[var(--bg-surface)] py-6">
        <div className="app-container">
          <p className="text-center text-xs uppercase tracking-[0.1em] text-[var(--text-muted)]">
            Trusted by 10,000+ users across modern operations teams
          </p>
          <div className="mt-5 flex flex-wrap items-center justify-center gap-8">
            {trustedLogos.map((logo) => (
              <p key={logo} className="display text-xl font-semibold text-[var(--text-muted)]">
                {logo}
              </p>
            ))}
          </div>
        </div>
      </section>

      <section id="features" className="app-container py-20">
        <div className="grid gap-8 lg:grid-cols-2">
          <div className="reveal-on-scroll rounded-2xl border border-[rgba(255,68,68,0.22)] bg-[rgba(255,68,68,0.05)] p-7">
            <p className="text-xs font-semibold uppercase tracking-[0.12em] text-[var(--danger)]">The Problem</p>
            <h2 className="display mt-3 text-4xl font-bold">Most inventory tools create extra work.</h2>
            <ul className="mt-5 space-y-3 text-sm text-[var(--text-secondary)]">
              {problemPoints.map((point) => (
                <li key={point} className="rounded-lg border border-[rgba(255,68,68,0.16)] px-3 py-2">
                  {point}
                </li>
              ))}
            </ul>
          </div>

          <div className="reveal-on-scroll rounded-2xl border border-[rgba(200,255,0,0.25)] bg-[var(--accent-dim)] p-7">
            <p className="text-xs font-semibold uppercase tracking-[0.12em] text-[var(--accent)]">The Solution</p>
            <h2 className="display mt-3 text-4xl font-bold">One calm workspace for your whole flow.</h2>
            <ul className="mt-5 space-y-3 text-sm text-[var(--text-secondary)]">
              {solutionPoints.map((point) => (
                <li key={point} className="rounded-lg border border-[rgba(200,255,0,0.2)] px-3 py-2">
                  {point}
                </li>
              ))}
            </ul>
          </div>
        </div>

        <div className="mt-14 grid gap-5 md:grid-cols-3">
          {featureDemos.map((feature) => (
            <article key={feature.title} className="reveal-on-scroll interactive-surface glass-card rounded-2xl p-5">
              <p className="text-xs uppercase tracking-[0.1em] text-[var(--accent)]">Interactive Feature</p>
              <h3 className="display mt-3 text-2xl font-bold">{feature.title}</h3>
              <p className="mt-2 text-sm leading-relaxed text-[var(--text-secondary)]">{feature.copy}</p>
              <div className="mt-5 inline-flex rounded-full border border-[var(--border-bright)] px-3 py-1 text-xs text-[var(--text-primary)]">
                {feature.metric}
              </div>
            </article>
          ))}
        </div>
      </section>

      <section className="border-y border-[var(--border-dim)] bg-[var(--bg-surface)] py-20">
        <div className="app-container">
          <div className="reveal-on-scroll flex flex-wrap items-end justify-between gap-4">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.12em] text-[var(--accent)]">Dashboard Showcase</p>
              <h2 className="display mt-3 text-4xl font-bold md:text-5xl">See the product before you sign up.</h2>
            </div>
            <div className="inline-flex rounded-full border border-[var(--border-base)] bg-[var(--bg-elevated)] p-1">
              {Object.entries(showcaseTabs).map(([key, tab]) => (
                <button
                  key={key}
                  type="button"
                  onClick={() => setActiveTab(key)}
                  className={`rounded-full px-4 py-2 text-sm transition ${
                    activeTab === key ? 'bg-[var(--accent)] text-[#08080a]' : 'text-[var(--text-secondary)]'
                  }`}
                >
                  {tab.label}
                </button>
              ))}
            </div>
          </div>

          <div className="reveal-on-scroll mt-8 glass-card rounded-2xl p-6">
            <p className="text-sm text-[var(--text-secondary)]">{currentShowcase.blurb}</p>
            <div className="mt-5 grid gap-4 md:grid-cols-3">
              {currentShowcase.cards.map((card) => (
                <div key={card.title} className="rounded-xl border border-[var(--border-dim)] bg-[rgba(8,8,10,0.66)] p-4">
                  <p className="text-xs text-[var(--text-muted)]">{card.title}</p>
                  <p className={`display mt-2 text-3xl font-bold ${card.tone}`}>{card.value}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="app-container py-20">
        <div className="reveal-on-scroll text-center">
          <p className="text-xs font-semibold uppercase tracking-[0.12em] text-[var(--accent)]">Testimonials</p>
          <h2 className="display mt-3 text-4xl font-bold md:text-5xl">Built for teams that run real inventory.</h2>
        </div>

        <div className="mt-9 grid gap-5 md:grid-cols-3">
          {testimonials.map((item) => (
            <article key={item.name} className="reveal-on-scroll interactive-surface rounded-2xl border border-[var(--border-dim)] bg-[var(--bg-surface)] p-6">
              <p className="text-sm leading-relaxed text-[var(--text-secondary)]">"{item.quote}"</p>
              <p className="mt-5 text-sm font-semibold text-[var(--text-primary)]">{item.name}</p>
              <p className="text-xs text-[var(--text-muted)]">{item.role}</p>
            </article>
          ))}
        </div>
      </section>

      <section id="pricing" className="app-container py-16">
        <div className="reveal-on-scroll text-center">
          <p className="text-xs font-semibold uppercase tracking-[0.12em] text-[var(--accent)]">Pricing</p>
          <h2 className="display mt-3 text-4xl font-bold md:text-5xl">Choose a plan that scales with your operation.</h2>
          <div className="mt-6 inline-flex rounded-full border border-[var(--border-base)] bg-[var(--bg-elevated)] p-1">
            <button
              className={`rounded-full px-4 py-2 text-sm ${billingCycle === 'monthly' ? 'bg-[var(--accent)] text-[#08080a]' : 'text-[var(--text-secondary)]'}`}
              onClick={() => setBillingCycle('monthly')}
              type="button"
            >
              Monthly
            </button>
            <button
              className={`rounded-full px-4 py-2 text-sm ${billingCycle === 'yearly' ? 'bg-[var(--accent)] text-[#08080a]' : 'text-[var(--text-secondary)]'}`}
              onClick={() => setBillingCycle('yearly')}
              type="button"
            >
              Yearly
            </button>
          </div>
        </div>

        <div className="mt-10 grid gap-5 md:grid-cols-3">
          {currentPricing.map((plan) => (
            <article
              key={plan.name}
              className={`reveal-on-scroll rounded-2xl border p-6 ${
                plan.popular
                  ? 'border-[var(--accent)] bg-[var(--bg-surface)] shadow-[var(--shadow-accent)]'
                  : 'border-[var(--border-dim)] bg-[var(--bg-surface)]'
              }`}
            >
              {plan.popular && (
                <span className="inline-flex rounded-full bg-[var(--accent)] px-3 py-1 text-xs font-semibold text-[#08080a]">
                  Most Popular
                </span>
              )}
              <h3 className="display mt-3 text-2xl font-bold">{plan.name}</h3>
              <p className="display mt-3 text-5xl font-extrabold">{plan.price}</p>
              <p className="mt-2 text-sm text-[var(--text-secondary)]">{plan.subtitle}</p>
              <button className={plan.popular ? 'btn-primary mt-6 w-full' : 'btn-secondary mt-6 w-full'} data-magnetic="true">
                {plan.cta}
              </button>
            </article>
          ))}
        </div>
      </section>

      <section id="faq" className="app-container py-12">
        <div className="reveal-on-scroll grid gap-8 lg:grid-cols-[1fr,1.1fr]">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.12em] text-[var(--accent)]">FAQ</p>
            <h2 className="display mt-3 text-4xl font-bold md:text-5xl">Answers for fast-moving teams.</h2>
          </div>
          <div className="space-y-3">
            {faqs.map((faq, index) => {
              const open = openFaq === index
              return (
                <article key={faq.question} className="rounded-xl border border-[var(--border-dim)] bg-[var(--bg-surface)]">
                  <button
                    type="button"
                    className="flex w-full items-center justify-between px-4 py-4 text-left"
                    onClick={() => setOpenFaq(open ? -1 : index)}
                  >
                    <span className="text-sm font-medium text-[var(--text-primary)]">{faq.question}</span>
                    <ChevronDown
                      size={16}
                      className={`transition ${open ? 'rotate-180 text-[var(--accent)]' : 'text-[var(--text-muted)]'}`}
                    />
                  </button>
                  {open && <p className="px-4 pb-4 text-sm leading-relaxed text-[var(--text-secondary)]">{faq.answer}</p>}
                </article>
              )
            })}
          </div>
        </div>
      </section>

      <section className="app-container py-16">
        <div className="reveal-on-scroll rounded-2xl border border-[var(--border-bright)] bg-[radial-gradient(circle_at_top,rgba(200,255,0,0.12),transparent_62%)] px-7 py-14 text-center">
          <h2 className="display text-4xl font-extrabold md:text-6xl">Upgrade the way your inventory team works.</h2>
          <p className="mx-auto mt-4 max-w-[560px] text-lg text-[var(--text-secondary)]">
            One platform for operators, managers, and leadership. Start in minutes.
          </p>
          <div className="mt-8 flex flex-wrap justify-center gap-3">
            <Link to="/register" className="btn-primary" data-magnetic="true">
              Get Started
            </Link>
            <Link to="/login" className="btn-secondary" data-magnetic="true">
              View Demo
            </Link>
          </div>
        </div>
      </section>

      <footer className="border-t border-[var(--border-dim)] py-10">
        <div className="app-container flex flex-col items-center justify-between gap-3 text-center text-sm text-[var(--text-muted)] md:flex-row md:text-left">
          <p>(c) {new Date().getFullYear()} stock.os</p>
          <p>Calm software for complex inventory operations.</p>
        </div>
      </footer>
    </div>
  )
}
