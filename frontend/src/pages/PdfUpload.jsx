import React from 'react'
import { FileUp, UploadCloud } from 'lucide-react'

const seedRows = [
  { sku: 'SKU-7723', name: 'Packaging Film', qty: 120, price: 44.5 },
  { sku: 'SKU-1193', name: 'Motor Shaft', qty: 18, price: 328.0 },
]

export default function PdfUpload() {
  const [dragging, setDragging] = React.useState(false)
  const [progress, setProgress] = React.useState(0)
  const [rows, setRows] = React.useState([])

  const onUpload = () => {
    let value = 0
    const timer = window.setInterval(() => {
      value += 20
      setProgress(value)
      if (value >= 100) {
        window.clearInterval(timer)
        setRows(seedRows)
      }
    }, 200)
  }

  const updateCell = (index, key, value) => {
    setRows((prev) => prev.map((row, rowIndex) => (rowIndex === index ? { ...row, [key]: value } : row)))
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="section-title text-4xl">PDF Upload</h1>
        <p className="section-subtitle">Drop supplier PDFs and map extracted line items to inventory.</p>
      </div>

      <section
        className={`card p-6 transition ${
          dragging ? 'scale-[1.01] border-[var(--accent)] bg-[var(--accent-dim)]' : ''
        }`}
        onDragOver={(event) => {
          event.preventDefault()
          setDragging(true)
        }}
        onDragLeave={() => setDragging(false)}
        onDrop={(event) => {
          event.preventDefault()
          setDragging(false)
          onUpload()
        }}
      >
        <div className="grid h-[200px] place-items-center rounded-lg border-2 border-dashed border-[var(--border-bright)]">
          <div className="text-center">
            <UploadCloud size={48} className="mx-auto text-[var(--text-muted)]" />
            <p className="mt-3 text-lg text-[var(--text-secondary)]">Drop PDFs here</p>
            <button className="mt-2 text-sm text-[var(--accent)]" onClick={onUpload}>or browse</button>
          </div>
        </div>

        {progress > 0 && progress < 100 && (
          <div className="mt-4">
            <div className="mb-1 flex items-center justify-between text-xs text-[var(--text-secondary)]">
              <span>supplier-invoice.pdf</span>
              <span>{progress}%</span>
            </div>
            <div className="h-2 rounded-full bg-[var(--bg-elevated)]">
              <div className="h-2 rounded-full bg-[var(--accent)]" style={{ width: `${progress}%` }} />
            </div>
          </div>
        )}
      </section>

      {rows.length > 0 && (
        <section className="grid gap-4 lg:grid-cols-2">
          <article className="card p-5">
            <h2 className="display text-2xl font-bold">PDF Preview</h2>
            <div className="mt-3 grid h-[320px] place-items-center rounded-lg border border-[var(--border-dim)] bg-[var(--bg-elevated)] text-[var(--text-muted)]">
              <div className="text-center">
                <FileUp size={40} className="mx-auto" />
                <p className="mt-2 text-sm">Supplier Invoice Preview</p>
              </div>
            </div>
          </article>

          <article className="card p-5">
            <h2 className="display text-2xl font-bold">Extracted Table</h2>
            <div className="mt-3 overflow-hidden rounded-lg border border-[var(--border-dim)]">
              <table className="table">
                <thead>
                  <tr>
                    <th>SKU</th>
                    <th>Name</th>
                    <th>Qty</th>
                    <th>Price</th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((row, idx) => (
                    <tr key={row.sku}>
                      <td><input className="input mono" value={row.sku} onChange={(event) => updateCell(idx, 'sku', event.target.value)} /></td>
                      <td><input className="input" value={row.name} onChange={(event) => updateCell(idx, 'name', event.target.value)} /></td>
                      <td><input className="input" value={row.qty} onChange={(event) => updateCell(idx, 'qty', event.target.value)} /></td>
                      <td><input className="input" value={row.price} onChange={(event) => updateCell(idx, 'price', event.target.value)} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="mt-4 flex justify-end gap-2">
              <button className="btn-ghost">Discard</button>
              <button className="btn-primary">Save to Inventory</button>
            </div>
          </article>
        </section>
      )}
    </div>
  )
}
