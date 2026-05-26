import './login.css'
import { renderLogin } from './screens/login.js'
import { renderOperator } from './screens/operator.js'
import { renderManager } from './screens/manager.js'
import { renderLeader } from './screens/leader.js'
import { mergeAccountWithProfile } from './store.js'
import { applySavedColorTheme, setRoleTheme, toggleColorTheme } from './components.js'
import { animateScreenIn } from './animations.js'

const app = document.getElementById('app')
applySavedColorTheme()

app.addEventListener('click', (event) => {
  const toggle = event.target.closest?.('[data-action="theme-toggle"]')
  if (toggle) toggleColorTheme()
})

function navigate(screen) {
  app.innerHTML = `<div class="phone-screen-inner">${screen.html}</div>`
  const inner = app.firstElementChild
  if (screen.mount) screen.mount(inner)
  animateScreenIn(inner)
}

function showLogin() {
  setRoleTheme(null)
  const screen = renderLogin((account) => {
    const profiled = mergeAccountWithProfile(account)
    setRoleTheme(account.role)
    switch (account.role) {
      case 'operator':
        navigate(renderOperator(profiled, handleLogout))
        break
      case 'manager':
        navigate(renderManager(profiled, handleLogout))
        break
      case 'leader':
        navigate(renderLeader(profiled, handleLogout))
        break
    }
  })
  navigate(screen)
}

function handleLogout() {
  showLogin()
}

showLogin()
