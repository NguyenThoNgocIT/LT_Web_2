#!/bin/bash
# Script kiểm tra môi trường Jenkins và Docker
set -e

echo "================================================"
echo "Jenkins CI/CD Environment Checker"
echo "================================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_command() {
  if command -v $1 &> /dev/null; then
    echo -e "${GREEN}✓${NC} $1 is installed"
    $1 --version 2>&1 | head -1
  else
    echo -e "${RED}✗${NC} $1 is NOT installed"
    return 1
  fi
  echo ""
}

check_service() {
  if systemctl is-active --quiet $1; then
    echo -e "${GREEN}✓${NC} $1 service is running"
  else
    echo -e "${RED}✗${NC} $1 service is NOT running"
    echo "  Run: sudo systemctl start $1"
  fi
  echo ""
}

check_port() {
  if netstat -tuln 2>/dev/null | grep -q ":$1 "; then
    echo -e "${GREEN}✓${NC} Port $1 is open"
  elif ss -tuln 2>/dev/null | grep -q ":$1 "; then
    echo -e "${GREEN}✓${NC} Port $1 is open"
  else
    echo -e "${YELLOW}⚠${NC} Port $1 is not listening"
  fi
}

echo "Checking required commands..."
echo "------------------------------"
check_command java
check_command mvn
check_command node
check_command npm
check_command git
check_command docker
check_command docker-compose || docker compose version

echo "Checking services..."
echo "--------------------"
check_service jenkins
check_service docker

echo "Checking ports..."
echo "-----------------"
check_port 8080
check_port 8088
check_port 3000
check_port 5432
echo ""

echo "Checking Docker permissions..."
echo "------------------------------"
if groups jenkins 2>/dev/null | grep -q docker; then
  echo -e "${GREEN}✓${NC} jenkins user is in docker group"
else
  echo -e "${RED}✗${NC} jenkins user is NOT in docker group"
  echo "  Run: sudo usermod -aG docker jenkins"
  echo "  Then: sudo systemctl restart jenkins"
fi
echo ""

echo "Checking firewall..."
echo "--------------------"
if command -v firewall-cmd &> /dev/null; then
  if firewall-cmd --list-ports | grep -q 8080; then
    echo -e "${GREEN}✓${NC} Port 8080 is allowed in firewall"
  else
    echo -e "${YELLOW}⚠${NC} Port 8080 may not be allowed in firewall"
    echo "  Run: sudo firewall-cmd --permanent --add-port=8080/tcp"
    echo "       sudo firewall-cmd --reload"
  fi
else
  echo -e "${YELLOW}⚠${NC} firewalld not installed or not active"
fi
echo ""

echo "Checking Docker containers..."
echo "-----------------------------"
if docker ps -a 2>/dev/null | grep -q ltweb2; then
  docker ps -a --filter "name=ltweb2" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
else
  echo "No ltweb2 containers found (this is OK if not deployed yet)"
fi
echo ""

echo "Checking Jenkins credentials (approximate)..."
echo "----------------------------------------------"
if [ -f /var/lib/jenkins/credentials.xml ]; then
  echo -e "${GREEN}✓${NC} Jenkins credentials file exists"
else
  echo -e "${YELLOW}⚠${NC} Cannot check credentials (file not accessible)"
fi
echo ""

echo "Testing GitHub connectivity..."
echo "------------------------------"
if curl -sI https://github.com | head -1 | grep -q "200"; then
  echo -e "${GREEN}✓${NC} Can connect to GitHub"
else
  echo -e "${RED}✗${NC} Cannot connect to GitHub"
  echo "  Check network/proxy settings"
fi
echo ""

echo "Testing DockerHub connectivity..."
echo "---------------------------------"
if curl -sI https://hub.docker.com | head -1 | grep -q "200\|301\|302"; then
  echo -e "${GREEN}✓${NC} Can connect to DockerHub"
else
  echo -e "${RED}✗${NC} Cannot connect to DockerHub"
fi
echo ""

echo "================================================"
echo "Summary"
echo "================================================"
echo "If all checks pass, you can proceed to:"
echo "1. Configure Jenkins credentials"
echo "2. Create Pipeline job"
echo "3. Set up GitHub webhook"
echo "4. Trigger first build"
echo ""
echo "For detailed steps, see: ci/JENKINS_SETUP.md"
echo "================================================"
