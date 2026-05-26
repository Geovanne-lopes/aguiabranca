const IDEAS_KEY = 'innovatecorp_ideas'
const PROFILES_KEY = 'innovatecorp_profiles'
const SUGGESTIONS_KEY = 'innovatecorp_suggestions'
const GUIDELINES_KEY = 'innovatecorp_guidelines'
const PROJECTS_KEY = 'innovatecorp_projects'
const MESSAGES_KEY = 'innovatecorp_messages'
const READ_NOTIF_KEY = 'innovatecorp_notif_read'
const READ_MANAGER_NOTIF_KEY = 'innovatecorp_manager_notif_read'
const READ_LEADER_NOTIF_KEY = 'innovatecorp_leader_notif_read'

// Identificadores dos 3 perfis ativos (login)
export const ACTIVE_USERS = [
  { id: 'demo-operador', email: 'operador@innovatecorp.com', name: 'Ana Silva', role: 'operator' },
  { id: 'demo-gestor', email: 'gestor@innovatecorp.com', name: 'Carlos Mendes', role: 'manager' },
  { id: 'demo-lider', email: 'lideranca@innovatecorp.com', name: 'Mariana Costa', role: 'leader' },
]

export const MOCK_OPERATORS = [
  { id: 'mock-op-joao', name: 'João Pereira', email: 'joao.pereira@innovatecorp.com' },
  { id: 'mock-op-maria', name: 'Maria Santos', email: 'maria.santos@innovatecorp.com' },
  { id: 'mock-op-pedro', name: 'Pedro Oliveira', email: 'pedro.oliveira@innovatecorp.com' },
  { id: 'mock-op-lucia', name: 'Lúcia Ferreira', email: 'lucia.ferreira@innovatecorp.com' },
  { id: 'mock-op-rafael', name: 'Rafael Costa', email: 'rafael.costa@innovatecorp.com' },
  { id: 'demo-operador', name: 'Ana Silva', email: 'operador@innovatecorp.com' },
]

const SEED_GUIDELINES = [
  {
    id: 'g1',
    title: 'Transformação Digital',
    content:
      'Priorizar iniciativas que digitalizem processos operacionais e reduzam retrabalho.',
    authorName: 'Liderança InnovateCorp',
    createdAt: Date.now() - 86400000 * 14,
  },
  {
    id: 'g2',
    title: 'Sustentabilidade Corporativa',
    content:
      'Incentivar ideias com impacto ambiental mensurável e metas ESG claras.',
    authorName: 'Liderança InnovateCorp',
    createdAt: Date.now() - 86400000 * 7,
  },
]

