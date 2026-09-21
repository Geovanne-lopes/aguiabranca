# 05 — Regras de negócio, RBAC e lifecycles

## 1. Ciclo principal

```
OPERATOR/MANAGER cria Idea (PENDING, guidelineId)
        ↓
MANAGER decide:
  APPROVED | PRIORITIZED | REJECTED (+ justificativa se reprova)
        ↓
MANAGER cria Project a partir de APPROVED ou PRIORITIZED
        ↓
MANAGER atualiza Project (status, investimento, prazo, lucro, produtividade)
        ↓
LEADER consulta projetos + dashboard
        ↓
LEADER solicita insight Gemini sobre os totais
```

---

## 2. Matriz RBAC (servidor)

Legenda: C criar · R ler · U atualizar · D excluir · — proibido

### 2.1 Recursos

| Recurso | OPERATOR | MANAGER | LEADER |
|---------|----------|---------|--------|
| Login / refresh / logout / me | C/R | C/R | C/R |
| Registrar conta | C (público) | C | C |
| Alterar senha da própria conta | U | U | U |
| Reset senha acadêmico | C (público, rate limit) | C | C |
| Perfil próprio | R/U | R/U | R/U |
| Listar usuários (diretório reduzido) | R limitado | R | R |
| Guidelines | R | R | CRUD |
| Guideline history | — | — | R |
| Ideias próprias | C/R | C/R | — (líder não cria) |
| Ideias de outros | — | R + status | R |
| Decisão de status de ideia | — | U | — |
| Projetos | — | CRUD* | R |
| Sugestões enviadas | — | C/R próprias | C/R próprias |
| Sugestões recebidas | R | — | — |
| Dashboard operator | R | — | — |
| Dashboard manager | — | R | R |
| Dashboard leader / strategies / period | — | — | R |
| Relatório por projeto | — | R | R |
| Insight diário AdviceSlip | R | R | R |
| AI insights gerar | — | — | C |
| AI insights ler último | — | — | R |
| Notifications | R próprias | R próprias | R próprias |

\* MANAGER: create/update/delete projetos. Não há “time” formal na Sprint 1: **escopo de projetos e ideias de curadoria é global da organização** (um tenant implícito).

### 2.2 Escopos

| Tipo | Significado nesta sprint |
|------|--------------------------|
| Próprio | `authorId` / `targetUserId` / `userId` = usuário autenticado |
| Time | **não modelado** — gestor vê todas as ideias e projetos |
| Liderança | leitura global + escrita de estratégia + IA |
| Global de leitura | guidelines ativas |

Não há multi-empresa. Um único tenant `InnovateCorp`.

---

## 3. Ideias — lifecycle

### 3.1 Status

`PENDING` → `APPROVED` | `REJECTED` | `PRIORITIZED`  
`APPROVED` → `PRIORITIZED`  
`PRIORITIZED` → (sem volta na Sprint 2)  
`REJECTED` → terminal

### 3.2 Transições permitidas

| De | Para | Quem | Condição |
|----|------|------|----------|
| — | PENDING | OPERATOR, MANAGER | título/descrição válidos |
| PENDING | APPROVED | MANAGER | ideia existe |
| PENDING | PRIORITIZED | MANAGER | ideia existe |
| PENDING | REJECTED | MANAGER | justification ≥ 10 |
| APPROVED | PRIORITIZED | MANAGER | ainda sem ou com projeto (permitido) |
| APPROVED | REJECTED | MANAGER | **proibido se** já existe projeto (409) |
| PRIORITIZED | REJECTED | MANAGER | **proibido se** já existe projeto (409) |
| qualquer | qualquer outro não listado | — | 409 `INVALID_STATUS_TRANSITION` |
| REJECTED | qualquer | — | 409 |

OPERATOR e LEADER que tentarem `PATCH status` → **403**.

### 3.3 Criação

- `authorId` **sempre** o usuário autenticado (ignorar authorId do body se enviado).
- `guidelineId` opcional. Se omitido e existir pelo menos uma guideline, preencher com a **mais recentemente atualizada**. Se não houver guideline, `null`.
- Se `guidelineId` informado e não existir → 422.

### 3.4 Leitura

- OPERATOR: `GET /ideas` retorna **somente as próprias**, mesmo sem query. `GET /ideas/{id}` 404 se não for autor (não 403, para não vazar existência — **decisão:** 404).
- MANAGER/LEADER: todas, com filtros.

