# 14 — Operações, configuração, logging, documentação de execução

## 1. Estrutura de config

`application.yml` (sem secrets):

```yaml
server:
  port: 8080
spring:
  application:
    name: aguiabranca-api
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/aguiabranca}
      database: ${MONGODB_DATABASE:aguiabranca}
app:
  jwt:
    secret: ${JWT_SECRET}
    issuer: aguiabranca
    access-token-ttl: PT1H
    refresh-token-ttl: P7D
  gemini:
    api-key: ${GEMINI_API_KEY:}
    model: ${GEMINI_MODEL:gemini-3.6-flash}
    timeout: PT12S
  advice:
    base-url: ${ADVICE_BASE_URL:https://api.adviceslip.com}
    timeout: PT5S
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:8080}
  seed:
    enabled: ${SEED_ENABLED:false}
```

Profile `local`: `seed.enabled=true`.

## 2. `.env.example` (sem valores reais secretos)

```
MONGODB_URI=mongodb://localhost:27017/aguiabranca
MONGODB_DATABASE=aguiabranca
JWT_SECRET=substitua-por-32bytes-minimo-de-segredo
JWT_ACCESS_TTL=PT1H
JWT_REFRESH_TTL=P7D
GEMINI_API_KEY=
GEMINI_MODEL=gemini-3.6-flash
ADVICE_BASE_URL=https://api.adviceslip.com
CORS_ALLOWED_ORIGINS=http://localhost:8080
SEED_ENABLED=true
SPRING_PROFILES_ACTIVE=local
```

`.env` e `application-prod.yml` com secret: **gitignore**.

Spring: `java-dotenv` opcional **ou** só variáveis do SO / IDEA env. Não obrigar biblioteca se o README ensinar `export`.

## 3. Logging

- JSON **ou** padrão Spring com `traceId`
- MDC: `traceId` = `X-Request-Id`
- Níveis: INFO request (method, path, userId, status, duration); DEBUG query Mongo só em local
- NUNCA: password, Authorization, refresh token, Gemini key, JWT inteiro
- Auth: logar `login success/fail` com email **mascarado** (`o***@innovatecorp.com`) + IP
- Integrações: logger `ext.gemini`, `ext.advice` com status/latency
- `@ControllerAdvice`: 5xx ERROR com stack; 4xx WARN sem stack

Exceção não tratada → 500 `INTERNAL_ERROR` mensagem `"Erro interno. Informe o código {traceId}."`

## 4. Observabilidade mínima

Sem ELK obrigatório. Suficiente:

- access log
- health: `GET /actuator/health` (mongo down → DOWN)
- Não expor `/actuator/env`

## 5. Como executar (texto para o README do backend)

Pré-requisitos: JDK 21, MongoDB 7 local ou Docker, Maven 3.9.

```
docker run -d --name aguia-mongo -p 27017:27017 mongo:7
cd backend
copy .env.example .env   # Windows
mvn spring-boot:run
```

Swagger: `http://localhost:8080/swagger-ui.html`

App Android: `API_BASE_URL=http://10.0.2.2:8080/`

Contas demo: iguais à Sprint 1.

Gemini: colar key gratuita da AI Studio em `GEMINI_API_KEY`. Sem key, insights usam fallback.

## 6. Documentação a gerar na implementação

| Artefato | Origem |
|----------|--------|
| `backend/README.md` | este arquivo + contas + ADRs resumidos |
| OpenAPI | springdoc |
| Arquitetura | copiar mermaid de `02-architecture.md` |
| Modelo | `04-domain.md` |
| Segurança | `06-security.md` |

## 7. Dependências Maven essenciais (orientação)

- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-mongodb`
- `jjwt-api` / `jjwt-impl` / `jjwt-jackson` (0.12.x)
- `springdoc-openapi-starter-webmvc-ui`
- `spring-boot-starter-actuator`
- Test: `spring-boot-starter-test`, `spring-security-test`, `testcontainers-junit-jupiter`, `mongodb` testcontainers

**Não** incluir `spring-boot-starter-data-jpa`.

## 8. CORS e Android

App nativo ignora CORS. Emulador usa HTTP cleartext (debug).

## 9. Ordem de boot

1. Mongo ping
2. índices
3. seed se enabled
4. Tomcat 8080
