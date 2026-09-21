# API — Projects

## ProjectResponse

```json
{
  "id": "proj-uuid",
  "ideaId": "idea-uuid",
  "guidelineId": "guideline-uuid",
  "guidelineTitle": "Transformação Digital",
  "title": "App mobile para fila express",
  "description": "...",
  "status": "IN_DEVELOPMENT",
  "investmentAmount": 15000.0,
  "obtainedProfit": 22000.0,
  "productivityGainPercent": 12.5,
  "roiPercent": 46.666...,
  "deadline": "2026-12-31T00:00:00Z",
  "managerId": "manager-uuid",
  "managerName": "Carlos Gestor",
  "createdAt": "2026-09-21T12:00:00Z",
  "updatedAt": "2026-09-22T12:00:00Z"
}
```

`roiPercent` calculado; não persistido.  
Android: `deadline` → `deadlineEpochMillis` (null se deadline null).

---

## 1. GET `/projects`

**Roles:** MANAGER, LEADER  
OPERATOR: 403

Filtros: `status`, `guidelineId`, `managerId`, `q`, `from`/`to` em `updatedAt`, paginação.  
Sort default: `updatedAt DESC`.

**200** página.

---

## 2. GET `/projects/{id}`

**Roles:** MANAGER, LEADER  
**200** / **404** / **403** OPERATOR

---

## 3. GET `/projects/by-idea/{ideaId}`

**Roles:** MANAGER, LEADER  
**200** ProjectResponse  
**404** se ideia sem projeto

Usado pelo Android após 409 no create (fluxo idempotente).

---

## 4. POST `/projects`

**Roles:** MANAGER

```json
{
  "ideaId": "idea-uuid"
}
```

Apenas `ideaId`. Title/description copiados.

**201** ProjectResponse BACKLOG  
**404** ideia inexistente  
**422** ideia não APPROVED/PRIORITIZED  
**409** `IDEA_ALREADY_HAS_PROJECT`  
**403** OPERATOR/LEADER

---

## 5. PUT `/projects/{id}`

**Roles:** MANAGER

```json
{
  "title": "App mobile para fila express",
  "description": "Escopo atualizado.",
  "status": "IN_DEVELOPMENT",
  "investmentAmount": 15000.0,
  "obtainedProfit": 22000.0,
  "productivityGainPercent": 12.5,
  "deadline": "2026-12-31T00:00:00Z",
  "guidelineId": "guideline-uuid"
}
```

| Campo | Validação |
|-------|-----------|
| title | 3–120 |
| description | 10–4000 |
| status | enum ProjectStatus |
| investmentAmount | ≥ 0, finito |
| obtainedProfit | ≥ 0, finito |
| productivityGainPercent | 0–100 |
| deadline | ISO-8601 ou null |
| guidelineId | existente ou null |

Não enviar `ideaId`/`managerId` (ignorados se enviados).

**200**  
**404**  
**403** LEADER/OPERATOR  
**400** números inválidos

Android hoje envia prazo como “dias a partir de agora”. O mapper remoto converte `deadlineDays` → ISO no Data layer **ou** o ViewModel continua calculando epoch e o mapper manda ISO. Preferência: converter no mapper a partir do `Project` de domínio (`deadlineEpochMillis`).

---

## 6. DELETE `/projects/{id}`

**Roles:** MANAGER  
**204**  
**404**  
**403** LEADER/OPERATOR

Ideia permanece.

---

## Regras de cálculo na resposta

Ver `08-calculations.md`. Sempre incluir `roiPercent`.
