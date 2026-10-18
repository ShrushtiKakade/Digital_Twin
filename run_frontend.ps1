# Launch React Vite Frontend on port 3000
Write-Host "Starting Digital Twin React Dashboard on http://localhost:3000..." -ForegroundColor Yellow
$env:Path = "D:\DevTools\nodejs;$env:Path"
cd "$PSScriptRoot\frontend"
& "D:\DevTools\nodejs\npm.cmd" run dev

