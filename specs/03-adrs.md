# 03 — Architecture Decision Records

Decisões **fechadas**. O agente implementador não deve reabrir sem um ADR novo numerado.

---

## ADR-001 — Persistência: Spring Data MongoDB (não JPA/Hibernate)

**Status:** Aceita  
**Data:** 2026-09-21

### Problema

O desafio cita Java + Spring Boot + **MongoDB** e também **JPA/Hibernate**. JPA/Hibernate mapeiam bancos **relacionais**. MongoDB é document-oriented. Usar os dois “para cumprir o enunciado” gera uma arquitetura falsa ou quebrada.

O Android da Sprint 1 usa Room (SQLite relacional). Reproduzir o esquema Room 1:1 no Mongo seria um anti-padrão (muitas joins lógicas, sem ganho).

### Impacto se ignorado

- Aplicação que não sobe, ou sobe com JPA apontando para um SQL fantasma além do Mongo.
- Rubrica acadêmica pode ser questionada se a escolha não estiver **documentada**.

### Alternativas

| Alternativa | Resultado |
|-------------|-----------|
| A. Spring Data JPA + Hibernate + PostgreSQL | Atende JPA; **viola** MongoDB obrigatório |
| B. Hibernate OGM | Projeto descontinuado; não usar |
| C. JPA nas entidades + `MongoTemplate` escondido | Mentira técnica; dois modelos |
| D. Spring Data MongoDB nativo | Atende Mongo + Spring Boot de forma idiomática |
| E. Postgres (JPA) **e** Mongo juntos | Complexidade injustificável na Sprint 2 |

### Decisão

Usar **Spring Data MongoDB** (`spring-boot-starter-data-mongodb`) como **única** persistência de negócio.

Não adicionar `spring-boot-starter-data-jpa` nem Hibernate.

Documentar no README do backend:

> O enunciado menciona JPA/Hibernate. Esta API persiste em MongoDB; o equivalente idiomático no ecossistema Spring é Spring Data MongoDB, não JPA.

### Consequências

- Documentos com referências (`ideaId`, `guidelineId`) e alguns arrays embutidos (decisões da ideia).
- Índices definidos em `12-persistence.md`.
- Testes com Testcontainers Mongo, não H2.
- Relatório acadêmico deve apontar este ADR.

---

## ADR-002 — Monólito modular Java 21 + Spring Boot 3.4 + Maven

**Status:** Aceita

- Java 21 LTS (Spring Boot 3 exige 17+; 21 é o alvo).
- Spring Boot 3.4.x.
- Maven (`pom.xml`) — um módulo. Gradle no Android já existe; backend separado evita misturar AGP com Spring.
- Pacote base: `br.com.fiap.aguiabranca`.
- Organização por feature (ADR implícita na `02-architecture.md`).

---

## ADR-003 — Autenticação JWT com access + refresh

**Status:** Aceita

Sprint 1 persiste sessão no Room sem token. Sprint 2 exige JWT.

- Access token: JWT HS256, **60 minutos**, claims `sub` (userId), `email`, `role`, `jti`.
- Refresh token: **7 dias**, string opaca (UUID) **hasheada** (SHA-256) na coleção `refresh_tokens`.
- Header: `Authorization: Bearer <access>`.
- Logout: revoga o refresh do dispositivo; o access morre na expiração (sem blacklist obrigatória na Sprint 2).
- Senha: BCrypt (`strength` 10). Nunca texto puro.
- Secret JWT: mínimo 32 bytes, variável `JWT_SECRET`.

Refresh é necessário porque o app restaura sessão na abertura (comportamento Sprint 1). Access de 60 min sem refresh forçaria relogin constante.

---

## ADR-004 — Histórico de diretrizes em coleção append-only

**Status:** Aceita

Prompt Sprint 2 exige histórico (id, data, categoria, campanha, versão, ação, usuário, timestamps).

**Decisão:** coleção `guideline_history`, **append-only**. Nenhum update/delete de histórico via API.

Motivo: diretrizes são poucas; histórico precisa sobreviver a edit/delete da diretriz atual. Embutir no documento atual perderia eventos se o documento fosse apagado.

Versão: inteiro incremental por `guidelineId`, começando em 1 no create.

Categoria e campanha **não existem** no modelo Sprint 1. Serão campos **opcionais** novos em `StrategicGuideline` + copiados para cada evento de histórico.

Ação: `CREATED` | `UPDATED` | `DELETED`.

---

## ADR-005 — Integrações externas

**Status:** Aceita

| Serviço Sprint 1 | Sprint 2 |
|------------------|----------|
| FakerAPI | **Remover.** Usuários no Mongo. |
| AdviceSlip | **Manter**, chamada **somente pelo backend**. Android consome `GET /api/v1/insights/daily`. |

