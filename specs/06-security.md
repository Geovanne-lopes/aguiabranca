# 06 — Segurança

Princípio: **o Android não é confiável para autorização.** Esconder o botão “Aprovar” não impede um PATCH. O servidor recusa.

## 1. Autenticação

### 1.1 Login

`POST /api/v1/auth/login`  
Body: `{ "email", "password" }`  
Compara e-mail lowercase + BCrypt.  
User `active=false` → 401 `ACCOUNT_DISABLED`.  
Credencial inválida → 401 `INVALID_CREDENTIALS` (mesma mensagem para e-mail inexistente e senha errada, para não enumerar).

Resposta 200:

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<opaque>",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": { "id": "...", "name": "...", "email": "...", "avatarUrl": null, "role": "OPERATOR" }
}
```

### 1.2 JWT access

- Algoritmo: HS256
- `exp`: now + 3600s
- Claims: `sub`=userId, `email`, `role`, `jti`
- Validação: assinatura, exp, issuer `aguiabranca`

### 1.3 Refresh

`POST /api/v1/auth/refresh` body `{ "refreshToken" }`  
Rotaciona: revoga o antigo, emite novo par.  
Token revogado/expirado/desconhecido → 401 `INVALID_REFRESH_TOKEN`.

### 1.4 Logout

`POST /api/v1/auth/logout` autenticado, body opcional `{ "refreshToken" }`.  
Revoga o refresh informado ou todos do usuário.  
Resposta 204. Client apaga DataStore.

### 1.5 Endpoints públicos

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/reset-password` (acadêmico)
- `GET /actuator/health` (se actuator ligado, só health)
- OpenAPI GET em profile local

Todos os demais exigem Bearer válido.

Reset-password acadêmico: body `{ email, newPassword }`. Rate limit 5/hora/IP. Logar evento sem senha. **Não usar em produção real.** Motivo: a UI Sprint 1 já faz reset sem e-mail transacional.

### 1.6 Acesso sem autenticação a recurso protegido

401 `UNAUTHENTICATED`.

Token malformado / assinatura inválida: 401 `INVALID_TOKEN`.  
Expirado: 401 `TOKEN_EXPIRED`.  
O Android interceptor, ao receber 401 em API de negócio, tenta **uma** vez refresh; se falhar, limpa sessão e navega ao login.

---

## 2. Autorização

Após JWT válido, `@PreAuthorize` / `SecurityExpression` por role.

Exemplos:

- guidelines write: `hasRole('LEADER')`
- ideas status: `hasRole('MANAGER')`
- projects write: `hasRole('MANAGER')`
- ideas read own: service checa `authorId`

Role no token é a fonte. **Não** aceitar header `X-Role`.

`hasRole` no Spring usa prefixo `ROLE_`. Gravar no JWT o valor `OPERATOR` e mapear para `ROLE_OPERATOR` no converter.

---

## 3. Senha

- BCrypt 10
- Tamanho: min 4 (compatibilidade `oper123` e UI), max 72 (limite BCrypt)
- Nunca logar senha
- Nunca retornar `passwordHash`

---

## 4. Secrets

| Secret | Env | Obrigatório |
|--------|-----|-------------|
| Mongo URI | `MONGODB_URI` | sim |
| JWT | `JWT_SECRET` | sim, ≥ 32 chars |
| Gemini | `GEMINI_API_KEY` | sim para IA real; se vazio, IA usa fallback e log WARN |
| DB name | `MONGODB_DATABASE` | default `aguiabranca` |

Não commitar `application-local.yml` com secret real. `.env` no `.gitignore`.

---

## 5. CORS

Profile `local`:

```
allowedOrigins: http://localhost:8080, http://10.0.2.2:8080
allowedMethods: GET,POST,PUT,PATCH,DELETE,OPTIONS
allowedHeaders: Authorization, Content-Type, X-Request-Id
allowCredentials: true
```

O app Android nativo não usa CORS; CORS existe para Swagger e futuros web clients.

Profile `prod`: origins explícitas via `CORS_ALLOWED_ORIGINS`.

---

## 6. Headers

- Cliente envia `Authorization: Bearer`
- Cliente pode enviar `X-Request-Id`; se ausente, servidor gera UUID
- Resposta ecoa `X-Request-Id` (= `traceId` do erro)

---

## 7. Comportamento HTTP de erro (segurança)

| Status | Quando | code típico |
|--------|--------|-------------|
| 400 | JSON inválido, tipos, bean validation | `VALIDATION_ERROR`, `MALFORMED_JSON` |
| 401 | sem/invalid/expired token, login falhou | `UNAUTHENTICATED`, `INVALID_TOKEN`, `TOKEN_EXPIRED`, `INVALID_CREDENTIALS` |
| 403 | autenticado sem papel/escopo | `FORBIDDEN` |
| 404 | recurso inexistente ou operador acessando ideia alheia | `NOT_FOUND` |
| 409 | conflito de unicidade/estado | `EMAIL_ALREADY_EXISTS`, `IDEA_ALREADY_HAS_PROJECT`, `INVALID_STATUS_TRANSITION` |
| 422 | regra de negócio de input semanticamente inválido | `BUSINESS_RULE_VIOLATION` |
| 429 | rate limit auth | `RATE_LIMITED` |
| 500 | bug | `INTERNAL_ERROR` |
| 502/503 | Gemini/AdviceSlip | `DEPENDENCY_UNAVAILABLE` |

Mensagens: português, sem stacktrace. Stack só no log.

422 é adotado (ADR-007). Não misturar 400 e 422: 400 = schema/validação de formato; 422 = domínio.

---

## 8. Rate limit (mínimo)

- login: 10 tentativas / 10 min / IP+email
- register: 10 / hora / IP
- reset-password: 5 / hora / IP
- Gemini generate: 5 / 10 min / user

Implementação aceitável: bucket in-memory (Caffeine) na Sprint 2. Sem Redis obrigatório.

---

## 9. Configuração Spring Security (conceitual)

```
http
  csrf disabled (API stateless)
  session STATELESS
  cors as spec
  authorize:
    public auth + swagger local + health
    anyRequest authenticated
  add JwtAuthFilter before UsernamePasswordAuthenticationFilter
```

---

## 10. Logout e sessão Android

Sprint 1: `clearSession()` apaga Room.  
Sprint 2: apaga tokens DataStore + chama logout + apaga cache de usuário.  
Sem token válido, `InnovationApp` abre Login.

---

## 11. Checklist de ameaças acadêmicas

- IDOR: OPERATOR não lê ideia de outro (404)
- Privilege escalation: LEADER não PATCH idea status (403)
- Mass assignment: ignorar `role` em update de perfil; ignorar `authorId` no create de ideia
- JWT no log interceptor Android: level BASIC em debug, nunca BODY com Authorization em release