const SEED_IDEAS = [
  { id: 'seed-1', title: 'App mobile para fila express', description: 'Atender clientes em fila com app dedicado, reduzindo tempo médio de espera.', category: 'Tecnologia', authorEmail: 'joao.pereira@innovatecorp.com', authorName: 'João Pereira', authorId: 'mock-op-joao', status: 'pending', createdAt: Date.now() - 86400000 * 2 },
  { id: 'seed-2', title: 'Treinamento gamificado de vendas', description: 'Gamificar o onboarding e treinamento contínuo das equipes de vendas.', category: 'Processo', authorEmail: 'maria.santos@innovatecorp.com', authorName: 'Maria Santos', authorId: 'mock-op-maria', status: 'prioritized', createdAt: Date.now() - 86400000 * 4 },
  { id: 'seed-3', title: 'Etiquetas digitais no estoque', description: 'Substituir etiquetas físicas por etiquetas digitais para controle em tempo real.', category: 'Tecnologia', authorEmail: 'pedro.oliveira@innovatecorp.com', authorName: 'Pedro Oliveira', authorId: 'mock-op-pedro', status: 'approved', createdAt: Date.now() - 86400000 * 6 },
  { id: 'seed-4', title: 'Coleta seletiva nas lojas', description: 'Programa de coleta seletiva em todas as filiais, integrando consumidores.', category: 'Sustentabilidade', authorEmail: 'lucia.ferreira@innovatecorp.com', authorName: 'Lúcia Ferreira', authorId: 'mock-op-lucia', status: 'pending', createdAt: Date.now() - 86400000 * 1 },
  { id: 'seed-5', title: 'Painel de metas por equipe', description: 'Dashboard com metas e progresso visível para todas as equipes.', category: 'Processo', authorEmail: 'rafael.costa@innovatecorp.com', authorName: 'Rafael Costa', authorId: 'mock-op-rafael', status: 'approved', createdAt: Date.now() - 86400000 * 8 },
  { id: 'seed-6', title: 'Reduzir fila no caixa', description: 'Pré-atendimento e sinalização nas filas longas do caixa.', category: 'Outro', authorEmail: 'operador@innovatecorp.com', authorName: 'Ana Silva', authorId: 'demo-operador', status: 'pending', createdAt: Date.now() - 86400000 * 3 },
  { id: 'seed-7', title: 'Programa de mentoria interna', description: 'Mentoria entre operadores experientes e novos colaboradores.', category: 'Processo', authorEmail: 'maria.santos@innovatecorp.com', authorName: 'Maria Santos', authorId: 'mock-op-maria', status: 'pending', createdAt: Date.now() - 86400000 * 0.5 },
  { id: 'seed-8', title: 'Wi-Fi exclusivo para clientes', description: 'Rede separada de alta qualidade, com landing page de fidelidade.', category: 'Tecnologia', authorEmail: 'joao.pereira@innovatecorp.com', authorName: 'João Pereira', authorId: 'mock-op-joao', status: 'prioritized', createdAt: Date.now() - 86400000 * 10 },
  { id: 'seed-9', title: 'Mural digital de reconhecimento', description: 'Tela na loja exibindo destaques do time semanalmente.', category: 'Processo', authorEmail: 'lucia.ferreira@innovatecorp.com', authorName: 'Lúcia Ferreira', authorId: 'mock-op-lucia', status: 'approved', createdAt: Date.now() - 86400000 * 12 },
  { id: 'seed-10', title: 'Embalagens 100% recicláveis', description: 'Substituir todas as embalagens internas por material reciclável.', category: 'Sustentabilidade', authorEmail: 'pedro.oliveira@innovatecorp.com', authorName: 'Pedro Oliveira', authorId: 'mock-op-pedro', status: 'pending', createdAt: Date.now() - 86400000 * 0.2 },
  { id: 'seed-11', title: 'Caixa rápido com tablet', description: 'Atendentes itinerantes com tablet para reduzir fila no horário de pico.', category: 'Tecnologia', authorEmail: 'rafael.costa@innovatecorp.com', authorName: 'Rafael Costa', authorId: 'mock-op-rafael', status: 'pending', createdAt: Date.now() - 86400000 * 0.8 },
  { id: 'seed-12', title: 'Treinamento de atendimento empático', description: 'Workshop trimestral com foco em escuta ativa e empatia.', category: 'Processo', authorEmail: 'joao.pereira@innovatecorp.com', authorName: 'João Pereira', authorId: 'mock-op-joao', status: 'approved', createdAt: Date.now() - 86400000 * 14 },
  { id: 'seed-13', title: 'Reciclagem de uniformes antigos', description: 'Programa de troca de uniformes por descontos em produtos parceiros.', category: 'Sustentabilidade', authorEmail: 'maria.santos@innovatecorp.com', authorName: 'Maria Santos', authorId: 'mock-op-maria', status: 'pending', createdAt: Date.now() - 86400000 * 1.2 },
]

const SEED_PROJECTS = [
  {
    id: 'proj-1',
    title: 'Checklist digital',
    description: 'App de checklist diário com fotos e validação em tempo real do gestor.',
    category: 'Tecnologia',
    owner: 'João Pereira',
    status: 'execution',
    investment: 45000,
    profit: 12000,
    deadlineEpoch: Date.now() + 86400000 * 12,
    createdAt: Date.now() - 86400000 * 30,
  },
  {
    id: 'proj-2',
    title: 'Treinamento gamificado',
    description: 'Plataforma com missões e ranking para acelerar onboarding das equipes.',
    category: 'Processo',
    owner: 'Maria Santos',
    status: 'planning',
    investment: 75000,
    profit: 0,
    deadlineEpoch: Date.now() + 86400000 * 60,
    createdAt: Date.now() - 86400000 * 15,
  },
  {
    id: 'proj-3',
    title: 'Etiquetas digitais',
    description: 'Substituição completa de etiquetas físicas por e-ink com integração ERP.',
    category: 'Tecnologia',
    owner: 'Pedro Oliveira',
    status: 'urgent',
    investment: 95000,
    profit: 18500,
    deadlineEpoch: Date.now() + 86400000 * 3,
    createdAt: Date.now() - 86400000 * 45,
  },
  {
    id: 'proj-4',
    title: 'Coleta seletiva nas lojas',
    description: 'Programa de coleta seletiva consolidado em 6 filiais piloto.',
    category: 'Sustentabilidade',
    owner: 'Lúcia Ferreira',
    status: 'completed',
    investment: 22000,
    profit: 6000,
    deadlineEpoch: Date.now() - 86400000 * 7,
    createdAt: Date.now() - 86400000 * 90,
  },
]

