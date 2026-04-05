import { useEffect, useRef } from 'react'
import { useLocation } from 'react-router-dom'

export default function GlobalEffects() {
  const location = useLocation()
  const cursorRef = useRef(null)
  const targetRef = useRef({ x: window.innerWidth / 2, y: window.innerHeight / 2 })
  const rafRef = useRef(null)

  useEffect(() => {
    const updateProgress = () => {
      const scrollTop = window.scrollY
      const scrollHeight = document.documentElement.scrollHeight - window.innerHeight
      const value = scrollHeight > 0 ? `${(scrollTop / scrollHeight) * 100}%` : '0%'
      document.documentElement.style.setProperty('--scroll-progress', value)
    }

    updateProgress()
    window.addEventListener('scroll', updateProgress, { passive: true })
    window.addEventListener('resize', updateProgress)

    return () => {
      window.removeEventListener('scroll', updateProgress)
      window.removeEventListener('resize', updateProgress)
    }
  }, [location.pathname])

  useEffect(() => {
    const node = cursorRef.current
    if (!node || window.matchMedia('(max-width: 768px)').matches) {
      return undefined
    }

    let x = targetRef.current.x
    let y = targetRef.current.y

    const onMove = (event) => {
      targetRef.current.x = event.clientX
      targetRef.current.y = event.clientY
      document.documentElement.style.setProperty('--cursor-x', `${event.clientX}px`)
      document.documentElement.style.setProperty('--cursor-y', `${event.clientY}px`)
    }

    const onHoverState = (event) => {
      const interactiveEl = event.target.closest(
        'a, button, input, select, textarea, [role="button"], [data-interactive="true"]'
      )

      if (interactiveEl) {
        node.classList.add('cursor-active')
      } else {
        node.classList.remove('cursor-active')
      }
    }

    const animate = () => {
      x += (targetRef.current.x - x) * 0.22
      y += (targetRef.current.y - y) * 0.22
      node.style.left = `${x}px`
      node.style.top = `${y}px`
      rafRef.current = window.requestAnimationFrame(animate)
    }

    window.addEventListener('mousemove', onMove)
    window.addEventListener('mouseover', onHoverState)
    rafRef.current = window.requestAnimationFrame(animate)

    return () => {
      window.removeEventListener('mousemove', onMove)
      window.removeEventListener('mouseover', onHoverState)
      if (rafRef.current) {
        window.cancelAnimationFrame(rafRef.current)
      }
    }
  }, [location.pathname])

  useEffect(() => {
    if (window.matchMedia('(max-width: 768px)').matches) {
      return undefined
    }

    const magneticNodes = [...document.querySelectorAll('[data-magnetic="true"]')]
    if (!magneticNodes.length) {
      return undefined
    }

    const cleanups = magneticNodes.map((node) => {
      const onMove = (event) => {
        const rect = node.getBoundingClientRect()
        const x = event.clientX - rect.left - rect.width / 2
        const y = event.clientY - rect.top - rect.height / 2
        const xOffset = Math.max(-8, Math.min(8, x * 0.12))
        const yOffset = Math.max(-8, Math.min(8, y * 0.12))
        node.style.transform = `translate(${xOffset}px, ${yOffset}px)`
      }

      const onLeave = () => {
        node.style.transform = 'translate(0, 0)'
      }

      node.addEventListener('mousemove', onMove)
      node.addEventListener('mouseleave', onLeave)

      return () => {
        node.removeEventListener('mousemove', onMove)
        node.removeEventListener('mouseleave', onLeave)
        node.style.transform = 'translate(0, 0)'
      }
    })

    return () => {
      cleanups.forEach((cleanup) => cleanup())
    }
  }, [location.pathname])

  useEffect(() => {
    const animatedNodes = [...document.querySelectorAll('.reveal-on-scroll')]
    if (!animatedNodes.length) {
      return undefined
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) {
            return
          }

          const element = entry.target
          element.classList.add('in-view')

          if (element.classList.contains('reveal-stagger')) {
            Array.from(element.children).forEach((child, index) => {
              child.style.transitionDelay = `${80 * index}ms`
              child.classList.add('reveal-on-scroll', 'in-view')
            })
          }

          observer.unobserve(element)
        })
      },
      {
        threshold: 0.15,
      }
    )

    animatedNodes.forEach((node) => observer.observe(node))

    return () => observer.disconnect()
  }, [location.pathname])

  useEffect(() => {
    const counters = [...document.querySelectorAll('[data-countup]')]
    if (!counters.length) {
      return undefined
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) {
            return
          }

          const node = entry.target
          const target = Number(node.getAttribute('data-countup') || 0)
          const duration = 1000
          const start = performance.now()

          const tick = (now) => {
            const progress = Math.min((now - start) / duration, 1)
            const value = Math.floor(progress * target)
            node.textContent = value.toLocaleString('en-IN')
            if (progress < 1) {
              window.requestAnimationFrame(tick)
            }
          }

          window.requestAnimationFrame(tick)
          observer.unobserve(node)
        })
      },
      { threshold: 0.2 }
    )

    counters.forEach((node) => observer.observe(node))

    return () => observer.disconnect()
  }, [location.pathname])

  return (
    <>
      <div aria-hidden className="scroll-progress" />
      <div aria-hidden className="cursor-dot" ref={cursorRef} />
    </>
  )
}
