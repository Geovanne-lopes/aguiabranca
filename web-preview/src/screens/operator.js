import {
  topBar,
  bottomNav,
  kpiCard,
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
  getIdeasByAuthor,
  addIdea,
  buildNotifications,
  hasUnreadNotifications,
  markNotificationsRead,
  mergeAccountWithProfile,
  saveProfile,
  statusLabel,
  statusClass,
  getGuidelines,
} from '../store.js'
import { createCollaboratorsModule } from './chat.js'
import { runUiAnimations } from '../animations.js'

const TABS = [
  { id: 'home', label: 'Início', icon: 'home' },
  { id: 'strategies', label: 'Estratégias', icon: 'track_changes' },
  { id: 'ideas', label: 'Ideias', icon: 'lightbulb' },
  { id: 'profile', label: 'Perfil', icon: 'person' },
]

const CATEGORIES = ['Processo', 'Produto', 'Tecnologia', 'Sustentabilidade', 'Outro']

function ideaItemHtml(idea) {
  return `
    <div class="idea-item">
      <strong>${escapeHtml(idea.title)}</strong>
      <div class="meta">${escapeHtml(idea.category)} · ${relativeTime(idea.createdAt)}</div>
      <span class="status ${statusClass(idea.status)}">${statusLabel(idea.status)}</span>
    </div>
  `
}

