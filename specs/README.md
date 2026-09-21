# Specs — Sprint 2 · Backend Águia Branca

Este diretório contém **somente especificações**. Nenhum arquivo aqui é código de implementação do backend.

Um agente de implementação deve seguir estas specs **nesta ordem** e **não tomar decisões arquiteturais novas** sem registrar um ADR complementar.

## Como usar

1. Ler `history.md` e `01-sprint1-as-is.md` para entender o que já existe.
2. Ler `00-overview.md` para o objetivo, DoD e o que está fora de escopo.
3. Seguir os ADRs em `03-adrs.md` — são decisões fechadas.
4. Implementar backend conforme arquitetura, domínio, segurança, persistência e APIs.
5. Integrar o Android conforme `11-android-integration.md` **sem redesenhar a UI**.
6. Cumprir testes, operações, rastreabilidade e critérios de aceite.

## Índice

| Arquivo | Conteúdo |
|---------|----------|
| [history.md](history.md) | Histórico do projeto e estado atual |
| [00-overview.md](00-overview.md) | Objetivo, escopo, DoD, fora de escopo |
| [01-sprint1-as-is.md](01-sprint1-as-is.md) | Estado real da Sprint 1, contratos e gaps |
| [02-architecture.md](02-architecture.md) | Arquitetura do backend, diagramas, módulos |
| [03-adrs.md](03-adrs.md) | Decisões arquiteturais (persistência, JWT, IA, erros) |
| [04-domain.md](04-domain.md) | Entidades, atributos, índices, embedding vs referência |
| [05-business-rules.md](05-business-rules.md) | Ciclo de inovação, transições, RBAC funcional |
| [06-security.md](06-security.md) | Authn, authz, JWT, secrets, CORS, HTTP errors |
| [07-api-conventions.md](07-api-conventions.md) | Convenções REST, paginação, erros, versionamento |
| [api/auth-users.md](api/auth-users.md) | Endpoints de auth e usuários |
| [api/guidelines.md](api/guidelines.md) | Estratégias/diretrizes + histórico |
| [api/ideas.md](api/ideas.md) | Ideias e curadoria |
| [api/projects.md](api/projects.md) | Projetos e resultados |
| [api/suggestions-notifications.md](api/suggestions-notifications.md) | Sugestões e notificações derivadas |
| [api/dashboard-reports-ai.md](api/dashboard-reports-ai.md) | Dashboard, relatórios e insights de IA |
| [08-calculations.md](08-calculations.md) | Fórmulas financeiras e KPIs |
| [09-ai-gemini.md](09-ai-gemini.md) | Integração Google Gemini |
| [10-external-integrations.md](10-external-integrations.md) | FakerAPI, AdviceSlip e substituições |
| [11-android-integration.md](11-android-integration.md) | Migração Retrofit sem quebrar Clean Architecture |
| [12-persistence.md](12-persistence.md) | MongoDB, coleções, seed |
| [13-testing.md](13-testing.md) | Estratégia de testes |
| [14-operations.md](14-operations.md) | Configuração, logging, execução, OpenAPI |
| [15-traceability.md](15-traceability.md) | Matriz Requisito → Endpoint → Teste |
| [16-acceptance.md](16-acceptance.md) | Critérios Given/When/Then |

## Precedência

1. Código real da Sprint 1
2. Documentação da Sprint 1
3. Requisitos explícitos da Sprint 2 (este conjunto)
4. ADRs nestas specs
5. Conhecimento geral de engenharia

## Regra de ouro

O frontend **não é** camada de autorização. Toda regra de perfil é aplicada no backend.
