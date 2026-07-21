#!/bin/bash

echo ""
echo "========== BUY MICROSERVICES =========="
echo ""

if ! command -v docker &>/dev/null; then
    echo "Docker is not installed."
    exit 1
fi

if [ "$(docker ps --filter "name=buy-mongo" --format "{{.Names}}")" != "buy-mongo" ]; then
    echo "MongoDB not running. Starting..."
    docker compose up -d mongodb

    echo "Waiting for MongoDB..."
    sleep 5
else
    echo "MongoDB already running."
fi

gnome-terminal -- bash -c "cd eureka && mvn spring-boot:run; exec bash"

sleep 10

gnome-terminal -- bash -c "cd user-service && mvn spring-boot:run; exec bash"

gnome-terminal -- bash -c "cd product-service && mvn spring-boot:run; exec bash"

sleep 8

gnome-terminal -- bash -c "cd api-gateway && mvn spring-boot:run; exec bash"

echo ""
echo "All services started."