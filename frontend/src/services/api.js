import axios from 'axios'

const configuredBaseUrl = (import.meta.env.VITE_API_URL || '').trim()
const hostname = typeof window !== 'undefined' ? window.location.hostname : 'localhost'
const isLocalHost = hostname === 'localhost' || hostname === '127.0.0.1'

const API_BASE_URL = configuredBaseUrl
  ? configuredBaseUrl.replace(/\/+$/, '')
  : isLocalHost
    ? 'http://localhost:8080/api'
    : '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
})

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    const normalized = {
      ...error,
      message: error.response?.data?.message || error.message || 'Request failed',
    }
    return Promise.reject(normalized)
  }
)

export const authService = {
  login: (username, password) =>
    api.post('/auth/login', { username, password }),
  register: (payload) => api.post('/auth/register', payload),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  getHealth: () => api.get('/auth/health'),
}

export const productService = {
  getAll: (page = 0, size = 10, sortBy = 'id', direction = 'DESC') =>
    api.get('/products', { params: { page, size, sortBy, direction } }),
  getById: (id) => api.get(`/products/${id}`),
  create: (product) => api.post('/products', product),
  update: (id, product) => api.put(`/products/${id}`, product),
  delete: (id) => api.delete(`/products/${id}`),
  search: (keyword, page = 0, size = 10) =>
    api.get('/products/search', { params: { keyword, page, size } }),
  getByCategory: (category, page = 0, size = 10) =>
    api.get(`/products/category/${category}`, { params: { page, size } }),
  getLowStock: () => api.get('/products/low-stock'),
}

export const inventoryService = {
  stockIn: (transaction) => api.post('/transactions/stock-in', transaction),
  stockOut: (transaction) => api.post('/transactions/stock-out', transaction),
  adjustment: (transaction) => api.post('/transactions/adjustment', transaction),
  getHistory: (page = 0, size = 10) =>
    api.get('/transactions/history', { params: { page, size } }),
  getProductTransactions: (productId, page = 0, size = 10) =>
    api.get(`/transactions/product/${productId}`, { params: { page, size } }),
}

export const dashboardService = {
  getStats: () => api.get('/dashboard/stats'),
}

export const warehouseService = {
  getAll: () => api.get('/warehouses'),
}

export const barcodeService = {
  scan: (barcode) => api.get(`/barcodes/scan/${encodeURIComponent(barcode)}`),
  generate: (productId) => api.post(`/barcodes/generate/${productId}`),
  image: (productId) => api.get(`/barcodes/image/${productId}`),
}

export default api
