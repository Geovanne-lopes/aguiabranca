# API — Suggestions e Notifications

## 1. POST `/suggestions`

**Roles:** MANAGER, LEADER  
OPERATOR: 403

```json
{
  "targetUserId": "operator-uuid",
  "message": "Priorize ideias alinhadas à diretriz de transformação digital."
}
```

Servidor preenche `authorUserId/Name` e `targetEmail/Name` a partir do user alvo.

**Validações:** message 10–1000; target deve ser OPERATOR ativo.

**201**

```json
{
  "id": "sug-uuid",
  "authorUserId": "manager-uuid",
  "authorName": "Carlos Gestor",
  "targetUserId": "operator-uuid",
  "targetEmail": "operador@innovatecorp.com",
  "targetName": "Ana Operadora",
  "message": "Priorize ideias...",
  "createdAt": "2026-09-21T16:00:00Z"
}
```

**422** se target não é OPERATOR  
**404** se target não existe

Campo legado Android `managerName` mapeia de `authorName`.

---

## 2. GET `/suggestions`

**OPERATOR:** recebidas (`targetUserId=me`)  
**MANAGER/LEADER:** enviadas (`authorUserId=me`)

Paginação, sort `createdAt DESC`.

**200** página SuggestionResponse.

---

## 3. GET `/notifications`

**Auth:** qualquer papel  
**Propósito:** substituir builders locais + MockOperatorsCatalog.

Query: nenhuma obrigatória. Não paginar na v1 (lista curta, max 50). Cortar em 50.

### OPERATOR (espelha OperatorNotificationsBuilder)

Itens:

- cada guideline ativa → tipo `GUIDELINE`
- cada suggestion recebida → tipo `SUGGESTION` (Android hoje usa GUIDELINE; **mapear SUGGESTION para GUIDELINE no mapper** se o enum Kotlin não for estendido, **ou** adicionar valor no enum Android. Preferência: **estender enum com SUGGESTION** se for mudança mínima; senão mapear GUIDELINE.)
- cada ideia própria por status → IDEA_PENDING / APPROVED / REJECTED / PRIORITIZED

Usar `updatedAt` da ideia para timestamp de decisão (melhor que `createdAt` do builder atual).

### MANAGER (espelha ManagerNotificationsBuilder)

- contagem de PENDING
- até 5 ideias PENDING com `authorName` real
- até 3 sugestões **enviadas**
- operador mais ativo (nome real)

### LEADER (espelha LeaderNotificationsBuilder)

- overview totais
- destaque do mês
- até 3 ideias pending (informativo, sem ação de curadoria)
- 2 guidelines recentes

**200**

```json
{
  "items": [
    {
      "id": "idea-approved-uuid",
      "title": "Ideia aprovada",
      "body": "Parabéns! \"App fila\" foi aprovada.",
      "type": "IDEA_APPROVED",
      "createdAt": "2026-09-21T16:00:00Z"
    }
  ],
  "unreadHint": true
}
```

`unreadHint` é sempre `true` se items não vazio; o app continua controlando “lido” em memória como hoje. Sem persistência de read-state.

OPERATOR/MANAGER/LEADER recebem tipos compatíveis com seus drawers.

---

## Chat

Não há endpoints. Fora de escopo.
