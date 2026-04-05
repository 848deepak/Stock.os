import React from 'react'

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false }
  }

  static getDerivedStateFromError() {
    return { hasError: true }
  }

  componentDidCatch(error, info) {
    console.error('UI ErrorBoundary caught:', error, info)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen grid place-items-center bg-slate-100 px-4">
          <div className="card max-w-md p-6 text-center">
            <h1 className="text-xl font-bold text-slate-900">Something went wrong</h1>
            <p className="mt-2 text-sm text-slate-600">
              We hit an unexpected UI error. Please refresh the page.
            </p>
            <button className="btn-primary mt-5" onClick={() => window.location.reload()}>
              Reload App
            </button>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}