export const PROJECT_STATUSES = [
  { id: 'planning', label: 'Planejamento', color: '#6b7280' },
  { id: 'execution', label: 'Em execução', color: '#3b82f6' },
  { id: 'urgent', label: 'Prazo urgente', color: '#ef4444' },
  { id: 'risk', label: 'Ticket médio', color: '#f59e0b' },
  { id: 'completed', label: 'Concluído', color: '#10b981' },
]

export function statusInfo(statusId) {
  return PROJECT_STATUSES.find((s) => s.id === statusId) || PROJECT_STATUSES[0]
}

// ------------------------------------------------------------------
// Profiles
// ------------------------------------------------------------------

function loadProfiles() {
  try {
    return JSON.parse(localStorage.getItem(PROFILES_KEY) || '{}')
  } catch {
    return {}
  }
}

function saveProfiles(profiles) {
  localStorage.setItem(PROFILES_KEY, JSON.stringify(profiles))
}

export function mergeAccountWithProfile(account) {
  const key = account.email.toLowerCase()
  const saved = loadProfiles()[key] || {}
  const fullName = saved.fullName || account.fullName
  const email = saved.email || account.email
  const name = saved.name || fullName.split(' ')[0] || account.name
  return {
    ...account,
    accountKey: key,
    name,
    fullName,
    email,
    avatar: saved.avatar || null,
    avatarColor: saved.avatarColor || defaultColorForRole(account.role),
  }
}

function defaultColorForRole(role) {
  switch (role) {
    case 'manager':
      return '#EC4899'
    case 'leader':
      return '#7C3AED'
    default:
      return '#6C5CE7'
  }
}

export function saveProfile(accountKey, { fullName, email, avatar, avatarColor }) {
  const profiles = loadProfiles()
  const previous = profiles[accountKey] || {}
  const name = fullName.split(' ')[0] || fullName

  profiles[accountKey] = {
    fullName: fullName.trim(),
    email: email.trim().toLowerCase(),
    name,
    avatar: avatar ?? previous.avatar ?? null,
    avatarColor: avatarColor ?? previous.avatarColor ?? '#6C5CE7',
  }
  saveProfiles(profiles)

  const oldEmail = previous.email || accountKey
  const newEmail = profiles[accountKey].email
  if (oldEmail !== newEmail) {
    const ideas = loadIdeas().map((idea) =>
      idea.authorEmail === oldEmail
        ? { ...idea, authorEmail: newEmail, authorName: fullName }
        : idea
    )
    saveIdeas(ideas)
  } else {
    const ideas = loadIdeas().map((idea) =>
      idea.authorEmail === newEmail ? { ...idea, authorName: fullName } : idea
    )
    saveIdeas(ideas)
  }

  return profiles[accountKey]
}

// ------------------------------------------------------------------
// Ideas
// ------------------------------------------------------------------

function loadIdeas() {
  try {
    const raw = localStorage.getItem(IDEAS_KEY)
    if (raw) {
      const existing = JSON.parse(raw)
      const existingIds = new Set(existing.map((idea) => idea.id))
      const missingSeeds = SEED_IDEAS.filter((idea) => !existingIds.has(idea.id))
      if (missingSeeds.length) {
        const merged = [...missingSeeds, ...existing]
        saveIdeas(merged)
        return merged
      }
      return existing
    }
  } catch {
    /* ignore */
  }
  localStorage.setItem(IDEAS_KEY, JSON.stringify(SEED_IDEAS))
  return [...SEED_IDEAS]
}

