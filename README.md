# Challenge Águia Branca — Gestão de Inovação Corporativa

App Android e API da Sprint 2 para três papéis: operador, gestor e líder.

## O que é

- **App** (`app/`): Kotlin, Jetpack Compose, Koin, Retrofit. Consome a API no prefixo `/api/v1`.
- **API** (`backend/`): Java 21, Spring Boot 3.4, Spring Security, Spring Data MongoDB, JWT (access + refresh).

O enunciado cita JPA/Hibernate ou NoSQL. Este projeto persiste em **MongoDB**; JPA não se aplica. A decisão e o detalhe da API estão em [backend/README.md](backend/README.md) (ADR-001).

## Integração

O login não usa FakerAPI. Ideias, projetos, diretrizes, dashboards, insight diário e insight Gemini passam pelos repositórios remotos. Access e refresh ficam no DataStore.

A UI esconde o botão fora do papel. A recusa é **403** no servidor.

O chat de colaboradores (`CollaboratorsChatScreen`) é local e simulado. Não há endpoint de chat.

## Papéis

| Papel | O que faz |
|-------|-----------|
| **OPERATOR** | Lê diretrizes. Cria ideia. Edita e exclui a própria ideia enquanto está pendente (`PUT` e `DELETE /api/v1/ideas/{id}`). |
| **MANAGER** | Lê diretrizes. Faz a curadoria da ideia (aprovar, reprovar, priorizar). CRUD de projeto. |
| **LEADER** | CRUD de diretriz e leitura do histórico (`GET /api/v1/guidelines/{id}/history`). Lê projetos e o dashboard, inclusive o retorno por estratégia (`GET /api/v1/dashboard/strategies`). Gera o insight de IA. |

Rotas, filtros e códigos de erro: [backend/README.md](backend/README.md).

## IA

O Gemini roda no backend. Só o líder gera insight (`POST /api/v1/ai/insights`).

O zip da entrega **não inclui** `.env` (lá iriam `GEMINI_API_KEY` e `JWT_SECRET`). Quem for testar copia `backend/.env.example` para `backend/.env`.

- **Sem chave** (`GEMINI_API_KEY` vazia, como no example): o botão do líder funciona. A API responde **200** com `source=FALLBACK` — texto local com os números do dashboard e o aviso de que **não é análise de IA**. Não chama a Google.
- **Com chave** (Google AI Studio, no `.env` local, depois **reiniciar** a API): `source=GEMINI` e o texto vem do modelo `gemini-3.6-flash`. Timeout, quota ou modelo indisponível também caem no fallback 200. A chave nunca vai no log nem na resposta.

O insight do dia (AdviceSlip) entra pelo backend (`GET /api/v1/insights/daily`). O app não chama o AdviceSlip direto.

## Código que sobrou da Sprint 1

Room e clientes antigos de API pública podem ainda existir no repositório. Não são a fonte de verdade de login, ideias, projetos ou diretrizes.

## Como rodar

Mongo e API, no PowerShell (aspas no `-D` são obrigatórias):

```powershell
docker start aguia-mongo 2>$null; if ($LASTEXITCODE -ne 0) { docker run -d --name aguia-mongo -p 27017:27017 mongo:7 }
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Seed, arquivo `.env` e Swagger (`http://localhost:8080/swagger-ui.html`): [backend/README.md](backend/README.md).

App: Android Studio, emulador. O padrão já é `API_BASE_URL=http://10.0.2.2:8080/`.

### Contas demo (profile `local`)

| Papel | E-mail | Senha |
|-------|--------|-------|
| Operador | `operador@innovatecorp.com` | `oper123` |
| Gestor | `gestor@innovatecorp.com` | `gest123` |
| Liderança | `lideranca@innovatecorp.com` | `lider123` |

## Entregas

| Pasta | Conteúdo |
|-------|----------|
| [entregas/Sprint-1/](entregas/Sprint-1/) | Protótipo do primeiro semestre (APK, zip, PDF). |
| [entregas/Sprint-2/](entregas/Sprint-2/) | Zip da API, zip do app + APK, apresentação. |
| [entregas/README.md](entregas/README.md) | Índice das pastas. |

`specs/` é material interno de desenvolvimento. A banca entra por este README e por [backend/README.md](backend/README.md).
