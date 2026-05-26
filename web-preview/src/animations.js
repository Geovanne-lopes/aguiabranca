import { gsap } from 'gsap'

const prefersReducedMotion = () =>
  window.matchMedia?.('(prefers-reduced-motion: reduce)').matches

function scope(root, selector) {
  return Array.from(root?.querySelectorAll?.(selector) || [])
}

export function animateScreenIn(root) {
  if (!root || prefersReducedMotion()) return
  gsap.fromTo(
    root,
    { autoAlpha: 0, y: 46, scale: 0.94, rotate: -0.8 },
    { autoAlpha: 1, y: 0, scale: 1, rotate: 0, duration: 0.72, ease: 'back.out(1.55)' }
  )
}

export function animateLogin(root) {
  if (!root || prefersReducedMotion()) return
  const tl = gsap.timeline({ defaults: { ease: 'back.out(1.7)' } })
  tl.fromTo('.login-header', { y: -44, autoAlpha: 0, rotate: -1.5 }, { y: 0, autoAlpha: 1, rotate: 0, duration: 0.72 })
    .fromTo('.login-logo', { scale: 0.15, rotate: -32 }, { scale: 1, rotate: 0, duration: 0.72 }, '-=0.35')
    .fromTo('.login-header h1, .login-header p', { y: 24, autoAlpha: 0, skewY: 3 }, { y: 0, autoAlpha: 1, skewY: 0, stagger: 0.08, duration: 0.5 }, '-=0.35')
    .fromTo('.login-card', { y: 70, autoAlpha: 0, scale: 0.88, rotate: 1.2 }, { y: 0, autoAlpha: 1, scale: 1, rotate: 0, duration: 0.82 }, '-=0.22')
    .fromTo('.demo-chip, .field, .login-forgot, #login-btn, .login-footer-text, .login-link', { y: 22, autoAlpha: 0, scale: 0.92 }, { y: 0, autoAlpha: 1, scale: 1, stagger: 0.055, duration: 0.38 }, '-=0.42')
}

export function animateContent(root) {
  if (!root || prefersReducedMotion()) return
  const items = scope(root, '.card, .quick-card, .idea-item, .project-card, .collab-card, .profile-header, .guideline-card, .empty-state-card')
  if (!items.length) return
  gsap.fromTo(
    items.slice(0, 12),
    { y: 34, autoAlpha: 0, scale: 0.88, rotate: -1.2 },
    { y: 0, autoAlpha: 1, scale: 1, rotate: 0, stagger: 0.055, duration: 0.56, ease: 'back.out(1.8)' }
  )
}

export function animateOverlay(root) {
  if (!root || prefersReducedMotion()) return
  const overlay = root.querySelector('.overlay-screen, .chat-screen')
  if (!overlay) return
  gsap.fromTo(
    overlay,
    { x: 80, autoAlpha: 0, scale: 0.9, rotate: 1.5 },
    { x: 0, autoAlpha: 1, scale: 1, rotate: 0, duration: 0.58, ease: 'back.out(1.55)' }
  )
  animateContent(overlay)
}

export function animateDrawer(root) {
  if (!root || prefersReducedMotion()) return
  const panel = root.querySelector('.drawer-panel.open')
  const backdrop = root.querySelector('.drawer-backdrop.open')
  if (backdrop) gsap.fromTo(backdrop, { autoAlpha: 0 }, { autoAlpha: 1, duration: 0.28 })
  if (panel) {
    gsap.fromTo(panel, { x: 80, autoAlpha: 0, scale: 0.96 }, { x: 0, autoAlpha: 1, scale: 1, duration: 0.48, ease: 'back.out(1.6)' })
    gsap.fromTo(scope(panel, '.notif-card'), { x: 28, autoAlpha: 0, scale: 0.92 }, { x: 0, autoAlpha: 1, scale: 1, stagger: 0.055, duration: 0.36, delay: 0.08, ease: 'back.out(1.8)' })
  }
}

export function animateBars(root) {
  if (!root || prefersReducedMotion()) return
  scope(root, '.bar-chart .bar').forEach((bar, index) => {
    const height = bar.style.height
    gsap.fromTo(bar, { height: '4%', scaleY: 0.4 }, { height, scaleY: 1, duration: 0.8, delay: index * 0.07, ease: 'elastic.out(1, 0.55)' })
  })
}

export function animateButtonPress(element) {
  if (!element || prefersReducedMotion()) return
  gsap.fromTo(element, { scale: 0.88, rotate: -1.5 }, { scale: 1, rotate: 0, duration: 0.34, ease: 'elastic.out(1, 0.45)' })
}

export function animateChatUpdate(root) {
  if (!root || prefersReducedMotion()) return
  const bubbles = scope(root, '.chat-bubble')
  const last = bubbles[bubbles.length - 1]
  if (last) {
    gsap.fromTo(last, { y: 22, autoAlpha: 0, scale: 0.82, rotate: 1.5 }, { y: 0, autoAlpha: 1, scale: 1, rotate: 0, duration: 0.42, ease: 'back.out(2)' })
  }
}

export function runUiAnimations(root) {
  animateOverlay(root)
  animateDrawer(root)
  animateContent(root)
  animateBars(root)
}