function saveIdeas(ideas) {
  localStorage.setItem(IDEAS_KEY, JSON.stringify(ideas))
}

export function getAllIdeas() {
  return loadIdeas().sort((a, b) => b.createdAt - a.createdAt)
}

export function getIdeasByAuthor(email) {
  return getAllIdeas().filter((i) => i.authorEmail === email)
}

export function resolveAuthorName(idea) {
  const op = MOCK_OPERATORS.find((o) => o.email === idea.authorEmail || o.id === idea.authorId)
  return op?.name || idea.authorName || 'Colaborador'
}

export function getOperatorRanking() {
  return buildRanking(getAllIdeas())
}

export function getMonthlyOperatorRanking() {
  const now = new Date()
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1).getTime()
  return buildRanking(getAllIdeas().filter((i) => i.createdAt >= startOfMonth))
}

function buildRanking(ideas) {
  const map = new Map()
  MOCK_OPERATORS.forEach((op) => {
    map.set(op.email, {
      key: op.email,
      name: op.name,
      email: op.email,
      submitted: 0,
      approved: 0,
    })
  })
  ideas.forEach((idea) => {
    const key = idea.authorEmail || idea.authorId
    if (!map.has(key)) {
      map.set(key, {
        key,
        name: resolveAuthorName(idea),
        email: idea.authorEmail,
        submitted: 0,
        approved: 0,
      })
    }
    const row = map.get(key)
    row.submitted++
    if (idea.status === 'approved' || idea.status === 'prioritized') row.approved++
  })
  return [...map.values()].sort(
    (a, b) => b.submitted - a.submitted || b.approved - a.approved || a.name.localeCompare(b.name)
  )
}

export function addIdea({ title, description, category, authorEmail, authorName, authorId }) {
  const ideas = loadIdeas()
  const idea = {
    id: `idea-${Date.now()}`,
    title: title.trim(),
    description: description.trim(),
    category,
    authorEmail,
    authorName,
    authorId: authorId || authorEmail,
    status: 'pending',
    createdAt: Date.now(),
  }
  ideas.unshift(idea)
  saveIdeas(ideas)
  markNotificationsUnread()
  markManagerNotificationsUnread()
  markLeaderNotificationsUnread()
  return idea
}

export function updateIdeaStatus(id, status) {
  const ideas = loadIdeas()
  const idx = ideas.findIndex((i) => i.id === id)
  if (idx >= 0) {
    ideas[idx] = { ...ideas[idx], status }
    saveIdeas(ideas)
    markNotificationsUnread()
    markLeaderNotificationsUnread()
  }
}

export function statusLabel(status) {
  const map = {
    pending: 'Pendente',
    approved: 'Aprovada',
    rejected: 'Reprovada',
    prioritized: 'Priorizada',
  }
  return map[status] || status
}

export function statusClass(status) {
  return `status-${status}`
}

// ------------------------------------------------------------------
// Guidelines (leader CRUD)
// ------------------------------------------------------------------

function loadGuidelinesInternal() {
  try {
    const raw = localStorage.getItem(GUIDELINES_KEY)
    if (raw) return JSON.parse(raw)
  } catch {
    /* ignore */
  }
  localStorage.setItem(GUIDELINES_KEY, JSON.stringify(SEED_GUIDELINES))
  return [...SEED_GUIDELINES]
}

function saveGuidelinesInternal(list) {
  localStorage.setItem(GUIDELINES_KEY, JSON.stringify(list))
}

export function getGuidelines() {
  return loadGuidelinesInternal().sort((a, b) => b.createdAt - a.createdAt)
}

export function addGuideline({ title, content, authorName }) {
  const list = loadGuidelinesInternal()
  const guideline = {
    id: `gl-${Date.now()}`,
    title: title.trim(),
    content: content.trim(),
    authorName,
    createdAt: Date.now(),
  }
  list.unshift(guideline)
  saveGuidelinesInternal(list)
  markNotificationsUnread()
  return guideline
}

