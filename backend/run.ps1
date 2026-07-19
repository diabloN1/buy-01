Write-Host ""
Write-Host "========== BUY MICROSERVICES ==========" -ForegroundColor Cyan
Write-Host ""

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "Docker is not installed."
    exit
}

$mongoRunning = docker ps --filter "name=buy-mongo" --format "{{.Names}}"

if ($mongoRunning -ne "buy-mongo") {

    Write-Host "MongoDB is not running. Starting Docker Compose..."
    docker compose up -d mongodb

    Write-Host "Waiting for MongoDB..."
    Start-Sleep -Seconds 5
}
else {
    Write-Host "MongoDB already running."
}

Write-Host ""
Write-Host "Starting Eureka..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$PSScriptRoot\eureka'; mvn spring-boot:run"

Start-Sleep -Seconds 10

Write-Host "Starting User Service..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$PSScriptRoot\user-service'; mvn spring-boot:run"

Write-Host "Starting Product Service..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$PSScriptRoot\product-service'; mvn spring-boot:run"

Start-Sleep -Seconds 8

Write-Host "Starting API Gateway..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$PSScriptRoot\api-gateway'; mvn spring-boot:run"

Write-Host ""
Write-Host "All services started."