# API — Auth e Users

Prefixo `/api/v1`. Erros no contrato de `07-api-conventions.md`.

---

## 1. POST `/auth/register`

**Nome:** Registrar usuário  
**Propósito:** persistir a tela de cadastro já existente no Login.  
**Auth:** pública  
**Roles:** —

**Request**

```json
{
  "name": "Ana Silva",
  "email": "ana@innovatecorp.com",
  "password": "oper123",
  "role": "OPERATOR"
}
```

| Campo | Validação |
|-------|-----------|
| name | 2–80 |
| email | válido, unique |
| password | 4–72 |
| role | OPERATOR \| MANAGER \| LEADER |

**201** body = mesmo do login (tokens + user). Já autentica.

**409** `EMAIL_ALREADY_EXISTS`  
**400** validação  
**429** rate limit

---

## 2. POST `/auth/login`

**Propósito:** autenticar e emitir JWT.

```json
{ "email": "operador@innovatecorp.com", "password": "oper123" }
```

**200**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "3d2f9c8a-....",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "11111111-1111-1111-1111-111111111111",
    "name": "Ana Operadora",
    "email": "operador@innovatecorp.com",
    "avatarUrl": null,
    "role": "OPERATOR"
  }
}
```

**401** `INVALID_CREDENTIALS` — mensagem: `"E-mail ou senha inválidos."`  
**429** rate limit

Não chamar FakerAPI.

---

## 3. POST `/auth/refresh`

```json
{ "refreshToken": "3d2f9c8a-...." }
```

**200** mesmo envelope do login (user atualizado do banco).  
**401** `INVALID_REFRESH_TOKEN`

---

## 4. POST `/auth/logout`

**Auth:** Bearer  
**Body opcional:** `{ "refreshToken": "..." }`  
**204** No Content

---

## 5. POST `/auth/reset-password`

Público, acadêmico.

```json
{ "email": "operador@innovatecorp.com", "newPassword": "nova1234" }
```

**204** se o e-mail existe **ou não** (não enumerar), **exceto** se e-mail formato inválido → 400.  
Se existir, atualiza hash.  
**429** rate limit.

Android: mapear sucesso para a mensagem atual de reset.

---

## 6. GET `/auth/me`

**Auth:** sim  
**200** `UserResponse` (sem tokens).

Usado para hidratar sessão após cold start com access ainda válido.

---

## 7. PATCH `/users/me`

**Auth:** sim

```json
{
  "name": "Ana Silva",
  "email": "operador@innovatecorp.com",
  "avatarUrl": "https://example.com/a.png"
}
```

Todos opcionais; ao menos um campo.  
**200** UserResponse  
**409** e-mail em uso  
**400** name < 2, e-mail inválido

Substitui `UpdateUserProfileUseCase` local.

---

## 8. GET `/users`

**Roles:** MANAGER, LEADER  
**Propósito:** ranking/sugestões/colaboradores reais no lugar de `MockOperatorsCatalog`.

Query: `role` (opcional), paginação, `q` (name/email).

**200** página de:

```json
{
  "id": "...",
  "name": "João Pereira",
  "email": "joao.pereira@innovatecorp.com",
  "avatarUrl": null,
  "role": "OPERATOR"
}
```

Sem hash. OPERATOR → 403.

Sugestões no Android devem passar a listar OPERATOR deste endpoint (ajuste mínimo de ViewModel, sem redesign).

---

## UserResponse (canônico)

```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "avatarUrl": "string|null",
  "role": "OPERATOR"
}
```
