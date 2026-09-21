# Histórico do projeto Águia Branca

Documento vivo. Toda evolução relevante do repositório deve ser acrescentada aqui, nunca sobrescrita sem marcar o que mudou.

---

## Fechamento da Sprint 2 (2026-09-21, fatia 8)

Auditoria do Definition of Done contra o código já entregue nas fatias 1–7. Nenhum gap funcional novo foi encontrado nos critérios automatizados. Ajustes desta fatia: um parágrafo no README raiz apontando o backend como fonte de verdade, e a frase do `backend/README.md` que ainda dizia que a tela Android estava fora.

Entregue:

- Backend monólito em Java 21, Spring Boot 3.4, Spring Data MongoDB e Spring Security stateless (JWT access 1h, refresh opaco 7d, BCrypt). Sem JPA.
- Auth (login, refresh, logout, register, reset), usuários, diretrizes com histórico, ideias com curadoria, projetos com ROI calculado na leitura (`ProjectRoi`), sugestões, notificações, dashboards no fuso `America/Sao_Paulo`, proxy AdviceSlip e insight Gemini com `FALLBACK` sem key.
- Seed idempotente das 3 contas demo e das 2 diretrizes. OpenAPI em `/swagger-ui.html` e `/v3/api-docs` (profile `local`/`test`).
- Segredos só por ambiente: `backend/.env.example` sem key real; `backend/.env` no gitignore. Logs de acesso não gravam senha, JWT nem `GEMINI_API_KEY`; e-mail de login sai mascarado.
- App Android na API pelos repositórios remotos. A UI não chama HTTP. Login via `POST /auth/login` (a FakerAPI não entra nesse fluxo). Dashboards de gestor e líder leem os números da API. Chat de colaboradores continua mock em memória.
- `mvn test` no fechamento: 91 testes, 0 falhas.

Não reexecutado neste fechamento: A-AND-01, A-AND-02 e A-AND-03 no emulador. Neste ambiente não há Android SDK nem `adb`.

Fora do escopo, como nas specs: chat real, Paging 3, FCM, microserviços, JPA.

O bloco **Estado atual (2026-09-21)** abaixo permanece o snapshot do repositório no início do dia, antes desta implementação.

---

## Estado atual (2026-09-21)

O repositório contém **somente o aplicativo Android da Sprint 1**. Não existe módulo backend, pasta `specs/` anterior, JWT, MongoDB nem integração com Gemini.

- **Produto:** InnovateCorp / Challenge Águia Branca — gestão de inovação corporativa
- **Cliente:** aplicativo Android nativo (Kotlin + Jetpack Compose)
- **Persistência de negócio:** Room (`innovation_database`) + DataStore (`manager_suggestions`)
- **Auth:** catálogo local `AuthCatalog` + enriquecimento de nome/avatar via FakerAPI
- **APIs externas no app:** FakerAPI (`GET /api/v1/users`) e AdviceSlip (`GET /advice`)
- **Etapa 8 (testes unitários críticos):** **não entregue** — apenas `ExampleUnitTest` e `ExampleInstrumentedTest`
- **Pasta `specs/`:** criada nesta data para a Sprint 2 (somente especificações)

### Contas demo (Sprint 1, a preservar no seed do backend)

| Perfil | Role técnica | E-mail | Senha |
|--------|--------------|--------|-------|
| Operador | `OPERATOR` | `operador@innovatecorp.com` | `oper123` |
| Gestor | `MANAGER` | `gestor@innovatecorp.com` | `gest123` |
| Liderança | `LEADER` | `lideranca@innovatecorp.com` | `lider123` |

### Git (HEAD conhecido no momento da spec)

Commits na `main`/histórico recente:

| Data | Hash | Mensagem |
|------|------|----------|
| 2026-05-22 | `5122b0b` | feat: app InnovateCorp completo (Operador, Gestor e Lider) |
| 2026-05-26 | `c5810af` | Final-Ver |
| 2026-05-26 | `7f90330` | last-ver |
| 2026-05-26 | `190ece0` | chore(ui): dark mode dinamico, insets corretos, colaboradores na bottom bar e cleanup de entregaveis |

Working tree observado no início desta sessão (não faz parte do produto Android):

- Modificado: `entregas/README-ENTREGAS.txt`
- Adicionado: `entregas/documentacao/documentacao-tecnica.pdf`
- Removido: `entregas/documentacao/documentacao-tecnica.pptx`, `gerar-documentacao-pptx.ps1`, `entregas/video/roteiro-video-demonstrativo.md`
- Não rastreados e **fora do escopo Águia Branca**: `generate_filtrado_pdf.py`, `generate_report_pdf.py`, `relatorio_empresas_associadas.md`, `relatorio_filtrado_empresas_cpa20_cea.md`

