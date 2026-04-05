import React, { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Building2, MapPin } from 'lucide-react'
import { warehouseService } from '../services/api'

export default function Warehouses() {
  const [warehouses, setWarehouses] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const loadWarehouses = async () => {
      try {
        const response = await warehouseService.getAll()
        setWarehouses(response.data || [])
      } catch (error) {
        console.error('Failed to fetch warehouses:', error)
      } finally {
        setLoading(false)
      }
    }

    loadWarehouses()
  }, [])

  return (
    <div className="space-y-5">
      <div>
        <h2 className="section-title text-4xl">Warehouses</h2>
        <p className="section-subtitle">Monitor multi-location capacity and stock readiness.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        {loading && [...Array(3)].map((_, idx) => (
          <article key={`warehouse-skeleton-${idx}`} className="card p-5">
            <div className="skeleton h-24 rounded" />
          </article>
        ))}

        {!loading && warehouses.map((w, idx) => (
          <motion.article
            key={w.id}
            className="card p-5"
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: idx * 0.08 }}
          >
            <div className="flex items-start justify-between">
              <div>
                <p className="font-semibold text-[var(--text-primary)]">{w.name}</p>
                <p className="mt-1 inline-flex items-center gap-1 text-sm text-[var(--text-secondary)]"><MapPin size={14} /> {w.location}</p>
              </div>
              <div className="rounded-xl bg-[var(--accent-dim)] p-2 text-[var(--accent)]">
                <Building2 size={16} />
              </div>
            </div>
            <p className="mt-4 text-xs text-[var(--text-muted)]">Storage Capacity</p>
            <div className="mt-2 h-2 rounded-full bg-[var(--bg-elevated)]">
              <div className="h-2 rounded-full bg-[var(--accent)]" style={{ width: `${Math.min(100, Math.round((Number(w.capacity || 0) / 50000) * 100))}%` }} />
            </div>
            <p className="mt-2 text-sm font-semibold text-[var(--text-primary)]">{Number(w.capacity || 0).toLocaleString('en-IN')} units</p>
          </motion.article>
        ))}

        {!loading && warehouses.length === 0 && (
          <article className="card p-5 text-sm text-[var(--text-muted)]">
            No warehouses found yet.
          </article>
        )}
      </div>
    </div>
  )
}
