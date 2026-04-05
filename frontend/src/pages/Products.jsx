import React, { useEffect, useMemo, useState } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import {
  AlertTriangle,
  Check,
  ChevronDown,
  Filter,
  MoreHorizontal,
  Plus,
  Search,
  Upload,
  X,
} from 'lucide-react'
import { productService } from '../services/api'

const categoryOptions = ['Electronics', 'Retail', 'Packing', 'Spare Parts', 'Consumables']

const initialForm = {
  name: '',
  sku: '',
  description: '',
  category: '',
  price: '',
  quantity: '',
  reorderLevel: '10',
  expiryDate: '',
}

function statusFor(product) {
  const quantity = Number(product.quantity || 0)
  const reorder = Number(product.reorderLevel || 0)

  if (quantity <= reorder) {
    return 'low'
  }
  if (quantity <= reorder * 1.5) {
    return 'warning'
  }
  return 'ok'
}

export default function Products() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [activeMenu, setActiveMenu] = useState(null)
  const [confirmDeleteId, setConfirmDeleteId] = useState(null)
  const [selectedIds, setSelectedIds] = useState([])
  const [filterOpen, setFilterOpen] = useState(false)
  const [filters, setFilters] = useState({
    categories: [],
    stock: 'all',
    minPrice: 0,
    maxPrice: 10000,
  })
  const [toast, setToast] = useState(null)
  const [formData, setFormData] = useState(initialForm)

  useEffect(() => {
    fetchProducts()
  }, [page, searchTerm])

  const fetchProducts = async () => {
    setLoading(true)
    try {
      const response = searchTerm
        ? await productService.search(searchTerm, page, 10)
        : await productService.getAll(page, 10)
      setProducts(response.data.content || [])
      setTotalPages(response.data.totalPages || 0)
      setError('')
    } catch {
      setError('Failed to fetch products')
    } finally {
      setLoading(false)
    }
  }

  const visibleProducts = useMemo(() => {
    return products.filter((product) => {
      const inCategory =
        filters.categories.length === 0 || filters.categories.includes(product.category)
      const inPrice =
        Number(product.price || 0) >= filters.minPrice &&
        Number(product.price || 0) <= filters.maxPrice

      const productStatus = statusFor(product)
      const inStockFilter =
        filters.stock === 'all' ||
        (filters.stock === 'low' && productStatus === 'low') ||
        (filters.stock === 'warning' && productStatus === 'warning') ||
        (filters.stock === 'ok' && productStatus === 'ok')

      return inCategory && inPrice && inStockFilter
    })
  }, [products, filters])

  const notify = (message, type = 'success') => {
    setToast({ id: Date.now(), message, type })
    window.setTimeout(() => setToast(null), 4000)
  }

  const openCreate = () => {
    setEditingId(null)
    setFormData(initialForm)
    setDrawerOpen(true)
  }

  const openEdit = (product) => {
    setEditingId(product.id)
    setFormData({
      name: product.name,
      sku: product.sku,
      description: product.description || '',
      category: product.category,
      price: product.price,
      quantity: product.quantity,
      reorderLevel: product.reorderLevel || '10',
      expiryDate: product.expiryDate || '',
    })
    setDrawerOpen(true)
  }

  const onSubmit = async (event) => {
    event.preventDefault()
    try {
      if (editingId) {
        await productService.update(editingId, formData)
        notify('Product updated', 'success')
      } else {
        await productService.create(formData)
        notify('Product created', 'success')
      }
      setDrawerOpen(false)
      fetchProducts()
    } catch (err) {
      notify(err.response?.data?.message || 'Save failed', 'error')
    }
  }

  const deleteProduct = async (id) => {
    try {
      await productService.delete(id)
      setConfirmDeleteId(null)
      notify('Product deleted', 'success')
      fetchProducts()
    } catch {
      notify('Delete failed', 'error')
    }
  }

  const toggleSelect = (id) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((value) => value !== id) : [...prev, id]
    )
  }

  const toggleAll = () => {
    const allVisible = visibleProducts.map((item) => item.id)
    const allSelected = allVisible.every((id) => selectedIds.includes(id))
    setSelectedIds(allSelected ? selectedIds.filter((id) => !allVisible.includes(id)) : [...new Set([...selectedIds, ...allVisible])])
  }

  return (
    <div className="space-y-4 pb-20 md:pb-0">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="section-title text-4xl">{(products?.length || 0).toLocaleString('en-IN')} Products</h1>
          <div className="mt-1 flex items-center gap-2 text-sm text-[var(--text-secondary)]">
            <span>in Warehouse A</span>
            <span className="rounded-full border border-[var(--border-base)] px-2 py-0.5 text-xs">Primary</span>
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <div className="relative min-w-[220px]">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[var(--text-muted)]" />
            <input
              value={searchTerm}
              onChange={(event) => {
                setSearchTerm(event.target.value)
                setPage(0)
              }}
              className="input pl-9"
              placeholder="Search products or SKUs"
            />
          </div>

          <button className="btn-secondary" onClick={() => setFilterOpen((prev) => !prev)}>
            <Filter size={14} />
            Filter
          </button>
          <button className="btn-ghost">
            <Upload size={14} />
            Import CSV
          </button>
          <button className="btn-primary" onClick={openCreate}>
            <Plus size={14} />
            Add Product
          </button>
        </div>
      </div>

      <AnimatePresence>
        {filterOpen && (
          <motion.section
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            className="card p-4"
          >
            <div className="grid gap-4 md:grid-cols-4">
              <div>
                <p className="mb-2 text-xs font-semibold uppercase tracking-[0.08em] text-[var(--text-secondary)]">Category</p>
                <div className="space-y-2">
                  {categoryOptions.map((category) => (
                    <label key={category} className="flex items-center gap-2 text-sm text-[var(--text-secondary)]">
                      <input
                        type="checkbox"
                        checked={filters.categories.includes(category)}
                        onChange={() =>
                          setFilters((prev) => ({
                            ...prev,
                            categories: prev.categories.includes(category)
                              ? prev.categories.filter((item) => item !== category)
                              : [...prev.categories, category],
                          }))
                        }
                      />
                      {category}
                    </label>
                  ))}
                </div>
              </div>

              <div>
                <p className="mb-2 text-xs font-semibold uppercase tracking-[0.08em] text-[var(--text-secondary)]">Stock Status</p>
                <div className="space-y-2 text-sm text-[var(--text-secondary)]">
                  {['all', 'ok', 'warning', 'low'].map((value) => (
                    <label key={value} className="flex items-center gap-2">
                      <input
                        type="radio"
                        name="stock"
                        checked={filters.stock === value}
                        onChange={() => setFilters((prev) => ({ ...prev, stock: value }))}
                      />
                      {value.toUpperCase()}
                    </label>
                  ))}
                </div>
              </div>

              <div>
                <p className="mb-2 text-xs font-semibold uppercase tracking-[0.08em] text-[var(--text-secondary)]">Min Price</p>
                <input
                  type="range"
                  min={0}
                  max={10000}
                  value={filters.minPrice}
                  onChange={(event) => setFilters((prev) => ({ ...prev, minPrice: Number(event.target.value) }))}
                  className="w-full"
                />
                <p className="mono text-xs text-[var(--text-muted)]">₹{filters.minPrice}</p>
              </div>

              <div>
                <p className="mb-2 text-xs font-semibold uppercase tracking-[0.08em] text-[var(--text-secondary)]">Max Price</p>
                <input
                  type="range"
                  min={0}
                  max={10000}
                  value={filters.maxPrice}
                  onChange={(event) => setFilters((prev) => ({ ...prev, maxPrice: Number(event.target.value) }))}
                  className="w-full"
                />
                <p className="mono text-xs text-[var(--text-muted)]">₹{filters.maxPrice}</p>
              </div>
            </div>

            <div className="mt-4 flex justify-end gap-2">
              <button className="btn-ghost" onClick={() => setFilters({ categories: [], stock: 'all', minPrice: 0, maxPrice: 10000 })}>
                Reset
              </button>
              <button className="btn-primary" onClick={() => setFilterOpen(false)}>Apply</button>
            </div>
          </motion.section>
        )}
      </AnimatePresence>

      <div className="table-shell overflow-hidden">
        <div className="overflow-x-auto">
          <table className="table min-w-[1100px]">
            <thead className="sticky top-0 z-10">
              <tr>
                <th>
                  <input
                    type="checkbox"
                    checked={visibleProducts.length > 0 && visibleProducts.every((item) => selectedIds.includes(item.id))}
                    onChange={toggleAll}
                  />
                </th>
                <th>Image</th>
                <th>SKU</th>
                <th>Name</th>
                <th>Category</th>
                <th>Quantity</th>
                <th>Reorder</th>
                <th>Unit Price</th>
                <th>Inventory</th>
                <th>
                  <button className="inline-flex items-center gap-1">
                    Actions
                    <ChevronDown size={12} />
                  </button>
                </th>
              </tr>
            </thead>
            <tbody>
              {loading &&
                [...Array(5)].map((_, idx) => (
                  <tr key={`skeleton-${idx}`}>
                    {[...Array(10)].map((__, col) => (
                      <td key={`${idx}-${col}`}>
                        <div className="skeleton h-8 rounded" />
                      </td>
                    ))}
                  </tr>
                ))}

              {!loading && error && (
                <tr>
                  <td colSpan={10} className="py-8">
                    <div className="mx-auto flex max-w-md items-center gap-2 rounded-md border border-[rgba(255,68,68,0.2)] bg-[var(--danger-dim)] px-3 py-2 text-sm text-[var(--danger)]">
                      <AlertTriangle size={15} />
                      {error}
                    </div>
                  </td>
                </tr>
              )}

              {!loading && !error && visibleProducts.length === 0 && (
                <tr>
                  <td colSpan={10} className="py-10 text-center text-sm text-[var(--text-muted)]">
                    No products match your current filters.
                  </td>
                </tr>
              )}

              {!loading &&
                !error &&
                visibleProducts.map((product) => {
                  const status = statusFor(product)
                  const selected = selectedIds.includes(product.id)
                  return (
                    <React.Fragment key={product.id}>
                      <tr className={selected ? 'bg-[var(--accent-dim)]' : ''}>
                        <td>
                          <input
                            type="checkbox"
                            checked={selected}
                            onChange={() => toggleSelect(product.id)}
                          />
                        </td>
                        <td>
                          <span className="grid h-8 w-8 place-items-center rounded-full border border-[var(--border-base)] bg-[var(--bg-elevated)] text-xs text-[var(--text-secondary)]">
                            {(product.name || 'P').slice(0, 1).toUpperCase()}
                          </span>
                        </td>
                        <td className="mono text-xs text-[var(--text-secondary)]">{product.sku}</td>
                        <td>
                          <p className="font-medium">{product.name}</p>
                          <p className="text-xs text-[var(--text-muted)]">{product.description || 'No description'}</p>
                        </td>
                        <td>
                          <span className="rounded-full border border-[var(--border-dim)] bg-[var(--bg-elevated)] px-2 py-0.5 text-xs text-[var(--text-secondary)]">
                            {product.category}
                          </span>
                        </td>
                        <td>
                          <div
                            className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs ${
                              status === 'low'
                                ? 'bg-[var(--danger-dim)] text-[var(--danger)]'
                                : status === 'warning'
                                  ? 'bg-[var(--warning-dim)] text-[var(--warning)]'
                                  : 'bg-[var(--success-dim)] text-[var(--success)]'
                            }`}
                          >
                            {status === 'low' && <span className="h-1.5 w-1.5 animate-pulse rounded-full bg-[var(--danger)]" />}
                            {product.quantity}
                          </div>
                        </td>
                        <td>{product.reorderLevel || 10}</td>
                        <td className="mono text-xs">₹{Number(product.price || 0).toFixed(2)}</td>
                        <td>Warehouse A</td>
                        <td className="relative">
                          <button
                            className="rounded p-2 text-[var(--text-secondary)] hover:bg-white/[0.04]"
                            onClick={() => setActiveMenu((prev) => (prev === product.id ? null : product.id))}
                          >
                            <MoreHorizontal size={14} />
                          </button>

                          {activeMenu === product.id && (
                            <div className="absolute right-4 z-20 w-36 rounded-md border border-[var(--border-base)] bg-[var(--bg-elevated)] p-1 text-sm shadow-[var(--shadow-md)]">
                              <button className="block w-full rounded px-2 py-1.5 text-left hover:bg-white/[0.04]" onClick={() => openEdit(product)}>Edit</button>
                              <button className="block w-full rounded px-2 py-1.5 text-left hover:bg-white/[0.04]">Move</button>
                              <button className="block w-full rounded px-2 py-1.5 text-left hover:bg-white/[0.04]">Duplicate</button>
                              <button
                                className="block w-full rounded px-2 py-1.5 text-left text-[var(--danger)] hover:bg-white/[0.04]"
                                onClick={() => {
                                  setConfirmDeleteId(product.id)
                                  setActiveMenu(null)
                                }}
                              >
                                Delete
                              </button>
                            </div>
                          )}
                        </td>
                      </tr>

                      <AnimatePresence>
                        {confirmDeleteId === product.id && (
                          <motion.tr
                            initial={{ opacity: 0, height: 0 }}
                            animate={{ opacity: 1, height: 'auto' }}
                            exit={{ opacity: 0, height: 0 }}
                          >
                            <td colSpan={10}>
                              <div className="mx-4 mb-3 mt-1 flex items-center justify-between rounded-md border border-[rgba(255,68,68,0.25)] bg-[var(--danger-dim)] p-3">
                                <p className="text-sm text-[var(--danger)]">Delete {product.name}? This cannot be undone.</p>
                                <div className="flex gap-2">
                                  <button className="btn-ghost" onClick={() => setConfirmDeleteId(null)}>Cancel</button>
                                  <button className="btn-secondary border-[rgba(255,68,68,0.35)] text-[var(--danger)]" onClick={() => deleteProduct(product.id)}>
                                    Confirm Delete
                                  </button>
                                </div>
                              </div>
                            </td>
                          </motion.tr>
                        )}
                      </AnimatePresence>
                    </React.Fragment>
                  )
                })}
            </tbody>
          </table>
        </div>
      </div>

      <div className="flex flex-wrap items-center justify-between gap-3">
        <p className="text-sm text-[var(--text-secondary)]">Page {page + 1} of {Math.max(totalPages, 1)}</p>
        <div className="flex items-center gap-2 text-sm text-[var(--text-secondary)]">
          <span>Page size</span>
          <select className="input w-20 py-1">
            <option>10</option>
            <option>25</option>
            <option>50</option>
          </select>
          <button className="btn-secondary" disabled={page === 0} onClick={() => setPage(Math.max(0, page - 1))}>Prev</button>
          <button className="btn-secondary" disabled={page >= totalPages - 1} onClick={() => setPage(Math.min(totalPages - 1, page + 1))}>Next</button>
        </div>
      </div>

      <AnimatePresence>
        {drawerOpen && (
          <>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 z-50 bg-black/50 backdrop-blur"
              onClick={() => setDrawerOpen(false)}
            />
            <motion.aside
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ duration: 0.3, ease: [0.16, 1, 0.3, 1] }}
              className="fixed right-0 top-0 z-[60] h-full w-full max-w-[480px] border-l border-[var(--border-dim)] bg-[var(--bg-surface)] shadow-[var(--shadow-lg)]"
            >
              <form className="flex h-full flex-col" onSubmit={onSubmit}>
                <header className="flex items-center justify-between border-b border-[var(--border-dim)] px-6 py-4">
                  <h2 className="display text-3xl font-bold">{editingId ? 'Edit Product' : 'Add Product'}</h2>
                  <button type="button" className="rounded p-2 text-[var(--text-secondary)] hover:bg-white/[0.04]" onClick={() => setDrawerOpen(false)}>
                    <X size={16} />
                  </button>
                </header>

                <div className="flex-1 space-y-4 overflow-y-auto px-6 py-5">
                  <div>
                    <label className="mb-1 block text-xs font-medium uppercase tracking-[0.06em] text-[var(--text-secondary)]">Product Name</label>
                    <input className="input" name="name" value={formData.name} required onChange={(event) => setFormData((prev) => ({ ...prev, name: event.target.value }))} />
                  </div>
                  <div>
                    <label className="mb-1 block text-xs font-medium uppercase tracking-[0.06em] text-[var(--text-secondary)]">SKU</label>
                    <input className="input mono" name="sku" value={formData.sku} required onChange={(event) => setFormData((prev) => ({ ...prev, sku: event.target.value }))} />
                  </div>
                  <div>
                    <label className="mb-1 block text-xs font-medium uppercase tracking-[0.06em] text-[var(--text-secondary)]">Description</label>
                    <textarea className="input min-h-[100px] resize-y py-2" name="description" value={formData.description} onChange={(event) => setFormData((prev) => ({ ...prev, description: event.target.value }))} />
                  </div>
                  <div>
                    <label className="mb-1 block text-xs font-medium uppercase tracking-[0.06em] text-[var(--text-secondary)]">Category</label>
                    <input className="input" name="category" value={formData.category} required onChange={(event) => setFormData((prev) => ({ ...prev, category: event.target.value }))} />
                  </div>
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="mb-1 block text-xs font-medium uppercase tracking-[0.06em] text-[var(--text-secondary)]">Price</label>
                      <input className="input" name="price" type="number" step="0.01" value={formData.price} required onChange={(event) => setFormData((prev) => ({ ...prev, price: event.target.value }))} />
                    </div>
                    <div>
                      <label className="mb-1 block text-xs font-medium uppercase tracking-[0.06em] text-[var(--text-secondary)]">Quantity</label>
                      <input className="input" name="quantity" type="number" value={formData.quantity} required onChange={(event) => setFormData((prev) => ({ ...prev, quantity: event.target.value }))} />
                    </div>
                  </div>
                  <div>
                    <label className="mb-1 block text-xs font-medium uppercase tracking-[0.06em] text-[var(--text-secondary)]">Reorder Level</label>
                    <input className="input" name="reorderLevel" type="number" value={formData.reorderLevel} onChange={(event) => setFormData((prev) => ({ ...prev, reorderLevel: event.target.value }))} />
                  </div>
                </div>

                <footer className="sticky bottom-0 flex gap-2 border-t border-[var(--border-dim)] bg-[var(--bg-surface)] px-6 py-4">
                  <button type="button" className="btn-ghost flex-1" onClick={() => setDrawerOpen(false)}>Cancel</button>
                  <button type="submit" className="btn-primary flex-1">{editingId ? 'Save Changes' : 'Save Product'}</button>
                </footer>
              </form>
            </motion.aside>
          </>
        )}
      </AnimatePresence>

      <AnimatePresence>
        {toast && (
          <motion.div
            key={toast.id}
            initial={{ opacity: 0, x: 40 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: 40 }}
            className="fixed bottom-4 right-4 z-[70] w-[340px] overflow-hidden rounded-md border border-[var(--border-base)] bg-[var(--bg-elevated)] shadow-[var(--shadow-md)]"
          >
            <div className={`h-full border-l-4 p-3 ${toast.type === 'error' ? 'border-[var(--danger)]' : 'border-[var(--success)]'}`}>
              <div className="flex items-start justify-between gap-3">
                <p className="text-sm text-[var(--text-primary)]">{toast.message}</p>
                <button className="text-[var(--text-muted)]" onClick={() => setToast(null)}>
                  <X size={14} />
                </button>
              </div>
            </div>
            <motion.div
              className={`h-0.5 ${toast.type === 'error' ? 'bg-[var(--danger)]' : 'bg-[var(--success)]'}`}
              initial={{ width: '100%' }}
              animate={{ width: '0%' }}
              transition={{ duration: 4, ease: 'linear' }}
            />
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
