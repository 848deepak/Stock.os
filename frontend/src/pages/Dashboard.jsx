import React, { useContext, useEffect, useMemo, useState } from 'react'
import { motion } from 'framer-motion'
import { AlertTriangle, ClipboardList, Package, TrendingUp } from 'lucide-react'
import {
  Area,
  AreaChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
  Cell,
} from 'recharts'
import { AuthContext } from '../context/AuthContext'
import { dashboardService, inventoryService, productService } from '../services/api'

const CATEGORY_COLORS = [
  'var(--chart-1)',
  'var(--chart-2)',
  'var(--chart-3)',
  'var(--chart-4)',
  'var(--chart-5)',
]

const ADMIN_DEMO_MOVEMENT = [
  { day: '01 Apr', in: 64, out: 52, value: 325000 },
  { day: '02 Apr', in: 74, out: 59, value: 332000 },
  { day: '03 Apr', in: 58, out: 61, value: 319000 },
  { day: '04 Apr', in: 82, out: 66, value: 348000 },
  { day: '05 Apr', in: 93, out: 71, value: 359000 },
  { day: '06 Apr', in: 79, out: 69, value: 356000 },
  { day: '07 Apr', in: 97, out: 74, value: 371000 },
]

const ADMIN_DEMO_CATEGORY = [
  { name: 'Electronics', value: 32, color: 'var(--chart-1)' },
  { name: 'Packing', value: 21, color: 'var(--chart-2)' },
  { name: 'Retail', value: 18, color: 'var(--chart-3)' },
  { name: 'Consumables', value: 16, color: 'var(--chart-4)' },
  { name: 'Others', value: 13, color: 'var(--chart-5)' },
]

const ADMIN_DEMO_ACTIVITY = [
  '3 units of SKU-1234 added by Admin - 2 hours ago',
  'Stock transfer moved to Warehouse B - 4 hours ago',
  'PO-2241 received and reconciled - 5 hours ago',
  'Low stock triggered for SKU-1193 - 8 hours ago',
  'Invoice extraction completed for INV-7294 - 1 day ago',
]

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) {
    return null
  }

  return (
    <div className="rounded-lg border border-[var(--border-bright)] bg-[var(--bg-elevated)] p-2 text-xs shadow-[var(--shadow-md)]">
      <p className="mono mb-1 text-[var(--text-secondary)]">{label}</p>
      {payload.map((item) => (
        <p key={item.dataKey} className="mono" style={{ color: item.color }}>
          {item.dataKey.toUpperCase()}: {item.value}
        </p>
      ))}
    </div>
  )
}

