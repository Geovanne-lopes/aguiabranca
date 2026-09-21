# Sprint 2 — como rodar

Este diretório é a entrega da Sprint 2 (API + app Android integrado). Siga os passos **nesta ordem**. A API precisa estar no ar antes do aplicativo.

O chat de colaboradores no app é **mock local**. Não há API de chat.

## O que tem nesta pasta

| Arquivo | Uso |
|---------|-----|
| `aguiabranca-backend-fonte.zip` | Código da API + `README.md` técnico |
| `aguiabranca-android-fonte.zip` | Projeto Android + APK dentro do zip |
| `aguiabranca-debug.apk` | Mesmo APK, para instalar direto |
| `documentacao/apresentacao-sprint2.pptx` | Apresentação (equipe, arquitetura, endpoints, IA) |

O arquivo `.env` **não vem no zip** (contém segredos). Você cria a partir do `.env.example`.

## O que instalar antes

- JDK **21**
- Maven **3.9+**
- Docker **ou** MongoDB 7 na porta `27017`
- Para o app: emulador Android (API 28+) **ou** um aparelho com “fontes desconhecidas”

## 1. MongoDB

No PowerShell:

```powershell
docker start aguia-mongo 2>$null; if ($LASTEXITCODE -ne 0) { docker run -d --name aguia-mongo -p 27017:27017 mongo:7 }
```

Se o Mongo já estiver instalado localmente em `localhost:27017`, pule o Docker.

## 2. API (backend)

Descompacte `aguiabranca-backend-fonte.zip`. Entre na pasta `aguiabranca-backend` (é a raiz do Maven, onde está o `pom.xml`).

```powershell
copy .env.example .env
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

No PowerShell as **aspas** no `-D` são obrigatórias. Sem elas o Maven quebra o comando.

Espere aparecer `Started AguiaBrancaApplication`. A API fica em `http://localhost:8080`.

Confira:

- Health: http://localhost:8080/actuator/health
- Swagger: http://localhost:8080/swagger-ui.html

Se aparecer `JWT_SECRET deve ter no mínimo 32 bytes`, o `.env` não foi lido. Confirme que o arquivo existe **dentro** da pasta da API e rode o `mvn` **nessa** pasta.

### Insight de IA (Gemini)

`GEMINI_API_KEY` no `.env.example` vem **vazia**. Assim o líder ainda gera insight: a API responde **200** com `source=FALLBACK` (texto local com os números do dashboard; **não** chama a Google).

Para texto real do Gemini:

1. Crie uma chave em https://aistudio.google.com/apikey
2. Cole em `GEMINI_API_KEY` no `.env`
3. **Reinicie** a API (`Ctrl+C` e o `mvn` de novo)

A chave não entra em log nem na resposta. Não envie `.env` para o Git.

## 3. Aplicativo Android

A API do passo 2 precisa continuar rodando.

**Opção A — APK no emulador**

1. Suba um emulador (o endereço `10.0.2.2` só funciona no emulador, não no celular físico na rede Wi‑Fi).
2. Instale `aguiabranca-debug.apk` (arraste no emulador ou `adb install aguiabranca-debug.apk`).
3. O app já aponta para `http://10.0.2.2:8080/`.

**Opção B — Android Studio**

1. Descompacte `aguiabranca-android-fonte.zip`.
2. Abra a pasta `aguiabranca-android` no Android Studio.
3. Sync Gradle e rode no emulador. O `API_BASE_URL` padrão já é `http://10.0.2.2:8080/`.

Celular físico: o `10.0.2.2` não alcança o PC. Use o IP da máquina na LAN e libere a porta 8080, ou teste no emulador.

## 4. Contas demo (criadas no primeiro start com profile `local`)

| Papel | E-mail | Senha |
|-------|--------|-------|
| Operador | `operador@innovatecorp.com` | `oper123` |
| Gestor | `gestor@innovatecorp.com` | `gest123` |
| Liderança | `lideranca@innovatecorp.com` | `lider123` |

## 5. Roteiro rápido no app

1. Login **operador** → cadastrar ideia (com diretriz, se houver alguma publicada).
2. Sair → login **gestor** → curadoria (aprovar/priorizar) → criar/atualizar projeto (investimento, lucro, diretriz).
3. Sair → login **liderança** → dashboard (ROI e retorno por estratégia) → **Gerar insight de IA**.

No Swagger o mesmo fluxo vale: `POST /api/v1/auth/login` → Authorize com o `accessToken` → chamar as rotas.

## Se algo falhar

| Sintoma | O que checar |
|---------|----------------|
| App: “não foi possível validar o usuário” | API não está no ar, ou não é emulador (`10.0.2.2`) |
| Porta 8080 ocupada | Encerrar o Java antigo ou mudar a porta |
| Insight “fallback local, não uma análise de IA” | Esperado sem `GEMINI_API_KEY` |
| Chat de colaboradores | Mock de propósito; não usa a API |
| Mongo recusa conexão | Container/serviço na `27017` |

Detalhe da API, papéis e endpoints: `README.md` **dentro** do zip do backend.
