import {
  topBar,
  bottomNav,
  kpiCard,
  barChart,
  icon,
  bindTabs,
  escapeHtml,
  overlayTopBar,
  sectionHeader,
  emptyState,
  relativeTime,
  avatarHtml,
  paletteForRole,
  roleLabel,
} from '../components.js'
import {
  getAllIdeas,
  getGuidelines,
  addGuideline,
  updateGuideline,
  deleteGuideline,
  getMonthlyOperatorRanking,
  getOperatorRanking,
  buildLeaderNotifications,
  hasUnreadLeaderNotifications,
  markLeaderNotificationsRead,
  mergeAccountWithProfile,
  saveProfile,
  sendSuggestion,
  resolveAuthorName,
  statusLabel,
  statusClass,
  MOCK_OPERATORS,
} from '../store.js'
import { createCollaboratorsModule } from './chat.js'
import { runUiAnimations } from '../animations.js'

const TABS = [
  { id: 'home', label: 'Executivo', icon: 'dashboard' },
  { id: 'guidelines', label: 'Diretrizes', icon: 'menu_book' },
  { id: 'team', label: 'Equipe', icon: 'groups' },
  { id: 'tracking', label: 'Acompanhar', icon: 'track_changes' },
  { id: 'profile', label: 'Perfil', icon: 'person' },
]

const monthFormatter = new Intl.DateTimeFormat('pt-BR', { month: 'long' })

