import { authenticate, ACCOUNTS, registerAccount, resetPassword } from '../auth.js'
import { icon } from '../components.js'
import { animateButtonPress, animateLogin } from '../animations.js'

export function renderLogin(onSuccess) {
  let mode = 'login'
  let recoveryEmail = ''
  let message = ''
  let error = ''

  const aboutSections = [
    {
      emoji: '🛣️',
      title: 'Nossa trajetória',
      image: 'https://images.unsplash.com/photo-1758873269117-d5845126928a?auto=format&fit=crop&w=900&q=70',
      text: 'A InnovateCorp nasceu para transformar ideias do dia a dia em melhorias reais. Cada sugestão vira parte de uma trilha: registro, curadoria, priorização, projeto e aprendizado para o time inteiro.',
    },
    {
      emoji: '🧩',
      title: 'O que fazemos',
      image: 'https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=900&q=70',
      text: 'Conectamos operadores, gestores e liderança em um workspace simples: ideias, orientações, projetos, ranking de participação, colaboradores e chat. Tudo pensado para reduzir ruído e acelerar decisões.',
    },
    {
      emoji: '💬',
      title: 'Feedbacks',
      image: 'https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=900&q=70',
      text: 'Feedback não fica perdido. O gestor consegue orientar, a liderança cria diretrizes e o operador acompanha o que aconteceu com cada ideia. O ciclo fica visível, humano e contínuo.',
    },
  ]

  function loginHtml() {
    return `
    <div class="login-screen">
      <button type="button" class="login-theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">◐</button>
      <div class="login-header">
        <div class="login-logo">⚡</div>
        <h1>InnovateCorp</h1>
        <p>Um workspace vivo para ideias, projetos e decisões.</p>
      </div>
      <form class="login-card" id="login-form">
        <h2>👋 Entrar na sua conta</h2>
        <p class="login-hint">
          Escolha um perfil demo abaixo. O que você editar fica salvo localmente no navegador.
        </p>
        <div class="demo-chips">
          ${ACCOUNTS.map(
            (a) => `
            <button type="button" class="demo-chip" data-email="${a.email}" data-pass="${a.password}">
              ${a.role === 'leader' ? 'Líder' : a.role === 'manager' ? 'Gestor' : 'Operador'}
            </button>
          `
          ).join('')}
        </div>
        <div class="field">
          <label for="email">E-mail</label>
          <input id="email" name="email" type="email" placeholder="seu@email.com" autocomplete="username" />
        </div>
        <div class="field">
          <label for="password">Senha</label>
          <input id="password" name="password" type="password" autocomplete="current-password" />
        </div>
        <button type="button" class="login-forgot">Esqueceu sua senha?</button>
        ${message ? `<div class="login-success">${message}</div>` : ''}
        <div id="login-error" class="login-error" ${error ? '' : 'hidden'}>${error}</div>
        <button type="submit" class="btn-primary" id="login-btn">Entrar</button>
        <p class="login-footer-text">Primeira vez aqui?</p>
        <button type="button" class="login-link" data-action="about">Conheça o InnovateCorp</button>
      </form>
      <p class="login-copyright">© 2026 InnovateCorp. Todos os direitos reservados.</p>
    </div>
  `
  }

  function aboutHtml() {
    return `
      <div class="login-screen about-screen">
        <button type="button" class="login-theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">◐</button>
        <div class="about-hero">
          <button type="button" class="about-back" data-action="back-login">← Voltar</button>
          <span class="about-kicker">⚡ InnovateCorp</span>
          <h1>Um lugar para ideias saírem do corredor e virarem projeto.</h1>
          <p>Conheça o fluxo por trás do app: pessoas, contexto e feedback no mesmo workspace.</p>
        </div>
        <div class="about-sections">
          ${aboutSections
            .map(
              (s) => `
            <article class="about-card reveal-on-scroll">
              <img src="${s.image}" alt="${s.title}" loading="lazy" />
              <div>
                <span class="about-topic">${s.emoji} ${s.title}</span>
                <p>${s.text}</p>
              </div>
            </article>
          `
            )
            .join('')}
        </div>
        <button type="button" class="btn-primary about-register" data-action="register">Criar minha conta</button>
      </div>
    `
  }

  function registerHtml() {
    return `
      <div class="login-screen">
        <button type="button" class="login-theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">◐</button>
        <form class="login-card auth-flow-card" id="register-form">
          <button type="button" class="about-back inline" data-action="about">← Voltar</button>
          <h2>🪪 Criar cadastro</h2>
          <p class="login-hint">Este cadastro é local, sem e-mail externo. Depois você entra normalmente usando o e-mail e senha criados aqui.</p>
          <div class="field"><label>Nome completo</label><input name="fullName" required placeholder="Seu nome" /></div>
          <div class="field"><label>E-mail</label><input name="email" type="email" required placeholder="voce@empresa.com" /></div>
          <div class="field"><label>Senha</label><input name="password" type="password" required minlength="4" /></div>
          <div class="field">
            <label>Perfil</label>
            <select name="role">
              <option value="operator">Operador</option>
              <option value="manager">Gestor</option>
              <option value="leader">Líder</option>
            </select>
          </div>
          ${error ? `<div class="login-error">${error}</div>` : ''}
          <button type="submit" class="btn-primary">Cadastrar e voltar ao login</button>
        </form>
      </div>
    `
  }

  function resetHtml() {
    return `
      <div class="login-screen">
        <button type="button" class="login-theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">◐</button>
        <form class="login-card auth-flow-card" id="reset-form">
          <button type="button" class="about-back inline" data-action="back-login">← Voltar</button>
          <h2>🔐 Alterar senha</h2>
          <p class="login-hint">Sem SMTP e sem envio de e-mail: informe o e-mail cadastrado abaixo e escolha uma nova senha. Tudo fica salvo localmente neste navegador.</p>
          <div class="reset-email-banner">E-mail para recuperação: <strong>${recoveryEmail || 'preencha abaixo'}</strong></div>
          <div class="field"><label>E-mail cadastrado</label><input name="email" type="email" required value="${recoveryEmail}" /></div>
          <div class="field"><label>Nova senha</label><input name="password" type="password" required minlength="4" /></div>
          ${error ? `<div class="login-error">${error}</div>` : ''}
          <button type="submit" class="btn-primary">Salvar nova senha</button>
        </form>
      </div>
    `
  }

  function html() {
    if (mode === 'about') return aboutHtml()
    if (mode === 'register') return registerHtml()
    if (mode === 'reset') return resetHtml()
    return loginHtml()
  }

  return {
    get html() {
      return html()
    },
    mount(root) {
      const rerender = () => {
        root.innerHTML = html()
        bind(root)
      }

      const revealObserver = () => {
        const items = root.querySelectorAll('.reveal-on-scroll')
        if (!items.length) return
        const observer = new IntersectionObserver(
          (entries) => {
            entries.forEach((entry) => {
              if (entry.isIntersecting) entry.target.classList.add('visible')
            })
          },
          { threshold: 0.25 }
        )
        items.forEach((item) => observer.observe(item))
      }

      const bind = (el) => {
      animateLogin(root)
      revealObserver()
      const form = el.querySelector('#login-form')
      const errorEl = el.querySelector('#login-error')
      const btn = el.querySelector('#login-btn')
      const emailEl = el.querySelector('#email')
      const passEl = el.querySelector('#password')

      el.querySelectorAll('[data-action="about"]').forEach((btn) =>
        btn.addEventListener('click', () => {
          mode = 'about'
          error = ''
          rerender()
        })
      )
      el.querySelector('[data-action="register"]')?.addEventListener('click', () => {
        mode = 'register'
        error = ''
        rerender()
      })
      el.querySelectorAll('[data-action="back-login"]').forEach((btn) =>
        btn.addEventListener('click', () => {
          mode = 'login'
          error = ''
          rerender()
        })
      )

      el.querySelector('.login-forgot')?.addEventListener('click', () => {
        recoveryEmail = emailEl?.value?.trim() || ''
        mode = 'reset'
        error = ''
        rerender()
      })

      el.querySelector('#register-form')?.addEventListener('submit', (e) => {
        e.preventDefault()
        const data = new FormData(e.target)
        const result = registerAccount({
          fullName: data.get('fullName'),
          email: data.get('email'),
          password: data.get('password'),
          role: data.get('role'),
        })
        if (!result.ok) {
          error = result.error
          rerender()
          return
        }
        mode = 'login'
        message = 'Cadastro criado. Agora entre usando seu e-mail e senha.'
        error = ''
        rerender()
      })

      el.querySelector('#reset-form')?.addEventListener('submit', (e) => {
        e.preventDefault()
        const data = new FormData(e.target)
        const result = resetPassword(data.get('email'), data.get('password'))
        if (!result.ok) {
          error = result.error
          rerender()
          return
        }
        recoveryEmail = data.get('email')
        mode = 'login'
        message = `Senha alterada para ${recoveryEmail}. Faça login com a nova senha.`
        error = ''
        rerender()
      })

      el.querySelectorAll('.demo-chip').forEach((chip) =>
        chip.addEventListener('click', () => {
          animateButtonPress(chip)
          emailEl.value = chip.dataset.email
          passEl.value = chip.dataset.pass
          errorEl.hidden = true
        })
      )

      form?.addEventListener('submit', (e) => {
        e.preventDefault()
        const email = emailEl.value
        const password = passEl.value

        if (!email.includes('@')) {
          errorEl.textContent = 'Informe um e-mail válido.'
          errorEl.hidden = false
          return
        }
        if (!password) {
          errorEl.textContent = 'Informe a senha.'
          errorEl.hidden = false
          return
        }

        btn.disabled = true
        btn.textContent = 'Entrando…'
        animateButtonPress(btn)

        setTimeout(() => {
          const result = authenticate(email, password)
          btn.disabled = false
          btn.textContent = 'Entrar'

          if (!result.ok) {
            errorEl.textContent = result.error
            errorEl.hidden = false
            return
          }
          errorEl.hidden = true
          onSuccess(result.account)
        }, 320)
      })
      }

      bind(root)
    },
  }
}
