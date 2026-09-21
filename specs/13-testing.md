# 13 — Estratégia de testes

Não implementar os testes nesta fase spec; a Sprint 2 de código deve cobrir o que segue.

## 1. Unitários (JUnit 5, sem Spring ou `@ExtendWith`)

| Área | Casos |
|------|-------|
| ROI / agregados | I=0 → 0; I>0 fórmula; lista vazia; média produtividade inclui zeros |
| Tendência | previous=0; current=0; percent pos/neg |
| Máquina ideia | PENDING→APPROVED ok; REJECTED terminal; REJECTED sem justification inválido; REJECTED com projeto inválido |
| Create project | ideia PENDING recusada; duplicata |
| AuthZ helpers | OPERATOR não escreve guideline |
| Prompt Gemini | JSON de entrada **não contém** email/name |
| Password | BCrypt match |
| JWT | expira; role claim |

## 2. Integração Mongo (Testcontainers `mongo:7`)

- Repositories: insert/find/índice unique email e ideaId
- History append após update guideline
- Seed idempotente

## 3. API (MockMvc ou `@SpringBootTest` + Testcontainers)

Happy path por recurso. Assert status + JSON path + codes.

Cobertura mínima de status: 200, 201, 204, 400, 401, 403, 404, 409, 422.

## 4. Segurança (obrigatório)

| # | Cenário | Esperado |
|---|---------|----------|
| S1 | GET `/ideas` sem token | 401 |
| S2 | token lixo | 401 |
| S3 | access expirado (clock) | 401 TOKEN_EXPIRED |
| S4 | OPERATOR POST `/guidelines` | 403 |
| S5 | OPERATOR PATCH `/ideas/{id}/status` | 403 |
| S6 | OPERATOR GET ideia de outro | 404 |
| S7 | LEADER POST `/projects` | 403 |
| S8 | MANAGER POST `/guidelines` | 403 |
| S9 | LEADER PATCH status ideia | 403 |
| S10 | login senha errada | 401 mesma mensagem |
| S11 | refresh revogado após logout | 401 |

## 5. IA

| # | Cenário | Esperado |
|---|---------|----------|
| A1 | stub 200 texto | 200 source GEMINI |
| A2 | timeout | 200 FALLBACK |
| A3 | 429 | fallback após retry |
| A4 | body vazio | FALLBACK |
| A5 | key missing | FALLBACK, **não chama** rede |
| A6 | OPERATOR POST ai | 403 |

WireMock ou mock bean `GeminiClient`.

## 6. AdviceSlip

Stub sucesso; stub down → fallback 200.

## 7. Integração Android (cenários E2E essenciais)

Manual ou Espresso se houver tempo (Etapa 8 ainda aberta):

1. Login operador demo → home
2. Criar ideia válida → aparece na lista
3. Logout → login gestor → curadoria vê a ideia
4. Aprovar → criar projeto → editar investimento/lucro
5. Login líder → dashboard ROI ≠ 0 se dados → gerar insight (GEMINI ou FALLBACK)
6. Operador tenta (via proxy/teste API) criar diretriz → 403

## 8. Qualidade

- Nomes Given_When_Then nos testes de API
- Um `@DisplayName` por caso de segurança
- Coverage de services de cálculo ≥ regras da tabela 08
- CI local: `mvn test` no backend

## 9. O que não testar

UI Compose pixel-perfect, chat mock, gamificação.
