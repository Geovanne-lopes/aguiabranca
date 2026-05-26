# Challenge Águia Branca — Gestão de Inovação Corporativa

App Android nativo (Kotlin + Jetpack Compose) para três perfis: **Operador**, **Gestor** e **Líder**.

> **Regra de trabalho:** antes de implementar cada funcionalidade, consultar este README, marcar a etapa atual e implementar **uma parte por vez**.

---

## Stack (obrigatória)

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | MVVM + Clean Architecture (Data / Domain / UI) |
| DI | **Koin** (leve, sem KAPT; Hilt conflitou com AGP 9 + plugin Compose-only do template) |
| Async | Coroutines + Flow |
| Rede | Retrofit + OkHttp + Kotlinx Serialization |
| Persistência local | Room (ideias, projetos, diretrizes, sessão) ✅ Etapa 3 |
| Navegação | Navigation Compose |

---

## APIs externas

| Uso | Endpoint | Observação |
|-----|----------|------------|
| Usuários / login mock | `https://fakerapi.it/api/v1/users?_quantity=3` | Mapear 3 perfis fixos (Operador, Gestor, Líder) |
| Insight do dia | `https://api.adviceslip.com/advice` | Card na Home do Operador |

---

## Estrutura de pacotes (Clean + MVVM)

```
br.com.fiap.challengeaguiabranca/
├── InnovationApplication.kt        # Inicialização Koin (Etapa 2)
├── di/                             # Módulos Koin (Network, Database, Repository)
├── data/
│   ├── local/                      # Room DAO, entities
│   ├── remote/
│   │   ├── api/                    # Retrofit services
│   │   ├── dto/                    # Respostas da API
│   │   └── mapper/                 # DTO → Domain
│   └── repository/                 # Implementações dos contratos
├── domain/
│   ├── model/                      # Entidades de negócio ✅ Etapa 1
│   ├── repository/                 # Interfaces (contratos)
│   └── usecase/                    # Casos de uso por feature
└── ui/
    ├── theme/                      # Material Theme
    ├── navigation/                 # NavGraph, rotas, guards por perfil
    ├── components/                 # Composables reutilizáveis
    └── feature/
        ├── auth/                   # Login e seleção de perfil
        ├── operator/
        │   ├── home/               # Diretrizes RO + Insight
        │   └── ideas/              # CRUD ideias (próprias)
        ├── manager/
        │   ├── curation/           # Aprovar / Reprovar / Priorizar
        │   ├── projects/             # CRUD projetos + progresso
        │   └── guidelines/         # Leitura diretrizes
        └── leader/
            ├── guidelines/         # CRUD diretrizes
            ├── tracking/           # Status de todos os projetos
            └── dashboard/          # ROI, lucro, investimento, produtividade
```

---

## Matriz de funcionalidades

### Operador (Operacional)
- [x] Home: diretrizes (read-only) + Insight do Dia (API)
- [x] Ideias: cadastro (título, descrição, categoria) + listagem própria

### Gestor (Tático)
- [x] Home/Dashboard: KPIs reais (mês vs mês), gráfico de barras, atalho para pendentes
- [x] Curadoria: listar todas as ideias + Aprovar / Reprovar / Priorizar
- [x] Projetos: CRUD a partir de ideias aprovadas/priorizadas + status + custos + resultados
- [x] Orientações: consulta das diretrizes da liderança (read-only)

### Líder (Estratégico)
- [x] Diretrizes: CRUD exclusivo
- [x] Acompanhamento: todos os projetos (investimento, prazo, status)
- [x] Dashboard ROI: cards/gráficos com dados locais + inputs dos gestores

### Infraestrutura transversal
- [x] **Etapa 1:** Estrutura, Gradle, modelos de domínio
- [x] **Etapa 2:** DI (Koin), Retrofit, módulos de rede, DTOs Faker + Advice
- [x] **Etapa 3:** Room + repositórios + casos de uso base
- [x] **Etapa 4:** Auth mock (login por perfil) + NavGraph por role
- [x] **Etapa 5:** Feature Operador (Home + Ideias)
- [x] **Etapa 6:** Feature Gestor (Dashboard + Curadoria + Orientações + Projetos)
- [x] **Etapa 7:** Feature Líder (Diretrizes + Acompanhamento + Dashboard ROI)
- [ ] **Etapa 8:** Testes unitários críticos + polish UI + validação final

