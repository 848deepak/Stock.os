import React, { createContext, useState, useEffect, useCallback } from 'react'
import { authService } from '../services/api'

export const AuthContext = createContext()

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [token, setToken] = useState(null)

  useEffect(() => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (savedToken && savedUser) {
      setToken(savedToken)
      try {
        setUser(JSON.parse(savedUser))
      } catch {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    }
    setLoading(false)
  }, [])

  const login = useCallback((userData) => {
    const nextUser = { ...userData }
    const nextToken = nextUser.token
    delete nextUser.token
    setUser(nextUser)
    setToken(nextToken)
    localStorage.setItem('token', nextToken)
    localStorage.setItem('user', JSON.stringify(nextUser))
  }, [])

  const register = useCallback(async (payload) => {
    await authService.register(payload)
  }, [])

  const forgotPassword = useCallback(async (email) => {
    return authService.forgotPassword(email)
  }, [])

  const hasRole = useCallback(
    (roleName) => {
      if (!user?.role) {
        return false
      }
      return String(user.role).toUpperCase() === String(roleName).toUpperCase()
    },
    [user]
  )

  const logout = useCallback(() => {
    setUser(null)
    setToken(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }, [])

  const isAuthenticated = !!token

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        token,
        login,
        logout,
        register,
        forgotPassword,
        hasRole,
        isAuthenticated,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