### Entregáveis Sprint 1

Conforme `entregas/README-ENTREGAS.txt` e documentação técnica:

1. APK debug (`aguiabranca-debug.apk`)
2. Código-fonte completo (zip)
3. Documentação técnica PDF
4. Vídeo (pasta `video/` — roteiro foi removido no working tree)

A documentação PDF ainda cita PPTX e roteiro de vídeo como incluídos. Isso é um **gap documentação vs working tree** (ver `01-sprint1-as-is.md`).

---

## Linha do tempo

### Origem acadêmica

Projeto FIAP Challenge: aplicativo Android nativo de gestão de inovação para três níveis organizacionais (Operador, Gestor, Líder), com Clean Architecture, duas APIs REST externas e persistência local.

### Sprint 1 — etapas do README (código)

| Etapa | Status | O que existe de fato |
|-------|--------|----------------------|
| 1. Estrutura, Gradle, modelos | Concluída | `domain/model`, Gradle KTS, version catalog |
| 2. DI, Retrofit, DTOs | Concluída | Koin, `FakerApiService`, `AdviceApiService` |
| 3. Room + repositórios + use cases | Concluída | 4 tabelas Room + sugestões no DataStore |
| 4. Auth mock + NavGraph por role | Concluída | Login 1 conta = 1 perfil; sessão Room |
| 5. Feature Operador | Concluída | Home, ideias próprias, estratégias RO, perfil, insight |
| 6. Feature Gestor | Concluída | Dashboard tático, curadoria, projetos, sugestões, time |
| 7. Feature Líder | Concluída | Dashboard ROI, CRUD diretrizes, acompanhamento, ranking |
| 8. Testes + polish final | **Não concluída** | README marca `[ ]` |

### 2026-05-22 — App completo

Commit `5122b0b`: primeira linha principal do app com os três perfis.

### 2026-05-26 — Fechamento Sprint 1

Commits `c5810af`, `7f90330`, `190ece0`: polish de UI (tema dinâmico, insets, colaboradores na bottom bar, limpeza de entregáveis).

Arquitetura de migração já prevista no README da Sprint 1: **trocar implementações de Repository, não a UI nem os use cases**.

### 2026-09-21 — Specs da Sprint 2 (este documento)

Criação de `specs/` após auditoria do código, README e PDF técnico.

Objetivo da Sprint 2 (a implementar **depois** destas specs):

- Backend Java + Spring Boot + Spring Security + JWT + MongoDB
- APIs REST reais com RBAC no servidor
- Integração do app Android existente
- Gemini para insights de dashboard
- Relatórios e cálculos centralizados
- Substituição de mocks/Room como fonte de verdade das entidades de negócio

**Nesta data não há implementação de backend.**

---

## Contratos da Sprint 1 que a Sprint 2 deve preservar

Ver detalhe em `01-sprint1-as-is.md`. Resumo:

- Roles: `OPERATOR`, `MANAGER`, `LEADER`
- Enums: `IdeaStatus`, `IdeaCategory`, `ProjectStatus`
- Modelos de domínio Android: `Idea`, `Project`, `StrategicGuideline`, `User`, `ManagerSuggestion`, `DailyInsight`, `RoiDashboardSummary`
- Interfaces: `IdeaRepository`, `ProjectRepository`, `GuidelineRepository`, `SessionRepository`, `InsightRepository`, `ManagerSuggestionRepository`
- Fórmulas de ROI e produtividade em `Project.roiPercent` e `RoiDashboardSummary.fromProjects`
- Telas Compose e navegação por perfil
- Validações visíveis na UI (título/descrição/conteúdo/sugestão)

## O que a Sprint 2 adiciona (não existia no código)

- Backend próprio
- JWT / refresh / hash de senha
- MongoDB
- Vinculação ideia/projeto ↔ estratégia
- Histórico de diretrizes
- Histórico de decisões de curadoria + justificativa
- Endpoints de dashboard/relatórios
- Google Gemini
- Autorização real no servidor
- OpenAPI
- Testes de API/segurança

## Registro de atualizações desta spec

| Data | Evento |
|------|--------|
| 2026-09-21 | Criação inicial de `specs/` com base no código Sprint 1 + requisitos Sprint 2 |
| 2026-09-21 | Fechamento da Sprint 2 (fatia 8): auditoria do DoD, suíte `mvn test` verde (91), README raiz e `backend/README.md` alinhados à API. A-AND-01..03 não reexecutados no emulador |