export function updateGuideline(id, { title, content }) {
  const list = loadGuidelinesInternal()
  const idx = list.findIndex((g) => g.id === id)
  if (idx >= 0) {
    list[idx] = { ...list[idx], title: title.trim(), content: content.trim() }
    saveGuidelinesInternal(list)
    markNotificationsUnread()
  }
}

export function deleteGuideline(id) {
  const list = loadGuidelinesInternal().filter((g) => g.id !== id)
  saveGuidelinesInternal(list)
}

// Compat: operator screen previously imported `GUIDELINES`
export const GUIDELINES = new Proxy([], {
  get(_target, prop) {
    const list = getGuidelines()
    return list[prop]
  },
  has(_target, prop) {
    return prop in getGuidelines()
  },
  ownKeys() {
    return Object.keys(getGuidelines())
  },
  getOwnPropertyDescriptor() {
    return { enumerable: true, configurable: true }
  },
})

// ------------------------------------------------------------------
// Suggestions
// ------------------------------------------------------------------

function loadSuggestions() {
  try {
    return JSON.parse(localStorage.getItem(SUGGESTIONS_KEY) || '[]')
  } catch {
    return []
  }
}

function saveSuggestions(list) {
  localStorage.setItem(SUGGESTIONS_KEY, JSON.stringify(list))
}

export function getSuggestionsForTarget(email) {
  return loadSuggestions()
    .filter((s) => s.targetEmail === email)
    .sort((a, b) => b.createdAt - a.createdAt)
}

export function getAllSuggestions() {
  return loadSuggestions().sort((a, b) => b.createdAt - a.createdAt)
}

export function sendSuggestion({ senderName, senderRole, targetId, targetEmail, targetName, message }) {
  const list = loadSuggestions()
  const suggestion = {
    id: `sug-${Date.now()}`,
    senderName,
    senderRole: senderRole || 'manager',
    managerName: senderName,
    targetId,
    targetEmail,
    targetName,
    message: message.trim(),
    createdAt: Date.now(),
  }
  list.unshift(suggestion)
  saveSuggestions(list)
  markNotificationsUnread()
  markManagerNotificationsUnread()
  markLeaderNotificationsUnread()
  return suggestion
}

export function sendManagerSuggestion(payload) {
  return sendSuggestion({ ...payload, senderName: payload.managerName, senderRole: 'manager' })
}

// ------------------------------------------------------------------
// Notifications — operator
// ------------------------------------------------------------------

export function buildNotifications(email) {
  const ideas = getIdeasByAuthor(email)
  const guidelines = getGuidelines()
  const list = []

  guidelines.forEach((g) => {
    list.push({
      id: `guideline-${g.id}`,
      title: 'Orientação da liderança',
      body: `${g.title}: ${g.content}`,
      icon: 'campaign',
      createdAt: g.createdAt,
    })
  })

  getSuggestionsForTarget(email).forEach((s) => {
    const role = s.senderRole === 'leader' ? 'Liderança' : 'Gestor'
    list.push({
      id: `sug-${s.id}`,
      title: `Sugestão do ${role.toLowerCase()}`,
      body: `${s.senderName || s.managerName}: ${s.message}`,
      icon: s.senderRole === 'leader' ? 'workspace_premium' : 'campaign',
      createdAt: s.createdAt,
    })
  })

  ideas.forEach((idea) => {
    if (idea.status === 'pending') {
      list.push({
        id: `pending-${idea.id}`,
        title: 'Ideia enviada',
        body: `Sua ideia "${idea.title}" está aguardando curadoria do gestor.`,
        icon: 'schedule',
        createdAt: idea.createdAt,
      })
    } else if (idea.status === 'approved') {
      list.push({
        id: `approved-${idea.id}`,
        title: 'Ideia aprovada',
        body: `Parabéns! "${idea.title}" foi aprovada. Confira na aba Ideias.`,
        icon: 'check_circle',
        createdAt: idea.createdAt,
      })
    } else if (idea.status === 'rejected') {
      list.push({
        id: `rejected-${idea.id}`,
        title: 'Ideia reprovada',
        body: `A ideia "${idea.title}" foi reprovada.`,
        icon: 'priority_high',
        createdAt: idea.createdAt,
      })
    } else if (idea.status === 'prioritized') {
      list.push({
        id: `prio-${idea.id}`,
        title: 'Ideia priorizada',
        body: `Sua ideia "${idea.title}" foi priorizada pela gestão.`,
        icon: 'lightbulb',
        createdAt: idea.createdAt,
      })
    }
  })

  return list.sort((a, b) => b.createdAt - a.createdAt)
}

