# 11 — Integração com o aplicativo Android

Objetivo: o app Sprint 1 consome o backend **sem redesenhar telas**. Mudanças concentradas em `data/`, `di/`, `AuthenticateUserUseCase` e mappers.

## 1. Arquitetura alvo

```
UI (Compose)           — NÃO alterar fluxos/rotas
  ViewModel            — só tratar 401/403 mensagens se necessário
    Use Case           — manter assinaturas; auth use case SIM altera
      Repository IF    — manter
        Remote Impl    — NOVO
          Retrofit InnovationApi
            AuthInterceptor (Bearer)
            TokenAuthenticator (refresh único)
```

## 2. Interfaces mantidas

Todas as de `domain/repository`.  
Adicionar **não é obrigatório**; se paginação precisar, o Impl remoto busca páginas e devolve `List` via `flow { emit(list) }` (Behavior simples: `callbackFlow` ou `MutableStateFlow` atualizado após fetch).

Sprint 1 usa `Flow` contínuo do Room. HTTP é pull. Padrão:

```
fun observeAll(): Flow<List<X>> = flow {
  emit(api.list(size=100).content.map { it.toDomain() })
}.flowOn(IO)
```

Refresh: ViewModel já coleta no `init`; pull-to-refresh não existe. Reentrar na tab pode não recarregar. **Decisão:** `observe*` dispara GET a cada collection (como init atual). Aceitável. Opcional: repositório com `MutableStateFlow` + `refresh()`. Não é requisito de UI nova.

## 3. Implementações a alterar

| Hoje | Sprint 2 |
|------|----------|
| `IdeaRepositoryImpl` (Room) | `IdeaRemoteRepository` |
| `ProjectRepositoryImpl` | `ProjectRemoteRepository` |
| `GuidelineRepositoryImpl` | `GuidelineRemoteRepository` |
| `ManagerSuggestionRepositoryImpl` (DataStore) | `SuggestionRemoteRepository` |
| `InsightRepositoryImpl` (Advice direto) | `InsightRemoteRepository` |
| `UserRepositoryImpl` (Faker) | remover do login |
| `SessionRepositoryImpl` (Room) | **manter** para User em cache **ou** substituir por DataStore de perfil; tokens em DataStore novo `TokenStore` |

Koin `dataModule`: bind interfaces nas impl remotas.  
Feature flag `BuildConfig.USE_REMOTE_API` default **true** na Sprint 2.

Base URL: `BuildConfig.API_BASE_URL`  
Emulador: `http://10.0.2.2:8080/`  
Device físico: IP da máquina.  
`network_security_config.xml`: cleartext permitido **somente** para debug/`10.0.2.2`/`localhost`.

## 4. Novos tipos Data (Android)

```
data/remote/api/InnovationApi.kt
data/remote/dto/*Dto.kt          // kotlinx.serialization
data/remote/mapper/*Mapper.kt
data/remote/auth/AuthInterceptor.kt
data/remote/auth/TokenAuthenticator.kt
data/local/datastore/TokenStore.kt
```

Não colocar JWT em Room.

## 5. DTOs Android ↔ Domain

| DTO API | Domain |
|---------|--------|
| UserResponse | User |
| IdeaResponse | Idea (+ ignorar history se não houver campo) |
| ProjectResponse | Project (`deadline` → epoch; `roiPercent` pode ser recalculado igual) |
| GuidelineResponse | StrategicGuideline (ignorar category/campaign/version na data class **ou** estender de forma aditiva) |
| SuggestionResponse | ManagerSuggestion (`authorName` → `managerName`) |
| DailyInsightDto | DailyInsight |
| Dashboard* | novos modelos **somente se** ViewModels passarem a usá-los |

### Extensões de domínio recomendadas (aditivas, default)

```kotlin
// Idea
val guidelineId: String? = null
// Project
val guidelineId: String? = null
// StrategicGuideline
val category: String? = null
val campaign: String? = null
val version: Int = 1
```

Sem isso, o mapper descarta os campos. Preferir **estender** as data classes para a Sprint 2 poder mostrar estratégia depois, sem obrigação de mudar Compose agora.

## 6. AuthenticateUserUseCase

Hoje: AuthCatalog + Faker + saveSession.

Novo:

1. `POST /auth/login`
2. `TokenStore.save(access, refresh)`
3. `sessionRepository.saveSession(user)`
4. erros HTTP 401 → `AuthException.InvalidPassword` ou novo `InvalidCredentials`
5. sem rede → `AuthException.UserDataUnavailable`

Remover dependência de `FetchSeedUsersUseCase`.

Register/reset da LoginViewModel: chamar API em vez de `AuthCatalog`.  
Pode manter `AuthCatalog` **apenas** para preenchimento dos botões demo (e-mails), **não** para validar senha.

## 7. Interceptor JWT

- Se TokenStore tem access, adiciona `Authorization: Bearer`
- Não adiciona em `/auth/login|register|refresh|reset-password`

## 8. 401 e refresh

`Authenticator` OkHttp:

- Se response 401 e request ainda não é retry:
  - `POST /auth/refresh` com refresh salvo
  - persiste novo par
  - reexecuta request
- Se refresh falha: `clearSession` + flag para UI ir ao Login

