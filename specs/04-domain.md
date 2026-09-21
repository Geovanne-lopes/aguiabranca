# 04 — Modelo de domínio (MongoDB)

O modelo **não** copia Room. Ele atende o domínio Android + extensões Sprint 2.

Tipos de ID: UUID string. Timestamps: UTC. Auditoria mínima: `createdAt`, `updatedAt`, `createdBy`, `updatedBy` onde houver mutação.

---

## 1. User

**Coleção:** `users`  
**Propósito:** identidade, papel, perfil.

| Campo | Tipo | Obrigatório | Default | Notas |
|-------|------|-------------|---------|-------|
| id | string UUID | sim | gerado | PK |
| name | string | sim | | 2–80 chars |
| email | string | sim | | unique, lowercase |
| passwordHash | string | sim | | BCrypt; **nunca** no response |
| role | enum | sim | | `OPERATOR`, `MANAGER`, `LEADER` |
| avatarUrl | string \| null | não | null | URL http(s) ou data URI curta; max 2048 |
| active | boolean | sim | true | soft-disable |
| createdAt | datetime | sim | now | |
| updatedAt | datetime | sim | now | |

**Índices:** unique `email`; índice `role`.

**Relacionamentos:** referenciado por `authorId`, `managerId`, `targetUserId`.

**Regras:** e-mail único. Role imutável após criação na Sprint 2 (sem endpoint de promoção).

---

## 2. StrategicGuideline (estratégia / diretriz)

**Coleção:** `guidelines`

| Campo | Tipo | Obrig. | Default |
|-------|------|--------|---------|
| id | UUID | sim | gerado |
| title | string | sim | |
| content | string | sim | |
| category | string \| null | não | null | **novo**; max 40 |
| campaign | string \| null | não | null | **novo**; max 80 |
| version | int | sim | 1 | incrementa a cada update |
| authorId | UUID | sim | líder criador |
| createdAt / updatedAt | datetime | sim | |
| deleted | boolean | sim | false | soft delete opcional no documento atual; histórico permanece |

Soft delete: `DELETE` marca `deleted=true` **ou** remove o documento atual mas **sempre** grava evento `DELETED` no histórico. Preferência: **hard delete do documento atual + append no history**, para a lista Android não mostrar item morto. Referências (`guidelineId` em ideias/projetos) permanecem como UUID órfão aceitável (exibir `"Estratégia removida"`).

Validação: title 3–120; content 10–4000; category/campaign opcionais.

---

## 3. GuidelineHistory (append-only)

**Coleção:** `guideline_history`  
**Nunca** atualizar ou apagar via API.

| Campo | Tipo | Obrig. |
|-------|------|--------|
| id | UUID | sim |
| guidelineId | UUID | sim |
| version | int | sim |
| action | enum `CREATED` `UPDATED` `DELETED` | sim |
| title | string | sim | snapshot |
| content | string | sim | snapshot |
| category | string \| null | não |
| campaign | string \| null | não |
| actorUserId | UUID | sim |
| occurredAt | datetime | sim |

**Índices:** `{ guidelineId: 1, version: 1 }` unique; `{ occurredAt: -1 }`.

---

## 4. Idea

**Coleção:** `ideas`

| Campo | Tipo | Obrig. | Default | Origem |
|-------|------|--------|---------|--------|
| id | UUID | sim | gerado | Sprint 1 |
| title | string | sim | | 3–120 |
| description | string | sim | | 10–4000 |
| category | enum IdeaCategory | sim | | Sprint 1 |
| authorId | UUID | sim | | |
| status | enum IdeaStatus | sim | `PENDING` | |
| guidelineId | UUID \| null | não | auto | **novo** |
| decisionHistory | array embutido | sim | `[]` | **novo** |
| createdAt | datetime | sim | | |
| updatedAt | datetime | sim | | |

### DecisionHistoryItem (embutido, append-only)

| Campo | Tipo |
|-------|------|
| status | IdeaStatus |
| actorUserId | UUID |
| justification | string \| null | max 1000 |
| occurredAt | datetime |

Item inicial na criação: `PENDING`, actor = author, justification null.

**Índices:** `authorId+createdAt`; `status+createdAt`; `guidelineId`; text index `title`+`description`.

**Unicidade de projeto:** garantida em `projects.ideaId` unique.

Justificativa: **obrigatória** se `REJECTED` (min 10 chars). Opcional em APPROVED/PRIORITIZED.

---

## 5. Project

**Coleção:** `projects`

