import React, { useState, useContext } from 'react'
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Menu, X, LogOut, Package, BarChart3 } from 'lucide-react'
import { AuthContext } from '../context/AuthContext'

export default function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const location = useLocation()
  const navigate = useNavigate()
  const { user, logout } = useContext(AuthContext)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const navItems = [
    { path: '/', label: 'Dashboard', icon: BarChart3 },
    { path: '/products', label: 'Products', icon: Package },
  ]

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <motion.div
        initial={{ x: -300 }}
        animate={{ x: 0 }}
        transition={{ duration: 0.3 }}
        className={`${
          sidebarOpen ? 'w-64' : 'w-20'
        } bg-gray-900 text-white transition-all duration-300 shadow-lg flex flex-col`}
      >
        <div className="p-6 flex items-center justify-between">
          {sidebarOpen && <h1 className="text-xl font-bold">Inventory</h1>}
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="p-2 hover:bg-gray-800 rounded-lg transition-colors"
          >
            {sidebarOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>

        <nav className="flex-1 px-3 py-8 space-y-3">
          {navItems.map((item) => {
            const Icon = item.icon
            const isActive = location.pathname === item.path
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                  isActive
                    ? 'bg-blue-600 text-white'
                    : 'text-gray-300 hover:bg-gray-800'
                }`}
              >
                <Icon size={20} />
                {sidebarOpen && <span>{item.label}</span>}
              </Link>
            )
          })}
        </nav>

        <div className="p-6 border-t border-gray-800">
          <div className={`${sidebarOpen ? 'mb-4' : 'mb-2'} text-sm`}>
            <p className="text-gray-400">Logged in as</p>
            {sidebarOpen && <p className="font-semibold truncate">{user?.username}</p>}
            {sidebarOpen && <p className="text-gray-400 text-xs">{user?.role}</p>}
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 px-4 py-2 bg-red-600 hover:bg-red-700 rounded-lg transition-colors"
          >
            <LogOut size={18} />
            {sidebarOpen && <span>Logout</span>}
          </button>
        </div>
      </motion.div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Bar */}
        <div className="bg-white shadow-soft px-8 py-4 flex items-center justify-between">
          <h2 className="text-2xl font-bold text-gray-900">Inventory Management System</h2>
          <div className="flex items-center gap-4">
            <div className="w-10 h-10 rounded-full bg-blue-600 flex items-center justify-center text-white font-semibold">
              {user?.username?.charAt(0).toUpperCase()}
            </div>
          </div>
        </div>

        {/* Content */}
        <main className="flex-1 overflow-auto">
          <div className="container py-8">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}
