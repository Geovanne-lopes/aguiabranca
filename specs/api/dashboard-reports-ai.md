# API — Dashboard, relatórios e IA (contratos)

Fórmulas em `08-calculations.md`. Gemini em `09-ai-gemini.md`.  
O Android **não** recalcula esses agregados após a migração.

Filtros temporais: `from`, `to` ISO-8601 UTC, aplicados em `projects.updatedAt` salvo menção contrária. Sem filtro = todo o histórico.

Ausência de dados: números **0**, listas vazias, `hasData: false`. Nunca 404 por “zero projetos”.

---

## 1. GET `/dashboard/operator`

**Roles:** OPERATOR

```json
{
  "ideasSubmittedCount": 4,
  "ideasApprovedCount": 1,
  "ideasPrioritizedCount": 1,
  "ideasRejectedCount": 0,
  "ideasPendingCount": 2,
  "submittedThisMonth": 2,
  "submittedPreviousMonth": 1,
  "submittedTrendPercent": 100,
  "approvedThisMonth": 1,
  "approvedPreviousMonth": 0,
  "hasData": true
}
```

O app pode continuar formatando “↑ 100% vs mês anterior” no mapper a partir de `submittedTrendPercent` **ou** o backend envia também `submittedTrendLabel` em PT. **Decisão:** enviar `submittedTrendLabel` e `approvedTrendLabel` já formatados iguais a `OperatorKpiTrend` para não duplicar regra.

---

## 2. GET `/dashboard/manager`

**Roles:** MANAGER, LEADER

```json
{
  "pendingIdeasCount": 7,
  "activeProjectsCount": 3,
  "ideasReceivedThisMonth": 5,
  "ideasReceivedPreviousMonth": 4,
  "ideasReceivedTrendLabel": "↑ 25% vs mês anterior",
  "approvalRatePercent": 40,
  "approvalRateTrendLabel": "40% no total",
  "monthlyBars": [
    { "label": "Mai", "count": 2 },
    { "label": "Jun", "count": 4 }
  ],
  "ideasByStatus": [
    { "status": "PENDING", "label": "Pendente", "count": 7 }
  ],
  "hasData": true
}
```

`monthlyBars`: últimos 5 meses, timezone `America/Sao_Paulo` (app do usuário). Documentar TZ no README.

`ideasByStatus`: extra (gap do PDF). UI pode ignorar.

---

## 3. GET `/dashboard/leader`

**Roles:** LEADER  
MANAGER/OPERATOR: 403

```json
{
  "totalProjects": 10,
  "activeProjectsCount": 6,
  "completedProjectsCount": 4,
  "totalInvestment": 100000.0,
  "totalObtainedProfit": 130000.0,
  "overallRoiPercent": 30.0,
  "averageProductivityGainPercent": 8.4,
  "averageDeadlineDays": 45.0,
  "projectsByStatus": [
    { "status": "BACKLOG", "label": "Planejamento", "count": 2 }
  ],
  "hasData": true
}
```

`averageDeadlineDays`: média de `(deadline - createdAt)` em dias, **somente** projetos com deadline não nulo. Sem nenhum: `null` (UI mostra "—" / 0). **Decisão de display:** se null, Android trata como 0 no card novo; se a UI atual não tem card, o campo fica para o payload.

`projectsByStatus`: uma entrada por enum, count 0 se vazio (o gráfico Sprint 1 usa `ProjectStatus.entries`).

---

## 4. GET `/dashboard/strategies`

**Roles:** LEADER  
Query: `from`, `to` opcionais.

```json
{
  "items": [
    {
      "guidelineId": "g1",
      "guidelineTitle": "Transformação Digital",
      "ideasCount": 12,
      "projectsCount": 4,
      "totalInvestment": 40000.0,
      "totalObtainedProfit": 55000.0,
      "overallRoiPercent": 37.5,
      "averageProductivityGainPercent": 10.0,
      "completedProjectsCount": 1
    }
  ]
}
```

Incluir bucket `guidelineId: null` com title `"Sem estratégia"` se houver projetos/ideias órfãos.

---

## 5. GET `/dashboard/period`

**Roles:** LEADER  
Query **obrigatória:** `from`, `to`.

Agrega projetos cujo `createdAt` está no intervalo (definições: criação do projeto, não da ideia).

Response: mesmo schema de `/dashboard/leader` + `from` + `to`.

**400** se from > to ou ausente.

---

## 6. GET `/dashboard/projects/{id}`

**Roles:** MANAGER, LEADER  
**200** indicadores **de um projeto** (ProjectResponse + campos já calculados).  
**404** se não existe.

Não é duplicata inútil: o relatório “por projeto” é o detalhe. Pode ser **o mesmo** GET `/projects/{id}` — para não redundar, o implementador **deve** usar `GET /projects/{id}` como relatório por projeto e **não** criar um terceiro payload. Este path `/dashboard/projects/{id}` pode ser alias 302 interno ou simplesmente **não existir**.

**Decisão fechada:** **não criar** `/dashboard/projects/{id}`. Relatório por projeto = `GET /projects/{id}`. Remover da tabela mental; manter `GET /projects/{id}` na rastreabilidade R-PROJ-READ.

---

## 7. GET `/dashboard/rankings`

**Roles:** MANAGER, LEADER  
Query: `period=MONTH|ALL` default ALL.

```json
{
  "items": [
    {
      "authorId": "u1",
      "name": "Ana Operadora",
      "email": "operador@innovatecorp.com",
      "ideasSubmitted": 6,
      "ideasApproved": 2
    }
  ]
}
```

`ideasApproved` conta APPROVED **ou** PRIORITIZED (igual `GetOperatorActivityRankingUseCase`).  
MONTH = `createdAt` ≥ início do mês `America/Sao_Paulo`.  
Ordenação: `ideasSubmitted` DESC.

Substitui `MockOperatorsCatalog` nos rankings.

---

## 8. POST `/ai/insights`

**Roles:** LEADER

Body opcional:

```json
{ "from": "2026-01-01T00:00:00Z", "to": "2026-12-31T23:59:59Z" }
```

Sem body: usa totais globais iguais a `/dashboard/leader`.

**200**

```json
{
  "id": "ai-uuid",
  "content": "O ROI consolidado de 30% indica...",
  "source": "GEMINI",
  "disclaimer": "Insight gerado a partir dos dados do sistema; não é um fato independente.",
  "createdAt": "2026-09-21T16:00:00Z",
  "basedOn": {
    "totalProjects": 10,
    "overallRoiPercent": 30.0,
    "totalInvestment": 100000.0
  }
}
```

Se Gemini falhar: **200** `source: "FALLBACK"` com texto padrão (não 500).  
Se key ausente: FALLBACK + log WARN.  
503 só se o implementador optar por não ter fallback — **proibido**; fallback é obrigatório.

---

## 9. GET `/ai/insights/latest`

**Roles:** LEADER  
**200** último insight SUCCESS ou FALLBACK do usuário.  
**404** se nunca gerou.

---

## 10. GET `/insights/daily`

**Auth:** qualquer papel  
AdviceSlip proxy.

**200** `{ "id": 123, "message": "..." }`  
Se AdviceSlip falhar: **200** fallback `{ "id": 0, "message": "Pequenas ideias consistentes constroem grandes resultados." }`  
Nunca quebrar a Home do operador.
