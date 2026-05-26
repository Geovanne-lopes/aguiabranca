/** Espelha AuthCatalog.kt — mesmas contas do app Android */
export const ACCOUNTS = [
  {
    email: 'operador@innovatecorp.com',
    password: 'oper123',
    role: 'operator',
    name: 'Ana',
    fullName: 'Ana Silva',
    accent: 'var(--innovate-operator-blue)',
  },
  {
    email: 'gestor@innovatecorp.com',
    password: 'gest123',
    role: 'manager',
    name: 'Carlos',
    fullName: 'Carlos Mendes',
    accent: 'var(--innovate-manager-pink)',
  },
  {
    email: 'lideranca@innovatecorp.com',
    password: 'lider123',
    role: 'leader',
    name: 'Mariana',
    fullName: 'Mariana Costa',
    accent: 'var(--innovate-leader-purple)',
  },
]

const REGISTERED_USERS_KEY = 'innovatecorp_registered_users'

function loadRegisteredUsers() {
  try {
    return JSON.parse(localStorage.getItem(REGISTERED_USERS_KEY) || '[]')
  } catch {
    return []
  }
}

function saveRegisteredUsers(users) {
  localStorage.setItem(REGISTERED_USERS_KEY, JSON.stringify(users))
}

export function allAccounts() {
  return [...ACCOUNTS, ...loadRegisteredUsers()]
}

export function registerAccount({ fullName, email, password, role }) {
  const cleanEmail = email.trim().toLowerCase()
  if (allAccounts().some((a) => a.email.toLowerCase() === cleanEmail)) {
    return { ok: false, error: 'Já existe uma conta com este e-mail.' }
  }
  const name = fullName.trim().split(' ')[0] || fullName.trim()
  const account = {
    email: cleanEmail,
    password,
    role,
    name,
    fullName: fullName.trim(),
    accent: 'var(--innovate-primary)',
  }
  const users = loadRegisteredUsers()
  users.push(account)
  saveRegisteredUsers(users)
  return { ok: true, account }
}

export function resetPassword(email, newPassword) {
  const cleanEmail = email.trim().toLowerCase()
  const registered = loadRegisteredUsers()
  const idx = registered.findIndex((a) => a.email.toLowerCase() === cleanEmail)
  if (idx >= 0) {
    registered[idx] = { ...registered[idx], password: newPassword }
    saveRegisteredUsers(registered)
    return { ok: true }
  }
  const demo = ACCOUNTS.find((a) => a.email.toLowerCase() === cleanEmail)
  if (demo) {
    const copy = { ...demo, password: newPassword }
    saveRegisteredUsers([...registered.filter((a) => a.email.toLowerCase() !== cleanEmail), copy])
    return { ok: true }
  }
  return { ok: false, error: 'Não encontramos esse e-mail cadastrado.' }
}

export function authenticate(email, password) {
  const trimmed = email.trim().toLowerCase()
  const account = allAccounts().find((a) => a.email.toLowerCase() === trimmed)
  if (!account) {
    return { ok: false, error: 'E-mail não autorizado para acesso.' }
  }
  if (password !== account.password) {
    return { ok: false, error: 'Senha incorreta para este e-mail.' }
  }
  return { ok: true, account }
}
