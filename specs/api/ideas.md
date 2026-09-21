# API — Ideas

## IdeaResponse

```json
{
  "id": "idea-uuid",
  "title": "App mobile para fila express",
  "description": "Reduzir espera no caixa com app de senha.",
  "category": "TECHNOLOGY",
  "authorId": "user-uuid",
  "authorName": "Ana Operadora",
  "status": "PENDING",
  "guidelineId": "guideline-uuid",
  "guidelineTitle": "Transformação Digital",
  "decisionHistory": [
    {
      "status": "PENDING",
      "actorUserId": "user-uuid",
      "actorName": "Ana Operadora",
      "justification": null,
      "occurredAt": "2026-09-21T12:00:00Z"
    }
  ],
  "createdAt": "2026-09-21T12:00:00Z",
  "updatedAt": "2026-09-21T12:00:00Z"
}
```

Android mapeia campos Sprint 1; extras podem ser ignorados até a UI evoluir. `decisionHistory` não quebra `ignoreUnknownKeys`.

---

## 1. GET `/ideas`

**Propósito:** listar ideias no escopo do papel.

**OPERATOR:** sempre filtrado por `authorId=me`. Query `authorId` de outro é ignorada.  
**MANAGER/LEADER:** todas.

Filtros: `status`, `category`, `guidelineId`, `authorId` (gestor/líder), `q`, `from`, `to` (createdAt), paginação.  
Default sort: `createdAt DESC`.

**200** página IdeaResponse.

Curation Android: pedir `status=PENDING` **ou** todas (a tela Sprint 1 lista todas). Remote deve permitir sem status para preservar a tela atual.

---

## 2. POST `/ideas`

**Roles:** OPERATOR, MANAGER  
**LEADER:** 403

```json
{
  "title": "Coleta seletiva nas lojas",
  "description": "Instalar pontos de coleta com meta mensal de volume.",
  "category": "SUSTAINABILITY",
  "guidelineId": null
}
```

| Campo | Regra |
|-------|-------|
| title | trim, 3–120 |
| description | trim, 10–4000 |
| category | enum obrigatório |
| guidelineId | UUID existente ou omitir/null → vigente |

**201** IdeaResponse status PENDING  
**400** validação  
**422** guidelineId inválido  
**403** líder

`authorId` forçado.

---

## 3. GET `/ideas/{id}`

**OPERATOR:** 200 se autor, senão 404  
**MANAGER/LEADER:** 200 ou 404 se não existe

---

## 4. PATCH `/ideas/{id}/status`

**Roles:** MANAGER  
Outros: **403**

```json
{
  "status": "REJECTED",
  "justification": "Fora da diretriz de sustentabilidade deste semestre."
}
```

| Campo | Regra |
|-------|-------|
| status | APPROVED \| REJECTED \| PRIORITIZED (não aceitar PENDING) |
| justification | obrigatória se REJECTED, 10–1000; senão opcional |

**200** IdeaResponse com history append  
**404**  
**403**  
**409** `INVALID_STATUS_TRANSITION`  
**409** tentativa de REJECTED com projeto existente  
**400** REJECTED sem justification

Efeito colateral: nenhum update automático de projeto.

---

## Filtro `mine`

`GET /ideas?mine=true`  
OPERATOR: redundante. MANAGER: restringe às ideias que **ele criou**. LEADER: 400 ou ignora — **decisão:** LEADER ignora `mine` (não cria ideias).

---

## Erros específicos

| Caso | Status | code |
|------|--------|------|
| título curto | 400 | VALIDATION_ERROR |
| líder cria | 403 | FORBIDDEN |
| operador lê ideia alheia | 404 | NOT_FOUND |
| operator patch status | 403 | FORBIDDEN |
| APPROVED → PENDING | 409 | INVALID_STATUS_TRANSITION |
