# One-Click Launch Script for Happiest Healthy Digital Twin
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  HAPPIEST HEALTHY DIGITAL TWIN — FULL SYSTEM LAUNCHER   " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

Write-Host "1. Launching Python FastAPI AI Service in new window..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run_ai.ps1"

Start-Sleep -Seconds 2

Write-Host "2. Launching Java Spring Boot Backend in new window..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run_backend.ps1"

Start-Sleep -Seconds 5

Write-Host "3. Launching React Frontend in new window..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run_frontend.ps1"

Write-Host "`nAll 3 services are launching in separate windows!" -ForegroundColor Green
Write-Host "- AI Service: http://localhost:8000" -ForegroundColor White
Write-Host "- Backend API: http://localhost:8080" -ForegroundColor White
Write-Host "- React Dashboard: http://localhost:3000" -ForegroundColor White
Write-Host "`nOpen your browser at: http://localhost:3000" -ForegroundColor Cyan