export function renderOperator(initialAccount, onLogout) {
  let account = { ...initialAccount }
  let tab = 'home'
  let overlay = null
  let drawerOpen = false
  let submitCategory = 'Processo'
  let formError = ''
  let profileEditing = false
  let profileError = ''

  let collaboratorsModule = null
  function rerenderRef() {
    /* será preenchido depois do mount */
  }
  function ensureCollabModule() {
    if (collaboratorsModule) return collaboratorsModule
    collaboratorsModule = createCollaboratorsModule({
      currentUser: {
        id: 'demo-operador',
        accountKey: account.accountKey,
        fullName: account.fullName,
        name: account.name,
        role: 'operator',
      },
      onClose: () => {
        overlay = null
        collaboratorsModule?.reset()
        rerenderRef()
      },
    })
    return collaboratorsModule
  }

  function getIdeas() {
    return getIdeasByAuthor(account.email)
  }

  function recentIdeas() {
    return getIdeas().slice(0, 3)
  }

  function sortedIdeasForTab() {
    const order = { approved: 3, prioritized: 3, pending: 2, rejected: 1 }
    return [...getIdeas()].sort(
      (a, b) => (order[b.status] || 0) - (order[a.status] || 0) || b.createdAt - a.createdAt
    )
  }

  function drawerHtml() {
    const items = buildNotifications(account.email)
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
              ? `<div class="empty-state">${icon('notifications_off')}<p style="margin:8px 0 0">Nenhuma notificação no momento.</p></div>`
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

  function profileContent() {
    const colors = paletteForRole('operator')
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
              <button type="button" class="avatar-color-dot ${account.avatarColor === c ? 'selected' : ''}" data-color="${c}" style="background:${c}" aria-label="Cor"></button>
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
        <span class="role-pill">${roleLabel('operator')}</span>
        <div style="margin-top:14px">
          <button type="button" class="btn-secondary" data-action="profile-edit" style="background:rgba(255,255,255,0.95);color:var(--accent-dark);border:none">
            ${icon('edit')} Editar perfil
          </button>
        </div>
      </div>
    `
  }

  function allIdeasOverlay() {
    const ideas = getIdeas()
    return `
      <div class="overlay-screen">
        ${overlayTopBar('Todas as suas ideias')}
        <div class="screen-scroll">
          ${
            ideas.length === 0
              ? emptyState('Você ainda não enviou nenhuma ideia.', 'lightbulb')
              : ideas.map(ideaItemHtml).join('')
          }
        </div>
      </div>
    `
  }

  function submitOverlay() {
    const title = submitCategory === 'Outro' ? 'Reportar problema' : 'Nova ideia'
    return `
      <div class="overlay-screen">
        ${overlayTopBar(title)}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Descreva sua ideia ou problema. Após o envio, o gestor avaliará na curadoria.</p>
          <form class="form-card" id="submit-idea-form">
            <label for="idea-title">Título</label>
            <input id="idea-title" name="title" required minlength="3" placeholder="Resumo da ideia" />
            <label for="idea-desc">Descrição</label>
            <textarea id="idea-desc" name="description" required minlength="10" placeholder="Descreva com detalhes"></textarea>
            <label for="idea-cat">Categoria</label>
            <select id="idea-cat" name="category">
              ${CATEGORIES.map((c) => `<option value="${c}" ${c === submitCategory ? 'selected' : ''}>${c}</option>`).join('')}
            </select>
            ${formError ? `<p class="form-error">${escapeHtml(formError)}</p>` : ''}
            <div class="form-actions">
              <button type="button" class="btn-secondary" data-action="back">Cancelar</button>
              <button type="submit" class="btn-primary">Enviar</button>
            </div>
          </form>
        </div>
      </div>
    `
  }

  function overlayHtml() {
    if (overlay === 'allIdeas') return allIdeasOverlay()
    if (overlay === 'submit') return submitOverlay()
    if (overlay === 'collaborators') return ensureCollabModule().html()
    return ''
  }

  function homeContent(acc) {
    const ideas = getIdeas()
    const approved = ideas.filter((i) => i.status === 'approved' || i.status === 'prioritized').length
    const guidelines = getGuidelines()
    const featured = guidelines[0]
    return `
      <div class="card-gradient" style="margin-top:8px">
        <p class="workspace-title" style="margin:0;font-size:1.15rem"><span>✨</span> Olá, ${escapeHtml(acc.name)}!</p>
        <p style="margin:6px 0 0;opacity:0.92;font-size:0.88rem">Seu espaço para registrar ideias que viram movimento.</p>
        <div style="margin-top:16px;background:rgba(255,255,255,0.22);border-radius:12px;height:10px;overflow:hidden">
          <div style="width:${Math.min(ideas.length * 12 + 5, 100)}%;height:100%;background:#fff;border-radius:12px;transition:width 0.4s ease"></div>
        </div>
        <p style="margin:10px 0 0;font-size:0.78rem;opacity:0.88">Nível ${Math.max(1, Math.floor(ideas.length / 3) + 1)} · ${ideas.length * 25} pontos</p>
      </div>
      <p class="section-title">⚡ Ações rápidas</p>
      <div class="row-2">
        <button type="button" class="quick-card" data-action="new-idea"><span class="emoji-badge">💡</span><span>Nova ideia</span></button>
        <button type="button" class="quick-card" data-action="report"><span class="emoji-badge">🚩</span><span>Reportar problema</span></button>
      </div>
      <div class="row-2" style="margin-top:12px">
        <button type="button" class="quick-card" data-action="collaborators"><span class="emoji-badge">👥</span><span>Colaboradores</span></button>
        <button type="button" class="quick-card" data-action="all-ideas"><span class="emoji-badge">🗂️</span><span>Minhas ideias</span></button>
      </div>
      <p class="section-title">📌 Indicadores</p>
      <div class="row-2">
        ${kpiCard('Ideias enviadas', String(ideas.length), ideas.length ? '+' + ideas.length + ' total' : '')}
        ${kpiCard('Aprovadas', String(approved), approved ? '+' + approved : '')}
      </div>
      <div class="card card-hoverable" style="margin-top:16px">
        <strong>🌤️ Insight do dia</strong>
        <p class="muted-text" style="margin:10px 0 0">
          Equipes que registram ideias semanalmente têm 40% mais chance de aprovação.
        </p>
      </div>
      ${
        featured
          ? `
        <div class="card card-hoverable" style="margin-top:12px">
          <strong>📣 Orientação estratégica</strong>
          <p style="margin:8px 0 4px;font-weight:600">${escapeHtml(featured.title)}</p>
          <p class="muted-text" style="margin:0">${escapeHtml(featured.content)}</p>
        </div>
      `
          : ''
      }
      ${sectionHeader('📝 Suas ideias recentes', getIdeas().length > 3 ? 'Ver todas' : '', 'all-ideas')}
      ${
        recentIdeas().length === 0
          ? emptyState('Comece enviando sua primeira ideia.', 'lightbulb')
          : recentIdeas().map(ideaItemHtml).join('')
      }
    `
  }

  function content() {
    switch (tab) {
      case 'home':
        return homeContent(account)
      case 'strategies': {
        const guidelines = getGuidelines()
        return `
          <p class="section-title">📚 Estratégias corporativas</p>
          ${
            guidelines.length === 0
              ? emptyState('Aguardando diretrizes da liderança.', 'menu_book')
              : guidelines
                  .map(
                    (g) => `
              <div class="card card-hoverable guideline-card" style="margin-bottom:12px">
                <span class="guideline-label">✅ Diretriz ativa</span>
                <strong>${escapeHtml(g.title)}</strong>
                <p class="muted-text" style="margin:8px 0 0">${escapeHtml(g.content)}</p>
                <div class="guideline-meta">${icon('workspace_premium')} ${escapeHtml(g.authorName || 'Liderança')} · ${relativeTime(g.createdAt)}</div>
              </div>
            `
                  )
                  .join('')
          }
        `
      }
      case 'ideas':
        return `
          <p class="section-title">🗂️ Minhas ideias</p>
          ${
            sortedIdeasForTab().length === 0
              ? emptyState('Você ainda não enviou ideias. Toque em + Nova ideia.', 'lightbulb')
              : sortedIdeasForTab().map(ideaItemHtml).join('')
          }
          <button type="button" class="btn-primary" data-action="new-idea" style="margin-top:18px">+ Nova ideia</button>
        `
      case 'profile': {
        const ideas = getIdeas()
        const approved = ideas.filter((i) => i.status === 'approved' || i.status === 'prioritized').length
        return `
          ${profileContent()}
          <div class="profile-stats">
            ${kpiCard('Ideias enviadas', String(ideas.length))}
            ${kpiCard('Aprovadas', String(approved))}
          </div>
          <div class="profile-insight-card">
            <strong>${icon('auto_awesome')} Próximo passo</strong>
            <p>Envie uma ideia com impacto claro no atendimento ou operação para aumentar suas chances na curadoria.</p>
          </div>
          <p class="muted-text" style="margin-top:8px;text-align:center">
            ${icon('lock')} Seus dados ficam salvos no navegador (mesmo em outra guia).
          </p>
          <button type="button" class="btn-primary btn-danger" data-action="logout" style="margin-top:16px">Sair da conta</button>
        `
      }
      default:
        return ''
    }
  }

  const titles = {
    home: 'Início',
    strategies: 'Estratégias',
    ideas: 'Ideias',
    profile: 'Perfil',
  }

  function build() {
    if (overlay) {
      return `<div class="phone-screen-inner" style="position:relative;flex:1;display:flex;flex-direction:column;min-height:0">${overlayHtml()}</div>`
    }
    return `
      <div style="position:relative;flex:1;display:flex;flex-direction:column;min-height:0">
        ${drawerHtml()}
        ${topBar(titles[tab], { showBadge: hasUnreadNotifications() })}
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

        el.querySelector('[data-action="notifications"]')?.addEventListener('click', () => {
          markNotificationsRead()
          drawerOpen = true
          rerender()
        })

        el.querySelectorAll('[data-action="close-drawer"]').forEach((btn) => {
          btn.addEventListener('click', () => {
            drawerOpen = false
            rerender()
          })
        })

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
          if (name.length < 2) {
            profileError = 'Nome deve ter pelo menos 2 caracteres.'
            rerender()
            return
          }
          if (!email.includes('@')) {
            profileError = 'Informe um e-mail válido.'
            rerender()
            return
          }
          const saved = saveProfile(account.accountKey, {
            fullName: name,
            email,
            avatar: account.avatar,
            avatarColor: account.avatarColor,
          })
          account = {
            ...account,
            ...saved,
          }
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

        el.querySelector('[data-action="all-ideas"]')?.addEventListener('click', () => {
          overlay = 'allIdeas'
          rerender()
        })

        el.querySelectorAll('[data-action="new-idea"]').forEach((btn) => {
          btn.addEventListener('click', () => {
            submitCategory = 'Processo'
            formError = ''
            overlay = 'submit'
            rerender()
          })
        })

        el.querySelector('[data-action="report"]')?.addEventListener('click', () => {
          submitCategory = 'Outro'
          formError = ''
          overlay = 'submit'
          rerender()
        })

        el.querySelectorAll('[data-action="back"]').forEach((btn) => {
          btn.addEventListener('click', () => {
            overlay = null
            formError = ''
            rerender()
          })
        })

        el.querySelectorAll('[data-action="logout"]').forEach((b) =>
          b.addEventListener('click', onLogout)
        )

        const form = el.querySelector('#submit-idea-form')
        if (form) {
          form.addEventListener('submit', (e) => {
            e.preventDefault()
            const title = form.title.value.trim()
            const description = form.description.value.trim()
            const category = form.category.value
            if (title.length < 3) {
              formError = 'Título deve ter pelo menos 3 caracteres.'
              rerender()
              return
            }
            if (description.length < 10) {
              formError = 'Descrição deve ter pelo menos 10 caracteres.'
              rerender()
              return
            }
            addIdea({
              title,
              description,
              category,
              authorEmail: account.email,
              authorName: account.fullName,
              authorId: account.accountKey,
            })
            overlay = null
            formError = ''
            tab = 'home'
            rerender()
          })
        }
      }

      rerender()
    },
  }
}
