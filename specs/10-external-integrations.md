# 10 — Integrações externas

## 1. Mapa de decisão

| Fornecedor Sprint 1 | Decisão Sprint 2 | Motivo |
|---------------------|------------------|--------|
| FakerAPI `GET /api/v1/users` | **Descontinuar** | Auth real + users Mongo; mapper por índice era frágil |
| AdviceSlip `GET /advice` | **Manter no backend** | Home do operador; keyless; mover para não depender do app |
| Chat mock | **Permanecer só no Android** | Sem domínio persistido |
| Google Gemini | **Novo, backend** | Requisito Sprint 2 |

## 2. AdviceSlip (mantida)

- Fornecedor: Advice Slip API
- URL: `GET https://api.adviceslip.com/advice`
- Auth: nenhuma
- Request: sem body
- Response:

```json
{ "slip": { "id": 123, "advice": "Keep it simple." } }
```

- Timeout: 5s
- Retry: 1x em I/O/5xx
- Fallback: mensagem fixa PT (`09` / `api/dashboard-reports-ai.md`)
- Mapeamento: `{ id, message: advice }`
- Cache: in-memory **60 minutos** por instância (o app chama “do dia”; a API é random — cache reduz ruído e carga)
- Erro: nunca 5xx para o app; 200 fallback
- Logging: INFO duration; não há secret

Android: `InsightRepository` passa a chamar `InnovationApi.getDailyInsight()` em vez de `AdviceApiService`. Remover Retrofit Advice do `NetworkModule` após migração.

## 3. FakerAPI (removida)

- Não configurar client.
- `UserRepository.fetchSeedUsers` deixa de ser usado no login.
- `FetchSeedUsersUseCase` / `UserRepositoryImpl` Faker: remover da cadeia de auth; pode permanecer arquivo morto **somente** se o compilador não exigir — preferir deletar uso no `AuthenticateUserUseCase`.
- `RemoteBootstrapVerifier` DEBUG: deixar de exigir Faker; opcionalmente pingar `/auth/login` não se aplica no app startup. **Remover ou no-op** o verifier Faker.

## 4. Google Gemini

Ver `09-ai-gemini.md`.

## 5. MongoDB

Não é API de negócio externa; conexão via URI. Timeout serverSelection 10s. Falha de boot se URI inválida no profile local (fail fast).

## 6. Resumo para o implementador

```
Android  --JWT-->  Spring
                    |-- MongoDB
                    |-- AdviceSlip (insights/daily)
                    |-- Gemini (ai/insights)
                    x-- FakerAPI (não)
```
