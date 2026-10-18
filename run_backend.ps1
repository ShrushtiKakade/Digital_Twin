# Launch Java Spring Boot Backend on port 8080
Write-Host "Starting Digital Twin Spring Boot Backend on http://localhost:8080..." -ForegroundColor Green
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;D:\DevTools\maven\bin;$env:Path"
cd "$PSScriptRoot\backend"
& "D:\DevTools\maven\bin\mvn.cmd" spring-boot:run

