# 08 — Cálculos financeiros e KPIs

Fonte de verdade: código Sprint 1 (`Project.roiPercent`, `RoiDashboardSummary.fromProjects`, `ManagerDashboardStats`, `OperatorKpiTrend`).  
O backend **replica** estas fórmulas. O Android **para de calculá-las** para dashboard/ROI após a migração (pode manter `Project.roiPercent` como getter local **somente se** o DTO já trouxer o mesmo número — preferir usar o campo do DTO).

Números em `double`. Percentuais **não** arredondados no JSON (o app formata `%.0f`).

Divisão por zero: resultado `0.0`, nunca NaN/Infinity (filtrar e rejeitar no write).

---

## 1. ROI de um projeto

Seja \( I \) = `investmentAmount`, \( P \) = `obtainedProfit`.

\[
\mathrm{roiPercent} =
\begin{cases}
\dfrac{P - I}{I} \times 100, & I > 0 \\
0, & I \le 0
\end{cases}
\]

Interpretação herdada do código: `obtainedProfit` é o **retorno financeiro registrado** pelo gestor, comparado ao investimento. Não há campo separado de “receita”.  
**Lucro consolidado** no dashboard = soma de `obtainedProfit` (não `P - I`). O ROI é que usa a diferença.

---

## 2. Agregados de portfólio (dashboard líder)

Conjunto \( S \) = projetos no filtro (todos, se sem período).

\[
\mathrm{totalInvestment} = \sum I_k
\]

\[
\mathrm{totalObtainedProfit} = \sum P_k
\]

\[
\mathrm{overallRoiPercent} =
\begin{cases}
\dfrac{\mathrm{totalObtainedProfit} - \mathrm{totalInvestment}}{\mathrm{totalInvestment}} \times 100, & \mathrm{totalInvestment} > 0 \\
0, & \text{senão}
\end{cases}
\]

\[
\mathrm{averageProductivityGainPercent} =
\begin{cases}
\dfrac{1}{|S|} \sum \mathrm{productivityGainPercent}_k, & |S| > 0 \\
0, & |S| = 0
\end{cases}
\]

Inclui projetos com produtividade 0 (média do código usa `projects.map { it.productivityGainPercent }.average()`).

\[
\mathrm{activeProjectsCount} = \# \{ k \mid \mathrm{status}_k \neq \mathrm{COMPLETED} \}
\]

\[
\mathrm{completedProjectsCount} = \# \{ k \mid \mathrm{status}_k = \mathrm{COMPLETED} \}
\]

\[
\mathrm{totalProjects} = |S| = \mathrm{active} + \mathrm{completed}
\]

---

## 3. Prazo médio (novo no backend)

Para projetos com `deadline != null`:

\[
\mathrm{deadlineDays}_k = \max\left(0,\; \frac{\mathrm{deadline}_k - \mathrm{createdAt}_k}{86400000}\right)
\]

\[
\mathrm{averageDeadlineDays} = \mathrm{média}(\mathrm{deadlineDays}_k)
\]

Se nenhum projeto tem deadline: `averageDeadlineDays = null`.

Não usar “dias restantes até hoje” no consolidado (depende do relógio e muda sozinho). O editor Android grava deadline absoluto.

---

## 4. Por estratégia

Mesmas fórmulas do portfólio, filtrando `project.guidelineId == id`.  
Ideias da estratégia: `idea.guidelineId == id` para `ideasCount`.

Projetos sem guideline entram no bucket `"Sem estratégia"`.

---

## 5. Taxa de aprovação (gestor)

\[
\mathrm{approvalRatePercent} = \mathrm{round}\left(\frac{\#\{\mathrm{APPROVED \lor PRIORITIZED}\}}{\# ideas} \times 100\right)
\]

`round` = `kotlin.math.roundToInt` (half up). Se 0 ideias: 0.

---

## 6. Tendência mês vs mês

Timezone `America/Sao_Paulo`.

- Mês atual: `[startOfMonth, startOfNextMonth)`
- Anterior: `[startOfPreviousMonth, startOfMonth)`

Contagem no atributo filtrado (ideias criadas; ideias aprovadas usa `createdAt` da ideia **como o código atual**, não a data da decisão — **preservar**, mesmo sendo impreciso). Sprint 2 **pode** melhorar usando `decisionHistory.occurredAt` para aprovadas; **decisão:** usar `occurredAt` do **primeiro** evento APPROVED/PRIORITIZED para tendência de aprovadas (melhoria justificada). Documentar no README. Teste deve fixar relógio.

Formato de label (copiar `OperatorKpiTrend` / `ManagerDashboardStats`):

| Situação | Label |
|----------|-------|
| current=0 e previous=0 | `Sem dados no período` |
| previous=0 e current>0 | `Primeiro registro neste mês` |
| current=0 e previous>0 | `↓ 100% vs mês anterior` |
| percent>0 | `↑ {percent}% vs mês anterior` |
| percent<0 | `↓ {abs}% vs mês anterior` |
| percent=0 | `Igual ao mês anterior` |

\[
percent = \mathrm{round}\left(\frac{current - previous}{previous} \times 100\right)
\]

quando previous ≠ 0.

---

## 7. Barras mensais do gestor

Últimos 5 meses incluindo o atual. Label: mês curto PT-BR com inicial maiúscula (`Mai`, `Jun`). Count = ideias com `createdAt` no mês.

---

## 8. Ranking

`ideasSubmitted` = count por `authorId`.  
`ideasApproved` = count status IN (APPROVED, PRIORITIZED).  
Sort submitted DESC, tie-break name ASC.

Filtro MONTH: ideias com createdAt ≥ início do mês atual.

---

## 9. O que o Android deixa de calcular

| Hoje | Depois |
|------|--------|
| `RoiDashboardSummary.fromProjects` no ViewModel | GET `/dashboard/leader` |
| `ManagerDashboardStats.*` | GET `/dashboard/manager` |
| `OperatorKpiTrend` | GET `/dashboard/operator` (labels prontos) |
| Rankings use cases | GET `/dashboard/rankings` |
| `project.roiPercent` getter | campo DTO; getter pode permanecer como fallback |

Gamificação **permanece** no app (`OperatorGamification.fromIdeasCount`).
