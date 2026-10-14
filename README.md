# Challenge Águia Branca — Gestão de Inovação Corporativa

App Android e API da Sprint 2 para três papéis: operador, gestor e líder.

## Arquitetura e tecnologias

- **Android:** Kotlin, Jetpack Compose, Gradle 9.3.1, Android Gradle Plugin 9.1.1.
- **API:** Java 21, Spring Boot 3.4.13, Maven, Spring Security, JWT e Spring Data MongoDB.
- **Persistência:** MongoDB 7. A API recebe a URI pela variável `MONGODB_URI`; dentro do Compose ela usa o hostname do serviço `mongo`.
- **Containers:** Docker multi-stage para compilar e executar somente a API; Compose para API e MongoDB.
- **CI/CD:** GitHub Actions executa testes/build Android e backend, publica a API no GHCR por digest e oferece deploy SSH para staging e produção.

### Estrutura relevante

| Caminho | Conteúdo |
|----------|----------|
| `app/` | Aplicativo Android e testes locais. |
| `backend/` | API Spring Boot, testes Maven e Dockerfile. |
| `compose.yaml` | Stack local da API e MongoDB. |
| `compose.staging.yaml` / `compose.production.yaml` | Stacks isoladas para deploy. |
| `.github/workflows/` | CI, publicação GHCR e workflow reutilizável de deploy. |
| `scripts/deploy-ssh.sh` | Atualização remota por SSH com healthcheck. |
| `docs/devops-technical.md` | Relatório técnico e espaços para evidências. |

### Pré-requisitos

- Docker Desktop com Docker Compose v2 para containerização local.
- JDK 21 e Maven 3.9+ para o backend local.
- JDK 21 e Android SDK para build/teste Android; use `gradlew.bat` no Windows ou `./gradlew` no Linux/macOS.
- GitHub Actions habilitado para CI/publicação. Servidor Linux com Docker Compose, SSH e `curl` só é necessário para deploy.

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

### Docker

Com Docker Desktop iniciado, crie a configuração local e suba a API com o MongoDB:

```powershell
Copy-Item backend/.env.example backend/.env
docker compose up --build -d
docker compose ps
docker compose logs -f api mongo
```

Healthcheck da API: `Invoke-RestMethod http://localhost:8080/actuator/health`. Swagger: `http://localhost:8080/swagger-ui.html`. O profile `local` habilita as contas demo. MongoDB usa o volume `aguiabranca_mongo-data`, a rede `aguiabranca-local` e só publica `27017` no loopback. Para parar sem apagar dados, use `docker compose down`; `docker compose down -v` remove os dados locais.

O `.env` é ignorado pelo Git e não entra na imagem. Antes de uso fora da máquina local, substitua o `JWT_SECRET` ilustrativo por um segredo aleatório próprio com pelo menos 32 bytes. O app Android continua executado no Android Studio/emulador; no emulador, use `http://10.0.2.2:8080/`.

### Build e testes

Na raiz do repositório, com JDK 21 e Maven instalados:

```powershell
mvn -B -f backend/pom.xml clean verify
./gradlew :app:testDebugUnitTest :app:assembleDebug
docker build -f backend/Dockerfile -t aguiabranca-api:local .
```

O backend contém testes unitários e testes de integração com Testcontainers; a execução completa precisa de Docker. O projeto não tem Maven Wrapper, por isso o CI instala/configura Maven via `actions/setup-java` e usa o Maven disponível no runner. No Windows, use `gradlew.bat :app:testDebugUnitTest :app:assembleDebug`.

### CI e publicação da imagem

`.github/workflows/ci.yml` roda em pull requests e pushes para `main`/`master`; tags `v*.*.*` também disparam o pipeline. O job backend executa `mvn -B -f backend/pom.xml clean verify` e publica relatórios Surefire como artefato. O job Android executa testes unitários e `assembleDebug`, publicando os relatórios e o APK. A imagem Docker da API é construída e enviada ao `ghcr.io/<owner>/<repo>` apenas depois desses jobs passarem, com tag `sha-<commit>` e referência imutável `@sha256:<digest>` para deploy.

Para visualizar resultados, abra **Actions** no GitHub, selecione a execução e baixe `backend-test-reports`, `android-test-reports` ou `aguiabranca-debug-apk`. O pacote GHCR usa `GITHUB_TOKEN` com permissão `packages: write`; não há token de publicação fixo no repositório.

