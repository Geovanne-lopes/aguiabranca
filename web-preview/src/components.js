export function icon(name) {
  return `<span class="material-symbols-rounded">${name}</span>`
}

export function escapeHtml(text) {
  const d = document.createElement('div')
  d.textContent = text == null ? '' : String(text)
  return d.innerHTML
}

export function topBar(title, options = {}) {
  const { showBadge = false, onLogout = false } = options
  return `
    <header class="top-bar">
      <button type="button" class="icon-btn" aria-label="Menu">${icon('menu')}</button>
      <h2>${escapeHtml(title)}</h2>
      <div class="top-bar-actions">
        <button type="button" class="icon-btn theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">
          <span class="theme-icon">◐</span>
        </button>
        <button type="button" class="icon-btn ${showBadge ? 'badge-dot' : ''}" data-action="notifications" aria-label="Notificações">
          ${icon('notifications')}
        </button>
        ${
          onLogout
            ? `<button type="button" class="icon-btn" data-action="logout" aria-label="Sair">${icon('logout')}</button>`
            : ''
        }
      </div>
    </header>
  `
}

export function overlayTopBar(title, extraActions = '') {
  return `
    <header class="overlay-top">
      <button type="button" class="icon-btn" data-action="back" aria-label="Voltar">${icon('arrow_back')}</button>
      <h2>${escapeHtml(title)}</h2>
      <div class="top-bar-actions">${extraActions}</div>
    </header>
  `
}

export function bottomNav(tabs, activeId) {
  return `
    <nav class="bottom-nav">
      ${tabs
        .map(
          (t) => `
        <button type="button" class="nav-item ${t.id === activeId ? 'active' : ''}" data-tab="${t.id}">
          ${icon(t.icon)}
          <span>${escapeHtml(t.label)}</span>
        </button>
      `
        )
        .join('')}
    </nav>
  `
}

export function kpiCard(title, value, trend = '') {
  return `
    <div class="kpi-card">
      <div class="label">${escapeHtml(title)}</div>
      <div class="value">${escapeHtml(value)}</div>
      ${trend ? `<div class="trend">${escapeHtml(trend)}</div>` : ''}
    </div>
  `
}

export function barChart(values, labels = []) {
  if (!values || values.length === 0) {
    return '<p class="muted-text">Sem dados.</p>'
  }
  const max = Math.max(...values, 1)
  return `
    <div class="bar-chart">
      ${values
        .map((v, i) => {
          const pct = Math.max((v / max) * 100, 6)
          const label = labels[i] || ''
          return `
            <div class="bar-col">
              <div class="bar-stack">
                <div class="bar" style="height:${pct}%" data-value="${v}"></div>
              </div>
              ${label ? `<span class="bar-label" title="${escapeHtml(label)}">${escapeHtml(label)}</span>` : ''}
            </div>
          `
        })
        .join('')}
    </div>
  `
}

const ROLE_PILL_LABELS = {
  operator: 'Operador',
  manager: 'Gestor',
  leader: 'Líder',
}

export function rolePill(role) {
  return `<span class="role-tag role-${role}">${ROLE_PILL_LABELS[role] || role}</span>`
}

export function initialsAvatar(name, color, size = 44) {
  const letter = (name || '?').trim().charAt(0).toUpperCase()
  const bg = color || '#6C5CE7'
  return `<div class="collab-avatar" style="width:${size}px;height:${size}px;background:${bg};font-size:${Math.floor(size / 2.6)}px">${escapeHtml(letter)}</div>`
}

export function roleColor(role) {
  switch (role) {
    case 'manager':
      return '#EC4899'
    case 'leader':
      return '#7C3AED'
    default:
      return '#3B82F6'
  }
}

export function sectionHeader(title, actionLabel = '', actionData = '') {
  return `
    <div class="section-header">
      <span class="section-title">${escapeHtml(title)}</span>
      ${
        actionLabel
          ? `<button type="button" class="btn-ghost" data-action="${actionData}">${escapeHtml(actionLabel)} <span class="material-symbols-rounded" style="font-size:18px;vertical-align:-3px">arrow_forward</span></button>`
          : ''
      }
    </div>
  `
}

export function emptyState(text, iconName = 'inbox') {
  return `
    <div class="empty-state-card">
      ${icon(iconName)}
      <strong>Nada por aqui ainda</strong>
      <p style="margin:0;font-size:0.85rem">${escapeHtml(text)}</p>
    </div>
  `
}

export function relativeTime(timestamp) {
  if (!timestamp) return ''
  const diff = Date.now() - timestamp
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  if (diff < minute) return 'agora'
  if (diff < hour) return `há ${Math.floor(diff / minute)} min`
  if (diff < day) return `há ${Math.floor(diff / hour)} h`
  return `há ${Math.floor(diff / day)} dia(s)`
}

export function bindTabs(root, onSelect) {
  root.querySelectorAll('[data-tab]').forEach((btn) => {
    btn.addEventListener('click', () => onSelect(btn.dataset.tab))
  })
}

export function bindLogout(root, handler) {
  const btn = root.querySelector('[data-action="logout"]')
  if (btn) btn.addEventListener('click', handler)
}

export function setRoleTheme(role) {
  document.documentElement.dataset.role = role || ''
}

export function applySavedColorTheme() {
  const saved = localStorage.getItem('innovatecorp_color_theme') || 'light'
  document.documentElement.dataset.theme = saved
}

export function toggleColorTheme() {
  const current = document.documentElement.dataset.theme === 'dark' ? 'dark' : 'light'
  const next = current === 'dark' ? 'light' : 'dark'
  document.documentElement.dataset.theme = next
  localStorage.setItem('innovatecorp_color_theme', next)
  return next
}

const ROLE_LABELS = {
  operator: 'Operador',
  manager: 'Gestor',
  leader: 'Líder',
}

export function roleLabel(role) {
  return ROLE_LABELS[role] || ''
}

const ROLE_PROFILE_PALETTE = {
  operator: ['#3B82F6', '#6C5CE7', '#10B981', '#F59E0B', '#1A1A2E', '#EC4899'],
  manager: ['#EC4899', '#7C3AED', '#3B82F6', '#10B981', '#F59E0B', '#1A1A2E'],
  leader: ['#7C3AED', '#4F1D96', '#3B82F6', '#EC4899', '#10B981', '#1A1A2E'],
}

export function paletteForRole(role) {
  return ROLE_PROFILE_PALETTE[role] || ROLE_PROFILE_PALETTE.operator
}

export function avatarHtml(account, size = 96) {
  const bg = account.avatarColor || '#6C5CE7'
  if (account.avatar) {
    return `<div class="profile-avatar" style="width:${size}px;height:${size}px;background:${bg}"><img src="${account.avatar}" alt="" /></div>`
  }
  return `<div class="profile-avatar" style="width:${size}px;height:${size}px;background:${bg};font-size:${Math.floor(size / 2.6)}px">${escapeHtml((account.name || account.fullName || '?').charAt(0).toUpperCase())}</div>`
}