Chat mock permanece no app, sem API.

---

## ADR-006 — Insights de IA via Google Gemini no backend

**Status:** Aceita

- Integração real HTTP com Gemini (API key em env).
- Modelo padrão: `gemini-3.6-flash` (substituto documentado se o nome gratuito vigente mudar: usar o flash/grátis atual da AI Studio).
- Consumidor: **LEADER** (MANAGER pode **ler o último insight cacheado**, não gerar).
- Entrada: JSON agregado do dashboard, **sem** e-mails, senhas, tokens, nomes completos de colaboradores se evitável (usar totais e status).
- Saída persistida em `ai_insights` (último por usuário/geração) para auditoria leve e fallback.
- Insight **não** é fato: UI deve tratar como interpretação.
- Fallback estático se timeout/quota/erro.

Detalhe: `09-ai-gemini.md`.

---

## ADR-007 — Contrato único de erro

**Status:** Aceita

```json
{
  "timestamp": "2026-09-21T16:00:00Z",
  "status": 403,
  "code": "FORBIDDEN",
  "message": "Perfil não autorizado para este recurso.",
  "path": "/api/v1/guidelines",
  "traceId": "9c2e...",
  "details": []
}
```

`details`: lista `{ "field": "title", "issue": "must not be blank" }` em validação.

Códigos: ver `07-api-conventions.md`. Não usar envelope diferente por controller.

---

## ADR-008 — Paginação, filtros e ordenação

**Status:** Aceita

Query padrão em listagens:

- `page` (0-based, default 0)
- `size` (default 20, max 100)
- `sort` (campo permitido por recurso)
- `direction` (`ASC` | `DESC`, default `DESC`)

Resposta:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

Android Sprint 1 usa `Flow<List<>>` sem página. Na integração, o remote repository pede `size=100` (ou pagina internamente e concatena) para não redesenhar listas. Não estourar max.

---

## ADR-009 — Android: Room deixa de ser fonte de verdade de negócio

**Status:** Aceita

- Ideias, projetos, diretrizes, sugestões: **API**.
- JWT + refresh: **DataStore** (`auth_prefs`).
- Perfil logado: DataStore **ou** Session Room apenas como cache de `User` (permitido).
- Room de ideias/projetos/guidelines: **não gravar** como master; pode ser removido da injeção ou usado como cache opcional read-only. Implementação mínima: **não usar Room para essas entidades** na Sprint 2, reduzindo dual-write.
- `DatabaseSeeder` DEBUG não deve mais popular dados de negócio se o backend estiver configurado.

Insight do dia: sempre remoto (backend).

---

## ADR-010 — OpenAPI / Swagger

**Status:** Aceita

- Dependência `springdoc-openapi-starter-webmvc-ui`
- UI: `/swagger-ui.html`
- JSON: `/v3/api-docs`
- Endpoints autenticados documentados com Bearer.
- Swagger UI acessível **sem** JWT no profile `local` (só a página; as APIs continuam protegidas).

---

## ADR-011 — Identificadores

**Status:** Aceita

IDs de negócio: UUID string (compatível com `IdGenerator` Android).  
Mongo `_id` = esse UUID (não ObjectId), para o app continuar usando `String`.

---

## ADR-012 — Datas na API em ISO-8601 UTC

**Status:** Aceita

JSON usa `OffsetDateTime` UTC (`2026-09-21T16:00:00Z`).  
Mapper Android converte para `epochMillis` dos modelos de domínio.

---

## ADR-013 — Sugestões continuam Operador-alvo

**Status:** Aceita

O código envia sugestões a operadores. A spec de implementação **não** cria `Suggestion-to-Leader`.  
MANAGER e LEADER podem criar. OPERATOR só lê as dirigidas a si.

---

## ADR-014 — Notificações como projeção

**Status:** Aceita

Não há coleção `notifications` obrigatória.  
`GET /api/v1/notifications` monta a lista como os `*NotificationsBuilder` do Android, usando dados reais (incluindo `updatedAt` das decisões).

Isso substitui `MockOperatorsCatalog` por nomes reais de `users`.

---

## ADR-015 — Quem gera vs quem consome dashboard

**Status:** Aceita

| Endpoint | OPERATOR | MANAGER | LEADER |
|----------|----------|---------|--------|
| `/dashboard/operator` | sim | não | não |
| `/dashboard/manager` | não | sim | sim (leitura) |
| `/dashboard/leader` | não | não | sim |
| `/dashboard/strategies` | não | não | sim |
| `/ai/insights` POST gerar | não | não | sim |
| `/ai/insights` GET último | não | não | sim |
