@echo off
cd /d "%~dp0web-preview"
if not exist node_modules (
  echo Instalando dependencias...
  call npm install
)
echo Abrindo preview em http://localhost:5173
call npm run dev