---

## Referência visual (Figma / WhatsApp)

Arquivos locais: `D:\Users\Pamela\AppData\Downloads\WhatsApp Unknown 2026-05-22 at 00.05.41`  
Cópia no workspace: pasta `assets/` do projeto.

**Regra:** telas Compose só após sua confirmação por etapa de UI. Esta seção é mapa funcional, não implementação.

| Tela Figma | Perfil | O que implementar no app | Etapa prevista |
|------------|--------|--------------------------|----------------|
| Login InnovateCorp | Auth | E-mail/senha vinculados ao perfil (sem escolha posterior) | 4 |
| ~~Selecione seu perfil~~ | — | **Removido** — perfil definido pelo login | — |
| Início (Maria, nível/pontos) | Operador | Home, ações rápidas, KPIs, ideias recentes, insight API | 5 |
| Dashboard Gestor | Gestor | KPIs, gráfico ideias/mês, resumo pendentes/projetos | 6 |
| Dashboard Executivo (cards + gráficos) | Líder | Métricas ROI, tendência, alinhamento estratégico | 7 |

**Extras opcionais (implementados):**
- [x] Operador — aba **Estratégias** (lista completa de diretrizes)
- [x] Operador — aba **Perfil** (dados, gamificação, resumo, logout)
- [x] Gestor — aba **Perfil** na bottom nav (resumo tático + logout)
- [x] Gamificação com barra de progresso (derivada da quantidade de ideias enviadas)

**Observações (requisito vs. extra Figma):**
- Gamificação (nível/pontos): **extra visual** — calculado localmente a partir das ideias do operador.
- "Reportar Problema" vs "Nova Ideia": mesma entidade `Idea`, categoria/default diferente.
- Gráficos (linha/barra): dados mockados/agregados de Room na Etapa 7; biblioteca de charts na UI.
- Brand "InnovateCorp": `strings.xml` na Etapa 4/5.

---

## Etapa 1 — Concluída

- [x] README, Gradle, modelos de domínio, pastas base

---

## Etapa 2 — Concluída

- [x] Koin: `networkModule`, `dataModule`, `appModules`
- [x] Retrofit: `FakerApiService`, `AdviceApiService`
- [x] DTOs + `UserMapper`, `InsightMapper`
- [x] `UserRepository` / `InsightRepository` + implementações
- [x] Modelo `DailyInsight`
- [x] Smoke test DEBUG → Logcat tag **`RemoteBootstrap`**

**Validar:** rodar o app com internet → Logcat filtrar `RemoteBootstrap` (3 usuários + 1 insight).

---

## Etapa 3 — Concluída

### Room (`innovation_database`)
| Tabela | Entidade | DAO |
|--------|----------|-----|
| `ideas` | `IdeaEntity` | `IdeaDao` |
| `projects` | `ProjectEntity` | `ProjectDao` |
| `strategic_guidelines` | `StrategicGuidelineEntity` | `StrategicGuidelineDao` |
| `user_session` | `SessionEntity` (linha única id=1) | `SessionDao` |

### Repositórios locais
- `IdeaRepository`, `ProjectRepository`, `GuidelineRepository`, `SessionRepository`

### Casos de uso base (`domain/usecase/`)
- **Sessão:** salvar, observar, limpar, `isLoggedIn`
- **Ideias:** `SubmitIdea`, listar, `UpdateIdeaStatus`, contar pendentes
- **Projetos:** criar a partir de ideia aprovada, atualizar, listar, contar ativos
- **Diretrizes:** CRUD + observar
- **Dashboard:** `GetRoiDashboardSummary`

### DEBUG
- `DatabaseSeeder` — 2 diretrizes de exemplo se o banco estiver vazio
- Logcat tag **`LocalBootstrap`** (contagens + ROI)

**Validar:** rodar app → Logcat `LocalBootstrap` → `diretrizes=2` na primeira execução.

---

## Etapa 4 — Concluída

### Autenticação (1 conta = 1 perfil)
Cada e-mail autorizado mapeia para um único `UserRole`. Não há tela de escolha de perfil após o login.

| Perfil | E-mail | Senha |
|--------|--------|-------|
| Operador | `operador@innovatecorp.com` | `oper123` |
| Gestor | `gestor@innovatecorp.com` | `gest123` |
| Liderança | `lideranca@innovatecorp.com` | `lider123` |

