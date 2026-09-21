# 09 — Integração Google Gemini

## 1. Objetivo

Gerar **insights interpretativos** para o Líder a partir de totais já calculados pelo dashboard.  
Não é chat livre. Não é demonstração desconectada.  
A resposta é **opinião derivada dos números**, não fato contábil.

## 2. Quem chama Gemini

Somente o backend. O Android nunca embute a API key.

Endpoint: `POST /api/v1/ai/insights` (LEADER).

## 3. Provedor

- API: Google Gemini (AI Studio / Generative Language API)
- Auth: query `key` **ou** header `x-goog-api-key` — usar header.
- Modelo env `GEMINI_MODEL` default `gemini-3.6-flash` (se 404 de modelo, tentar `gemini-3.5-flash-lite` ou o flash gratuito vigente; encapsular em um único client).
- HTTP timeout: **12s**
- Retry: **1** retry em 429/503 com backoff 1s. Sem retry em 400.
- Rate limit app: 5 gerações / 10 min / user (além do quota Google)

## 4. Proteção da key

- `GEMINI_API_KEY` só em env
- Nunca logar a key nem o header
- Nunca devolver a key em erro

## 5. Entrada enviada ao modelo (permitida)

Objeto JSON compacto, **sem PII**:

```json
{
  "totalProjects": 10,
  "activeProjects": 6,
  "completedProjects": 4,
  "totalInvestment": 100000,
  "totalObtainedProfit": 130000,
  "overallRoiPercent": 30.0,
  "averageProductivityGainPercent": 8.4,
  "averageDeadlineDays": 45,
  "projectsByStatus": { "BACKLOG": 2, "IN_DEVELOPMENT": 3, "COMPLETED": 4 },
  "ideasPending": 7,
  "ideasApprovedOrPrioritized": 9,
  "ideasRejected": 2,
  "strategies": [
    { "title": "Transformação Digital", "projects": 4, "roiPercent": 37.5 }
  ]
}
```

**Proibido enviar:** e-mail, senha, JWT, nomes de pessoas, avatars, texto completo de ideias, justificativas, mensagens de sugestão, API keys.

Títulos de estratégia são institucionais e **permitidos** (não são PII de pessoa). Se o título parecer nome de pessoa, ainda assim é dado de guideline — aceitável.

Truncar lista `strategies` em 8 itens. Números com 2 casas.

## 6. Prompt

**System instruction (fixa):**

```
Você é um analista de inovação corporativa da InnovateCorp.
Comente APENAS os números JSON fornecidos.
Não invente métricas ausentes.
Não trate suas frases como auditoria ou garantia financeira.
Estrutura obrigatória em português (Brasil), no máximo 220 palavras:
1) Interpretação dos resultados
2) Tendências ou concentrações
3) Pontos de atenção
4) Oportunidades e recomendações práticas para a liderança
Seja concreto e use os números citados.
```

**User message:**

```
Dados do dashboard:
{json}
```

`temperature`: 0.4  
`maxOutputTokens`: 512

## 7. Formato esperado da resposta

Texto puro (não JSON). O backend grava `content` como string.

Se o modelo devolver markdown, preservar (a UI pode mostrar Text simples).

## 8. Resposta inválida

Inválida se: vazia, só whitespace, ou > 8000 chars (cortar em 4000 com reticências).  
Se vazia → fallback.

## 9. Fallback obrigatório

Texto:

```
Não foi possível gerar um insight automático agora. Com base nos dados internos: ROI consolidado de {overallRoiPercent}%, investimento de {totalInvestment} e {totalProjects} projeto(s). Revise projetos em execução e o volume de ideias pendentes antes de novas alocações. Este texto é um fallback local, não uma análise de IA.
```

`source = FALLBACK`. HTTP 200.

## 10. Erros da API Google

| HTTP Google | Ação |
|-------------|------|
| 200 + texto | SUCCESS |
| 400 | ERROR log + fallback |
| 401/403 key | ERROR log WARN (sem key) + fallback |
| 429 | retry 1x + fallback |
| 5xx | retry 1x + fallback |
| timeout | fallback |
| key vazia | não chama rede; fallback |

Não relançar ao cliente como 500.

## 11. Persistência

Gravar `ai_insights` com `inputSummary` (= JSON enviado), `content`, `status`, `model`.  
Não gravar system prompt completo se redundante; gravar `promptVersion = 1`.

## 12. Observabilidade

Log INFO: userId, durationMs, source, model, charCount.  
Log WARN: status Google, **sem** body completo se enorme.  
Métrica conceitual: `ai.generate.success` / `ai.generate.fallback`.

## 13. Custo / limite acadêmico

Uso do tier gratuito. Documentar no README: quota diária da Google pode esgotar; fallback cobre a demo.

## 14. Testes (ver 13-testing)

Mock do client HTTP: sucesso, timeout, 429, JSON vazio, key missing.
