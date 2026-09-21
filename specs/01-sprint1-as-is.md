# 01 — Sprint 1 as-is, contratos e gaps

Auditoria feita no código-fonte, README e `entregas/documentacao/documentacao-tecnica.pdf`.  
Quando documentação e código divergem, **o código vence**.

## 1. O que existe de verdade

### 1.1 Aplicativo Android

- Pacote: `br.com.fiap.challengeaguiabranca`
- `minSdk` 28, `targetSdk` 36, Kotlin 2.2.10, AGP 9.1.1
- Compose BOM 2026.05.00, Material 3, Navigation Compose, Koin 4.0.2
- Room 2.7.1, DataStore 1.1.7, Retrofit 2.11, OkHttp 4.12
- Arquitetura: UI → ViewModel → Use Case → Repository interface → Data (Room ou Retrofit)

### 1.2 Camadas

| Camada | Responsabilidade real |
|--------|------------------------|
| `ui/` | Compose, ViewModels, StateFlow, navegação por role |
| `domain/model` | Data classes puras |
| `domain/repository` | Contratos |
| `domain/usecase` | Regras locais (create project, submit idea, ROI) |
| `data/local` | Room + DataStore |
| `data/remote` | Apenas FakerAPI e AdviceSlip |
| `di/` | Koin: network, database, data, use case, view model |

A UI **não** chama DAO/Retrofit direto. Isso deve ser preservado.

### 1.3 Modelos de domínio (código)

**User**

- `id: String` (UUID da FakerAPI após login)
- `name`, `email`, `avatarUrl: String?`, `role: UserRole`

**UserRole:** `OPERATOR`, `MANAGER`, `LEADER`

**Idea**

- `id`, `title`, `description`, `category: IdeaCategory`, `authorId`, `status: IdeaStatus = PENDING`, `createdAtEpochMillis`
- **Não possui** `guidelineId`, justificativa, histórico, `updatedAt`

**IdeaStatus:** `PENDING`, `APPROVED`, `REJECTED`, `PRIORITIZED`

**IdeaCategory:** `PROCESS`, `PRODUCT`, `TECHNOLOGY`, `SUSTAINABILITY`, `OTHER`

**Project**

- `id`, `ideaId`, `title`, `description`
- `status: ProjectStatus = BACKLOG`
- `investmentAmount`, `obtainedProfit`, `productivityGainPercent` (0–100)
- `deadlineEpochMillis: Long?`
- `managerId`, `createdAtEpochMillis`, `updatedAtEpochMillis`
- Propriedade calculada `roiPercent`
- **Não possui** `guidelineId` nem etapa separada do status

**ProjectStatus** (é status **e** etapa visual; preservar nomes):

| Enum | Label UI |
|------|----------|
| `BACKLOG` | Planejamento |
| `IN_DEVELOPMENT` | Em execução |
| `URGENT_DEADLINE` | Prazo urgente |
| `AVERAGE_TICKET` | Ticket médio |
| `COMPLETED` | Concluído |

**StrategicGuideline**

- `id`, `title`, `content`, `authorId`, `createdAtEpochMillis`, `updatedAtEpochMillis`
- **Não possui** categoria, campanha, versão, histórico

**ManagerSuggestion** (DataStore, não Room)

- `id`, `managerName`, `targetAuthorId`, `targetEmail`, `targetName`, `message`, `createdAtEpochMillis`

**DailyInsight:** `id: Int`, `message: String` (AdviceSlip)

**RoiDashboardSummary:** `totalInvestment`, `totalObtainedProfit`, `overallRoiPercent`, `averageProductivityGainPercent`, `activeProjectsCount`, `completedProjectsCount`

**OperatorActivity:** ranking (`authorId`, `name`, `email`, `ideasSubmitted`, `ideasApproved`)

Notificações (`OperatorNotification`, `ManagerNotification`) são **objetos de UI derivados**, não persistidos.

### 1.4 Persistência local

Room `innovation_database` v1 (`exportSchema = false`, `fallbackToDestructiveMigration`):

| Tabela | Entity |
|--------|--------|
| `ideas` | `IdeaEntity` |
| `projects` | `ProjectEntity` |
| `strategic_guidelines` | `StrategicGuidelineEntity` |
| `user_session` | `SessionEntity` (linha única `id = 1`) |

DataStore `manager_suggestions`: JSON de sugestões.

`DatabaseSeeder` (DEBUG): 2 diretrizes + ideias mock com `authorId` do `MockOperatorsCatalog` (`mock-op-joao`, etc.).

### 1.5 Repositórios (contratos a manter no Android)

```
IdeaRepository
  observeAll / observeByAuthor / observeByStatus / getById
  countByStatus / insert / update / updateStatus / delete

ProjectRepository
  observeAll / observeByStatus / getById / getByIdeaId
  insert / update / delete

GuidelineRepository
  observeAll / getById / insert / update / delete

SessionRepository
  observeCurrentUser / getCurrentUser / saveSession / clearSession / isLoggedIn

UserRepository
  fetchSeedUsers()          // Sprint 2: substituir ou deprecar

InsightRepository
  fetchDailyInsight()

ManagerSuggestionRepository
  observeAll / observeForTarget / insert
```

