# Launch Python FastAPI AI Service on port 8000
Write-Host "Starting Digital Twin AI Service on http://localhost:8000..." -ForegroundColor Cyan
cd "$PSScriptRoot\ai_service"
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload

