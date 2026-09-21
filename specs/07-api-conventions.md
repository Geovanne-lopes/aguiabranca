# 07 — Convenções da API REST

Base URL: `http://{host}:8080/api/v1`  
Content-Type: `application/json; charset=UTF-8`  
Auth: `Authorization: Bearer {accessToken}` salvo nos públicos.

## 1. Versionamento

Prefixo `/api/v1`. Sem quebra de contrato sem nova versão.

## 2. Recursos e verbos

| Verbo | Uso |
|-------|-----|
| GET | leitura, idempotente |
| POST | criação, login, ações (refresh, insights) |
| PUT | substituição completa do recurso editável |
| PATCH | alteração parcial (status da ideia) |
| DELETE | remoção |

Nomes no plural: `/ideas`, `/projects`, `/guidelines`, `/users`.

Não criar `/approveIdea`. Usar `PATCH /ideas/{id}/status`.

## 3. Paginação (ADR-008)

Query: `page`, `size`, `sort`, `direction`.

Envelope:

```json
{
  "content": [ ],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

`size` > 100 → 400 `VALIDATION_ERROR`.

Ordenação: whitelist por recurso; campo inválido → 400.

## 4. Filtros comuns

| Query | Tipo | Onde |
|-------|------|------|
| `status` | enum | ideas, projects |
| `category` | enum | ideas |
| `guidelineId` | UUID | ideas, projects |
| `authorId` | UUID | ideas (MANAGER/LEADER) |
| `q` | string | busca title/description (regex escaped / text index) |
| `from` `to` | ISO-8601 | createdAt/updatedAt, dashboards |
| `mine` | boolean | ideas (ignorado para OPERATOR; forçado true) |

## 5. Envelope de item

Recursos retornam o objeto direto (sem `{ data: ... }`), **exceto** listagens paginadas.

## 6. Erro (ADR-007)

```json
{
  "timestamp": "2026-09-21T16:42:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Um ou mais campos são inválidos.",
  "path": "/api/v1/ideas",
  "traceId": "b7c1e2a0-....",
  "details": [
    { "field": "title", "issue": "deve ter no mínimo 3 caracteres" }
  ]
}
```

Handler global `@RestControllerAdvice`. Exceções de domínio traduzidas por type → status/code.

## 7. Códigos de negócio

`UNAUTHENTICATED` `INVALID_TOKEN` `TOKEN_EXPIRED` `INVALID_CREDENTIALS` `INVALID_REFRESH_TOKEN` `ACCOUNT_DISABLED`  
`FORBIDDEN`  
`NOT_FOUND`  
`VALIDATION_ERROR` `MALFORMED_JSON`  
`BUSINESS_RULE_VIOLATION`  
`EMAIL_ALREADY_EXISTS` `IDEA_ALREADY_HAS_PROJECT` `INVALID_STATUS_TRANSITION`  
`RATE_LIMITED`  
`DEPENDENCY_UNAVAILABLE` `AI_INVALID_RESPONSE`  
`INTERNAL_ERROR`

## 8. Idempotência

POST create **não** é idempotente (novo UUID).  
Cliente não reenvia automaticamente create de ideia.  
Create project em ideia já ligada: 409.

## 9. Datas

ISO-8601 UTC. Exemplo `"2026-09-21T16:00:00Z"`.

## 10. Enums (strings idênticas ao Kotlin)

IdeaStatus, IdeaCategory, ProjectStatus, UserRole, GuidelineHistoryAction.

## 11. Campos extras no DTO de leitura (aditivos)

Para eliminar mocks:

- `authorName` em Idea
- `guidelineTitle` em Idea e Project
- `roiPercent` em Project (calculado)
- `version`, `category`, `campaign` em Guideline

Android ignora campos desconhecidos (`ignoreUnknownKeys = true` já existe no Json Koin).

## 12. OpenAPI

Cada controller anotado. Tag por recurso. Exemplos iguais aos desta spec.

## 13. Lista de endpoints (mapa)

| Método | Rota | Auth | Roles |
|--------|------|------|-------|
| POST | `/auth/register` | público | — |
| POST | `/auth/login` | público | — |
| POST | `/auth/refresh` | público | — |
| POST | `/auth/logout` | sim | qualquer |
| POST | `/auth/reset-password` | público | — |
| GET | `/auth/me` | sim | qualquer |
| PATCH | `/users/me` | sim | qualquer |
| GET | `/users` | sim | MANAGER, LEADER; OPERATOR só colegas? **MANAGER+LEADER**; OPERATOR 403 |
| GET | `/guidelines` | sim | todos |
| GET | `/guidelines/{id}` | sim | todos |
| POST | `/guidelines` | sim | LEADER |
| PUT | `/guidelines/{id}` | sim | LEADER |
| DELETE | `/guidelines/{id}` | sim | LEADER |
| GET | `/guidelines/{id}/history` | sim | LEADER |
| GET | `/ideas` | sim | todos (escopo) |
| POST | `/ideas` | sim | OPERATOR, MANAGER |
| GET | `/ideas/{id}` | sim | escopo |
| PATCH | `/ideas/{id}/status` | sim | MANAGER |
| GET | `/projects` | sim | MANAGER, LEADER |
| POST | `/projects` | sim | MANAGER |
| GET | `/projects/{id}` | sim | MANAGER, LEADER (também é o relatório por projeto) |
| PUT | `/projects/{id}` | sim | MANAGER |
| DELETE | `/projects/{id}` | sim | MANAGER |
| GET | `/projects/by-idea/{ideaId}` | sim | MANAGER, LEADER |
| GET | `/suggestions` | sim | escopo |
| POST | `/suggestions` | sim | MANAGER, LEADER |
| GET | `/notifications` | sim | todos |
| GET | `/insights/daily` | sim | todos |
| GET | `/dashboard/operator` | sim | OPERATOR |
| GET | `/dashboard/manager` | sim | MANAGER, LEADER |
| GET | `/dashboard/leader` | sim | LEADER |
| GET | `/dashboard/strategies` | sim | LEADER |
| GET | `/dashboard/period` | sim | LEADER |
| GET | `/dashboard/rankings` | sim | MANAGER, LEADER |
| POST | `/ai/insights` | sim | LEADER |
| GET | `/ai/insights/latest` | sim | LEADER |

Detalhamento: pasta `api/`.
