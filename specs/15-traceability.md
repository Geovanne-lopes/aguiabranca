# 15 — Matriz de rastreabilidade

Formato: Requisito → Regra → Endpoint → Entidade → Use case Android → Teste → Aceite

IDs de aceite em `16-acceptance.md`.

| ID | Requisito | Regra | Endpoint | Entidade | Use case / tela Android | Teste | Aceite |
|----|-----------|-------|----------|----------|-------------------------|-------|--------|
| R-AUTH-LOGIN | Login real | BCrypt + JWT | POST `/auth/login` | users, refresh_tokens | AuthenticateUserUseCase, LoginScreen | API login ok/401; S10 | A-AUTH-01 |
| R-AUTH-REFRESH | Sessão durável | refresh 7d | POST `/auth/refresh` | refresh_tokens | TokenAuthenticator | S3, refresh ok | A-AUTH-02 |
| R-AUTH-ME | Hidratar usuário | Bearer | GET `/auth/me` | users | InnovationApp start | 200/401 | A-AUTH-02 |
| R-AUTH-LOGOUT | Sair | revoga refresh | POST `/auth/logout` | refresh_tokens | ClearSessionUseCase | S11 | A-AUTH-03 |
| R-AUTH-REG | Cadastro UI | e-mail único | POST `/auth/register` | users | LoginViewModel.register | 201/409 | A-AUTH-04 |
| R-AUTH-RESET | Reset UI | acadêmico | POST `/auth/reset-password` | users | LoginViewModel.reset | 204/429 | A-AUTH-05 |
| R-USER-PATCH | Editar perfil | próprio | PATCH `/users/me` | users | UpdateUserProfileUseCase | 200/409 | A-USER-01 |
| R-USER-LIST | Diretório operadores | MANAGER/LEADER | GET `/users` | users | Suggestion/ranking | 403 OPERATOR | A-USER-02 |
| R-GL-READ | Consultar estratégias | todos auth | GET `/guidelines` | guidelines | ObserveGuidelines | 200 | A-GL-01 |
| R-GL-WRITE | CRUD líder | só LEADER | POST/PUT/DELETE `/guidelines` | guidelines, guideline_history | LeaderGuidelinesViewModel | S4 S8 | A-GL-02 |
| R-GL-HIST | Histórico | append-only LEADER | GET `/guidelines/{id}/history` | guideline_history | Swagger (UI opcional) | integration history | A-GL-03 |
| R-IDEA-CREATE | Cadastrar ideia | OPERATOR/MANAGER | POST `/ideas` | ideas | SubmitIdeaUseCase | 201/403 líder | A-IDEA-01 |
| R-IDEA-OWN | Operador vê as suas | filtro authorId | GET `/ideas` | ideas | ObserveIdeasByAuthor | S6 | A-IDEA-02 |
| R-IDEA-ALL | Gestor vê todas | global | GET `/ideas` | ideas | ObserveAllIdeas | 200 | A-IDEA-03 |
| R-IDEA-STATUS | Curadoria | MANAGER + máquina | PATCH `/ideas/{id}/status` | ideas.decisionHistory | UpdateIdeaStatusUseCase | S5 S9 409 | A-IDEA-04 |
| R-IDEA-STRAT | Vincular estratégia | vigente/auto | POST `/ideas` guidelineId | ideas, guidelines | SubmitIdea (mapper) | 422 id inválido | A-IDEA-05 |
| R-PROJ-CREATE | Projeto a partir de ideia | MANAGER + status | POST `/projects` | projects | CreateProjectFromIdea | 403 líder; 409 dup | A-PROJ-01 |
| R-PROJ-UPD | Resultados | MANAGER | PUT `/projects/{id}` | projects | UpdateProjectUseCase | 400 produtividade | A-PROJ-02 |
| R-PROJ-DEL | Excluir projeto | MANAGER | DELETE `/projects/{id}` | projects | DeleteProjectUseCase | 403 líder | A-PROJ-03 |
| R-PROJ-READ | Acompanhar | MANAGER+LEADER | GET `/projects` | projects | ObserveAll / Tracking | 403 OPERATOR | A-PROJ-04 |
| R-SUG | Sugestão a operador | MANAGER+LEADER | POST/GET `/suggestions` | suggestions | SendManagerSuggestion | 422 target | A-SUG-01 |
| R-NOTIF | Notificações | projeção | GET `/notifications` | — | drawers | 200 por role | A-NOTIF-01 |
| R-DASH-OP | KPIs operador | próprio | GET `/dashboard/operator` | ideas | OperatorHome | 403 gestor | A-DASH-01 |
| R-DASH-MG | Dashboard tático | fórmulas 08 | GET `/dashboard/manager` | ideas, projects | ManagerDashboard | zeros empty | A-DASH-02 |
| R-DASH-LD | Dashboard executivo | ROI 08 | GET `/dashboard/leader` | projects | LeaderDashboard | 403 OPERATOR | A-DASH-03 |
| R-DASH-ST | Por estratégia | agrupamento | GET `/dashboard/strategies` | projects, guidelines | (API; UI extra opcional) | bucket null | A-DASH-04 |
| R-DASH-PR | Por período | from/to | GET `/dashboard/period` | projects | API | 400 from>to | A-DASH-05 |
| R-RANK | Ranking | month/all | GET `/dashboard/rankings` | ideas, users | ranking use cases | nomes reais | A-RANK-01 |
| R-INSIGHT | Insight do dia | AdviceSlip proxy | GET `/insights/daily` | — | FetchDailyInsight | fallback | A-INS-01 |
| R-AI | Insights Gemini | LEADER | POST/GET `/ai/insights*` | ai_insights | Leader dashboard botão | A1–A6 | A-AI-01 |
| R-SEC | AuthZ servidor | 401/403 | todos protegidos | — | interceptor | S1–S11 | A-SEC-01 |
| R-CALC | Cálculos no server | 08 | dashboards + Project.roiPercent | — | ViewModels param de calcular | unit ROI | A-CALC-01 |
| R-FAKER | Remover Faker | ADR-005 | — | users | AuthenticateUserUseCase | login sem Faker | A-INT-01 |
| R-MONGO | Persistência real | ADR-001 | * | Mongo | — | Testcontainers | A-OPS-01 |
| R-CONF | Secrets env | 14 | — | — | — | README .env.example | A-OPS-02 |

## Fluxo ponta a ponta (rastreio único)

`R-IDEA-CREATE` → `R-IDEA-STATUS` → `R-PROJ-CREATE` → `R-PROJ-UPD` → `R-DASH-LD` → `R-AI`

Entidades: Idea → Project → agregados → AiInsight.
