$ErrorActionPreference = "Stop"

$out = Join-Path $PSScriptRoot "documentacao-tecnica.pptx"
$temp = Join-Path $PSScriptRoot "_pptx"
if (Test-Path $temp) { Remove-Item $temp -Recurse -Force }
if (Test-Path $out) { Remove-Item $out -Force }

New-Item -ItemType Directory -Force -Path `
    (Join-Path $temp "_rels"), `
    (Join-Path $temp "ppt"), `
    (Join-Path $temp "ppt\_rels"), `
    (Join-Path $temp "ppt\slides"), `
    (Join-Path $temp "ppt\slides\_rels") | Out-Null

function Escape-Xml([string]$value) {
    return [System.Security.SecurityElement]::Escape($value)
}

function Write-Utf8File([string]$path, [string]$content) {
    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($path, $content, $utf8NoBom)
}

$slides = @(
    @{
        Title = "Challenge Águia Branca"
        Lines = @(
            "Gestão de Inovação Corporativa",
            "Aplicativo Android nativo (Kotlin + Jetpack Compose)",
            "Três perfis com fluxos dedicados: Operador, Gestor e Líder",
            "Tema dinâmico (claro/escuro) e identidade visual por perfil"
        )
    },
    @{
        Title = "Tecnologias Utilizadas"
        Lines = @(
            "Linguagem: Kotlin 2.x",
            "UI: Jetpack Compose + Material 3 (BOM 2026.05.00)",
            "Arquitetura: MVVM + Clean Architecture (data / domain / ui)",
            "Injeção de dependência: Koin",
            "Persistência local: Room + DataStore",
            "Rede: Retrofit + OkHttp + Kotlinx Serialization",
            "Concorrência: Coroutines + Flow",
            "Gradle Kotlin DSL com Version Catalog (libs.versions.toml)"
        )
    },
    @{
        Title = "Arquitetura da Solução"
        Lines = @(
            "Camada Data: APIs remotas (Retrofit), banco local Room, DAOs, DTOs, mappers e repositórios",
            "Camada Domain: modelos puros, contratos de repositório e Use Cases por feature",
            "Camada UI: telas Compose, ViewModels com StateFlow e componentes reutilizáveis",
            "Organização por feature: auth, operator, manager, leader e shared",
            "Navegação centralizada com Compose Navigation",
            "RoleThemedScreen aplica esquema de cores por perfil em runtime"
        )
    },
    @{
        Title = "Conectividade com Serviços Externos"
        Lines = @(
            "REST API real - FakerAPI (https://fakerapi.it/api/v1/users) para seed inicial de usuários",
            "REST API real - AdviceSlip (https://api.adviceslip.com/advice) para o Insight do Dia",
            "Banco de dados local em Room com 4 entidades: Idea, Project, StrategicGuideline e Session",
            "DAO + Flow para reatividade ponta a ponta entre banco e UI",
            "Tratamento robusto de erros e estados de loading/empty/error em cada chamada"
        )
    },
    @{
        Title = "Fluxo da Aplicação"
        Lines = @(
            "1. Tela de login com seleção e validação de credenciais por perfil",
            "2. Sessão persistida localmente via Room (SessionRepository)",
            "3. Navegação direciona automaticamente para a home do perfil logado",
            "4. Cada perfil enxerga abas e ações conforme as regras de negócio",
            "5. Logout limpa a sessão e retorna para o login"
        )
    },
    @{
        Title = "Funcionalidades do Operador"
        Lines = @(
            "Home com KPIs pessoais, banner de gamificação e ações rápidas",
            "Insight do Dia carregado via API REST externa",
            "Cadastro e listagem de ideias com categoria e status",
            "Acesso à equipe via Chat de Colaboradores (filtra por relação direta)",
            "Consulta às diretrizes estratégicas (read-only)",
            "Perfil com avatar customizável, pontos e nível de gamificação"
        )
    },
    @{
        Title = "Funcionalidades do Gestor"
        Lines = @(
            "Dashboard tático com indicadores agregados e gráfico de ideias",
            "Curadoria de ideias: aprovar, reprovar e priorizar",
            "CRUD completo de projetos a partir das ideias aprovadas",
            "Atualização de progresso, status, prazo e retorno financeiro",
            "Envio de sugestões para a liderança",
            "Chat com colaboradores diretos e visão de equipe",
            "Consulta às diretrizes estratégicas"
        )
    },
    @{
        Title = "Funcionalidades do Líder"
        Lines = @(
            "Dashboard executivo: ROI, investimento, lucro, prazo e produtividade",
            "CRUD completo de diretrizes estratégicas",
            "Acompanhamento de projetos com etapa, status, investimento e retorno",
            "Ranking mensal e geral da equipe",
            "Recebimento de sugestões dos gestores",
            "Chat com gestores e visão estratégica consolidada"
        )
    },
    @{
        Title = "UX e Diferenciais"
        Lines = @(
            "Tema dinâmico claro/escuro com alternância em tempo real",
            "Paleta e header coloridos por perfil (operador, gestor, líder)",
            "Animações premium de transição entre abas e entrada de lista",
            "Tratamento correto de insets do sistema (status bar e barra de gestos)",
            "Gamificação: pontos, níveis e progresso de participação",
            "Notificações in-app por perfil com badge de não lidos",
            "Componentes reutilizáveis (InnovateCard, InnovateScreenBackground, etc.)"
        )
    },
    @{
        Title = "Entregáveis"
        Lines = @(
            "APK Android assinado em modo debug",
            "Código-fonte completo compactado em .zip",
            "Documentação técnica em PPTX",
            "Roteiro do vídeo demonstrativo (até 5 minutos)",
            "Critérios atendidos: autenticação, CRUD por perfil, dashboard de ROI e conectividade REST + DB local"
        )
    }
)

$contentTypesSlides = ""
for ($i = 1; $i -le $slides.Count; $i++) {
    $contentTypesSlides += "<Override PartName=`"/ppt/slides/slide$i.xml`" ContentType=`"application/vnd.openxmlformats-officedocument.presentationml.slide+xml`"/>"
}

