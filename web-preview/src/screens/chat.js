import {
  icon,
  escapeHtml,
  overlayTopBar,
  emptyState,
  relativeTime,
  initialsAvatar,
  roleColor,
  rolePill,
  roleLabel,
} from '../components.js'
import {
  getCollaborators,
  getCollaboratorIdeas,
  getThreadMessages,
  sendMessage,
  statusLabel,
  statusClass,
} from '../store.js'
import { animateButtonPress, animateChatUpdate } from '../animations.js'

/**
 * Cria o módulo de colaboradores + chat reutilizável.
 *
 * @param {Object} options
 * @param {Object} options.currentUser - { id, accountKey, fullName, name, role }
 * @param {Function} options.onClose    - chamada quando o overlay fecha
 * @returns API com {html(view), bind(el), open(view), state()}
 */
export function createCollaboratorsModule({ currentUser, onClose }) {
  let view = 'list' // 'list' | 'detail' | 'chat'
  let activeCollaborator = null
  let chatRefreshTimer = null

  function listView() {
    const list = getCollaborators(currentUser.accountKey)
    return `
      <div class="overlay-screen">
        ${overlayTopBar('👥 Colaboradores')}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">
            Um painel de pessoas como no Notion: veja ideias, contexto e converse sem sair do fluxo.
          </p>
          ${
            list.length === 0
              ? emptyState('Nenhum colaborador disponível.', 'groups')
              : list.map(collaboratorCard).join('')
          }
        </div>
      </div>
    `
  }

  function collaboratorCard(c) {
    const ideas = getCollaboratorIdeas(c)
    return `
      <div class="collab-card" data-collab-id="${c.id}">
        ${initialsAvatar(c.name, roleColor(c.role))}
        <div class="collab-info">
          <strong>${escapeHtml(c.name)}</strong>
          <div class="meta">${escapeHtml(c.email)}</div>
          ${rolePill(c.role)}
          <div class="meta" style="margin-top:6px">
            💡 ${ideas.length} ideia(s)
          </div>
        </div>
        <div class="actions">
          <button type="button" data-action="open-chat" data-collab-id="${c.id}" aria-label="Conversar">
            ${icon('chat')}
          </button>
        </div>
      </div>
    `
  }

  function detailView() {
    if (!activeCollaborator) return listView()
    const ideas = getCollaboratorIdeas(activeCollaborator)
    return `
      <div class="overlay-screen">
        ${overlayTopBar(activeCollaborator.name)}
        <div class="screen-scroll">
          <div class="collab-detail-header">
            ${initialsAvatar(activeCollaborator.name, roleColor(activeCollaborator.role), 84)}
            <strong>${escapeHtml(activeCollaborator.name)}</strong>
            <p>${escapeHtml(activeCollaborator.email)}</p>
            <span class="role-pill">${roleLabel(activeCollaborator.role)}</span>
            <button type="button" class="chat-btn" data-action="open-chat" data-collab-id="${activeCollaborator.id}">
              ${icon('chat')} Conversar
            </button>
          </div>
          <p class="section-title">💡 Ideias enviadas</p>
          ${
            ideas.length === 0
              ? emptyState('Este colaborador ainda não enviou ideias.', 'lightbulb')
              : ideas
                  .map(
                    (i) => `
                <div class="idea-item">
                  <strong>${escapeHtml(i.title)}</strong>
                  <div class="meta">${escapeHtml(i.category)} · ${relativeTime(i.createdAt)}</div>
                  <p>${escapeHtml(i.description || '')}</p>
                  <span class="status ${statusClass(i.status)}">${statusLabel(i.status)}</span>
                </div>
              `
                  )
                  .join('')
          }
        </div>
      </div>
    `
  }

  function chatView() {
    if (!activeCollaborator) return listView()
    const messages = getThreadMessages(currentUser.id, activeCollaborator.id)
    return `
      <div class="chat-screen">
        <header class="chat-header">
          <button type="button" class="icon-btn" data-action="back-from-chat" aria-label="Voltar">${icon('arrow_back')}</button>
          <div class="chat-header-info">
            ${initialsAvatar(activeCollaborator.name, roleColor(activeCollaborator.role), 38)}
            <div>
              <strong>${escapeHtml(activeCollaborator.name)}</strong>
              <div class="meta">${escapeHtml(roleLabel(activeCollaborator.role) || '')}${activeCollaborator.isMock ? ' · responde automaticamente' : ''}</div>
            </div>
          </div>
        </header>
        <div class="chat-body" id="chat-body">
          ${
            messages.length === 0
              ? `<p class="chat-empty">${icon('forum')}<br>Diga olá para começar a conversa.</p>`
              : messages
                  .map(
                    (m) => `
                <div class="chat-bubble ${m.fromId === currentUser.id ? 'me' : 'them'}">
                  ${escapeHtml(m.text)}
                  <time>${relativeTime(m.createdAt)}</time>
                </div>
              `
                  )
                  .join('')
          }
        </div>
        <form class="chat-composer" id="chat-form">
          <input type="text" id="chat-input" placeholder="Escreva uma mensagem..." autocomplete="off" />
          <button type="submit" class="send-btn" aria-label="Enviar">${icon('send')}</button>
        </form>
      </div>
    `
  }

  function html() {
    if (view === 'chat') return chatView()
    if (view === 'detail') return detailView()
    return listView()
  }

  function findCollaborator(id) {
    return getCollaborators(currentUser.accountKey).find((c) => c.id === id) || null
  }

  function bind(el, rerender) {
    // Click em card → abrir detalhe
    el.querySelectorAll('.collab-card').forEach((card) => {
      card.addEventListener('click', (e) => {
        if (e.target.closest('[data-action]')) return
        animateButtonPress(card)
        activeCollaborator = findCollaborator(card.dataset.collabId)
        view = 'detail'
        rerender()
      })
    })

    el.querySelectorAll('[data-action="open-chat"]').forEach((btn) => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation()
        animateButtonPress(btn)
        activeCollaborator = findCollaborator(btn.dataset.collabId)
        view = 'chat'
        rerender()
        setTimeout(scrollChatToBottom, 30)
      })
    })

    el.querySelector('[data-action="back-from-chat"]')?.addEventListener('click', () => {
      view = activeCollaborator ? 'detail' : 'list'
      rerender()
    })

    el.querySelectorAll('[data-action="back"]').forEach((btn) =>
      btn.addEventListener('click', () => {
        if (view === 'detail') {
          view = 'list'
          rerender()
        } else if (view === 'list') {
          stopChatTimer()
          onClose?.()
        }
      })
    )

    const form = el.querySelector('#chat-form')
    if (form && activeCollaborator) {
      form.addEventListener('submit', (e) => {
        e.preventDefault()
        const input = form.querySelector('#chat-input')
        const text = input.value.trim()
        if (!text) return
        sendMessage({
          fromId: currentUser.id,
          fromName: currentUser.fullName,
          toId: activeCollaborator.id,
          toName: activeCollaborator.name,
          text,
        })
        input.value = ''
        rerender()
        setTimeout(() => {
          scrollChatToBottom()
          animateChatUpdate(document)
        }, 30)
      })
    }

    // Auto-refresh enquanto está no chat (para receber respostas dos mocks)
    stopChatTimer()
    if (view === 'chat') {
      chatRefreshTimer = window.setInterval(() => {
        rerender()
        setTimeout(() => {
          scrollChatToBottom()
          animateChatUpdate(document)
        }, 30)
      }, 1200)
    }
  }

  function scrollChatToBottom() {
    const body = document.getElementById('chat-body')
    if (body) body.scrollTop = body.scrollHeight
  }

  function stopChatTimer() {
    if (chatRefreshTimer) {
      window.clearInterval(chatRefreshTimer)
      chatRefreshTimer = null
    }
  }

  return {
    html,
    bind,
    stop: stopChatTimer,
    isActive: () => view !== 'list' || activeCollaborator != null,
    reset() {
      view = 'list'
      activeCollaborator = null
      stopChatTimer()
    },
  }
}
