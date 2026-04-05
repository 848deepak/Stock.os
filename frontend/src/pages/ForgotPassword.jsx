import React, { useContext, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { AlertCircle, Mail, Send } from 'lucide-react'
import { AuthContext } from '../context/AuthContext'

export default function ForgotPassword() {
  const { forgotPassword } = useContext(AuthContext)
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const onSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)

    try {
      const response = await forgotPassword(email)
      const message = response?.data?.message || 'If this email exists, a reset link will be sent shortly.'
      setSuccess(message)
    } catch (err) {
      setError(err?.message || 'Unable to process request right now. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="grid min-h-screen place-items-center bg-[var(--bg-base)] px-4 dot-grid page-transition">
      <motion.div
        initial={{ opacity: 0, y: 14 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="w-full max-w-md"
      >
        <div className="glass-card overflow-hidden rounded-2xl border border-[var(--border-dim)]">
          <div className="border-b border-[var(--border-dim)] bg-[rgba(255,255,255,0.02)] px-7 py-6">
            <p className="brand-wordmark text-lg text-[var(--text-primary)]">
              stock<span className="text-[var(--accent)]">.</span>os
            </p>
            <h1 className="display mt-3 text-3xl font-bold text-[var(--text-primary)]">Reset password</h1>
            <p className="mt-2 text-sm text-[var(--text-secondary)]">
              Enter your work email and we will send a password reset link.
            </p>
          </div>

          <div className="px-7 py-7">
            {error && (
              <div className="form-error mb-4 flex items-start gap-2">
                <AlertCircle className="mt-0.5 h-4 w-4" />
                <span>{error}</span>
              </div>
            )}

            {success && (
              <div className="mb-4 rounded-md border border-[rgba(0,208,132,0.25)] bg-[var(--success-dim)] px-3 py-2 text-sm text-[var(--success)]">
                {success}
              </div>
            )}

            <form onSubmit={onSubmit} className="space-y-4">
              <div>
                <label className="field-label">Work Email</label>
                <div className="relative">
                  <Mail className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[var(--text-muted)]" />
                  <input
                    type="email"
                    className="input pl-9"
                    placeholder="you@company.com"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    required
                    disabled={loading}
                  />
                </div>
              </div>

              <button className="btn-primary w-full" type="submit" disabled={loading} data-magnetic="true">
                <Send size={15} />
                {loading ? 'Sending link...' : 'Send reset link'}
              </button>
            </form>

            <p className="mt-5 text-center text-sm text-[var(--text-secondary)]">
              Remember your password?{' '}
              <Link to="/login" className="font-semibold text-[var(--accent)]">
                Back to sign in
              </Link>
            </p>
          </div>
        </div>
      </motion.div>
    </div>
  )
}