Write-Utf8File (Join-Path $temp "[Content_Types].xml") @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/ppt/presentation.xml" ContentType="application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml"/>
  $contentTypesSlides
</Types>
"@

Write-Utf8File (Join-Path $temp "_rels\.rels") @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="ppt/presentation.xml"/>
</Relationships>
"@

$slideIds = ""
$presentationRels = ""
for ($i = 1; $i -le $slides.Count; $i++) {
    $slideId = 255 + $i
    $slideIds += "<p:sldId id=`"$slideId`" r:id=`"rId$i`"/>"
    $presentationRels += "<Relationship Id=`"rId$i`" Type=`"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide`" Target=`"slides/slide$i.xml`"/>"
}

Write-Utf8File (Join-Path $temp "ppt\presentation.xml") @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:presentation xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main">
  <p:sldIdLst>$slideIds</p:sldIdLst>
  <p:sldSz cx="12192000" cy="6858000" type="screen16x9"/>
  <p:notesSz cx="6858000" cy="9144000"/>
</p:presentation>
"@

Write-Utf8File (Join-Path $temp "ppt\_rels\presentation.xml.rels") @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  $presentationRels
</Relationships>
"@

for ($i = 0; $i -lt $slides.Count; $i++) {
    $slideNumber = $i + 1
    $title = Escape-Xml $slides[$i].Title
    $bodyParagraphs = ""
    foreach ($line in $slides[$i].Lines) {
        $bodyParagraphs += "<a:p><a:r><a:rPr lang=`"pt-BR`" sz=`"2000`"><a:solidFill><a:srgbClr val=`"E2E8F0`"/></a:solidFill></a:rPr><a:t>$(Escape-Xml $line)</a:t></a:r></a:p>"
    }

    $slideXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sld xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main">
  <p:cSld>
    <p:bg><p:bgPr><a:solidFill><a:srgbClr val="0B1220"/></a:solidFill></p:bgPr></p:bg>
    <p:spTree>
      <p:nvGrpSpPr><p:cNvPr id="1" name=""/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>
      <p:grpSpPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="0" cy="0"/><a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/></a:xfrm></p:grpSpPr>
      <p:sp>
        <p:nvSpPr><p:cNvPr id="2" name="Title"/><p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr><p:nvPr/></p:nvSpPr>
        <p:spPr><a:xfrm><a:off x="685800" y="457200"/><a:ext cx="10820400" cy="1143000"/></a:xfrm></p:spPr>
        <p:txBody><a:bodyPr/><a:lstStyle/><a:p><a:r><a:rPr lang="pt-BR" sz="4000" b="1"><a:solidFill><a:srgbClr val="6C5CE7"/></a:solidFill></a:rPr><a:t>$title</a:t></a:r></a:p></p:txBody>
      </p:sp>
      <p:sp>
        <p:nvSpPr><p:cNvPr id="3" name="Content"/><p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr><p:nvPr/></p:nvSpPr>
        <p:spPr><a:xfrm><a:off x="914400" y="1905000"/><a:ext cx="10363200" cy="4114800"/></a:xfrm></p:spPr>
        <p:txBody><a:bodyPr/><a:lstStyle/>$bodyParagraphs</p:txBody>
      </p:sp>
    </p:spTree>
  </p:cSld>
  <p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>
</p:sld>
"@

    Write-Utf8File (Join-Path $temp "ppt\slides\slide$slideNumber.xml") $slideXml
    Write-Utf8File (Join-Path $temp "ppt\slides\_rels\slide$slideNumber.xml.rels") @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"/>
"@
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::CreateFromDirectory($temp, $out)
Remove-Item $temp -Recurse -Force
Write-Host "Documentacao gerada em $out"
