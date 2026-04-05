import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { AlertCircle, UserPlus } from 'lucide-react'
import { authService } from '../services/api'

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onSubmit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')

    try {
      await authService.register(form)
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to create account right now.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="grid min-h-screen place-items-center bg-[var(--bg-base)] px-4 dot-grid page-transition">
      <motion.div
        initial={{ opacity: 0, y: 12 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="w-full max-w-md"
      >
        <div className="glass-card rounded-2xl border border-[var(--border-dim)] p-6">
          <p className="brand-wordmark text-lg text-[var(--text-primary)]">
            stock<span className="text-[var(--accent)]">.</span>os
          </p>
          <h1 className="display mt-3 text-3xl font-bold text-[var(--text-primary)]">Create your workspace</h1>
          <p className="mt-1 text-sm text-[var(--text-secondary)]">Start managing inventory in minutes.</p>

          {error && (
            <div className="form-error mt-4">
              <div className="flex items-start gap-2">
                <AlertCircle size={16} className="mt-0.5" />
                <span>{error}</span>
              </div>
            </div>
          )}

          <form onSubmit={onSubmit} className="mt-5 space-y-3">
            <input
              className="input"
              placeholder="Username"
              value={form.username}
              onChange={(event) => setForm((prev) => ({ ...prev, username: event.target.value }))}
              required
            />
            <input
              className="input"
              type="email"
              placeholder="Work email"
              value={form.email}
              onChange={(event) => setForm((prev) => ({ ...prev, email: event.target.value }))}
              required
            />
            <input
              className="input"
              type="password"
              placeholder="Password"
              minLength={8}
              value={form.password}
              onChange={(event) => setForm((prev) => ({ ...prev, password: event.target.value }))}
              required
            />

            <button className="btn-primary w-full" disabled={loading} data-magnetic="true" type="submit">
              <UserPlus size={16} />
              {loading ? 'Creating account...' : 'Create Account'}
            </button>
          </form>

          <p className="mt-5 text-center text-sm text-[var(--text-secondary)]">
            Already have an account?{' '}
            <Link className="font-semibold text-[var(--accent)]" to="/login">
              Sign in
            </Link>
          </p>
        </div>
      </motion.div>
    </div>
  )
}