function markNotificationsUnread() {
  localStorage.removeItem(READ_NOTIF_KEY)
}

export function hasUnreadNotifications() {
  return localStorage.getItem(READ_NOTIF_KEY) !== '1'
}

export function markNotificationsRead() {
  localStorage.setItem(READ_NOTIF_KEY, '1')
}

// ------------------------------------------------------------------
// Notifications — manager
// ------------------------------------------------------------------

export function buildManagerNotifications() {
  const ideas = getAllIdeas()
  const list = []
  const pending = ideas.filter((i) => i.status === 'pending').length
  if (pending > 0) {
    list.push({
      id: 'pending',
      title: 'Ideias pendentes',
      body: `${pending} ideia(s) aguardam curadoria.`,
      icon: 'warning',
      createdAt: Date.now(),
    })
  }
  ideas
    .filter((i) => i.status === 'pending')
    .slice(0, 5)
    .forEach((idea) => {
      list.push({
        id: `idea-${idea.id}`,
        title: `Nova ideia — ${resolveAuthorName(idea)}`,
        body: `"${idea.title}" aguarda avaliação.`,
        icon: 'lightbulb',
        createdAt: idea.createdAt,
      })
    })
  const top = getOperatorRanking()[0]
  if (top) {
    list.push({
      id: 'top',
      title: 'Operador mais ativo',
      body: `${top.name} lidera com ${top.submitted} ideia(s).`,
      icon: 'groups',
      createdAt: Date.now() - 3_600_000,
    })
  }
  return list.sort((a, b) => b.createdAt - a.createdAt)
}

export function hasUnreadManagerNotifications() {
  return localStorage.getItem(READ_MANAGER_NOTIF_KEY) !== '1'
}

export function markManagerNotificationsRead() {
  localStorage.setItem(READ_MANAGER_NOTIF_KEY, '1')
}

function markManagerNotificationsUnread() {
  localStorage.removeItem(READ_MANAGER_NOTIF_KEY)
}

// ------------------------------------------------------------------
// Notifications — leader
// ------------------------------------------------------------------

export function buildLeaderNotifications() {
  const list = []
  const ideas = getAllIdeas()
  const pending = ideas.filter((i) => i.status === 'pending').length
  const approved = ideas.filter((i) => i.status === 'approved' || i.status === 'prioritized').length

  list.push({
    id: 'overview',
    title: 'Visão estratégica',
    body: `${ideas.length} ideias registradas · ${pending} pendentes · ${approved} aprovadas.`,
    icon: 'insights',
    createdAt: Date.now(),
  })

  const top = getMonthlyOperatorRanking()[0]
  if (top) {
    list.push({
      id: 'monthly-top',
      title: 'Destaque do mês',
      body: `${top.name} enviou ${top.submitted} ideia(s) neste mês.`,
      icon: 'emoji_events',
      createdAt: Date.now() - 60_000,
    })
  }

  ideas
    .filter((i) => i.status === 'pending')
    .slice(0, 3)
    .forEach((idea) => {
      list.push({
        id: `idea-${idea.id}`,
        title: 'Ideia pendente de curadoria',
        body: `"${idea.title}" — ${resolveAuthorName(idea)}`,
        icon: 'lightbulb',
        createdAt: idea.createdAt,
      })
    })

  return list.sort((a, b) => b.createdAt - a.createdAt)
}

export function hasUnreadLeaderNotifications() {
  return localStorage.getItem(READ_LEADER_NOTIF_KEY) !== '1'
}

export function markLeaderNotificationsRead() {
  localStorage.setItem(READ_LEADER_NOTIF_KEY, '1')
}

function markLeaderNotificationsUnread() {
  localStorage.removeItem(READ_LEADER_NOTIF_KEY)
}

// ------------------------------------------------------------------
// Projetos
// ------------------------------------------------------------------

