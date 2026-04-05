import React, { useState, useContext } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { AlertCircle, LogIn, ShieldCheck } from 'lucide-react'
import { authService } from '../services/api'
import { AuthContext } from '../context/AuthContext'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useContext(AuthContext)
  const navigate = useNavigate()

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await authService.login(username, password)
      const { data } = response
      login({ ...data, token: data.token })
      navigate('/app')
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="grid min-h-screen place-items-center bg-[var(--bg-base)] px-4 dot-grid page-transition">
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="w-full max-w-md"
      >
        <div className="glass-card overflow-hidden rounded-2xl border border-[var(--border-dim)]">
          <div className="border-b border-[var(--border-dim)] bg-[rgba(255,255,255,0.02)] px-7 py-6">
            <p className="brand-wordmark text-lg text-[var(--text-primary)]">
              stock<span className="text-[var(--accent)]">.</span>os
            </p>
            <h1 className="display mt-3 text-3xl font-bold text-[var(--text-primary)]">Welcome back</h1>
            <p className="mt-2 text-sm text-[var(--text-secondary)]">Sign in to access your inventory workspace</p>
          </div>

          <div className="px-7 py-7">
            <div className="mb-6 rounded-md border border-[var(--border-base)] bg-[var(--bg-elevated)] px-3 py-2 text-xs text-[var(--text-muted)]">
              Demo credentials: admin / admin123
            </div>

            {error && (
              <motion.div
                initial={{ opacity: 0, y: -8 }}
                animate={{ opacity: 1, y: 0 }}
                className="form-error mb-5 flex items-start gap-2"
              >
                <AlertCircle className="mt-0.5 h-4 w-4" />
                <p>{error}</p>
              </motion.div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="field-label">Username</label>
                <input
                  type="text"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                  placeholder="admin"
                  className="input"
                  required
                  disabled={loading}
                />
              </div>

              <div>
                <div className="mb-1 flex items-center justify-between">
                  <label className="field-label !mb-0">Password</label>
                  <Link to="/forgot-password" className="text-xs font-medium text-[var(--accent)] hover:underline">
                    Forgot password?
                  </Link>
                </div>
                <input
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="........"
                  className="input"
                  required
                  disabled={loading}
                />
              </div>

              <button type="submit" disabled={loading} className="btn-primary w-full disabled:opacity-50" data-magnetic="true">
                <LogIn className="h-4 w-4" />
                {loading ? 'Signing in...' : 'Sign In'}
              </button>
            </form>

            <div className="mt-5 flex items-center gap-2 rounded-md border border-[rgba(200,255,0,0.2)] bg-[var(--accent-dim)] px-3 py-2 text-xs text-[var(--text-secondary)]">
              <ShieldCheck size={14} className="text-[var(--accent)]" />
              Session protected with token-based auth and auto-expiry handling.
            </div>

            <div className="mt-6 border-t border-[var(--border-dim)] pt-5 text-center text-sm text-[var(--text-secondary)]">
              No account yet?{' '}
              <Link to="/register" className="font-semibold text-[var(--accent)]">
                Create one
              </Link>
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  )
}
