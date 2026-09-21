# 12 — Persistência MongoDB

## 1. Tecnologia

Spring Data MongoDB. Sem JPA. IDs UUID string (`@MongoId` / `@Id String`).

Database: `aguiabranca` (env `MONGODB_DATABASE`).

## 2. Coleções

| Coleção | Document |
|---------|----------|
| `users` | UserDocument |
| `guidelines` | GuidelineDocument |
| `guideline_history` | GuidelineHistoryDocument |
| `ideas` | IdeaDocument |
| `projects` | ProjectDocument |
| `suggestions` | SuggestionDocument |
| `refresh_tokens` | RefreshTokenDocument |
| `ai_insights` | AiInsightDocument |

## 3. Índices (criar no startup `MongoIndexConfig`)

```
users: unique email; role
guidelines: updatedAt desc
guideline_history: unique (guidelineId, version); occurredAt
ideas: (authorId, createdAt desc); (status, createdAt desc); guidelineId
projects: unique ideaId; (status, updatedAt desc); guidelineId; managerId
suggestions: (targetUserId, createdAt desc); (authorUserId, createdAt desc)
refresh_tokens: unique tokenHash; userId; expireAfterSeconds on expiresAt
ai_insights: (requestedByUserId, createdAt desc)
```

Text index opcional ideas.title + ideas.description.

## 4. Unicidade

- email
- ideaId em projects
- (guidelineId, version) no history
- tokenHash

Violações → 409.

## 5. Transações

Mongo standalone acadêmico pode **não** ter replica set.  
Não exigir transação multi-documento. Ordem:

1. Insert history  
2. Insert/update guideline  

Se (2) falha após (1), history extra é aceitável (append-only).  
Create project: check ideia + insert; unique index evita duplicata.

## 6. Seed `local`

Users (BCrypt das senhas demo):

| email | role | id estável recomendado |
|-------|------|------------------------|
| operador@innovatecorp.com | OPERATOR | `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa` |
| gestor@innovatecorp.com | MANAGER | `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb` |
| lideranca@innovatecorp.com | LEADER | `cccccccc-cccc-cccc-cccc-cccccccccccc` |

IDs fixos facilitam testes.

Guidelines seed do DatabaseSeeder, `authorId` = líder.

Não seedar ideias mock com authors inexistentes.

Seed **idempotente** (`existsByEmail`).

## 7. Validação no documento

Além do Bean Validation nos DTOs, o service aplica regras de domínio. Não confiar só no Mongo.

## 8. Auditoria

`createdAt`/`updatedAt` via `@CreatedDate`/`@LastModifiedDate` + `MongoAuditing`.  
`createdBy`/`updatedBy` quando aplicável (`AuditorAware` = CurrentUser).

## 9. Paginação

`Pageable` Spring. `Page` → `ApiPage` DTO.

## 10. Aggregation

Dashboard líder/gestor: aggregation pipeline ou fetch + calculate in-memory se N pequeno (demo).  
**Decisão:** até 5.000 documentos, cálculo in-memory no service é aceitável e mais simples de testar as fórmulas. Se passar, migrar para pipeline. Testes unitários das fórmulas **não** precisam do Mongo.

## 11. Relógio

Injetar `Clock` para testes de tendência mensal.