| Campo | Tipo | Obrig. | Default |
|-------|------|--------|---------|
| id | UUID | sim | |
| ideaId | UUID | sim | unique |
| guidelineId | UUID \| null | não | copiado da ideia |
| title | string | sim | copiado da ideia |
| description | string | sim | copiado da ideia |
| status | ProjectStatus | sim | `BACKLOG` |
| investmentAmount | double | sim | 0 |
| obtainedProfit | double | sim | 0 |
| productivityGainPercent | double | sim | 0 |
| deadline | datetime \| null | não | |
| managerId | UUID | sim | criador |
| createdAt / updatedAt | datetime | sim | |

Não há coleção `project_results` separada: resultados **são campos do projeto** (como na Sprint 1).

Não há campo `stage` distinto: `ProjectStatus` **é** a etapa.

**Índices:** unique `ideaId`; `status`; `managerId`; `guidelineId`; `updatedAt`.

**Valores:** investment ≥ 0; profit ≥ 0; productivity 0–100; title 3–120.

ROI **não persistido** — calculado na leitura (`08-calculations.md`).

---

## 6. ManagerSuggestion

**Coleção:** `suggestions`

| Campo | Tipo | Obrig. |
|-------|------|--------|
| id | UUID | sim |
| authorUserId | UUID | sim | gestor ou líder remetente |
| authorName | string | sim | denormalizado para a UI atual |
| targetUserId | UUID | sim | deve ser OPERATOR |
| targetEmail | string | sim | denormalizado |
| targetName | string | sim |
| message | string | sim | 10–1000 |
| createdAt | datetime | sim |

**Índices:** `targetUserId+createdAt`; `authorUserId+createdAt`.

Sem update/delete na Sprint 2 (append-only funcional).

---

## 7. RefreshToken

**Coleção:** `refresh_tokens`

| Campo | Tipo |
|-------|------|
| id | UUID |
| userId | UUID |
| tokenHash | string SHA-256 |
| expiresAt | datetime |
| revoked | boolean |
| createdAt | datetime |

Índice unique `tokenHash`; índice `userId`; TTL opcional em `expiresAt`.

---

## 8. AiInsight

**Coleção:** `ai_insights`

| Campo | Tipo |
|-------|------|
| id | UUID |
| requestedByUserId | UUID |
| model | string |
| promptHash | string | SHA-256 do payload enviado (não o prompt completo se contiver dados) |
| inputSummary | object | totais enviados (sem PII) |
| content | string | texto do insight |
| status | `SUCCESS` `FALLBACK` `ERROR` |
| createdAt | datetime |

Manter os **20 mais recentes** (limpeza no insert). Não é cache compartilhado global obrigatório; cada geração é um documento.

---

## 9. O que não vira coleção

| Conceito Sprint 1 | Tratamento |
|-------------------|------------|
| SessionEntity | JWT + DataStore no app |
| OperatorNotification / ManagerNotification | GET projeção |
| DailyInsight | resposta transitória + opcional cache em memória 1h no backend |
| RoiDashboardSummary | agregado calculado |
| OperatorActivity | agregado calculado |
| ChatMessage / Collaborator | mock Android |
| Gamification | Android |

---

## 10. Embedding vs referência

| Dado | Estratégia |
|------|------------|
| Decisões da ideia | **embutido** (poucos eventos) |
| Histórico de diretriz | **coleção própria** |
| Autor da ideia | **referência** `authorId` + lookup de nome na listagem |
| Ideia do projeto | **referência** `ideaId` |
| Estratégia | **referência** `guidelineId` |
| Nomes em sugestão | **denormalizados** (a UI atual não faz join) |

Nas listagens de ideias/projetos, o backend **pode** incluir `authorName` e `guidelineTitle` no DTO para eliminar `MockOperatorsCatalog`.

---

## 11. Consultas críticas

1. Ideias do autor, mais recentes  
2. Ideias `PENDING` para curadoria  
3. Ideias por status/categoria/guideline  
4. Projeto por `ideaId`  
5. Projetos por status, updatedAt desc  
6. Guidelines não deletadas, updatedAt desc  
7. Histórico por guidelineId, version desc  
8. Sugestões por targetUserId  
9. Agregações de dashboard (aggregation pipeline)

Paginação em todas as listagens (ADR-008).

---

## 12. Seed mínimo (profile `local`)

Três users demo (senhas iguais à Sprint 1, BCrypt).  
Duas guidelines equivalentes ao `DatabaseSeeder` (Transformação Digital, Sustentabilidade Corporativa), autor = líder.  
Opcional: 3 ideias PENDING de operadores seed extras **somente se** existirem users reais correspondentes. **Não** seedar `mock-op-*` sem user.

Não seedar em `prod`.
