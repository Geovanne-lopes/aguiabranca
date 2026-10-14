# Projeto — Cidades ESG Inteligentes

Aplicativo Android e API da Sprint 2 para apoiar a gestão de inovação corporativa nos papéis de operador, gestor e líder.

## Tecnologias utilizadas

- **Android:** Kotlin, Jetpack Compose, Koin, Retrofit, Gradle 9.3.1 e Android Gradle Plugin 9.1.1.
- **Backend/API:** Java 21, Spring Boot 3.4.13, Maven, Spring Security, Spring Data MongoDB, JWT e Springdoc OpenAPI.
- **Banco de dados:** MongoDB 7.
- **Containerização:** Docker, Docker Compose v2 e imagens multi-stage.
- **CI/CD e registry:** GitHub Actions e GitHub Container Registry (GHCR).
- **Deploy:** Linux, SSH, Docker Compose e healthcheck HTTP do Spring Actuator.
- **Integrações externas:** Google Gemini e AdviceSlip; a chave do Gemini é opcional.

## Arquitetura

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

## Como executar localmente com Docker

Requisitos: Docker Desktop iniciado com Docker Compose v2. No PowerShell, execute os comandos a partir da raiz do repositório:

```powershell
if (-not (Test-Path backend/.env)) { Copy-Item backend/.env.example backend/.env }
docker compose up --build -d
docker compose ps
docker compose logs -f api mongo
```

Healthcheck da API: `Invoke-RestMethod http://localhost:8080/actuator/health`. Swagger: `http://localhost:8080/swagger-ui.html`. O profile `local` habilita as contas demo. MongoDB usa o volume `aguiabranca_mongo-data`, a rede `aguiabranca-local` e só publica `27017` no loopback. Para parar sem apagar dados, use `docker compose down`; `docker compose down -v` remove os dados locais.

O `.env` é ignorado pelo Git e não entra na imagem. Antes de uso fora da máquina local, substitua o `JWT_SECRET` ilustrativo por um segredo aleatório próprio com pelo menos 32 bytes. O app Android continua executado no Android Studio/emulador; no emulador, use `http://10.0.2.2:8080/`.

### Evidência da execução local

A saída abaixo foi compartilhada após a execução do Compose; API e MongoDB estavam saudáveis:

```text
NAME                  IMAGE             SERVICE   STATUS
aguiabranca-api-1      aguiabranca-api   api       Up (healthy)
aguiabranca-mongo-1    mongo:7           mongo     Up (healthy)
```

### Execução da API sem container

Para executar a API no host, use JDK 21, Maven 3.9+ e MongoDB 7 local:

