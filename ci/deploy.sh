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
docker compose up -d --remove-orphans

echo "Pruning old images (optional)..."
docker image prune -f || true

echo "Deployment finished at $(date)"