Evitar loop: um refresh por vez (Mutex).

`TOKEN_EXPIRED` e `UNAUTHENTICATED` tratados igual.

403: ViewModel mostra snackbar `"Sem permissão para esta ação."` sem logout.

## 9. Sessão

Cold start:

1. Se há access não-expirado (decode `exp` no client **opcional**) → `GET /auth/me` para validar
2. Se 401 → tenta refresh
3. Se ok → `Routes.homeForRole`
4. Senão Login

Logout: `POST /auth/logout` best-effort + clear tokens + clear session.

## 10. Loading / empty / error / success

Manter padrões atuais (`isLoading`, listas vazias, `insightError`, snackbars).  
Mapear IOException para as mesmas mensagens de conexão.

Paginação: Impl usa `size=100`. Se `totalElements > 100`, log WARN; na Sprint 2 as listas cabem. Não construir Paging 3.

## 11. Telas e origem de dados

| Tela | Origem nova |
|------|-------------|
| Login | Auth API |
| Operator home KPIs | GET `/dashboard/operator` **ou** continuar das ideias próprias se o GET falhar. **Preferir dashboard** para labels. Se esforço: fase 1 calcular ainda no VM a partir da lista remota (fórmulas iguais). **DoD:** pelo menos ROI líder e dashboard gestor vêm da API. Operator KPIs podem permanecer derivados da lista própria na primeira entrega, desde que a lista venha da API. |
| Operator ideias | GET/POST ideas |
| Operator estratégias | GET guidelines |
| Insight | GET `/insights/daily` |
| Notificações | GET `/notifications` **ou** builders locais sobre listas remotas. Preferir endpoint. |
| Curation | GET ideas + PATCH status |
| Manager dashboard | GET `/dashboard/manager` |
| Projects | CRUD projects |
| Suggestions | POST/GET suggestions + GET `/users?role=OPERATOR` |
| Leader dashboard | GET `/dashboard/leader` + POST `/ai/insights` (novo botão **somente se** couber no card existente; se não houver espaço, gerar insight ao abrir o dashboard uma vez por sessão). **Decisão:** ao abrir Leader dashboard, ViewModel chama GET latest; botão “Gerar insight de IA” na home do líder (ação rápida extra). É o único acréscimo de UI **justificado** pelo requisito de IA. Não redesenhar o restante. |
| Leader tracking | GET projects |
| Rankings | GET `/dashboard/rankings` |
| Profile | PATCH `/users/me` |

## 12. O que não mexer

- Tema, bottom bars, rotas, gamificação visual
- `CollaboratorsChatScreen`
- Guards de navegação por role (complementares ao backend)

## 13. Build

`local.properties` ou `gradle.properties`:

```
API_BASE_URL=http://10.0.2.2:8080/
```

`build.gradle.kts` `buildConfigField`. Não commitar IP de produção com secret.

## 14. Remoções após migração estável

- Uso de IdeaDao/ProjectDao/GuidelineDao nos repositories
- DatabaseSeeder de ideias (DEBUG)
- Faker services
- Advice Retrofit direto
- AuthCatalog.validate

Room session pode ficar.

## 15. Estados de erro HTTP no mapper

| HTTP | Efeito |
|------|--------|
| 400/422 | message da API no snackbar |
| 401 | refresh/login |
| 403 | snackbar permissão |
| 404 | empty/not found |
| 409 create project | GET by-idea e seguir |
| 5xx | mensagem genérica |

## 16. Use cases que mudam de implementação mas não de assinatura

Todos os de idea/project/guideline/suggestion/insight.  
`CreateProjectFromIdeaUseCase`: hoje faz regra + insert. Com API, a **regra vive no servidor**; o use case Android pode só chamar `projectRepository.insert` **ou** um método `createFromIdea(ideaId)`. Para não mudar a assinatura `invoke(idea, managerId)`: o remote `insert` ignora campos e manda `{ ideaId }` se um flag interno; **mais limpo:** adicionar `createFromIdea(ideaId)` na interface ProjectRepository.

**Decisão:** estender `ProjectRepository` com

```kotlin
suspend fun createFromIdea(ideaId: String): Project
```

`CreateProjectFromIdeaUseCase` chama esse método após o `require` local **ou** remove o require e deixa o 422/409 do servidor. **Preservar require local** como fail-fast de UX + servidor como autoridade.

`UpdateIdeaStatusUseCase`: passar a enviar justification opcional. Estender:

```kotlin
suspend fun invoke(ideaId: String, status: IdeaStatus, justification: String? = null)
```

UI de curadoria: para REJECTED, diálogo de justificativa **mínimo** (campo extra no card). Sem isso o backend rejeita 400. **Exceção permitida de UI:** um `OutlinedTextField` de justificativa no `CurationIdeaCard` ao reprovar. Sem redesign de layout geral.

## 17. Testes Android essenciais (pós-integração)

- Mapper DTO → Domain
- Interceptor adiciona Bearer
- Fake API: 403 em update guideline como operator (unidade do repo)

E2E device: login operador, criar ideia, login gestor, aprovar, criar projeto, login líder, ver dashboard. Manual aceitável se instrumented for caro; spec de teste backend cobre a API.