Implementações atuais: `*Impl` em Room/DataStore/Retrofit público.

### 1.6 Casos de uso existentes

Auth: `AuthenticateUserUseCase`, `FetchSeedUsersUseCase`  
Session: save/get/observe/clear/isLoggedIn, `UpdateUserProfileUseCase`  
Ideas: `SubmitIdeaUseCase`, observe by author / all, `UpdateIdeaStatusUseCase`, pending count  
Projects: observe all, `CreateProjectFromIdeaUseCase`, update, delete, active count  
Guidelines: observe, create, update, delete  
Dashboard: `GetRoiDashboardSummaryUseCase`  
Manager: ranking geral, ranking mensal, send/observe suggestions  
Insight: `FetchDailyInsightUseCase`

### 1.7 Autenticação Sprint 1

1. E-mail/senha contra `AuthCatalog` (memória + 3 contas base).
2. `GET https://fakerapi.it/api/v1/users?_quantity=3`
3. `UserMapper` atribui roles por **índice** (0=OPERATOR, 1=MANAGER, 2=LEADER).
4. Copia o e-mail da conta local para o `User` da FakerAPI.
5. Persiste `SessionEntity` no Room.
6. `InnovationApp` redireciona para `Routes.homeForRole(role)` se já logado.

Login UI extra (in-memory, perde no restart salvo overlay de senha):

- Cadastro local (`AuthCatalog.register`) com escolha de role
- Reset de senha local (`AuthCatalog.resetPassword`)

Senha mínima na UI de cadastro/reset: **4 caracteres**. Título ideia ≥ 3. Descrição ≥ 10. Diretriz conteúdo ≥ 10. Sugestão ≥ 10. Produtividade 0–100.

### 1.8 Navegação

Rotas reais no `InnovationNavGraph`: `login`, `operator_home`, `manager_home`, `leader_home`.

`Routes.PROFILE_SELECTION` e `ProfileSelectionScreen` **existem no código mas não estão no NavGraph**. Login já define o perfil. Não reativar.

### 1.9 Telas por perfil (código)

**Operador** — tabs: HOME, STRATEGIES, IDEAS, PROFILE  
Home: gamificação local, KPIs próprios, insight AdviceSlip, diretriz em destaque, ideias recentes, notificações derivadas, overlays (submit, all ideas, collaborators).  
Ideias: cria e lista **somente as próprias** (`observeByAuthor(user.id)`). Sem editar/excluir na UI.  
Perfil: edita nome/e-mail/avatar **somente na sessão Room** (não há API).

**Gestor** — tabs: HOME, CURATION, PROJECTS, GUIDELINES, PROFILE  
Dashboard: pendentes, projetos ativos, ideias do mês, taxa de aprovação, barras **mensais** (últimos 5 meses).  
Curadoria: lista **todas** as ideias; Aprovar / Reprovar / Priorizar **sem justificativa** e **sem restrição de transição** no use case.  
Projetos: cria a partir de ideia `APPROVED` ou `PRIORITIZED` ainda sem projeto; edita status, investimento, lucro, produtividade, prazo (dias); exclui projeto.  
Pode **criar ideia** (`ManagerCreateIdeaViewModel`).  
Sugestões: enviadas a **operadores** do `MockOperatorsCatalog`, não à liderança.  
Time: ranking por ideias.

**Líder** — tabs: HOME, GUIDELINES, TEAM, TRACKING, PROFILE  
Dashboard: investimento, ROI, lucro, produtividade média, total de projetos, gráfico **por status**.  
Diretrizes: CRUD exclusivo na UI.  
Acompanhamento: leitura de todos os projetos (investimento, lucro, ROI, produtividade, prazo).  
Ranking mensal e geral. Overlay de sugestão (mesma tela do gestor → operadores) e colaboradores.

### 1.10 Integrações externas

| Fornecedor | Endpoint | Auth | Uso real |
|------------|----------|------|----------|
| FakerAPI | `GET https://fakerapi.it/api/v1/users?_quantity=3` | nenhuma | nome/avatar no login |
| AdviceSlip | `GET https://api.adviceslip.com/advice` | nenhuma | card Insight do Dia do operador |

Timeouts OkHttp: connect/read 30s. Logging BODY só em DEBUG.

### 1.11 Regras de negócio já no domínio

`CreateProjectFromIdeaUseCase`:

- exige `APPROVED` ou `PRIORITIZED`
- se já existe projeto com o mesmo `ideaId`, **retorna o existente** (idempotente)

ROI (código):

```
roiPercent = investment > 0 ? ((obtainedProfit - investment) / investment) * 100 : 0
overallRoi = totalInvestment > 0 ? ((totalProfit - totalInvestment) / totalInvestment) * 100 : 0
active = status != COMPLETED
completed = status == COMPLETED
avgProductivity = média de productivityGainPercent de todos os projetos
```

Taxa de aprovação do gestor (UI): `(APPROVED + PRIORITIZED) / totalIdeas`.

Gamificação (UI, extra): nível = min(5, 1 + ideasCount/2); pontos = 250 + ideasCount*200.

