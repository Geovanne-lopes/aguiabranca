# 16 — Critérios de aceite (Given / When / Then)

Critérios objetivos da Sprint 2. Não são código de teste; o implementador os traduz para JUnit/MockMvc.

---

## Autenticação

### A-AUTH-01
Given um usuário seed `operador@innovatecorp.com` com senha `oper123`  
When `POST /api/v1/auth/login` com essas credenciais  
Then 200, `user.role=OPERATOR`, `accessToken` e `refreshToken` não vazios.

### A-AUTH-02
Given um access token expirado e um refresh válido  
When o cliente chama um GET protegido e em seguida `POST /auth/refresh`  
Then o refresh retorna 200 com novo access; o access antigo não autentica.

### A-AUTH-03
Given um usuário autenticado  
When `POST /auth/logout` com o refresh  
Then 204 e o mesmo refresh em `POST /auth/refresh` retorna 401.

### A-AUTH-04
Given um e-mail ainda não cadastrado  
When `POST /auth/register` válido  
Then 201 e um login posterior com a mesma senha retorna 200.  
Given o mesmo e-mail  
When register de novo  
Then 409 `EMAIL_ALREADY_EXISTS`.

### A-AUTH-05
Given um e-mail existente  
When `POST /auth/reset-password` com nova senha ≥ 4  
Then 204 e login com a senha antiga falha; login com a nova sucede.

---

## Segurança

### A-SEC-01
Given nenhuma header Authorization  
When `GET /api/v1/ideas`  
Then 401 `UNAUTHENTICATED`.

### A-SEC-02
Given um JWT com `role=OPERATOR`  
When `POST /api/v1/guidelines`  
Then 403 `FORBIDDEN` e nenhuma guideline é criada.

### A-SEC-03
Given um JWT OPERATOR e uma ideia de outro usuário  
When `GET /ideas/{id}`  
Then 404.

### A-SEC-04
Given um JWT LEADER  
When `PATCH /ideas/{id}/status`  
Then 403.

---

## Usuários

### A-USER-01
Given usuário autenticado  
When `PATCH /users/me` com name válido  
Then 200 e `GET /auth/me` reflete o nome.

### A-USER-02
Given JWT OPERATOR  
When `GET /users`  
Then 403.

---

## Diretrizes

### A-GL-01
Given qualquer papel autenticado  
When `GET /guidelines`  
Then 200 e lista (possivelmente vazia) sem itens deletados.

### A-GL-02
Given JWT LEADER  
When cria, edita e exclui uma guideline  
Then 201, 200, 204 respectivamente.  
Given JWT MANAGER  
When as mesmas escritas  
Then 403.

### A-GL-03
Given uma guideline editada duas vezes  
When `GET /guidelines/{id}/history` como LEADER  
Then há eventos CREATED + UPDATED append-only, versions 1..n.  
When MANAGER chama o history  
Then 403.

---

## Ideias

### A-IDEA-01
Given JWT OPERATOR  
When `POST /ideas` com title ≥3 e description ≥10  
Then 201, `status=PENDING`, `authorId` = sub do token.  
Given JWT LEADER  
When o mesmo POST  
Then 403.

### A-IDEA-02
Given OPERATOR A criou uma ideia  
When OPERATOR A `GET /ideas`  
Then a ideia aparece.  
When OPERATOR B `GET /ideas`  
Then a ideia de A não aparece.

### A-IDEA-03
Given ideias de vários autores  
When MANAGER `GET /ideas`  
Then todas aparecem.

### A-IDEA-04
Given ideia PENDING  
When MANAGER PATCH status APPROVED  
Then 200 e history contém APPROVED.  
When MANAGER PATCH REJECTED **sem** justification  
Then 400.  
When MANAGER PATCH REJECTED **com** justification ≥10  
Then 200.  
When OPERATOR PATCH status  
Then 403.  
When PATCH PENDING→PENDING ou REJECTED→APPROVED  
Then 409 `INVALID_STATUS_TRANSITION`.

### A-IDEA-05
Given existe guideline vigente  
When POST ideia sem guidelineId  
Then `guidelineId` preenchido com a mais recente.  
When POST com UUID inexistente  
Then 422.

---

## Projetos

### A-PROJ-01
Given ideia APPROVED sem projeto e JWT MANAGER  
When `POST /projects` `{ ideaId }`  
Then 201, status BACKLOG, title copiado, guidelineId copiado.  
When POST de novo  
Then 409.  
Given ideia PENDING  
When POST project  
Then 422.  
Given JWT LEADER  
When POST project  
Then 403.

### A-PROJ-02
Given projeto existente e JWT MANAGER  
When PUT com investment 1000, profit 1500, productivity 10  
Then 200 e `roiPercent` = 50.  
When productivity 101  
Then 400.

