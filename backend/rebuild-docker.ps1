Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Rebuilding Marketplace Backend"
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Ensure Docker is running
try {
    docker info *> $null
}
catch {
    Write-Host "Docker Desktop is not running!" -ForegroundColor Red
    exit 1
}

Write-Host "[1/4] Stopping containers..." -ForegroundColor Yellow
docker compose down

Write-Host ""
Write-Host "[2/4] Removing old images..." -ForegroundColor Yellow
docker compose down --rmi local

Write-Host ""
Write-Host "[3/4] Building fresh images..." -ForegroundColor Yellow
docker compose build --no-cache

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[4/4] Starting containers..." -ForegroundColor Yellow
docker compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Backend successfully rebuilt and started!" -ForegroundColor Green
    Write-Host ""
    docker compose ps
}
else {
    Write-Host ""
    Write-Host "Failed to start containers." -ForegroundColor Red
}