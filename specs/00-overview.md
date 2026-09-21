# 00 — Overview da Sprint 2

## 1. Missão

Transformar o protótipo Android da Sprint 1 (Room + mocks + APIs públicas) em uma **plataforma real** de gestão de inovação:

```
Android (UI / ViewModel / Use Case / Repository IF)
        → Remote Repository (Retrofit + JWT)
        → Spring Boot API
        → MongoDB
        → Gemini + AdviceSlip (via backend)
```

A Sprint 2 **não redesenha o frontend**. Ela substitui a fonte de dados e adiciona contratos que o código atual ainda não tem (estratégia vinculada, histórico, IA, RBAC servidor).

## 2. Entregáveis da implementação (quando a Sprint 2 for executada)

| Entregável | Local previsto |
|------------|----------------|
| Backend Spring Boot | `backend/` na raiz do repositório |
| README do backend | `backend/README.md` |
| `.env.example` | `backend/.env.example` |
| OpenAPI (springdoc) | `/swagger-ui.html` e `/v3/api-docs` |
| Seed de usuários demo | profile `local` |
| App Android integrado | mesmo módulo `app/` |
| Testes backend | `backend/src/test` |

Estas specs **não** criam o código acima.

## 3. Stack obrigatória (fechada)

| Camada | Tecnologia | ADR |
|--------|------------|-----|
| Linguagem | Java 21 | ADR-001 / ADR-002 |
| Framework | Spring Boot 3.4.x | ADR-002 |
| Segurança | Spring Security + JWT (access + refresh) | ADR-003 |
| Banco | MongoDB 7.x | ADR-001 |
| Persistência | **Spring Data MongoDB** (não JPA/Hibernate) | ADR-001 |
| Documentação API | springdoc-openapi | ADR-010 |
| IA | Google Gemini API (modelo flash gratuito) | ADR-006 |
| Insight motivacional | AdviceSlip **proxied** pelo backend | ADR-005 |

## 4. Princípios

1. **Código Sprint 1 é a fonte de verdade** do domínio já construído.
2. **Backend autoriza.** UI apenas esconde ações.
3. **Cálculos no servidor.** Android não recalcula ROI/agregações de negócio.
4. **Secrets fora do Git.**
5. **Contratos estáveis** com os enums e campos já usados nas telas.
6. **Extensões são aditivas** (`guidelineId`, histórico, justificativa) — não quebrar telas existentes.
7. **Chat de colaboradores permanece mock local** (não há persistência nem API na Sprint 1).

## 5. Ciclo de negócio a formalizar

```
OPERAÇÃO
  Operador (ou Gestor) registra ideia/problema
    → vincula (opcional/automático) à estratégia vigente
GESTOR analisa
  → prioriza / aprova / reprova (com justificativa quando pertinente)
  → transforma ideia aprovada ou priorizada em projeto
  → acompanha execução, atualiza status, registra investimento/prazo/resultados
LÍDER acompanha
  → dashboard consolida
  → Gemini gera insights a partir dos dados estruturados
```

Detalhe das transições: `05-business-rules.md`.

## 6. Perfis técnicos

| Funcional | Enum Android e JWT | Escopo de dados |
|-----------|--------------------|-----------------|
| Operador | `OPERATOR` | próprios recursos + leitura global de estratégias |
| Gestor | `MANAGER` | time/global de ideias, projetos, sugestões, dashboard tático |
| Líder | `LEADER` | global de leitura + CRUD de estratégias + dashboard executivo + IA |

Mapeamento completo: `05-business-rules.md` e `06-security.md`.

## 7. Fora de escopo da Sprint 2

Não implementar, a menos que uma spec futura autorize:

- Microserviços / Kubernetes / service mesh
- Chat em tempo real / WebSocket / persistência de mensagens
- Push notification (FCM)
- Gamificação no backend (permanece cálculo visual no app)
- Escolha de perfil após login (já removida no NavGraph)
- Troca de Room por SQL no Android
- Recriação das telas Compose
- Hibernate/JPA sobre MongoDB
- FakerAPI como fonte de usuários
- Pagamentos, e-mail transacional real, OAuth social
- Relatórios PDF/Excel

## 8. Definition of Done — especificação

Uma funcionalidade destas specs está **especificada** quando possui:

- requisito
- fluxo
- regra de negócio
- endpoint (se aplicável)
- validação
- autorização
- erros HTTP
- impacto Android
- teste previsto
- critério de aceite

## 9. Definition of Done — implementação (Sprint 2)

Uma funcionalidade só está **pronta** quando:

1. Comporta-se conforme a spec correspondente.
2. Autorização está no backend (403 real, não só botão escondido).
3. Validações existem no backend mesmo se a UI já valida.
4. Cálculos financeiros vêm do backend.
5. Segredos vêm de variável de ambiente.
6. Há testes unitários + de API/segurança cobrindo o fluxo.
7. O app Android consome o endpoint via Repository remoto, **sem** a UI conhecer HTTP.
8. Casos loading / empty / error / success são tratados.
9. OpenAPI descreve o endpoint.
10. Logs não gravam senha, JWT nem API key.
11. Critérios Given/When/Then de `16-acceptance.md` passam.

## 10. Ordem de implementação recomendada

1. Skeleton Spring Boot + Mongo + security + error handler + OpenAPI
2. Auth (register/login/refresh/me) + seed
3. Users
4. Guidelines + history
5. Ideas + decisions
6. Projects
7. Suggestions
8. Dashboard/reports
9. AdviceSlip proxy
10. Gemini insights
11. Notifications derivadas
12. Integração Android (auth interceptor → repositórios remotos → telas)
13. Testes de segurança e E2E essenciais
