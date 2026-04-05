import React from 'react'
import {
  Bar,
  BarChart,
  CartesianGrid,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
  Cell,
  Area,
  AreaChart,
} from 'recharts'

const ranges = ['7D', '30D', '90D', 'Custom']

const movement = [
  { day: '01', in: 52, out: 43, value: 258 },
  { day: '02', in: 60, out: 49, value: 264 },
  { day: '03', in: 57, out: 47, value: 271 },
  { day: '04', in: 71, out: 55, value: 278 },
  { day: '05', in: 76, out: 58, value: 289 },
  { day: '06', in: 74, out: 56, value: 292 },
  { day: '07', in: 82, out: 62, value: 300 },
]

const categoryData = [
  { name: 'Electronics', value: 35, color: 'var(--chart-1)' },
  { name: 'Retail', value: 24, color: 'var(--chart-2)' },
  { name: 'Packing', value: 18, color: 'var(--chart-3)' },
  { name: 'Others', value: 23, color: 'var(--chart-4)' },
]

const topProducts = [
  { name: 'SKU-7723', volume: 188 },
  { name: 'SKU-4331', volume: 176 },
  { name: 'SKU-9981', volume: 153 },
  { name: 'SKU-1193', volume: 142 },
  { name: 'SKU-2241', volume: 128 },
]

const metrics = [
  { title: 'Turnover Rate', value: '2.8x', delta: '+0.4' },
  { title: 'Stockout Events', value: '12', delta: '-3' },
  { title: 'Avg Days to Reorder', value: '4.1', delta: '-0.8' },
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
          {item.dataKey}: {item.value}
        </p>
      ))}
    </div>
  )
}

export default function Analytics() {
  const [activeRange, setActiveRange] = React.useState('30D')

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="section-title text-4xl">Analytics</h1>
          <p className="section-subtitle">Inventory movement and category value trends.</p>
        </div>

        <div className="flex gap-2 rounded-lg border border-[var(--border-dim)] bg-[var(--bg-surface)] p-1">
          {ranges.map((range) => (
            <button
              key={range}
              className={`rounded px-3 py-1.5 text-xs font-medium transition ${
                activeRange === range
                  ? 'bg-[var(--accent)] text-[#08080a]'
                  : 'text-[var(--text-secondary)] hover:text-[var(--text-primary)]'
              }`}
              onClick={() => setActiveRange(range)}
            >
              {range}
            </button>
          ))}
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        {metrics.map((item) => (
          <article key={item.title} className="card p-5">
            <p className="text-sm text-[var(--text-secondary)]">{item.title}</p>
            <p className="display mt-2 text-4xl font-bold">{item.value}</p>
            <p className="mt-1 text-xs text-[var(--success)]">{item.delta} vs prev period</p>
          </article>
        ))}
      </div>

      <section className="card p-5">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="display text-2xl font-bold">Stock Movement</h2>
          <button className="btn-ghost text-xs">Export CSV</button>
        </div>
        <div className="h-[280px]">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={movement}>
              <CartesianGrid strokeDasharray="4 4" stroke="var(--border-dim)" opacity={0.5} />
              <XAxis dataKey="day" tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
              <YAxis tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
              <Tooltip content={<CustomTooltip />} />
              <Line dataKey="in" stroke="var(--success)" strokeWidth={2.4} dot={false} activeDot={{ r: 4 }} />
              <Line dataKey="out" stroke="var(--danger)" strokeWidth={2.4} dot={false} activeDot={{ r: 4 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="grid gap-4 lg:grid-cols-[1.2fr,0.8fr]">
        <article className="card p-5">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="display text-2xl font-bold">Inventory Value Over Time</h2>
            <button className="btn-ghost text-xs">Export CSV</button>
          </div>
          <div className="h-[280px]">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={movement}>
                <defs>
                  <linearGradient id="valueGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="var(--chart-1)" stopOpacity={0.2} />
                    <stop offset="95%" stopColor="var(--chart-1)" stopOpacity={0.02} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="4 4" stroke="var(--border-dim)" opacity={0.5} />
                <XAxis dataKey="day" tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
                <YAxis tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
                <Tooltip content={<CustomTooltip />} />
                <Area type="monotone" dataKey="value" stroke="var(--chart-1)" fill="url(#valueGradient)" strokeWidth={2.4} />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </article>

        <article className="card p-5">
          <h2 className="display text-2xl font-bold">Category Distribution</h2>
          <div className="h-[280px]">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={categoryData} dataKey="value" nameKey="name" innerRadius={70} outerRadius={110}>
                  {categoryData.map((item) => (
                    <Cell key={item.name} fill={item.color} />
                  ))}
                </Pie>
                <Tooltip content={<CustomTooltip />} />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="grid grid-cols-1 gap-2 text-xs text-[var(--text-secondary)]">
            {categoryData.map((item) => (
              <p key={item.name} className="mono">
                <span className="mr-2 inline-block h-2 w-2 rounded-full" style={{ background: item.color }} />
                {item.name}: {item.value}%
              </p>
            ))}
          </div>
        </article>
      </section>

      <section className="card p-5">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="display text-2xl font-bold">Top 10 Moving Products</h2>
          <button className="btn-ghost text-xs">Export CSV</button>
        </div>
        <div className="h-[300px]">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={topProducts} layout="vertical" margin={{ left: 14 }}>
              <CartesianGrid strokeDasharray="4 4" stroke="var(--border-dim)" opacity={0.5} />
              <XAxis type="number" tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'IBM Plex Mono' }} />
              <YAxis dataKey="name" type="category" tick={{ fill: 'var(--text-secondary)', fontSize: 12, fontFamily: 'IBM Plex Mono' }} width={88} />
              <Tooltip content={<CustomTooltip />} />
              <Bar dataKey="volume" fill="var(--chart-1)" radius={[0, 4, 4, 0]} barSize={28} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>
    </div>
  )
}
