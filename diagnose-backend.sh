#!/bin/bash

echo "====== Backend Container Status ======"
docker ps --filter "name=ltweb2-backend" --format "table {{.Names}}\t{{.Status}}\t{{.ID}}"

echo ""
echo "====== Backend Logs (last 50 lines) ======"
docker logs --tail=50 ltweb2-backend 2>&1

echo ""
echo "====== Testing Backend Health Endpoint ======"
echo "1. Direct curl from host:"
curl -v http://localhost:8088/actuator/health 2>&1 | head -30

echo ""
echo "2. Curl from inside backend container:"
docker compose exec -T backend curl -v http://localhost:8088/actuator/health 2>&1 | head -30

echo ""
echo "3. Testing Prometheus endpoint:"
docker compose exec -T backend curl -v http://localhost:8088/actuator/prometheus 2>&1 | head -30

echo ""
echo "====== Backend Environment Variables ======"
docker compose exec -T backend env | grep -E "(JAVA|SPRING)" | head -10

echo ""
echo "====== Checking if backend is listening on port 8088 ======"
docker compose exec -T backend netstat -tlnp 2>/dev/null | grep 8088 || echo "Port 8088 not listening"
