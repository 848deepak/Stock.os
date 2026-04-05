import React, { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { ArrowDownToLine, ArrowUpFromLine, RefreshCw } from 'lucide-react'
import { inventoryService } from '../services/api'

export default function Inventory() {
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)

  const stockInCount = history
    .filter((item) => String(item.type || '').toUpperCase() === 'STOCK_IN')
    .reduce((sum, item) => sum + Number(item.quantity || 0), 0)

  const stockOutCount = history
    .filter((item) => String(item.type || '').toUpperCase() === 'STOCK_OUT')
    .reduce((sum, item) => sum + Number(item.quantity || 0), 0)

  const adjustmentCount = history
    .filter((item) => String(item.type || '').toUpperCase() === 'ADJUSTMENT')
    .reduce((sum, item) => sum + Number(item.quantity || 0), 0)

  useEffect(() => {
    const load = async () => {
      try {
        const res = await inventoryService.getHistory(0, 12)
        setHistory(res.data?.content || [])
      } catch (err) {
        console.error('Inventory history failed:', err)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  return (
    <div className="space-y-5">
      <div>
        <h2 className="section-title text-4xl">Inventory Transactions</h2>
        <p className="section-subtitle">Track stock movement and adjustment events.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <div className="metric-card">
          <p className="text-xs text-[var(--text-muted)]">Stock In</p>
          <p className="mt-2 flex items-center gap-2 text-2xl font-bold text-[var(--success)]"><ArrowDownToLine size={20} /> +{stockInCount}</p>
        </div>
        <div className="metric-card">
          <p className="text-xs text-[var(--text-muted)]">Stock Out</p>
          <p className="mt-2 flex items-center gap-2 text-2xl font-bold text-[var(--danger)]"><ArrowUpFromLine size={20} /> -{stockOutCount}</p>
        </div>
        <div className="metric-card">
          <p className="text-xs text-[var(--text-muted)]">Adjustments</p>
          <p className="mt-2 flex items-center gap-2 text-2xl font-bold text-[var(--info)]"><RefreshCw size={20} /> {adjustmentCount}</p>
        </div>
      </div>

      <div className="table-shell">
        <table className="table">
          <thead>
            <tr>
              <th>Type</th>
              <th>Product</th>
              <th>Quantity</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="px-5 py-8 text-center text-[var(--text-muted)]">Loading transactions...</td>
              </tr>
            ) : history.length === 0 ? (
              <tr>
                <td colSpan={4} className="px-5 py-8 text-center text-[var(--text-muted)]">No transactions yet.</td>
              </tr>
            ) : (
              history.map((row, idx) => (
                <motion.tr key={row.id || idx} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: idx * 0.03 }}>
                  <td>{row.type || 'N/A'}</td>
                  <td>{row.productName || row.productId || 'Unknown'}</td>
                  <td>{row.quantity}</td>
                  <td>{row.createdAt ? new Date(row.createdAt).toLocaleString() : '-'}</td>
                </motion.tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
