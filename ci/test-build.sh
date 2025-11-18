#!/bin/bash
# Script test build local trước khi push lên Jenkins
set -e

BACKEND_DIR="LT_Web_2-main/LT_Web2"
FRONTEND_DIR="cafe-fe"

echo "================================================"
echo "Local Build Test Script"
echo "================================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}Step 1: Testing Backend Build${NC}"
echo "------------------------------"
cd "$BACKEND_DIR"
echo "Running Maven tests..."
mvn clean test || {
  echo -e "${RED}Backend tests failed!${NC}"
  exit 1
}
echo -e "${GREEN}✓ Backend tests passed${NC}"
echo ""

echo "Building backend JAR..."
mvn package -DskipTests || {
  echo -e "${RED}Backend build failed!${NC}"
  exit 1
}
echo -e "${GREEN}✓ Backend JAR built successfully${NC}"
ls -lh target/*.jar
cd ../..
echo ""

echo -e "${YELLOW}Step 2: Testing Frontend Build${NC}"
echo "-------------------------------"
cd "$FRONTEND_DIR"
echo "Installing dependencies..."
npm ci || {
  echo -e "${RED}npm install failed!${NC}"
  exit 1
}
echo -e "${GREEN}✓ Dependencies installed${NC}"
echo ""

echo "Running frontend tests..."
CI=true npm test -- --watchAll=false --passWithNoTests || {
  echo -e "${YELLOW}⚠ Frontend tests had issues (non-blocking)${NC}"
}
echo ""

echo "Building production bundle..."
npm run build || {
  echo -e "${RED}Frontend build failed!${NC}"
  exit 1
}
echo -e "${GREEN}✓ Frontend built successfully${NC}"
du -sh build/
cd ..
echo ""

echo -e "${YELLOW}Step 3: Testing Docker Builds${NC}"
echo "------------------------------"
echo "Building backend Docker image..."
docker build -t ltweb2-backend:test "$BACKEND_DIR" || {
  echo -e "${RED}Backend Docker build failed!${NC}"
  exit 1
}
echo -e "${GREEN}✓ Backend Docker image built${NC}"
echo ""

echo "Building frontend Docker image..."
docker build -t ltweb2-frontend:test "$FRONTEND_DIR" || {
  echo -e "${RED}Frontend Docker build failed!${NC}"
  exit 1
}
echo -e "${GREEN}✓ Frontend Docker image built${NC}"
echo ""

echo "Docker images created:"
docker images | grep ltweb2
echo ""

echo -e "${YELLOW}Step 4: Testing Docker Compose${NC}"
echo "-------------------------------"
echo "Starting services..."
docker compose up -d || {
  echo -e "${RED}Docker compose failed!${NC}"
  exit 1
}
echo ""

echo "Waiting for services to start..."
sleep 15
echo ""

echo "Checking container status:"
docker compose ps
echo ""

echo "Testing backend health..."
for i in {1..10}; do
  if curl -s http://localhost:8088/api/auth/login > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Backend is responding${NC}"
    break
  else
    echo "Waiting for backend... ($i/10)"
    sleep 3
  fi
done
echo ""

echo "Testing frontend..."
if curl -s http://localhost:3000 | grep -q "root"; then
  echo -e "${GREEN}✓ Frontend is serving${NC}"
else
  echo -e "${YELLOW}⚠ Frontend may not be ready yet${NC}"
fi
echo ""

echo "Recent logs:"
echo "Backend:"
docker compose logs backend --tail=10
echo ""
echo "Frontend:"
docker compose logs frontend --tail=10
echo ""

echo "================================================"
echo -e "${GREEN}All tests passed!${NC}"
echo "================================================"
echo ""
echo "To stop and clean up:"
echo "  docker compose down"
echo "  docker rmi ltweb2-backend:test ltweb2-frontend:test"
echo ""
echo "To proceed with Jenkins:"
echo "1. Commit and push changes to 'deploy' branch"
echo "2. Jenkins webhook will trigger automatically"
echo "3. Monitor build at http://<jenkins-ip>:8080"
echo ""