### Staging e produção

`compose.staging.yaml` e `compose.production.yaml` criam bancos, volumes, redes e nomes de projeto distintos. MongoDB não tem porta publicada nesses ambientes. A API fica vinculada a `127.0.0.1:8081` em staging e `127.0.0.1:8082` em produção; configure um reverse proxy TLS no servidor para tráfego externo. Healthcheck do container consulta Actuator e o deploy também aguarda `GET /actuator/health` no host remoto.

O push em `main`/`master` pode acionar staging, mas somente depois de cadastrar a variável de repositório `STAGING_DEPLOY_ENABLED=true`. Uma tag Git `vX.Y.Z` publica a imagem validada e inicia o deploy de produção no GitHub Environment `production`; configure nesse Environment a aprovação manual dos responsáveis. Ambos os deploys reutilizam o mesmo digest que foi gerado pelo build.

Em **Settings > Environments**, crie `staging` e `production`. Em cada ambiente, cadastre separadamente os secrets:

- `DEPLOY_HOST`: hostname/IP do servidor Linux.
- `DEPLOY_USER`: usuário SSH com acesso ao Docker/Compose.
- `DEPLOY_DIRECTORY`: caminho absoluto diferente por ambiente (por exemplo `/opt/aguiabranca/staging` e `/opt/aguiabranca/production`).
- `DEPLOY_SSH_PRIVATE_KEY`: chave privada dedicada ao deploy.
- `DEPLOY_KNOWN_HOSTS`: linha(s) verificadas de `known_hosts`; não desative a validação da chave do host.
- `DEPLOY_ENV_FILE`: conteúdo completo do arquivo de ambiente daquele ambiente, baseado em `.env.staging.example` ou `.env.production.example`, com segredos próprios. Use senha Mongo URL-safe e não reutilize a senha nem o `JWT_SECRET` entre ambientes.
- `GHCR_USERNAME` e `GHCR_READ_TOKEN`: necessários se o pacote GHCR for privado; o token remoto precisa somente de `read:packages`. Para pacote público, podem ficar vazios.

O servidor deve ter Docker Engine/Compose, `curl`, acesso de saída ao GHCR e portas 8081/8082 livres localmente. O deploy não provisiona o host, TLS, DNS, backup ou firewall. O CI de publicação funciona sem servidores; os jobs de deploy ainda não podem ser considerados realizados até cadastrar esses secrets e obter uma execução saudável.

### Evidências e limitações

Registre a URL/ID da execução GitHub Actions, resultado dos jobs, relatório de testes e digest GHCR. Para deploy, anexe também a aprovação do Environment de produção, a saída do healthcheck e `docker compose ps`/logs sem dados sensíveis. Os campos para essas evidências estão em [docs/devops-technical.md](docs/devops-technical.md); não há evidência fictícia neste repositório.

Nesta entrega, a sintaxe dos três Compose foi validada localmente, mas a imagem não foi construída nem os containers iniciados porque o Docker Desktop daemon não estava ativo. Maven também não está instalado neste host. Não foram executados workflows GitHub nem deploys; esses itens dependem do runner, registry, servidores e secrets acima.

### Checklist de entrega

- [x] Projeto compactado em ZIP com estrutura organizada (geração após esta validação; artefato indicado no relatório final).
- [ ] Dockerfile funcional (healthcheck e build multi-stage implementados; build não validado por daemon indisponível).
- [x] Docker Compose para API/MongoDB, incluindo configurações isoladas de staging e produção.
- [x] Volumes, redes e variáveis de ambiente configurados; `.env` ignorado.
- [x] Pipeline com build automático definido.
- [x] Testes automatizados ligados ao pipeline; execução local pendente.
- [ ] Imagem Docker construída e publicada no GHCR.
- [ ] Deploy em staging realizado e validado.
- [ ] Deploy em produção realizado e validado.
- [x] README com instruções e procedimento para anexar evidências.
- [x] Documentação técnica preparada em Markdown com campos para evidências, pronta para exportar a PDF.
- [x] Segredos não adicionados aos arquivos versionados; os arquivos `.env` reais são ignorados.

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
