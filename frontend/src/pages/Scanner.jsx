import React, { useContext } from 'react'
import { ScanLine, Search } from 'lucide-react'
import { AuthContext } from '../context/AuthContext'
import { barcodeService, productService } from '../services/api'

export default function Scanner() {
  const { user } = useContext(AuthContext)
  const isAdminDemoUser = String(user?.username || '').toLowerCase() === 'admin'

  const [sku, setSku] = React.useState('')
  const [scanned, setScanned] = React.useState(null)
  const [flash, setFlash] = React.useState(false)
  const [loading, setLoading] = React.useState(false)
  const [error, setError] = React.useState('')

  const normalizeScanned = (product) => ({
    sku: product.sku,
    name: product.name,
    qty: Number(product.quantity || 0),
    inventory: 'Main inventory',
  })

  const lookupBarcode = async () => {
    if (!sku.trim()) {
      setError('Enter a barcode or SKU first.')
      return
    }

    setLoading(true)
    setError('')
    setFlash(true)

    try {
      const barcodeResponse = await barcodeService.scan(sku.trim())
      setScanned(normalizeScanned(barcodeResponse.data))
    } catch {
      try {
        const productSearch = await productService.search(sku.trim(), 0, 1)
        const fallbackProduct = productSearch.data?.content?.[0]

        if (fallbackProduct) {
          setScanned(normalizeScanned(fallbackProduct))
        } else if (isAdminDemoUser) {
          setScanned({
            sku: sku,
            name: 'Demo Product',
            qty: 42,
            inventory: 'Demo Warehouse',
          })
        } else {
          setScanned(null)
          setError('No product found for this barcode or SKU.')
        }
      } catch {
        setScanned(null)
        setError('Lookup failed. Please try again.')
      }
    } finally {
      window.setTimeout(() => setFlash(false), 400)
      setLoading(false)
    }
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="section-title text-4xl">Barcode Scanner</h1>
        <p className="section-subtitle">Scan a barcode or enter SKU manually for instant stock actions.</p>
      </div>

      <section className="card p-6">
        <div className="mx-auto max-w-[520px] space-y-5">
          <div
            className={`relative mx-auto h-[300px] w-[400px] max-w-full overflow-hidden rounded-lg border-2 ${
              flash ? 'border-[var(--success)]' : 'border-[var(--accent)]'
            } bg-[var(--bg-elevated)]`}
          >
            <span className="absolute left-2 top-2 h-5 w-5 border-l-2 border-t-2 border-[var(--accent)]" />
            <span className="absolute right-2 top-2 h-5 w-5 border-r-2 border-t-2 border-[var(--accent)]" />
            <span className="absolute bottom-2 left-2 h-5 w-5 border-b-2 border-l-2 border-[var(--accent)]" />
            <span className="absolute bottom-2 right-2 h-5 w-5 border-b-2 border-r-2 border-[var(--accent)]" />

            <span className="absolute left-0 right-0 top-0 h-[2px] bg-gradient-to-r from-transparent via-[var(--accent)] to-transparent animate-[scanner_2s_linear_infinite]" />

            <div className="grid h-full place-items-center text-[var(--text-muted)]">
              <ScanLine size={36} />
            </div>
          </div>

          <p className="text-center text-sm text-[var(--text-secondary)]">Align barcode in frame...</p>

          {scanned && (
            <article className="rounded-lg border border-[var(--border-dim)] bg-[var(--bg-elevated)] p-4">
              <p className="mono text-xs text-[var(--text-muted)]">{scanned.sku}</p>
              <h2 className="mt-1 text-lg font-semibold text-[var(--text-primary)]">{scanned.name}</h2>
              <p className="mt-1 text-sm text-[var(--text-secondary)]">Current Qty: {scanned.qty} | {scanned.inventory}</p>
              <div className="mt-3 flex flex-wrap gap-2">
                <button className="btn-primary">+ Add Stock</button>
                <button className="btn-secondary">- Remove Stock</button>
                <button className="btn-ghost">View Product</button>
              </div>
            </article>
          )}

          <div className="rounded-lg border border-[var(--border-dim)] bg-[var(--bg-surface)] p-4">
            <label className="mb-2 block text-xs font-medium uppercase tracking-[0.08em] text-[var(--text-secondary)]">
              Manual Entry
            </label>
            <div className="flex gap-2">
              <div className="relative flex-1">
                <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[var(--text-muted)]" />
                <input
                  value={sku}
                  onChange={(event) => setSku(event.target.value)}
                  className="input pl-9"
                  placeholder="Enter SKU or barcode..."
                />
              </div>
              <button className="btn-primary" onClick={lookupBarcode} disabled={loading}>
                {loading ? 'Looking up...' : 'Lookup'}
              </button>
            </div>
            {error && <p className="mt-2 text-xs text-[var(--danger)]">{error}</p>}
          </div>
        </div>
      </section>
    </div>
  )
}
