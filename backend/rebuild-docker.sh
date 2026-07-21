#!/usr/bin/env bash

set -e

echo
echo "========================================="
echo "   Rebuilding Marketplace Backend"
echo "========================================="
echo

# Ensure Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running!"
    exit 1
fi

echo "[1/4] Stopping containers..."
docker compose down

echo
echo "[2/4] Removing old images..."
docker compose down --rmi local

echo
echo "[3/4] Building fresh images..."
docker compose build --no-cache

echo
echo "[4/4] Starting containers..."
docker compose up -d

echo
echo "✅ Backend successfully rebuilt and started!"
echo
docker compose ps