const formatActivityAge = (isoTimestamp) => {
  if (!isoTimestamp) {
    return 'just now'
  }
  const time = new Date(isoTimestamp).getTime()
  const now = Date.now()
  const diffMs = Math.max(0, now - time)
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))

  if (diffHours < 1) {
    const diffMinutes = Math.max(1, Math.floor(diffMs / (1000 * 60)))
    return `${diffMinutes} min ago`
  }
  if (diffHours < 24) {
    return `${diffHours} hours ago`
  }

  const diffDays = Math.floor(diffHours / 24)
  return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`
}

export default function Dashboard() {
  const { user } = useContext(AuthContext)
  const isAdminDemoUser = String(user?.username || '').toLowerCase() === 'admin'

  const [stats, setStats] = useState(null)
  const [products, setProducts] = useState([])
  const [history, setHistory] = useState([])
  const [lowStock, setLowStock] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, lowStockRes, productsRes, historyRes] = await Promise.all([
          dashboardService.getStats(),
          productService.getLowStock(),
          productService.getAll(0, 200),
          inventoryService.getHistory(0, 200),
        ])

        setStats(statsRes.data)
        setLowStock(lowStockRes.data || [])
        setProducts(productsRes.data?.content || [])
        setHistory(historyRes.data?.content || [])
      } catch (error) {
        console.error('Failed to fetch dashboard data:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  const { movementData, categoryData, activityFeed, totalValue } = useMemo(() => {
    const hasProducts = products.length > 0
    const hasTransactions = history.length > 0

    if (!hasProducts && !hasTransactions && isAdminDemoUser) {
      return {
        movementData: ADMIN_DEMO_MOVEMENT,
        categoryData: ADMIN_DEMO_CATEGORY,
        activityFeed: ADMIN_DEMO_ACTIVITY,
        totalValue: 371000,
      }
    }

    const productMap = new Map(products.map((product) => [product.id, product]))

    const now = new Date()
    const days = [...Array(7)].map((_, index) => {
      const date = new Date(now)
      date.setDate(now.getDate() - (6 - index))
      const label = date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short' })
      return { key: date.toISOString().slice(0, 10), day: label, in: 0, out: 0, value: 0 }
    })

    const dayIndex = new Map(days.map((entry, index) => [entry.key, index]))

    history.forEach((transaction) => {
      const createdAt = transaction.createdAt ? new Date(transaction.createdAt) : null
      if (!createdAt || Number.isNaN(createdAt.getTime())) {
        return
      }

      const key = createdAt.toISOString().slice(0, 10)
      const targetIndex = dayIndex.get(key)
      if (targetIndex === undefined) {
        return
      }

      const qty = Number(transaction.quantity || 0)
      const product = productMap.get(transaction.productId)
      const productPrice = Number(product?.price || 0)
      const type = String(transaction.type || '').toUpperCase()

      if (type === 'STOCK_IN') {
        days[targetIndex].in += qty
      } else if (type === 'STOCK_OUT') {
        days[targetIndex].out += qty
      }

      if (productPrice > 0) {
        days[targetIndex].value += qty * productPrice
      }
    })

    const valueByCategory = products.reduce((acc, product) => {
      const category = product.category || 'Uncategorized'
      const value = Number(product.price || 0) * Number(product.quantity || 0)
      acc[category] = (acc[category] || 0) + value
      return acc
    }, {})

    const totalInventoryValue = Object.values(valueByCategory).reduce((sum, value) => sum + value, 0)

    const categoryBreakdown = Object.entries(valueByCategory)
      .map(([name, value], index) => ({
        name,
        value: totalInventoryValue > 0 ? Math.round((value / totalInventoryValue) * 100) : 0,
        color: CATEGORY_COLORS[index % CATEGORY_COLORS.length],
      }))
      .sort((left, right) => right.value - left.value)

    const activity = [...history]
      .sort((left, right) => {
        const leftTime = left.createdAt ? new Date(left.createdAt).getTime() : 0
        const rightTime = right.createdAt ? new Date(right.createdAt).getTime() : 0
        return rightTime - leftTime
      })
      .slice(0, 5)
      .map((item) => {
        const productName = item.productName || `Product #${item.productId}`
        const actor = item.performedByUsername || 'system'
        const eventType = String(item.type || 'TRANSACTION').replaceAll('_', ' ').toLowerCase()
        return `${item.quantity} units ${eventType} for ${productName} by ${actor} - ${formatActivityAge(item.createdAt)}`
      })

    return {
      movementData: days,
      categoryData: categoryBreakdown,
      activityFeed: activity,
      totalValue: totalInventoryValue,
    }
  }, [history, isAdminDemoUser, products])

  const kpis = [
    {
      icon: Package,
      label: 'Total Products',
      value: stats?.totalProducts || products.length,
      change: '+0.0%',
      tone: 'text-[var(--accent)] bg-[var(--accent-dim)]',
    },
    {
      icon: AlertTriangle,
      label: 'Low Stock Alerts',
      value: stats?.lowStockProducts || lowStock.length,
      change: '+0.0%',
      tone: 'text-[var(--danger)] bg-[var(--danger-dim)]',
      danger: (stats?.lowStockProducts || lowStock.length) > 0,
    },
    {
      icon: TrendingUp,
      label: 'Total Inventory Value',
      value: `₹${((totalValue || 0) / 100000).toFixed(1)}L`,
      change: '+0.0%',
      tone: 'text-[var(--success)] bg-[var(--success-dim)]',
    },
    {
      icon: ClipboardList,
      label: 'Recent Transactions (24h)',
      value: stats?.recentTransactions || 0,
      change: '+0.0%',
      tone: 'text-[var(--warning)] bg-[var(--warning-dim)]',
    },
  ]

  const StatCard = ({ icon: Icon, label, value, tone, change, danger }) => (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      className="metric-card transition hover:-translate-y-0.5 hover:border-[var(--border-bright)]"
    >
      <div className="mb-3 flex items-center justify-between">
        <span className={`grid h-8 w-8 place-items-center rounded-full ${tone}`}>
          <Icon size={16} />
        </span>
        <span className="rounded-full bg-[var(--info-dim)] px-2 py-0.5 text-xs text-[var(--info)]">{change}</span>
      </div>
      <p className="text-sm text-[var(--text-secondary)]">{label}</p>
      <div className="mt-1 flex items-end justify-between">
        <p className={`display text-3xl font-bold ${danger ? 'text-[var(--danger)]' : 'text-[var(--text-primary)]'}`}>
          {value}
        </p>
      </div>
    </motion.div>
  )

  if (loading) {
    return (
      <div className="grid gap-4 md:grid-cols-4">
        {[...Array(4)].map((_, idx) => (
          <div key={idx} className="metric-card h-36 skeleton" />
        ))}
      </div>
    )
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="section-title text-4xl">Dashboard Home</h1>
        <p className="section-subtitle">Data-first visibility across stock movement, risk, and value.</p>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
        {kpis.map((kpi) => (
          <StatCard key={kpi.label} {...kpi} />
        ))}
      </div>

      <section className="grid gap-4 xl:grid-cols-[1.4fr,0.8fr]">
        <div className="card p-5">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="display text-2xl font-bold">Stock Movement</h2>
            <span className="mono text-xs text-[var(--text-muted)]">Last 7 days</span>
          </div>
          <div className="h-[280px]">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={movementData}>
                <CartesianGrid strokeDasharray="4 4" stroke="var(--border-dim)" opacity={0.5} />
                <XAxis dataKey="day" tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
                <YAxis tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
                <Tooltip content={<CustomTooltip />} />
                <Legend wrapperStyle={{ color: 'var(--text-secondary)', fontSize: 12 }} />
                <Line type="monotone" dataKey="in" stroke="var(--success)" strokeWidth={2.4} dot={false} activeDot={{ r: 4 }} />
                <Line type="monotone" dataKey="out" stroke="var(--danger)" strokeWidth={2.4} dot={false} activeDot={{ r: 4 }} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card p-5">
          <h2 className="display text-2xl font-bold">Low Stock Alerts</h2>
          <div className="mt-4 space-y-3">
            {lowStock.slice(0, 5).map((product) => (
              <div key={product.id} className="rounded-md border border-[var(--border-dim)] bg-[var(--bg-elevated)] p-3">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="mono text-xs text-[var(--text-muted)]">{product.sku}</p>
                    <p className="text-sm font-medium text-[var(--text-primary)]">{product.name}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-semibold text-[var(--danger)]">{product.quantity || 0} units</p>
                  </div>
                </div>
              </div>
            ))}
            {lowStock.length === 0 && (
              <div className="rounded-md border border-[var(--border-dim)] bg-[var(--bg-elevated)] p-4 text-sm text-[var(--text-muted)]">
                No low stock alerts right now.
              </div>
            )}
          </div>
          <button className="mt-3 text-sm text-[var(--accent)]">View all {lowStock.length} →</button>
        </div>
      </section>

      <section className="grid gap-4 xl:grid-cols-2">
        <div className="card p-5">
          <h2 className="display text-2xl font-bold">Inventory Value by Category</h2>
          <div className="mt-3 h-[280px]">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={categoryData} dataKey="value" nameKey="name" innerRadius={76} outerRadius={110} paddingAngle={3}>
                  {categoryData.map((entry) => (
                    <Cell key={entry.name} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip content={<CustomTooltip />} />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="grid grid-cols-2 gap-2">
            {categoryData.map((item) => (
              <div key={item.name} className="mono text-xs text-[var(--text-secondary)]">
                <span className="mr-2 inline-block h-2 w-2 rounded-full" style={{ background: item.color }} />
                {item.name}: {item.value}%
              </div>
            ))}
            {categoryData.length === 0 && (
              <div className="text-xs text-[var(--text-muted)]">No category data available yet.</div>
            )}
          </div>
        </div>

        <div className="card p-5">
          <h2 className="display text-2xl font-bold">Recent Activity Feed</h2>
          <div className="mt-4 space-y-4">
            {activityFeed.map((item, idx) => (
              <div key={`${item}-${idx}`} className="flex gap-3">
                <span className={`mt-1 h-2.5 w-2.5 rounded-full ${idx % 2 === 0 ? 'bg-[var(--accent)]' : 'bg-[var(--info)]'}`} />
                <p className="text-sm text-[var(--text-secondary)]">{item}</p>
              </div>
            ))}
            {activityFeed.length === 0 && (
              <p className="text-sm text-[var(--text-muted)]">No activity yet.</p>
            )}
          </div>
        </div>
      </section>

      <section className="card p-5">
        <h2 className="display mb-4 text-2xl font-bold">Inventory Value Trajectory</h2>
        <div className="h-[240px]">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={movementData}>
              <defs>
                <linearGradient id="dashboardValueFill" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="var(--chart-1)" stopOpacity={0.2} />
                  <stop offset="95%" stopColor="var(--chart-1)" stopOpacity={0.01} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="4 4" stroke="var(--border-dim)" opacity={0.5} />
              <XAxis dataKey="day" tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
              <YAxis tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
              <Tooltip content={<CustomTooltip />} />
              <Area type="monotone" dataKey="value" stroke="var(--chart-1)" fill="url(#dashboardValueFill)" strokeWidth={2.4} />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </section>
    </div>
  )
}