### A-PROJ-03
Given JWT MANAGER  
When DELETE `/projects/{id}`  
Then 204 e a ideia permanece.  
Given JWT LEADER  
When DELETE  
Then 403.

### A-PROJ-04
Given JWT OPERATOR  
When GET `/projects`  
Then 403.  
Given JWT LEADER  
When GET `/projects`  
Then 200.

---

## Sugestões e notificações

### A-SUG-01
Given JWT MANAGER e um user OPERATOR  
When POST `/suggestions` message ≥10  
Then 201.  
When target é MANAGER  
Then 422.  
When OPERATOR GET `/suggestions`  
Then vê a sugestão.  
When OPERATOR POST `/suggestions`  
Then 403.

### A-NOTIF-01
Given OPERATOR com ideia aprovada  
When GET `/notifications`  
Then existe item tipo IDEA_APPROVED.

---

## Dashboard e cálculos

### A-DASH-01
Given JWT OPERATOR  
When GET `/dashboard/operator`  
Then 200 com contagens das **suas** ideias.  
Given JWT MANAGER  
When o mesmo  
Then 403.

### A-DASH-02
Given ideias e projetos no banco  
When MANAGER GET `/dashboard/manager`  
Then pendingIdeasCount e monthlyBars coerentes com os dados; `hasData=true`.  
Given banco sem ideias  
Then counts 0 e `hasData=false`.

### A-DASH-03
Given dois projetos: (I=100, P=150) e (I=0, P=10)  
When LEADER GET `/dashboard/leader`  
Then totalInvestment=100, totalObtainedProfit=160, overallRoiPercent=60.  
Given JWT OPERATOR  
When GET `/dashboard/leader`  
Then 403.

### A-DASH-04
Given projetos com e sem guidelineId  
When GET `/dashboard/strategies`  
Then há item por estratégia e item `"Sem estratégia"` se houver órfãos.

### A-DASH-05
Given `from` posterior a `to`  
When GET `/dashboard/period`  
Then 400.

### A-RANK-01
Given ideias de usuários reais  
When GET `/dashboard/rankings`  
Then `name`/`email` vêm de `users`, não de mock-op.

### A-CALC-01
Given investmentAmount=0  
When GET projeto  
Then roiPercent=0.  
Given investment=200, profit=100  
Then roiPercent=-50.

---

## Integrações

### A-INS-01
Given AdviceSlip disponível  
When GET `/insights/daily` autenticado  
Then 200 com message não vazia.  
Given AdviceSlip indisponível  
Then 200 fallback id=0.

### A-AI-01
Given JWT LEADER e Gemini stub de sucesso  
When POST `/ai/insights`  
Then 200 `source=GEMINI`, content não vazio, `basedOn` sem e-mail.  
Given Gemini timeout  
Then 200 `source=FALLBACK`.  
Given JWT MANAGER  
When POST `/ai/insights`  
Then 403.

### A-INT-01
Given o backend no ar e o app apontando para ele  
When login  
Then **nenhuma** chamada a `fakerapi.it` é necessária para autenticar.

---

## Operação

### A-OPS-01
Given MongoDB no ar e profile local  
When a API sobe  
Then seed cria os 3 e-mails demo se não existirem; restart não duplica.

### A-OPS-02
Given o repositório  
When inspecionar Git  
Then não há JWT_SECRET nem GEMINI_API_KEY reais; existe `.env.example`.

---

## Android (aceitação de integração)

### A-AND-01
Given app configurado com `API_BASE_URL` do backend local  
When login com contas demo  
Then abre a home do perfil correspondente (comportamento Sprint 1).

### A-AND-02
Given operador cria ideia pelo app  
When gestor abre Curadoria (outro device/sessão)  
Then a ideia aparece (fonte API, não Room isolado).

### A-AND-03
Given gestor aprova e cria projeto e registra I/P  
When líder abre dashboard  
Then ROI/investimento/lucro refletem os valores do servidor.

### A-AND-04
Given token expirado com refresh válido  
When o usuário navega  
Then a sessão permanece sem voltar ao login.  
Given refresh inválido  
Then volta ao login.

### A-AND-05
Given UI de chat colaboradores  
When enviar mensagem  
Then o comportamento mock local **continua** (não quebra a Sprint 2).

---

## Definition of Done (fechamento)

A Sprint 2 de implementação está Done quando A-AUTH-01, A-SEC-01..04, A-GL-02, A-IDEA-01..04, A-PROJ-01..04, A-DASH-03, A-CALC-01, A-AI-01, A-INS-01, A-OPS-02 e A-AND-01..03 passam, OpenAPI está no ar e o README do backend descreve como executar.
