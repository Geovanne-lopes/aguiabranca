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
            "Aplicativo Android nativo com preview web para demonstração",
            "Perfis: Operador, Gestor e Líder"
        )
    },
    @{
        Title = "Tecnologias Utilizadas"
        Lines = @(
            "Android: Kotlin, Jetpack Compose, Material 3 e Navigation Compose",
            "Arquitetura: MVVM + Clean Architecture",
            "Injeção de dependência: Koin",
            "Persistência local: Room e DataStore",
            "Rede: Retrofit, OkHttp e Kotlinx Serialization",
            "Web preview: Vite, JavaScript, HTML e CSS"
        )
    },
    @{
        Title = "Arquitetura da Solução"
        Lines = @(
            "Camada Data: APIs remotas, banco local Room, DAOs, DTOs e repositórios",
            "Camada Domain: modelos, contratos de repositório e casos de uso",
            "Camada UI: telas Compose, ViewModels, estados de tela e componentes reutilizáveis",
            "Separação por feature: auth, operator, manager e leader"
        )
    },
    @{
        Title = "Fluxo da Aplicação"
        Lines = @(
            "1. Usuário acessa a tela de login",
            "2. Credenciais mockadas definem automaticamente o perfil",
            "3. A sessão é salva localmente",
            "4. A navegação direciona para a home correta do perfil",
            "5. Cada perfil acessa funcionalidades específicas por regra de negócio"
        )
    },
    @{
        Title = "Funcionalidades do Operador"
        Lines = @(
            "Home com KPIs, insight do dia e diretrizes estratégicas",
            "Cadastro de novas ideias e reporte de problemas",
            "Listagem das próprias ideias com status",
            "Perfil com gamificação, pontos e resumo de participação"
        )
    },
    @{
        Title = "Funcionalidades do Gestor"
        Lines = @(
            "Dashboard tático com indicadores e gráfico de ideias",
            "Curadoria de ideias: aprovar, reprovar e priorizar",
            "CRUD de projetos a partir de ideias aprovadas",
            "Consulta das diretrizes da liderança",
            "Perfil e visão de equipe"
        )
    },
    @{
        Title = "Funcionalidades do Líder"
        Lines = @(
            "Dashboard executivo de ROI, investimento, lucro e produtividade",
            "CRUD de diretrizes estratégicas",
            "Acompanhamento dos projetos com prazo, status e retorno",
            "Notificações e visão estratégica consolidada"
        )
    },
    @{
        Title = "Entregáveis"
        Lines = @(
            "Pasta web: build estático do preview no navegador",
            "Pasta apk: local destinado ao APK Android",
            "Código-fonte completo compactado em .zip",
            "Documentação técnica em PPTX",
            "Material de apoio para vídeo demonstrativo de até 5 minutos"
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
        $bodyParagraphs += "<a:p><a:r><a:rPr lang=`"pt-BR`" sz=`"2200`"/><a:t>$(Escape-Xml $line)</a:t></a:r></a:p>"
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
        <p:txBody><a:bodyPr/><a:lstStyle/><a:p><a:r><a:rPr lang="pt-BR" sz="4000" b="1"><a:solidFill><a:srgbClr val="FFFFFF"/></a:solidFill></a:rPr><a:t>$title</a:t></a:r></a:p></p:txBody>
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
Write-Host "Documentação gerada em $out"
