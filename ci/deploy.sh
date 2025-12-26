#!/bin/bash
set -e
COMPOSE_FILE="docker-compose.yml"
TARGET_BRANCH="deploy"

if [ -n "$GIT_BRANCH" ]; then
  echo "Current branch: $GIT_BRANCH"
fi

echo "Pulling latest images..."
docker compose pull || true

echo "Restarting stack..."
docker compose down --remove-orphans || true
docker compose up -d

# Ensure Prometheus config is reloaded
echo "Reloading Prometheus configuration..."
docker compose restart prometheus
sleep 5

echo "Pruning old images (optional)..."
docker image prune -f || true

echo "Deployment finished at $(date)"
