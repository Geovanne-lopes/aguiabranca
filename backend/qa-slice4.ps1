# Validação ao vivo da fatia 4 (projetos) contra a API local.
# Pré-requisito: Mongo + API no profile local em http://localhost:8080
$ErrorActionPreference = 'Continue'
$ProgressPreference = 'SilentlyContinue'
$base = 'http://localhost:8080'

function Login([string]$email, [string]$password) {
    return Invoke-RestMethod -Method Post -Uri "$base/api/v1/auth/login" -ContentType 'application/json; charset=utf-8' -Body (@{ email = $email; password = $password } | ConvertTo-Json)
}

function CallApi([string]$method, [string]$path, [string]$token, $payload) {
    $headers = @{ Authorization = "Bearer $token" }
    $uri = "$base$path"
    try {
        if ($null -ne $payload) {
            $json = $payload | ConvertTo-Json -Compress
            $resp = Invoke-WebRequest -Method $method -Uri $uri -Headers $headers -ContentType 'application/json; charset=utf-8' -Body $json -UseBasicParsing
        } else {
            $resp = Invoke-WebRequest -Method $method -Uri $uri -Headers $headers -UseBasicParsing
        }
        return @{ status = [int]$resp.StatusCode; body = [string]$resp.Content }
    } catch {
        $response = $_.Exception.Response
        if ($null -eq $response) {
            return @{ status = 0; body = $_.Exception.Message }
        }
        $stream = $response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $txt = $reader.ReadToEnd()
        $code = 0
        try { $code = [int]$response.StatusCode } catch { $code = 0 }
        return @{ status = $code; body = $txt }
    }
}

function AsJson($raw) {
    if ([string]::IsNullOrWhiteSpace($raw)) { return $null }
    return $raw | ConvertFrom-Json
}

$op = Login 'operador@innovatecorp.com' 'oper123'
$leader = Login 'lideranca@innovatecorp.com' 'lider123'
$manager = Login 'gestor@innovatecorp.com' 'gest123'
Write-Output "LOGIN operador=$($op.user.role) gestor=$($manager.user.role) lider=$($leader.user.role)"

$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$ideaTitle = "QA fatia4 $stamp"
$r = CallApi 'POST' '/api/v1/ideas' $op.accessToken @{
    title = $ideaTitle
    description = 'Descricao com tamanho valido para a ideia da fatia 4.'
    category = 'TECHNOLOGY'
}
$idea = AsJson $r.body
Write-Output "IDEA_CREATE status=$($r.status) id=$($idea.id) guidelineId=$($idea.guidelineId) title=$($idea.title)"

$r = CallApi 'PATCH' "/api/v1/ideas/$($idea.id)/status" $manager.accessToken @{ status = 'APPROVED' }
Write-Output "IDEA_APPROVE status=$($r.status) ideaStatus=$((AsJson $r.body).status)"

$r = CallApi 'POST' '/api/v1/projects' $leader.accessToken @{ ideaId = $idea.id }
Write-Output "A-PROJ-01 LEADER POST status=$($r.status) body=$($r.body)"

$r = CallApi 'POST' '/api/v1/projects' $op.accessToken @{ ideaId = $idea.id }
Write-Output "A-PROJ-01 OPERATOR POST status=$($r.status) body=$($r.body)"

$r = CallApi 'GET' '/api/v1/projects' $op.accessToken $null
Write-Output "A-PROJ-04 OPERATOR GET /projects status=$($r.status) body=$($r.body)"

$r = CallApi 'POST' '/api/v1/projects' $manager.accessToken @{ ideaId = $idea.id }
$project = AsJson $r.body
Write-Output "A-PROJ-01 MANAGER POST status=$($r.status) projectStatus=$($project.status) title=$($project.title) guidelineId=$($project.guidelineId) investment=$($project.investmentAmount) profit=$($project.obtainedProfit) productivity=$($project.productivityGainPercent) roi=$($project.roiPercent)"

$r = CallApi 'POST' '/api/v1/projects' $manager.accessToken @{ ideaId = $idea.id }
$dup = AsJson $r.body
Write-Output "A-PROJ-01 SECOND POST status=$($r.status) code=$($dup.code)"

$pendingTitle = "QA fatia4 pending $stamp"
$r = CallApi 'POST' '/api/v1/ideas' $op.accessToken @{
    title = $pendingTitle
    description = 'Descricao com tamanho valido para a ideia pendente.'
    category = 'PROCESS'
}
$pending = AsJson $r.body
$r = CallApi 'POST' '/api/v1/projects' $manager.accessToken @{ ideaId = $pending.id }
$pendingProject = AsJson $r.body
Write-Output "A-PROJ-01 PENDING POST status=$($r.status) code=$($pendingProject.code)"