### 3.5 Edição de conteúdo / delete de ideia

Não há UI. **Não expor PUT/DELETE de ideia** na Sprint 2 (evita superfície extra). Status é o único PATCH.

---

## 4. Projetos — lifecycle

### 4.1 Criação

Quem: **MANAGER**. LEADER/OPERATOR → 403.

Condições:

1. `ideaId` existe.
2. Status da ideia é `APPROVED` ou `PRIORITIZED`.
3. Não existe projeto com esse `ideaId` → senão **409** `IDEA_ALREADY_HAS_PROJECT`.

(No Android, o use case retorna o existente. No backend REST, 409 é mais explícito. O remote repository Android, ao receber 409, deve **buscar o projeto por ideaId** e seguir, para não quebrar o fluxo idempotente da UI.)

Campos iniciais: title/description da ideia; status `BACKLOG`; números 0; `guidelineId` da ideia; `managerId` = autenticado.

### 4.2 Atualização

Quem: **MANAGER**.

Pode alterar: `status`, `investmentAmount`, `obtainedProfit`, `productivityGainPercent`, `deadline`, `title`, `description`.  
Não pode alterar: `ideaId`, `managerId`, `createdAt`.  
`guidelineId` opcionalmente atualizável (se a liderança mudou o eixo).

Qualquer `ProjectStatus` do enum pode ser setado (o app trata os valores como classificação operacional, não como máquina rígida).  
Única regra extra: valores numéricos válidos.

### 4.3 Exclusão

Quem: **MANAGER**.  
Não cascateia a ideia (ideia permanece APPROVED/PRIORITIZED).  
LEADER não exclui.

### 4.4 Leitura

MANAGER e LEADER: lista e detalhe.  
OPERATOR: 403 em todos os endpoints de projeto.

---

## 5. Diretrizes

| Ação | OPERATOR | MANAGER | LEADER |
|------|----------|---------|--------|
| Listar ativas | sim | sim | sim |
| Detalhe | sim | sim | sim |
| Criar/editar/excluir | 403 | 403 | sim |
| Histórico | 403 | 403 | sim |

Update incrementa `version` e grava `guideline_history`.  
Delete grava evento e remove (ou soft-delete) o documento atual.

---

## 6. Sugestões

- Create: MANAGER, LEADER.
- `targetUserId` deve existir e ter role OPERATOR → senão 422.
- OPERATOR create → 403.
- OPERATOR list: só as recebidas.
- MANAGER/LEADER list: as que **enviaram** (query `mine=sent` default). Não veem caixa de outros gestores.

---

## 7. Perfil

Usuário autenticado atualiza `name`, `avatarUrl`.  
**E-mail:** a UI Sprint 1 permite editar e-mail na sessão local. No backend, mudança de e-mail é **permitida** se o novo e-mail não existir (409 se conflito). Role não muda.

---

## 8. Registro de conta

Público. Body: name, email, password, role.

Role autoatribuída é aceita na Sprint 2 **por ser desafio acadêmico com UI de registro já existente**. Documentar risco. Rate limit: 10/hora/IP.

---

## 9. Tentativas inválidas (padrão)

| Situação | HTTP | code |
|----------|------|------|
| Sem token / token lixo | 401 | `UNAUTHENTICATED` |
| Token expirado | 401 | `TOKEN_EXPIRED` |
| Role errada | 403 | `FORBIDDEN` |
| Recurso inexistente | 404 | `NOT_FOUND` |
| Transição ilegal / ideia já tem projeto / e-mail duplicado | 409 | específico |
| Validação Bean Validation | 400 | `VALIDATION_ERROR` |
| Regra de campo de negócio (guideline inexistente, target não operator) | 422 | `BUSINESS_RULE_VIOLATION` |
| Integração Gemini/AdviceSlip falhou e há fallback | 200 com flag **ou** 503 se não houver fallback |
| Integração sem fallback | 503 | `DEPENDENCY_UNAVAILABLE` |
| Erro inesperado | 500 | `INTERNAL_ERROR` |

---

## 10. Auto-vínculo à estratégia vigente

“Estratégia vigente” = guideline com maior `updatedAt`.  
Aplicar na **criação da ideia** se `guidelineId` ausente.  
Não alterar ideias antigas automaticamente.
