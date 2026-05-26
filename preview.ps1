# Inicia o preview web do InnovateCorp no navegador
$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$preview = Join-Path $root "web-preview"

Set-Location $preview

if (-not (Test-Path "node_modules")) {
    Write-Host "Instalando dependencias (npm install)..." -ForegroundColor Cyan
    npm install
}

Write-Host "Abrindo preview em http://localhost:5173" -ForegroundColor Green
npm run dev
