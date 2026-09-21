# API — Guidelines (estratégias)

Recurso do líder. Leitura para todos autenticados.

DTO de leitura:

```json
{
  "id": "g1",
  "title": "Transformação Digital",
  "content": "Priorizar iniciativas que digitalizem processos...",
  "category": "DIGITAL",
  "campaign": "2026-H2",
  "version": 2,
  "authorId": "leader-id",
  "authorName": "Mariana Costa",
  "createdAt": "2026-05-22T12:00:00Z",
  "updatedAt": "2026-09-01T12:00:00Z"
}
```

`category` e `campaign` podem ser `null`. Android ignora se a UI ainda não mostra.

---

## 1. GET `/guidelines`

**Nome:** Listar estratégias vigentes  
**Auth:** sim  
**Roles:** OPERATOR, MANAGER, LEADER

Query: paginação, `q`, `sort=updatedAt` default DESC.

Não retorna deletadas.

**200** página de GuidelineResponse.

---

## 2. GET `/guidelines/{id}`

**200** item  
**404** se não existe

Todos os papéis autenticados.

---

## 3. POST `/guidelines`

**Roles:** LEADER  
Outros: **403**

```json
{
  "title": "Sustentabilidade Corporativa",
  "content": "Incentivar ideias com impacto ambiental mensurável.",
  "category": "ESG",
  "campaign": "NetZero"
}
```

| Campo | Regra |
|-------|-------|
| title | 3–120, obrigatório |
| content | 10–4000, obrigatório |
| category | opcional, ≤40 |
| campaign | opcional, ≤80 |

**201** GuidelineResponse version=1  
Efeito: insert guideline + history `CREATED`.

OPERATOR/MANAGER: 403.

---

## 4. PUT `/guidelines/{id}`

**Roles:** LEADER

Body igual ao POST (substituição dos campos editáveis).

**200** version incrementada  
**404**  
**403** não líder  
Efeito: update + history `UPDATED` com snapshot **novo**.

---

## 5. DELETE `/guidelines/{id}`

**Roles:** LEADER  
**204**  
History `DELETED` com último snapshot.  
**404** se já não existe.

Ideias/projetos que apontam para o id permanecem.

---

## 6. GET `/guidelines/{id}/history`

**Roles:** LEADER  
MANAGER/OPERATOR: **403**

Paginação, sort `version` DESC.

**200**

```json
{
  "content": [
    {
      "id": "h2",
      "guidelineId": "g1",
      "version": 2,
      "action": "UPDATED",
      "title": "...",
      "content": "...",
      "category": "ESG",
      "campaign": "NetZero",
      "actorUserId": "leader-id",
      "actorName": "Mariana Costa",
      "occurredAt": "2026-09-01T12:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1
}
```

A UI Android **não tem tela de histórico** na Sprint 1. O endpoint existe para o requisito Sprint 2 e Swagger/Postman. Integração Android do histórico é **opcional** (não redesenhar UI). Critério de aceite: API + teste, não tela nova.

---

## Regras extras

- `authorId` no create = `CurrentUser.id`
- Não aceitar `authorId` no body