Após validar credenciais, o app busca na FakerAPI o usuário do **mesmo perfil** (nome/avatar) e salva a sessão no Room.

### Telas
- **Login** InnovateCorp
- **Homes placeholder** — Operador / Gestor / Líder

### Navegação
- Login → home do perfil autenticado (direto)
- Sessão salva: reabrir app vai direto à home do último perfil

### MVVM
- `LoginViewModel` + `AuthenticateUserUseCase`
- `RoleHomeViewModel`

**Fluxo de teste:** login com conta do perfil → home correta → Sair → outro perfil exige **outro e-mail/senha**.

### Etapa 5 — Operador ✅
- **Home:** banner, KPIs, insight, diretriz, ideias recentes
- **Ideias:** formulário (título, descrição, categoria), listagem própria (Room), FAB, status
- Atalhos da Home: Nova Ideia, Reportar Problema (categoria Outro), Ver todas → aba Ideias

### Etapa 6 — Gestor ✅
- **Shell:** `ManagerMainScreen` + bottom nav + logout
- **Dashboard, Curadoria, Orientações, Projetos CRUD**

### Etapa 7 — Líder ✅
- **Shell:** `LeaderMainScreen` + bottom nav (Executivo, Diretrizes, Acompanhamento)
- **Dashboard ROI:** KPIs e gráfico por status (dados reais de projetos)
- **Diretrizes:** CRUD exclusivo
- **Acompanhamento:** lista de projetos com investimento, lucro, ROI, prazo

**Próximo:** Etapa 8 — testes unitários + polish UI.

---

## Sprint 2 — Dá para adaptar este projeto?

**Sim.** A base atual foi pensada para trocar a **fonte de dados**, não refazer o app do zero.

| Hoje (Sprint 1 / protótipo) | Sprint 2 (alvo) | Esforço |
|-----------------------------|-----------------|---------|
| `AuthCatalog` + Room sessão | `POST /auth/login` → JWT + `role` | Médio — trocar `AuthenticateUserUseCase` + interceptor OkHttp |
| `IdeaRepositoryImpl` (Room) | `IdeaRepositoryApiImpl` (Retrofit) | Médio — **mesma interface** `IdeaRepository` |
| `ProjectRepositoryImpl` (Room) | API + opcional cache Room | Médio |
| `GuidelineRepositoryImpl` (Room) | API + RBAC no servidor | Médio |
| FakerAPI (nome/avatar) | Backend devolve `User` no login | Baixo |
| Advice Slip (insight) | Mantém externo ou proxy no backend | Baixo |
| NavGraph por `UserRole` | **Mantém** | Baixo |
| ViewModels / Use Cases | **Mantém** (chamam repositório) | Baixo |
| Telas Compose | **Mantém** (ajuste de estados/erros HTTP) | Baixo–médio |

**O que NÃO joga fora:** `domain/model`, `domain/repository` (contratos), `domain/usecase`, `ui/feature/*`, navegação por perfil, tema.

**O que cresce (novo repo ou módulo):** API REST própria (Spring Boot, Node, etc.) com auth, CRUD e **restrições por role**.

**Risco se ignorar agora:** colocar regra de negócio só na UI (ex.: botão “Aprovar” escondido) sem 403 no backend — na Sprint 2 a correção é no servidor.

---

## Matriz RBAC (referência Sprint 2)

Legenda: ✅ permitido · ❌ proibido · 🔶 permitido com escopo limitado

### 1) Funcionalidades × perfil (regra de negócio)

| Funcionalidade | Operador | Gestor | Líder |
|----------------|:--------:|:------:|:-----:|
| Login (conta própria) | ✅ | ✅ | ✅ |
| Ver insight do dia (API externa) | ✅ | 🔶 | 🔶 |
| Ler diretrizes estratégicas | ✅ | ✅ | ✅ |
| Criar/editar/excluir diretrizes | ❌ | ❌ | ✅ |
| Cadastrar ideia / problema | ✅ | 🔶 | 🔶 |
| Listar **próprias** ideias | ✅ | ✅ | ✅ |
| Listar **todas** ideias (curadoria) | ❌ | ✅ | ✅ |
| Aprovar / reprovar / priorizar ideia | ❌ | ✅ | 🔶 |
| Criar projeto a partir de ideia aprovada | ❌ | ✅ | ❌ |
| Atualizar projeto (status, custos, ROI inputs) | ❌ | ✅ | 🔶 |
| Listar todos os projetos (acompanhamento) | ❌ | ✅ | ✅ |
| Dashboard ROI / métricas agregadas | ❌ | 🔶 | ✅ |