```powershell
cd backend
Copy-Item .env.example .env
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Detalhes do seed, das contas de demonstração e do Swagger estão em [backend/README.md](backend/README.md).

## Containerização

O [backend/Dockerfile](backend/Dockerfile) usa build multi-stage: Maven e JDK 21 compilam o artefato; a imagem final contém JRE 21, o JAR executável e `curl` para o healthcheck. O processo roda sem root (UID/GID 10001), expõe a porta 8080 e verifica o Actuator. O [`.dockerignore`](.dockerignore) exclui artefatos locais e arquivos `.env`. O Compose local orquestra API e MongoDB; os arquivos de staging e produção isolam redes, volumes e bancos.

Conteúdo do Dockerfile utilizado:

<details>
<summary>Mostrar Dockerfile</summary>

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY backend/pom.xml ./pom.xml
RUN mvn -B -DskipTests dependency:go-offline

COPY backend/src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN apt-get update \
	&& apt-get install -y --no-install-recommends curl \
	&& rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/target/aguiabranca-api-*.jar app.jar

EXPOSE 8080
USER 10001:10001

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
	CMD curl --fail --silent http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

</details>

### Build e testes

Na raiz do repositório, com JDK 21 e Maven instalados:

```powershell
mvn -B -f backend/pom.xml clean verify
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
docker build -f backend/Dockerfile -t aguiabranca-api:local .
```

O backend contém testes unitários e testes de integração com Testcontainers; a execução completa precisa de Docker. O projeto não tem Maven Wrapper, por isso o CI instala/configura Maven via `actions/setup-java` e usa o Maven disponível no runner.

## Pipeline CI/CD

O workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) é acionado em pull requests para `main`/`master`, pushes para essas branches e tags `v*.*.*`.

1. **Backend:** configura Java 21 e cache Maven, executa `mvn -B -f backend/pom.xml clean verify` e publica relatórios de testes.
2. **Android:** configura JDK 21, Android SDK e cache Gradle; executa testes unitários e `assembleDebug`, publicando relatórios e APK debug.
3. **Publicação:** após os jobs anteriores passarem em um push, Docker Buildx constrói e publica a imagem da API no GHCR com `GITHUB_TOKEN`; o digest imutável é encaminhado aos deploys.
4. **Staging:** um push em `main`/`master` pode iniciar o deploy se `STAGING_DEPLOY_ENABLED=true` e os secrets estiverem configurados.
5. **Produção:** o push de uma tag `vX.Y.Z` inicia o deploy no GitHub Environment `production`, onde se recomenda exigir aprovação manual.

O workflow reutilizável [`.github/workflows/deploy-environment.yml`](.github/workflows/deploy-environment.yml) chama [scripts/deploy-ssh.sh](scripts/deploy-ssh.sh), que envia Compose e configuração por SSH e aguarda o healthcheck. O envio da branch deste checkout ao remoto foi negado por falta de permissão; portanto, ainda não há uma execução real do workflow para registrar.

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

## Prints do funcionamento

Não há capturas de tela anexadas nesta versão. A evidência local disponível é a saída de `docker compose ps` mostrada na seção de execução local. Para completar esta seção com evidências reais, adicione prints ou links para:

- **CI/CD:** execução bem-sucedida no GitHub Actions, com os jobs backend e Android e a publicação da imagem no GHCR.
- **Staging:** deploy concluído e resposta saudável de `/actuator/health`.
- **Produção:** deploy aprovado no Environment `production` e resposta saudável de `/actuator/health`.

**Status:** execução local demonstrada; execução no GitHub Actions, staging e produção pendentes. Não foram incluídos resultados nem imagens simulados. Remova segredos e dados pessoais de qualquer evidência antes de publicá-la.

### Evidências e limitações

Registre a URL/ID da execução GitHub Actions, resultado dos jobs, relatório de testes e digest GHCR. Para deploy, anexe também a aprovação do Environment de produção, a saída do healthcheck e `docker compose ps`/logs sem dados sensíveis. Os campos para essas evidências estão em [docs/devops-technical.md](docs/devops-technical.md); não há evidência fictícia neste repositório.

Validação atual: o usuário confirmou que `docker compose up --build -d` concluiu e `docker compose ps` mostrou API e MongoDB como `healthy`. O teste Maven local terminou com sucesso; os testes de integração MongoDB que usam Testcontainers foram ignorados naquele ambiente por indisponibilidade do Docker para o processo de testes. O workflow GitHub ainda não foi executado, pois o push da branch foi negado por falta de permissão. Não há deploys de staging ou produção registrados.

### Checklist de entrega

- [x] Projeto compactado em ZIP com estrutura organizada (geração após esta validação; artefato indicado no relatório final).
- [x] Dockerfile multi-stage construído localmente; API iniciada e healthcheck validado pelo Compose.
- [x] Docker Compose para API/MongoDB, incluindo configurações isoladas de staging e produção.
- [x] Volumes, redes e variáveis de ambiente configurados; `.env` ignorado.
- [x] Pipeline com build automático definido.
- [x] Testes automatizados ligados ao pipeline; execução Maven local concluída (integrações Testcontainers dependem de Docker).
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