function loadProjects() {
  try {
    const raw = localStorage.getItem(PROJECTS_KEY)
    if (raw) return JSON.parse(raw)
  } catch {
    /* ignore */
  }
  localStorage.setItem(PROJECTS_KEY, JSON.stringify(SEED_PROJECTS))
  return [...SEED_PROJECTS]
}

function saveProjects(list) {
  localStorage.setItem(PROJECTS_KEY, JSON.stringify(list))
}

export function getAllProjects() {
  return loadProjects().sort((a, b) => (a.status === 'completed' ? 1 : -1) - (b.status === 'completed' ? 1 : -1) || b.createdAt - a.createdAt)
}

export function getProjectById(id) {
  return loadProjects().find((p) => p.id === id) || null
}

export function updateProject(id, patch) {
  const list = loadProjects()
  const idx = list.findIndex((p) => p.id === id)
  if (idx >= 0) {
    list[idx] = { ...list[idx], ...patch }
    saveProjects(list)
    return list[idx]
  }
  return null
}

// ------------------------------------------------------------------
// Chat (entre operador, gestor, líder e operadores mock)
// ------------------------------------------------------------------

const MOCK_OPERATOR_REPLIES = [
  'Combinado! Vou começar agora.',
  'Obrigado pela mensagem! Vou avaliar.',
  'Anotado, qualquer dúvida te chamo.',
  'Excelente ideia, vou aplicar.',
  'Pode contar comigo!',
  'Vou alinhar com o time e retorno.',
]

function loadMessages() {
  try {
    return JSON.parse(localStorage.getItem(MESSAGES_KEY) || '[]')
  } catch {
    return []
  }
}

function saveMessages(list) {
  localStorage.setItem(MESSAGES_KEY, JSON.stringify(list))
}

export function threadIdFor(idA, idB) {
  return [idA, idB].sort().join('|')
}

export function getThreadMessages(myId, otherId) {
  const tid = threadIdFor(myId, otherId)
  return loadMessages()
    .filter((m) => m.threadId === tid)
    .sort((a, b) => a.createdAt - b.createdAt)
}

export function sendMessage({ fromId, fromName, toId, toName, text }) {
  const trimmed = text.trim()
  if (!trimmed) return null
  const list = loadMessages()
  const message = {
    id: `msg-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
    threadId: threadIdFor(fromId, toId),
    fromId,
    fromName,
    toId,
    toName,
    text: trimmed,
    createdAt: Date.now(),
  }
  list.push(message)
  saveMessages(list)

  const isMock = MOCK_OPERATORS.some((op) => op.id === toId) && !ACTIVE_USERS.some((u) => u.id === toId)
  if (isMock) {
    setTimeout(() => {
      const reply = MOCK_OPERATOR_REPLIES[Math.floor(Math.random() * MOCK_OPERATOR_REPLIES.length)]
      const repliesList = loadMessages()
      repliesList.push({
        id: `msg-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
        threadId: message.threadId,
        fromId: toId,
        fromName: toName,
        toId: fromId,
        toName: fromName,
        text: reply,
        createdAt: Date.now(),
      })
      saveMessages(repliesList)
      window.dispatchEvent(new CustomEvent('innovate:new-message', { detail: { threadId: message.threadId } }))
    }, 1500 + Math.random() * 1500)
  }

  window.dispatchEvent(new CustomEvent('innovate:new-message', { detail: { threadId: message.threadId } }))
  return message
}

export function getCollaborators(currentAccountKey) {
  // Inclui os 3 perfis ativos + operadores mock
  const result = []
  ACTIVE_USERS.forEach((u) => {
    if (u.email.toLowerCase() === currentAccountKey?.toLowerCase()) return
    result.push({
      id: u.id,
      name: u.name,
      email: u.email,
      role: u.role,
      isMock: false,
    })
  })
  MOCK_OPERATORS.forEach((u) => {
    // já incluímos demo-operador via ACTIVE_USERS
    if (u.id === 'demo-operador') return
    if (u.email.toLowerCase() === currentAccountKey?.toLowerCase()) return
    result.push({
      id: u.id,
      name: u.name,
      email: u.email,
      role: 'operator',
      isMock: true,
    })
  })
  return result
}

export function getCollaboratorIdeas(collaborator) {
  return getAllIdeas().filter(
    (i) => i.authorId === collaborator.id || i.authorEmail === collaborator.email
  )
}