export function renderLeader(initialAccount, onLogout) {
  let account = { ...initialAccount }
  let tab = 'home'
  let overlay = null
  let drawerOpen = false
  let guidelineEditing = null // { id, title, content } | { title: '', content: '' } when new
  let guidelineError = ''
  let suggestionTarget = MOCK_OPERATORS[0]?.id || ''
  let suggestionError = ''
  let profileEditing = false
  let profileError = ''

  let collaboratorsModule = null
  let rerenderRef = () => {}
  function ensureCollabModule() {
    if (collaboratorsModule) return collaboratorsModule
    collaboratorsModule = createCollaboratorsModule({
      currentUser: {
        id: 'demo-lider',
        accountKey: account.accountKey,
        fullName: account.fullName,
        name: account.name,
        role: 'leader',
      },
      onClose: () => {
        overlay = null
        collaboratorsModule?.reset()
        rerenderRef()
      },
    })
    return collaboratorsModule
  }

  function drawerHtml() {
    const items = buildLeaderNotifications()
    return `
      <div class="drawer-backdrop ${drawerOpen ? 'open' : ''}" data-action="close-drawer"></div>
      <aside class="drawer-panel ${drawerOpen ? 'open' : ''}">
        <div class="drawer-header">
          <h2>${icon('notifications')} Notificações</h2>
          <button type="button" class="icon-btn" data-action="close-drawer">${icon('close')}</button>
        </div>
        <div class="drawer-body">
          ${
            items.length === 0
              ? `<div class="empty-state">${icon('notifications_off')}<p>Sem notificações.</p></div>`
              : items
                  .map(
                    (n) => `
                <div class="notif-card">
                  <strong>${icon(n.icon)} ${escapeHtml(n.title)}</strong>
                  <p>${escapeHtml(n.body)}</p>
                  <time>${relativeTime(n.createdAt)}</time>
                </div>
              `
                  )
                  .join('')
          }
        </div>
      </aside>
    `
  }

  function homeContent() {
    const ideas = getAllIdeas()
    const pending = ideas.filter((i) => i.status === 'pending').length
    const approved = ideas.filter((i) => i.status === 'approved' || i.status === 'prioritized').length
    const ranking = getMonthlyOperatorRanking()
    const monthName = monthFormatter.format(new Date())
    return `
      <div class="card-gradient" style="margin-top:8px">
        <p class="workspace-title" style="margin:0;font-size:1.15rem"><span>🧠</span> Olá, ${escapeHtml(account.name)}!</p>
        <p style="margin:6px 0 0;opacity:0.92;font-size:0.88rem">A visão executiva do workspace de inovação.</p>
        <div class="row-2" style="margin-top:16px;gap:10px">
          <div style="background:rgba(255,255,255,0.22);padding:12px;border-radius:14px">
            <div style="font-size:1.55rem;font-weight:700">R$ 120k</div>
            <div style="font-size:0.74rem;opacity:0.92">investimento</div>
          </div>
          <div style="background:rgba(255,255,255,0.22);padding:12px;border-radius:14px">
            <div style="font-size:1.55rem;font-weight:700">32%</div>
            <div style="font-size:0.74rem;opacity:0.92">ROI consolidado</div>
          </div>
        </div>
      </div>
      <p class="section-title">⚡ Ações rápidas</p>
      <div class="row-2">
        <button type="button" class="quick-card" data-action="new-guideline"><span class="emoji-badge">📣</span><span>Nova diretriz</span></button>
        <button type="button" class="quick-card" data-action="suggestion"><span class="emoji-badge">📨</span><span>Enviar sugestão</span></button>
      </div>
      <div class="row-2" style="margin-top:12px">
        <button type="button" class="quick-card" data-action="collaborators"><span class="emoji-badge">👥</span><span>Colaboradores</span></button>
        <button type="button" class="quick-card" data-action="all-team"><span class="emoji-badge">🏆</span><span>Ver ranking</span></button>
      </div>
      ${sectionHeader(`🏆 Mais ativos · ${monthName}`, ranking.length > 3 ? 'Ver todos' : '', 'team-tab')}
      ${
        ranking.length === 0
          ? emptyState('Sem registros de ideias neste mês.', 'emoji_events')
          : ranking
              .map(
                (op, i) => `
              <div class="idea-item rank-${i + 1}">
                <strong>#${i + 1} ${escapeHtml(op.name)}</strong>
                <div class="meta">${op.submitted} ideias · ${op.approved} aprovadas</div>
              </div>
            `
              )
              .join('')
      }
      <div class="row-2" style="margin-top:14px">
        ${kpiCard('Ideias totais', String(ideas.length), 'no portfólio')}
        ${kpiCard('Aprovadas', String(approved), pending ? `${pending} pendentes` : '')}
      </div>
      <div class="card card-hoverable" style="margin-top:16px">
        <strong>📊 Pipeline de inovação</strong>
        ${
          ranking.length === 0
            ? `<p class="muted-text" style="margin-top:8px">Aguardando dados do mês.</p>`
            : barChart(
                ranking.slice(0, 6).map((r) => r.submitted),
                ranking.slice(0, 6).map((r) => (r.name || '').split(' ')[0])
              )
        }
      </div>
    `
  }

  function guidelinesContent() {
    const guidelines = getGuidelines()
    return `
      <p class="section-title">📚 Diretrizes estratégicas</p>
      <p class="muted-text" style="margin:-6px 0 12px">Crie, edite e remova as diretrizes que orientam gestores e operadores.</p>
      ${
        guidelines.length === 0
          ? emptyState('Publique a primeira diretriz da liderança.', 'menu_book')
          : guidelines
              .map(
                (g) => `
            <div class="card card-hoverable guideline-card" style="margin-bottom:10px">
              <span class="guideline-label">🧭 Diretriz da liderança</span>
              <strong>${escapeHtml(g.title)}</strong>
              <p class="muted-text" style="margin:8px 0 0">${escapeHtml(g.content)}</p>
              <div class="guideline-meta">${icon('calendar_month')} ${escapeHtml(g.authorName || 'Liderança')} · ${relativeTime(g.createdAt)}</div>
              <div style="display:flex;gap:6px;margin-top:10px">
                <button type="button" class="chip-btn" data-action="edit-guideline" data-id="${g.id}">${icon('edit')} Editar</button>
                <button type="button" class="chip-btn reject" data-action="delete-guideline" data-id="${g.id}">${icon('delete')} Excluir</button>
              </div>
            </div>
          `
              )
              .join('')
      }
      <button type="button" class="btn-primary" data-action="new-guideline" style="margin-top:16px">+ Nova diretriz</button>
    `
  }

  function guidelineFormOverlay() {
    const editing = guidelineEditing || { title: '', content: '' }
    const title = editing.id ? 'Editar diretriz' : 'Nova diretriz'
    return `
      <div class="overlay-screen">
        ${overlayTopBar(title)}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Diretrizes aparecem para todos os colaboradores como orientação estratégica.</p>
          <form class="form-card" id="guideline-form">
            <label>Título</label>
            <input name="title" required minlength="3" value="${escapeHtml(editing.title || '')}" />
            <label>Conteúdo</label>
            <textarea name="content" required minlength="10">${escapeHtml(editing.content || '')}</textarea>
            ${guidelineError ? `<p class="form-error">${escapeHtml(guidelineError)}</p>` : ''}
            <div class="form-actions">
              <button type="button" class="btn-secondary" data-action="back">Cancelar</button>
              <button type="submit" class="btn-primary">${editing.id ? 'Salvar alterações' : 'Publicar'}</button>
            </div>
          </form>
        </div>
      </div>
    `
  }

  function suggestionOverlay() {
    return `
      <div class="overlay-screen">
        ${overlayTopBar('Sugestão da liderança')}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Envie uma orientação personalizada a um operador.</p>
          <form class="form-card" id="suggestion-form">
            <label>Operador</label>
            <select name="target">
              ${MOCK_OPERATORS.map(
                (op) => `
                <option value="${op.id}" data-email="${op.email}" data-name="${op.name}" ${op.id === suggestionTarget ? 'selected' : ''}>
                  ${escapeHtml(op.name)} (${escapeHtml(op.email)})
                </option>
              `
              ).join('')}
            </select>
            <label>Mensagem</label>
            <textarea name="message" required minlength="10" placeholder="Escreva sua orientação..."></textarea>
            ${suggestionError ? `<p class="form-error">${escapeHtml(suggestionError)}</p>` : ''}
            <div class="form-actions">
              <button type="button" class="btn-secondary" data-action="back">Cancelar</button>
              <button type="submit" class="btn-primary">Enviar</button>
            </div>
          </form>
        </div>
      </div>
    `
  }

  function teamOverlay() {
    const monthly = getMonthlyOperatorRanking()
    const overall = getOperatorRanking()
    const monthName = monthFormatter.format(new Date())
    return `
      <div class="overlay-screen">
        ${overlayTopBar('Equipe')}
        <div class="screen-scroll">
          <p class="section-title" style="margin-top:4px">Destaques de ${monthName}</p>
          ${
            monthly.length === 0
              ? emptyState('Sem registros neste mês.', 'emoji_events')
              : monthly
                  .map(
                    (op, i) => `
                <div class="idea-item rank-${i + 1}">
                  <strong>#${i + 1} ${escapeHtml(op.name)}</strong>
                  <div class="meta">${escapeHtml(op.email || '')}</div>
                  <div class="meta">${op.submitted} no mês · ${op.approved} aprovadas</div>
                </div>
              `
                  )
                  .join('')
          }
          <p class="section-title">Ranking acumulado</p>
          ${
            overall.length === 0
              ? emptyState('Sem dados.', 'groups')
              : overall
                  .map(
                    (op, i) => `
                <div class="idea-item">
                  <strong>#${i + 1} ${escapeHtml(op.name)}</strong>
                  <div class="meta">${op.submitted} enviadas · ${op.approved} aprovadas</div>
                </div>
              `
                  )
                  .join('')
          }
        </div>
      </div>
    `
  }

  function teamContent() {
    const monthly = getMonthlyOperatorRanking()
    const monthName = monthFormatter.format(new Date())
    return `
      <p class="section-title">Equipe ativa em ${monthName}</p>
      <p class="muted-text" style="margin:-6px 0 12px">Acompanhe todos os colaboradores mockados e quem está movimentando a inovação neste mês.</p>
      ${
        monthly.length === 0
          ? emptyState('Sem registros neste mês.', 'emoji_events')
          : monthly
              .map(
                (op, i) => `
            <div class="idea-item rank-${i + 1}">
              <strong>#${i + 1} ${escapeHtml(op.name)}</strong>
              <div class="meta">${escapeHtml(op.email || '')}</div>
              <div class="meta">${op.submitted} ideias · ${op.approved} aprovadas${op.submitted === 0 ? ' · sem envios neste mês' : ''}</div>
            </div>
          `
              )
              .join('')
      }
      <button type="button" class="btn-primary" data-action="suggestion" style="margin-top:16px">${icon('send')} Enviar sugestão</button>
    `
  }

  function trackingContent() {
    const ideas = getAllIdeas()
    return `
      <p class="section-title">Pipeline de ideias</p>
      <p class="muted-text" style="margin:-6px 0 12px">Acompanhe o status de todas as ideias do portfólio.</p>
      ${
        ideas.length === 0
          ? emptyState('Sem ideias registradas ainda.', 'track_changes')
          : ideas
              .map(
                (i) => `
            <div class="idea-item">
              <strong>${escapeHtml(i.title)}</strong>
              <div class="meta">${icon('person')} ${escapeHtml(resolveAuthorName(i))} · ${escapeHtml(i.category)} · ${relativeTime(i.createdAt)}</div>
              <p style="font-size:0.85rem;color:var(--innovate-text-secondary);margin-top:8px">${escapeHtml(i.description)}</p>
              <span class="status ${statusClass(i.status)}">${statusLabel(i.status)}</span>
            </div>
          `
              )
              .join('')
      }
    `
  }

  function profileContent() {
    const ideas = getAllIdeas()
    const guidelines = getGuidelines()
    const colors = paletteForRole('leader')

    if (profileEditing) {
      return `
        <div class="card-gradient" style="margin-top:8px;text-align:center">
          <div class="profile-avatar-wrap">
            <label class="profile-avatar-wrap" style="cursor:pointer">
              ${avatarHtml(account)}
              <input type="file" accept="image/*" data-action="avatar-file" hidden />
            </label>
            <button type="button" class="profile-avatar-camera" data-action="avatar-file-btn">${icon('photo_camera')}</button>
          </div>
          <div class="avatar-colors">
            ${colors
              .map(
                (c) => `
              <button type="button" class="avatar-color-dot ${account.avatarColor === c ? 'selected' : ''}" data-color="${c}" style="background:${c}"></button>
            `
              )
              .join('')}
          </div>
          <div class="profile-edit-fields" style="text-align:left;margin-top:12px">
            <label>Nome completo</label>
            <input id="profile-name" value="${escapeHtml(account.fullName)}" />
            <label>E-mail</label>
            <input id="profile-email" type="email" value="${escapeHtml(account.email)}" />
          </div>
          ${profileError ? `<p class="form-error">${escapeHtml(profileError)}</p>` : ''}
          <div class="form-actions" style="margin-top:14px;justify-content:center">
            <button type="button" class="btn-secondary" data-action="profile-cancel">Cancelar</button>
            <button type="button" class="btn-primary" data-action="profile-save" style="width:auto;background:rgba(255,255,255,0.92);color:var(--accent-dark);box-shadow:none">Salvar</button>
          </div>
        </div>
      `
    }

    return `
      <div class="card-gradient profile-header" style="margin-top:8px">
        <div class="profile-avatar-wrap">${avatarHtml(account)}</div>
        <strong>${escapeHtml(account.fullName)}</strong>
        <p style="margin:4px 0 0;opacity:0.95;font-size:0.88rem">${escapeHtml(account.email)}</p>
        <span class="role-pill">${roleLabel('leader')}</span>
        <div style="margin-top:14px">
          <button type="button" class="btn-secondary" data-action="profile-edit" style="background:rgba(255,255,255,0.95);color:var(--accent-dark);border:none">
            ${icon('edit')} Editar perfil
          </button>
        </div>
      </div>
      <div class="profile-stats">
        ${kpiCard('Ideias portfólio', String(ideas.length))}
        ${kpiCard('Diretrizes', String(guidelines.length))}
      </div>
      <div class="profile-insight-card">
        <strong>${icon('insights')} Foco executivo</strong>
        <p>Use diretrizes curtas, mensuráveis e conectadas a ROI para guiar melhor gestores e operadores.</p>
      </div>
      <p class="muted-text" style="margin-top:8px;text-align:center">
        ${icon('shield')} Acesso total: diretrizes, equipe, curadoria e analítico.
      </p>
      <button type="button" class="btn-primary btn-danger" data-action="logout" style="margin-top:16px">Sair</button>
    `
  }

  function content() {
    switch (tab) {
      case 'home':
        return homeContent()
      case 'guidelines':
        return guidelinesContent()
      case 'team':
        return teamContent()
      case 'tracking':
        return trackingContent()
      case 'profile':
        return profileContent()
      default:
        return ''
    }
  }

  function build() {
    if (overlay === 'guideline-form') return `<div class="phone-screen-inner">${guidelineFormOverlay()}</div>`
    if (overlay === 'suggestion') return `<div class="phone-screen-inner">${suggestionOverlay()}</div>`
    if (overlay === 'team') return `<div class="phone-screen-inner">${teamOverlay()}</div>`
    if (overlay === 'collaborators') return `<div class="phone-screen-inner">${ensureCollabModule().html()}</div>`

    const titles = {
      home: 'Executivo',
      guidelines: 'Diretrizes',
      team: 'Equipe',
      tracking: 'Acompanhamento',
      profile: 'Perfil',
    }
    return `
      <div style="position:relative;flex:1;display:flex;flex-direction:column;min-height:0">
        ${drawerHtml()}
        ${topBar(titles[tab], { showBadge: hasUnreadLeaderNotifications() })}
        <div class="screen-scroll">${content()}</div>
        ${bottomNav(TABS, tab)}
      </div>
    `
  }

  return {
    html: build(),
    mount(root) {
      const rerender = () => {
        root.innerHTML = build()
        bindHandlers(root)
        runUiAnimations(root)
      }
      rerenderRef = rerender

      const bindHandlers = (el) => {
        if (overlay === 'collaborators') {
          ensureCollabModule().bind(el, rerender)
          return
        }
        collaboratorsModule?.stop?.()

        bindTabs(el, (id) => {
          tab = id
          overlay = null
          rerender()
        })

        el.querySelector('[data-action="collaborators"]')?.addEventListener('click', () => {
          ensureCollabModule().reset()
          overlay = 'collaborators'
          rerender()
        })

        el.querySelector('[data-action="all-team"]')?.addEventListener('click', () => {
          tab = 'team'
          rerender()
        })

        el.querySelector('[data-action="notifications"]')?.addEventListener('click', () => {
          markLeaderNotificationsRead()
          drawerOpen = true
          rerender()
        })
        el.querySelectorAll('[data-action="close-drawer"]').forEach((b) =>
          b.addEventListener('click', () => {
            drawerOpen = false
            rerender()
          })
        )

        el.querySelectorAll('[data-action="new-guideline"]').forEach((btn) =>
          btn.addEventListener('click', () => {
            guidelineEditing = { title: '', content: '' }
            guidelineError = ''
            overlay = 'guideline-form'
            rerender()
          })
        )

        el.querySelectorAll('[data-action="edit-guideline"]').forEach((btn) =>
          btn.addEventListener('click', () => {
            const g = getGuidelines().find((x) => x.id === btn.dataset.id)
            if (!g) return
            guidelineEditing = { id: g.id, title: g.title, content: g.content }
            guidelineError = ''
            overlay = 'guideline-form'
            rerender()
          })
        )

        el.querySelectorAll('[data-action="delete-guideline"]').forEach((btn) =>
          btn.addEventListener('click', () => {
            if (window.confirm('Excluir esta diretriz?')) {
              deleteGuideline(btn.dataset.id)
              rerender()
            }
          })
        )

        el.querySelector('#guideline-form')?.addEventListener('submit', (e) => {
          e.preventDefault()
          const form = e.target
          const title = form.title.value.trim()
          const content = form.content.value.trim()
          if (title.length < 3 || content.length < 10) {
            guidelineError = 'Preencha título (3+) e conteúdo (10+).'
            rerender()
            return
          }
          if (guidelineEditing?.id) {
            updateGuideline(guidelineEditing.id, { title, content })
          } else {
            addGuideline({ title, content, authorName: account.fullName })
          }
          guidelineEditing = null
          overlay = null
          tab = 'guidelines'
          rerender()
        })

        el.querySelectorAll('[data-action="suggestion"]').forEach((btn) =>
          btn.addEventListener('click', () => {
            overlay = 'suggestion'
            suggestionError = ''
            rerender()
          })
        )

        el.querySelector('#suggestion-form')?.addEventListener('submit', (e) => {
          e.preventDefault()
          const form = e.target
          const opt = form.target.selectedOptions[0]
          const message = form.message.value.trim()
          if (message.length < 10) {
            suggestionError = 'Mensagem com no mínimo 10 caracteres.'
            rerender()
            return
          }
          sendSuggestion({
            senderName: account.fullName,
            senderRole: 'leader',
            targetId: opt.value,
            targetEmail: opt.dataset.email,
            targetName: opt.dataset.name,
            message,
          })
          suggestionTarget = opt.value
          overlay = null
          tab = 'home'
          rerender()
        })

        el.querySelectorAll('[data-action="team-tab"]').forEach((b) =>
          b.addEventListener('click', () => {
            overlay = 'team'
            rerender()
          })
        )

        el.querySelectorAll('[data-action="back"]').forEach((b) =>
          b.addEventListener('click', () => {
            overlay = null
            guidelineError = ''
            suggestionError = ''
            rerender()
          })
        )

        el.querySelector('[data-action="profile-edit"]')?.addEventListener('click', () => {
          profileEditing = true
          profileError = ''
          rerender()
        })

        el.querySelector('[data-action="profile-cancel"]')?.addEventListener('click', () => {
          profileEditing = false
          account = mergeAccountWithProfile({ ...account, email: account.accountKey })
          rerender()
        })

        el.querySelector('[data-action="profile-save"]')?.addEventListener('click', () => {
          const name = el.querySelector('#profile-name')?.value?.trim() || ''
          const email = el.querySelector('#profile-email')?.value?.trim() || ''
          if (name.length < 2 || !email.includes('@')) {
            profileError = 'Dados inválidos.'
            rerender()
            return
          }
          const saved = saveProfile(account.accountKey, {
            fullName: name,
            email,
            avatar: account.avatar,
            avatarColor: account.avatarColor,
          })
          account = { ...account, ...saved }
          profileEditing = false
          profileError = ''
          rerender()
        })

        el.querySelectorAll('[data-color]').forEach((btn) => {
          btn.addEventListener('click', () => {
            account.avatarColor = btn.dataset.color
            account.avatar = null
            rerender()
          })
        })

        const fileInput = el.querySelector('[data-action="avatar-file"]')
        el.querySelector('[data-action="avatar-file-btn"]')?.addEventListener('click', () => fileInput?.click())
        fileInput?.addEventListener('change', () => {
          const file = fileInput.files?.[0]
          if (!file) return
          const reader = new FileReader()
          reader.onload = () => {
            account.avatar = reader.result
            rerender()
          }
          reader.readAsDataURL(file)
        })

        el.querySelectorAll('[data-action="logout"]').forEach((b) => b.addEventListener('click', onLogout))
      }

      rerender()
    },
  }
}