### 1.12 Chat / colaboradores

`CollaboratorsChatScreen` é **100% mock**:

- lista hardcoded + `MockOperatorsCatalog`
- mensagens só em memória
- respostas enlatadas (`cannedReply`)

Não há repository, DAO nem API. **Sprint 2 não cria backend de chat.**

### 1.13 Testes

Somente placeholders. Etapa 8 não feita.

---

## 2. Gaps documentação vs código

| Afirmação (PDF/README) | Código real | Classificação |
|------------------------|-------------|---------------|
| Gestor envia sugestões **para a liderança** | Sugestões têm `targetAuthorId` de **operador**; operador as vê nas notificações; líder **não** observa o DataStore de sugestões | Gap de documentação |
| Líder tem “canal de recebimento das sugestões dos gestores” | Não existe observer de sugestões no fluxo do líder | Gap de código vs doc; Sprint 2 **não** inventa inbox de líder a menos que se preserve o código (destino = operador) |
| Dashboard do gestor: gráfico de ideias **por status** | `ManagerDashboardStats.buildMonthlyBars` — volume **por mês** (5 meses). Distribuição por status está no dashboard do **líder** | Gap de documentação |
| Dashboard do líder inclui **prazo** como KPI consolidado | `RoiDashboardSummary` não tem prazo médio; prazo aparece só no card de cada projeto em Tracking | Gap; Sprint 2 **adiciona** prazo médio no dashboard |
| “Sincronização opcional via APIs REST” para entidades | APIs externas não sincronizam ideias/projetos/diretrizes | Gap de documentação |
| PPTX e roteiro de vídeo incluídos | Working tree: PPTX/roteiro deletados; PDF presente | Gap de entregáveis |
| Auth “segura” | Senha em texto no `AuthCatalog`; sem hash, sem JWT | Limitação Sprint 1 |
| Tela “Selecione seu perfil” | Removida do NavGraph; arquivos órfãos permanecem | Código legado |
| Cadastro/reset de senha | Só memória do processo | Não persiste |
| Ideias seed visíveis ao operador logado | Seed usa `mock-op-*`; login usa UUID FakerAPI → operador demo **não vê** as ideias seed como próprias | Gap funcional Sprint 1 |
| `GetRoiDashboardSummaryUseCase` | Dashboard do líder chama `RoiDashboardSummary.fromProjects` direto no ViewModel | Use case subutilizado |
| `IdeaRepository.delete/update` | Sem use case/UI de exclusão ou edição de ideia | Contrato ocioso |
| Curadoria sem máquina de estados | Qualquer status pode ser aplicado a qualquer ideia | Sprint 2 deve restringir |

---

## 3. O que NÃO existe (não inventar como “já pronto”)

- Backend, JWT, MongoDB, Gemini
- Ligação ideia/projeto ↔ diretriz
- Histórico de estratégias
- Histórico/justificativa de curadoria
- Endpoints de relatório
- Paginação
- Notificações persistidas
- Chat persistido
- Campo “etapa” separado de `ProjectStatus`
- Categoria/campanha/versão em diretriz
- Refresh token
- CORS, RBAC servidor, OpenAPI

---

## 4. Contratos que a Sprint 2 deve preservar

1. Enums e nomes de campos do domínio Android (extensões **aditivas**).
2. Interfaces de Repository; trocar **implementação**.
3. ViewModels/Use Cases como orquestração; HTTP só em Data.
4. NavGraph por role; sem tela de escolha de perfil.
5. Fórmula de ROI exatamente como no código (ver `08-calculations.md`).
6. Um projeto por ideia.
7. Só `APPROVED`/`PRIORITIZED` viram projeto.
8. CRUD de diretriz exclusivo do líder na prática (agora também no servidor).
9. Operador lista só as próprias ideias.
10. Insight do dia continua existindo na Home do operador.
11. Contas demo e-mail/senha.
12. Validações mínimas da UI (backend pode ser igual ou mais estrito, mas não mais frouxo).

## 5. Decisões de reconciliação (doc vs código vs Sprint 2)

| Tema | Decisão da spec |
|------|-----------------|
| Destino das sugestões | **Preservar código:** MANAGER e LEADER enviam para OPERATOR. Líder não tem inbox de sugestão. |
| Gráfico gestor | Backend entrega **barras mensais** (como o app) **e** contagem por status (campo extra). |
| Prazo no dashboard líder | **Adicionar** `averageDeadlineDays` no payload executivo. |
| Quem aprova ideia | **MANAGER apenas.** LEADER é leitura. (Prompt Sprint 2 prevalece sobre 🔶 do README.) |
| Quem cria projeto | **MANAGER apenas.** |
| Quem atualiza resultados | **MANAGER apenas.** LEADER leitura. |
| Quem cria ideia | OPERATOR e MANAGER (código). LEADER **não** cria ideia. |
| Chat | Permanece mock no app. |
| FakerAPI | **Descontinuada** no login; usuários reais no Mongo. |
| AdviceSlip | **Migra para o backend** (`GET /insights/daily`). |
| Registro/reset | Passam a persistir no Mongo, mantendo a UI existente. |
