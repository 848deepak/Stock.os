import React, { useContext } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthContext } from './context/AuthContext'
import AppShell from './components/app/AppShell'
import ErrorBoundary from './components/system/ErrorBoundary'
import GlobalEffects from './components/system/GlobalEffects'
import Landing from './pages/Landing'
import Login from './pages/Login'
import Register from './pages/Register'
import ForgotPassword from './pages/ForgotPassword'
import Dashboard from './pages/Dashboard'
import Products from './pages/Products'
import Inventory from './pages/Inventory'
import Warehouses from './pages/Warehouses'
import Analytics from './pages/Analytics'
import Scanner from './pages/Scanner'
import PdfUpload from './pages/PdfUpload'

function FullPageLoader() {
  return (
    <div className="grid min-h-screen place-items-center bg-[var(--bg-base)]">
      <div className="h-10 w-10 animate-spin rounded-full border-2 border-[var(--border-base)] border-t-[var(--accent)]" />
    </div>
  )
}

function PrivateRoute({ children }) {
  const { isAuthenticated } = useContext(AuthContext)
  return isAuthenticated ? children : <Navigate to="/login" replace />
}

function PublicOnlyRoute({ children }) {
  const { isAuthenticated } = useContext(AuthContext)
  return isAuthenticated ? <Navigate to="/app" replace /> : children
}

function App() {
  const { isAuthenticated, loading } = useContext(AuthContext)

  if (loading) {
    return <FullPageLoader />
  }

  return (
    <ErrorBoundary>
      <Router>
        <GlobalEffects />
        <Routes>
          <Route path="/" element={<Landing />} />
          <Route
            path="/login"
            element={
              <PublicOnlyRoute>
                <Login />
              </PublicOnlyRoute>
            }
          />
          <Route
            path="/register"
            element={
              <PublicOnlyRoute>
                <Register />
              </PublicOnlyRoute>
            }
          />
          <Route
            path="/forgot-password"
            element={
              <PublicOnlyRoute>
                <ForgotPassword />
              </PublicOnlyRoute>
            }
          />

          <Route
            path="/app"
            element={
              <PrivateRoute>
                <AppShell />
              </PrivateRoute>
            }
          >
            <Route index element={<Dashboard />} />
            <Route path="products" element={<Products />} />
            <Route path="inventory" element={<Inventory />} />
            <Route path="warehouses" element={<Warehouses />} />
            <Route path="analytics" element={<Analytics />} />
            <Route path="scanner" element={<Scanner />} />
            <Route path="pdf-upload" element={<PdfUpload />} />
          </Route>

          <Route path="*" element={<Navigate to={isAuthenticated ? '/app' : '/'} replace />} />
        </Routes>
      </Router>
    </ErrorBoundary>
  )
}

export default App