🔶 = definir no enunciado Sprint 2 (ex.: líder só leitura em curadoria, gestor vê ROI resumido).

### 2) Endpoints REST sugeridos (backend Sprint 2)

Base: `/api/v1` — header: `Authorization: Bearer <token>`

| Método | Endpoint | Operador | Gestor | Líder |
|--------|----------|:--------:|:------:|:-----:|
| `POST` | `/auth/login` | ✅ | ✅ | ✅ |
| `GET` | `/auth/me` | ✅ | ✅ | ✅ |
| `GET` | `/insights/daily` | ✅ | ✅ | ✅ |
| `GET` | `/guidelines` | ✅ | ✅ | ✅ |
| `POST` | `/guidelines` | ❌ | ❌ | ✅ |
| `PUT` | `/guidelines/{id}` | ❌ | ❌ | ✅ |
| `DELETE` | `/guidelines/{id}` | ❌ | ❌ | ✅ |
| `GET` | `/ideas` | 🔶 só `?mine=true` | ✅ todas | ✅ todas |
| `POST` | `/ideas` | ✅ | ✅ | ✅ |
| `GET` | `/ideas/{id}` | 🔶 se autor | ✅ | ✅ |
| `PATCH` | `/ideas/{id}/status` | ❌ | ✅ | 🔶 |
| `GET` | `/projects` | ❌ | ✅ | ✅ |
| `POST` | `/projects` | ❌ | ✅ | ❌ |
| `PUT` | `/projects/{id}` | ❌ | ✅ | 🔶 |
| `GET` | `/dashboard/roi` | ❌ | 🔶 | ✅ |

Respostas esperadas ao violar perfil: **`403 Forbidden`** (corpo com código, ex.: `ROLE_NOT_ALLOWED`).

### 3) O que o app Android faz em cada camada

| Camada | Sprint 1 (hoje) | Sprint 2 |
|--------|-----------------|----------|
| **UI** | Esconde rotas/botões por `UserRole` | Igual + trata `403` com mensagem clara |
| **Domain** | Use cases sem saber origem dos dados | Igual |
| **Data** | Room + APIs públicas | Retrofit → **seu backend** (+ Room opcional como cache) |
| **Auth** | `AuthCatalog` local | Token JWT persistido; role vinda do backend |

### 4) Checklist de migração (quando a Sprint 2 abrir)

- [ ] Backend: usuários, login, JWT com claim `role`
- [ ] Middleware/guard por role em **cada** rota da tabela acima
- [ ] App: `AuthApi`, `TokenStorage` (DataStore), interceptor Bearer
- [ ] App: implementações `*RepositoryImpl` remotas mantendo interfaces
- [ ] Teste Postman: operador recebe 403 em `PATCH /ideas/{id}/status`
- [ ] Logs/auditoria no backend (quem aprovou ideia, quando) — governança do slide
- [ ] Manter Advice Slip (externo) ou expor via `GET /insights/daily` no backend

---

## Critérios de aceite (desafio)

- Duas APIs REST consumidas de forma funcional
- Três perfis com fluxos distintos e navegação coerente
- MVVM visível (ViewModel + UI state + Repository)
- Código legível, camadas separadas, sem lógica de negócio na Composable

---

## Como rodar

1. Abrir no Android Studio (Giraffe ou superior).
2. Sync Gradle.
3. Executar em emulador/dispositivo API 28+.

### Preview no navegador (somente visualização)

O app Android em `app/` **não é alterado**. A pasta `web-preview/` espelha login, cores e telas principais para você navegar no browser.

**Requisito:** [Node.js](https://nodejs.org/) instalado.

```powershell
# Na raiz do projeto
.\preview.ps1
```

Ou manualmente:

```bash
cd web-preview
npm install
npm run dev
```

Abre em **http://localhost:5173**. Use as mesmas contas demo do login Android (`operador@…`, `gestor@…`, `lideranca@…`).

---

## Notas acadêmicas

- Código gerado com assistência de IA deve ser **entendido, testado e adaptado** antes da entrega.
- Validar com o enunciado/rubrica da FIAP uso de IA e escopo em grupo.