$putBase = @{
    title = $project.title
    description = 'Descricao atualizada do projeto com tamanho valido.'
    status = 'IN_DEVELOPMENT'
    deadline = '2026-12-31T00:00:00Z'
    guidelineId = $project.guidelineId
    ideaId = 'ignorado'
    managerId = 'ignorado'
}

$put = $putBase.Clone()
$put.investmentAmount = 1000
$put.obtainedProfit = 1500
$put.productivityGainPercent = 10
$r = CallApi 'PUT' "/api/v1/projects/$($project.id)" $manager.accessToken $put
$updated = AsJson $r.body
Write-Output "A-PROJ-02 PUT 1000/1500/10 status=$($r.status) roi=$($updated.roiPercent) ideaId=$($updated.ideaId) managerId=$($updated.managerId)"

$put.productivityGainPercent = 101
$r = CallApi 'PUT' "/api/v1/projects/$($project.id)" $manager.accessToken $put
$bad = AsJson $r.body
Write-Output "A-PROJ-02 PUT productivity 101 status=$($r.status) code=$($bad.code)"

$put.productivityGainPercent = 0
$put.investmentAmount = 0
$put.obtainedProfit = 100
$put.status = 'BACKLOG'
$r = CallApi 'PUT' "/api/v1/projects/$($project.id)" $manager.accessToken $put
$zero = AsJson $r.body
Write-Output "A-CALC-01 PUT investment 0 status=$($r.status) roi=$($zero.roiPercent)"

$r = CallApi 'GET' "/api/v1/projects/$($project.id)" $manager.accessToken $null
$gotZero = AsJson $r.body
Write-Output "A-CALC-01 GET investment 0 status=$($r.status) investment=$($gotZero.investmentAmount) roi=$($gotZero.roiPercent)"

$put.investmentAmount = 200
$put.obtainedProfit = 100
$put.status = 'AVERAGE_TICKET'
$r = CallApi 'PUT' "/api/v1/projects/$($project.id)" $manager.accessToken $put
$neg = AsJson $r.body
Write-Output "A-CALC-01 PUT 200/100 status=$($r.status) roi=$($neg.roiPercent)"

$r = CallApi 'GET' "/api/v1/projects/$($project.id)" $manager.accessToken $null
$gotNeg = AsJson $r.body
Write-Output "A-CALC-01 GET 200/100 status=$($r.status) investment=$($gotNeg.investmentAmount) profit=$($gotNeg.obtainedProfit) roi=$($gotNeg.roiPercent)"

$encodedTitle = [uri]::EscapeDataString($ideaTitle)
$r = CallApi 'GET' "/api/v1/projects?q=$encodedTitle" $leader.accessToken $null
$list = AsJson $r.body
Write-Output "A-PROJ-04 LEADER GET /projects status=$($r.status) total=$($list.totalElements)"

$r = CallApi 'DELETE' "/api/v1/projects/$($project.id)" $leader.accessToken $null
$leaderDelete = AsJson $r.body
Write-Output "A-PROJ-03 LEADER DELETE status=$($r.status) code=$($leaderDelete.code)"

$r = CallApi 'PATCH' "/api/v1/ideas/$($idea.id)/status" $manager.accessToken @{
    status = 'REJECTED'
    justification = 'Ja virou projeto e nao pode ser reprovada.'
}
$reject = AsJson $r.body
Write-Output "REJECT_WITH_PROJECT status=$($r.status) code=$($reject.code)"

$r = CallApi 'DELETE' "/api/v1/projects/$($project.id)" $manager.accessToken $null
Write-Output "A-PROJ-03 MANAGER DELETE status=$($r.status) body='$($r.body)'"

$r = CallApi 'GET' "/api/v1/ideas/$($idea.id)" $manager.accessToken $null
$still = AsJson $r.body
Write-Output "A-PROJ-03 IDEA_REMAINS status=$($r.status) ideaId=$($still.id) ideaStatus=$($still.status)"

$r = CallApi 'GET' '/v3/api-docs' $manager.accessToken $null
$docs = AsJson $r.body
$hasDashboardProject = $false
if ($null -ne $docs.paths) {
    foreach ($pathName in $docs.paths.PSObject.Properties.Name) {
        if ($pathName -match 'dashboard/projects') { $hasDashboardProject = $true }
    }
}
Write-Output "OPENAPI dashboard/projects present=$hasDashboardProject projectsPath=$($null -ne $docs.paths.'/api/v1/projects') reportPath=$($null -ne $docs.paths.'/api/v1/projects/{id}')"
