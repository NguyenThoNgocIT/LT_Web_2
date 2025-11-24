# LT_Web_2 - CI/CD Pipeline với Jenkins

## 📋 Mô Tả Dự Án

Hệ thống quản lý quán cafe với:

- **Backend**: Spring Boot 3.1.5 (Java 21) + PostgreSQL
- **Frontend**: React 19 + Tailwind CSS
- **CI/CD**: Jenkins Pipeline + Docker + Docker Compose
- **Deploy**: CentOS VM (VMware)

## 🏗️ Kiến Trúc CI/CD

```
GitHub (push)
   ↓ (webhook)
Jenkins Pipeline
   ↓
   ├─ Checkout Code
   ├─ Run Tests (Backend + Frontend)
   ├─ Build Artifacts (JAR + React bundle)
   ├─ Build Docker Images
   ├─ Push to DockerHub
   └─ Deploy with docker-compose
       ↓
   Production (VM)
```

## 📁 Cấu Trúc Project

```
LT_Web_2/
├── LT_Web_2-main/LT_Web2/     # Spring Boot backend
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── cafe-fe/                    # React frontend
│   ├── src/
│   ├── package.json
│   ├── Dockerfile
│   └── nginx.conf
├── ci/                         # CI/CD scripts & docs
│   ├── JENKINS_SETUP.md       # Hướng dẫn chi tiết
│   ├── check-environment.sh   # Script kiểm tra môi trường
│   ├── test-build.sh          # Script test build local
│   └── deploy.sh              # Script deploy
├── docker-compose.yml         # Orchestration
├── Jenkinsfile                # Pipeline definition
└── README.md                  # File này
```

## 🚀 Quick Start

### 1. Cài Đặt Môi Trường (CentOS VM)

```bash
# Update system
sudo yum update -y

# Install Java 21
sudo yum install -y java-21-openjdk-devel

# Install Maven
sudo yum install -y maven

# Install Node.js 18
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs
java
# Install Docker
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io
sudo systemctl enable --now docker

# Install Docker Compose
DOCKER_COMPOSE_VERSION=2.27.0
sudo curl -SL https://github.com/docker/compose/releases/download/v${DOCKER_COMPOSE_VERSION}/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install Jenkins
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
sudo yum install -y jenkins
sudo systemctl enable --now jenkins

# Add jenkins user to docker group
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins

# Open firewall ports
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=8088/tcp
sudo firewall-cmd --permanent --add-port=3000/tcp
sudo firewall-cmd --reload
```

### 2. Kiểm Tra Môi Trường

```bash
cd /path/to/LT_Web_2
chmod +x ci/check-environment.sh
./ci/check-environment.sh
```

### 3. Cấu Hình Jenkins

**Đọc hướng dẫn chi tiết**: [ci/JENKINS_SETUP.md](ci/JENKINS_SETUP.md)

**Các bước chính**:

1. Tạo GitHub Personal Access Token
2. Thêm credentials trong Jenkins (github-token, dockerhub-creds)
3. Tạo Pipeline Job
4. Cấu hình GitHub webhook
5. Test build

### 4. Test Build Local (Trước Khi Push)

```bash
chmod +x ci/test-build.sh
./ci/test-build.sh
```

### 5. Deploy

```bash
# Option 1: Sử dụng docker-compose trực tiếp
docker compose up -d

# Option 2: Qua Jenkins pipeline (tự động khi push lên branch deploy)
git add .
git commit -m "Deploy changes"
git push origin deploy
```

## 🔧 Cấu Hình

### Environment Variables

Copy file mẫu và điều chỉnh:

```bash
cp .env.example .env
# Sửa các giá trị trong .env
```

Các biến quan trọng:

- `POSTGRES_PASSWORD`: Mật khẩu PostgreSQL
- `JWT_SECRET`: Secret key cho JWT (tối thiểu 32 ký tự)
- `DOCKER_NAMESPACE`: DockerHub username

### Jenkinsfile

Sửa trong `Jenkinsfile`:

```groovy
DOCKER_NAMESPACE = 'your_dockerhub_username'  // Đổi thành username của bạn
```

## 📊 Jenkins Pipeline Stages

1. **Checkout**: Clone code từ GitHub
2. **Backend Tests**: Chạy `mvn test`
3. **Frontend Tests**: Chạy `npm test`
4. **Build Frontend**: Build React production bundle
5. **Build Backend Jar**: Package Spring Boot JAR
6. **Docker Build Images**: Build và push Docker images
7. **Deploy**: Deploy với docker-compose (chỉ branch `deploy`)

## 🔍 Monitoring

### Xem Logs

```bash
# Jenkins logs
sudo journalctl -u jenkins -f

# Container logs
docker compose logs -f

# Specific service
docker compose logs backend -f
docker compose logs frontend -f
docker compose logs postgres -f
```

### Health Checks

```bash
# Backend API
curl http://localhost:8088/api/auth/login

# Frontend
curl http://localhost:3000

# Database
docker exec -it ltweb2-postgres psql -U postgres -d cafe_shop -c "SELECT version();"
```

## 🐛 Troubleshooting

### Jenkins không trigger khi push

**Giải pháp**:

- Kiểm tra webhook trong GitHub Settings → Webhooks
- Xem Recent Deliveries (phải có status 200)
- Đảm bảo firewall cho phép port 8080 từ internet
- Test: `curl -X POST http://<jenkins-ip>:8080/github-webhook/`

### Docker permission denied

**Giải pháp**:

```bash
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### Port already in use

**Giải pháp**:

```bash
# Kiểm tra process đang dùng port
sudo netstat -tlnp | grep 8088
sudo lsof -i :8088
# Kill process hoặc đổi port
```

### Maven build failed

**Giải pháp**:

```bash
# Test build thủ công
cd LT_Web_2-main/LT_Web2
mvn clean install
# Xem error log
```

## 📚 Tài Liệu Tham Khảo

- [Jenkins Setup Guide](ci/JENKINS_SETUP.md) - Hướng dẫn chi tiết từng bước
- [Backend API Documentation](LT_Web_2-main/LT_Web2/README.md)
- [Frontend Documentation](cafe-fe/README.md)

## 🔐 Bảo Mật

**⚠️ Lưu ý quan trọng**:

- Không commit file `.env` vào git
- Không commit passwords, tokens vào code
- Sử dụng Jenkins Credentials để lưu secrets
- Đổi `JWT_SECRET` trong production
- Sử dụng HTTPS cho production (setup nginx + Let's Encrypt)

## 🎯 Roadmap

- [x] Docker hóa backend và frontend
- [x] Setup Jenkins pipeline
- [x] Auto build khi push code
- [x] Run automated tests
- [x] Deploy với docker-compose
- [ ] Setup monitoring (Prometheus + Grafana)
- [ ] Add security scanning (Trivy)
- [ ] Setup staging environment
- [ ] Implement blue-green deployment

## 👥 Contributors

- Nguyễn Thọ Ngọc ([@NguyenThoNgocIT](https://github.com/NguyenThoNgocIT))

## 📄 License

This project is for educational purposes (Đồ án cuối kỳ môn DevOps).

---

**Cần hỗ trợ?** Xem chi tiết tại [ci/JENKINS_SETUP.md](ci/JENKINS_SETUP.md)
