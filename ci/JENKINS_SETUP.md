# Hướng Dẫn Cấu Hình Jenkins CI/CD Chi Tiết

## Bước 1: Tạo GitHub Personal Access Token

### 1.1. Truy cập GitHub Settings

1. Đăng nhập GitHub → Click avatar góc phải → **Settings**
2. Scroll xuống dưới sidebar trái → Click **Developer settings**
3. Click **Personal access tokens** → **Tokens (classic)**
4. Click **Generate new token** → **Generate new token (classic)**

### 1.2. Cấu hình Token

1. **Note**: Đặt tên dễ nhớ, ví dụ: `Jenkins CI/CD LT_Web_2`
2. **Expiration**: Chọn thời hạn (khuyến nghị: 90 days hoặc No expiration cho dev)
3. **Select scopes** - Tick các quyền sau:

   - ✅ `repo` (Full control of private repositories)
     - ✅ `repo:status` (Access commit status)
     - ✅ `repo_deployment` (Access deployment status)
     - ✅ `public_repo` (Access public repositories)
     - ✅ `repo:invite` (Access repository invitations)
   - ✅ `admin:repo_hook` (Full control of repository hooks)
     - ✅ `write:repo_hook` (Write repository hooks)
     - ✅ `read:repo_hook` (Read repository hooks)
   - ✅ `workflow` (Update GitHub Action workflows - optional nếu có)

4. Click **Generate token** (nút màu xanh ở cuối trang)
5. **⚠️ QUAN TRỌNG**: Copy token ngay (chỉ hiển thị 1 lần)
   - Token có dạng: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`
   - Lưu vào file text tạm hoặc password manager

---

## Bước 2: Tạo Credentials Trong Jenkins

### 2.1. Truy cập Credentials Manager

1. Mở Jenkins: `http://<jenkins-ip>:8080`
2. Dashboard → Click **Manage Jenkins** (sidebar trái)
3. Click **Credentials** (hoặc **Manage Credentials**)
4. Click **(global)** domain (hoặc domain phù hợp)
5. Click **Add Credentials** (hoặc **+ Add Credentials** góc trái)

### 2.2. Tạo Credential #1: GitHub Token

**Mục đích**: Cho Jenkins truy cập repo để checkout code

⚠️ **QUAN TRỌNG**: Git SCM chỉ nhận loại **Username with password**, KHÔNG nhận Secret text!

| Field           | Giá trị                                              |
| --------------- | ---------------------------------------------------- |
| **Kind**        | **Username with password**                           |
| **Scope**       | Global (Jenkins, nodes, items, all child items, etc) |
| **Username**    | `NguyenThoNgocIT` (GitHub username của bạn)          |
| **Password**    | Paste GitHub token vừa tạo (`ghp_xxx...`)            |
| **ID**          | `github-token`                                       |
| **Description** | GitHub Personal Access Token for LT_Web_2            |

→ Click **Create**

**💡 Giải thích**:

- Git plugin trong Jenkins yêu cầu credential có Username + Password
- Token GitHub sẽ được điền vào field **Password** (không phải Secret)
- Nếu dùng Secret text, credential sẽ không hiện trong dropdown Git SCM

### 2.3. Tạo Credential #2: DockerHub

**Mục đích**: Push Docker images lên DockerHub

1. Click **Add Credentials** lần nữa
2. Điền thông tin:

| Field           | Giá trị                                     |
| --------------- | ------------------------------------------- |
| **Kind**        | Username with password                      |
| **Scope**       | Global                                      |
| **Username**    | DockerHub username (ví dụ: `nguyenthongoc`) |
| **Password**    | DockerHub password hoặc Access Token        |
| **ID**          | `dockerhub-creds`                           |
| **Description** | DockerHub credentials for pushing images    |

→ Click **Create**

### 2.4. (Optional) Tạo Credential #3: Webhook Secret

**Mục đích**: Xác thực webhook từ GitHub

1. Tạo secret string random:

```bash
openssl rand -hex 20
# Output example: 801b6cd8d7d7ed5db48dd3391b67fa5a0562543a

```

2. Trong Jenkins, thêm credential:

| Field           | Giá trị                   |
| --------------- | ------------------------- |
| **Kind**        | Secret text               |
| **Scope**       | Global                    |
| **Secret**      | Paste secret vừa generate |
| **ID**          | `github-webhook-secret`   |
| **Description** | GitHub webhook secret     |

→ Click **Create**

---

## Bước 3: Tạo Pipeline Job (Option B)

### 3.1. Tạo Job Mới

1. Dashboard → Click **New Item** (góc trái)
2. **Enter an item name**: `LT_Web_2_Pipeline`
3. Chọn **Pipeline**
4. Click **OK**

### 3.2. Cấu hình General

Trong trang cấu hình job:

#### Section: General

- ✅ Tick **GitHub project**
- **Project url**: `https://github.com/NguyenThoNgocIT/LT_Web_2/`
- ✅ Tick **Discard old builds**
  - **Strategy**: Log Rotation
  - **Max # of builds to keep**: `10`

#### Section: Build Triggers

- ✅ Tick **GitHub hook trigger for GITScm polling**
  - Cho phép GitHub webhook trigger build tự động

### 3.3. Cấu hình Pipeline

#### Section: Pipeline

1. **Definition**: Chọn **Pipeline script from SCM**
2. **SCM**: Chọn **Git**
3. **Repository URL**:
   ```
   https://github.com/NguyenThoNgocIT/LT_Web_2.git
   ```
4. **Credentials**: Chọn `github-token` (vừa tạo ở Bước 2.2)

5. **Branches to build**:

   - **Branch Specifier**: `*/deploy`
   - Có thể thêm nhiều branch pattern: `*/main`, `*/master`, `*/develop`

6. **Repository browser**: (Optional) Chọn **githubweb**

   - **URL**: `https://github.com/NguyenThoNgocIT/LT_Web_2/`

7. **Script Path**: `Jenkinsfile`

   - Đây là đường dẫn file Jenkinsfile trong repo (đã tạo ở root)

8. **Lightweight checkout**: ✅ Tick (tối ưu tốc độ)

### 3.4. Advanced Settings (Optional)

Scroll xuống **Additional Behaviours** → Click **Add**:

- **Clean before checkout**: Đảm bảo workspace sạch
- **Prune stale remote-tracking branches**: Dọn branch cũ

### 3.5. Lưu Cấu Hình

Click **Save** ở cuối trang

---

## Bước 4: Cấu Hình GitHub Webhook

### 4.1. Lấy Jenkins Webhook URL

#### 4.1.1. Tìm IP của Máy Ảo CentOS

Nếu bạn đang chạy Jenkins trên `http://localhost:8080` trong máy ảo, bạn cần tìm IP thật của VM:

**Cách 1: Trong VM CentOS, chạy lệnh:**

```bash
# Lấy tất cả IP
ip addr show

# Hoặc chỉ lấy IP chính (thường là enp0s3 hoặc ens33)
ip addr show | grep "inet " | grep -v 127.0.0.1

# Hoặc dùng hostname
hostname -I

# Kết quả ví dụ: 192.168.1.100 (đây là IP bạn cần)
```

**Cách 2: Kiểm tra trong VMware:**

- Mở VM → Click vào VM settings → Network Adapter
- Nếu dùng **NAT**: IP thường dạng `192.168.x.x`
- Nếu dùng **Bridged**: IP cùng dải với máy host (ví dụ: `192.168.1.x`)

**Cách 3: Từ máy Windows host:**

```powershell
# Ping hostname của VM (nếu biết)
ping centos-vm

# Hoặc kiểm tra ARP table
arp -a
```

#### 4.1.2. Jenkins Webhook URL Format

Sau khi có IP, URL webhook sẽ là:

```
http://<jenkins-ip>:8080/github-webhook/
```

**Ví dụ cụ thể:**

- Nếu IP VM là `192.168.1.100`: `http://192.168.1.100:8080/github-webhook/`
- Nếu dùng domain: `http://jenkins.yourcompany.com/github-webhook/`

**⚠️ Lưu ý quan trọng**:

- URL phải có dấu `/` cuối cùng
- Jenkins phải truy cập được từ internet (GitHub sẽ gửi webhook từ bên ngoài)
- Nếu VM ở mạng nội bộ (NAT), cần cấu hình port forwarding hoặc dùng ngrok

**Option C: Bridged Network (Khuyến nghị)**

```bash
# Đổi VM network sang Bridged mode
# VM sẽ nhận IP từ router, truy cập được từ mạng LAN
# Nhưng vẫn cần public IP hoặc VPN để GitHub webhook gọi vào
```

### 4.2. Tạo Webhook Trên GitHub

1. Truy cập repo: `https://github.com/NguyenThoNgocIT/LT_Web_2`
2. Click tab **Settings** (phải là owner/admin repo)
3. Sidebar trái → Click **Webhooks**
4. Click **Add webhook**

### 4.3. Cấu hình Webhook

Điền form:

| Field                | Giá trị                                                            |
| -------------------- | ------------------------------------------------------------------ |
| **Payload URL**      | `http://192.168.1.52:8080/github-webhook/`                         |
| **Content type**     | `application/json`                                                 |
| **Secret**           | Paste secret từ Bước 2.4 (nếu đã tạo) - Optional nhưng khuyến nghị |
| **SSL verification** | Enable (nếu dùng HTTPS), Disable (nếu HTTP dev)                    |
| **Which events**     | Chọn **Just the push event** (mặc định)                            |
| **Active**           | ✅ Tick                                                            |

Click **Add webhook** (nút xanh)

### 4.4. Kiểm Tra Webhook

Sau khi tạo, GitHub sẽ gửi ping request:

1. Trong danh sách webhooks, click vào webhook vừa tạo
2. Scroll xuống **Recent Deliveries**
3. Click vào delivery đầu tiên (ping event)
4. Xem **Response**:
   - ✅ Status code **200 OK**: Thành công
   - ❌ Status code **4xx/5xx**: Lỗi (kiểm tra URL, firewall, Jenkins status)

---
done-------------------------------------------------
## Bước 5: Test Pipeline Thủ Công

### 5.1. Trigger Build Thủ Công Lần Đầu

1. Vào job `LT_Web_2_Pipeline`
2. Click **Build Now** (sidebar trái)
3. Xem progress trong **Build History**
4. Click vào build number (ví dụ: `#1`)
5. Click **Console Output** để xem log chi tiết

### 5.2. Theo Dõi Stages

Trong build view, bạn sẽ thấy các stage:

- ✅ Checkout
- ✅ Backend Tests
- ✅ Frontend Tests
- ✅ Build Frontend
- ✅ Build Backend Jar
- ✅ Docker Build Images
- ✅ Deploy (chỉ chạy nếu branch là `deploy`)

### 5.3. Xử Lý Lỗi Thường Gặp

#### Lỗi: "docker: command not found"

**Nguyên nhân**: Jenkins user không có quyền docker

**Giải pháp**:

```bash
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

#### Lỗi: "mvn: command not found"

**Nguyên nhân**: Maven chưa cài hoặc không trong PATH

**Giải pháp**:

```bash
# Kiểm tra
which mvn
# Nếu không có, cài:
sudo yum install -y maven
```

#### Lỗi: "npm: command not found"

**Giải pháp**:

```bash
# Cài Node.js
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs
node --version
npm --version
```

#### Lỗi: Authentication failed (GitHub)

**Nguyên nhân**: Token hết hạn hoặc không đủ quyền

**Giải pháp**:

1. Tạo lại GitHub token với đủ quyền
2. Cập nhật credential `github-token` trong Jenkins
3. Re-run build

---

## Bước 6: Test Webhook Tự Động

### 6.1. Push Code Lên GitHub

```bash
cd /path/to/LT_Web_2
git checkout deploy
# Sửa file bất kỳ (ví dụ README)
echo "# Test CI/CD" >> README.md
git add .
git commit -m "Test Jenkins webhook trigger"
git push origin deploy
```

### 6.2. Kiểm Tra Jenkins

1. Sau vài giây, trở lại Jenkins Dashboard
2. Job `LT_Web_2_Pipeline` sẽ tự động trigger (có icon ⚙️ đang chạy)
3. Click vào build → Console Output để theo dõi

### 6.3. Xác Minh Webhook Hoạt Động

GitHub Webhooks page → Recent Deliveries:

- Thấy delivery mới với status **200**
- Response body có chứa `{"jobs":{...}}`

---

## Bước 7: Cấu Hình Docker Registry (DockerHub)

### 7.1. Cập Nhật Jenkinsfile

Mở file `Jenkinsfile` và sửa dòng:

```groovy
DOCKER_NAMESPACE = 'your_dockerhub_username'  // Đổi thành username thật
```

Ví dụ:

```groovy
DOCKER_NAMESPACE = 'nguyenthongoc'
```

### 7.2. Verify DockerHub Credentials

```bash
# Test trên Jenkins server
docker login
# Username: <dockerhub_username>
# Password: <dockerhub_password>
# Login Succeeded

# Test push
docker tag alpine:latest <username>/test:latest
docker push <username>/test:latest
docker rmi <username>/test:latest
```

---

## Bước 8: Deploy & Verify

### 8.1. Chạy Deploy Stage

Khi build thành công và branch là `deploy`:

- Stage "Deploy (Docker Compose)" sẽ chạy
- Docker compose sẽ pull images và khởi động containers

### 8.2. Kiểm Tra Services

```bash
# Trên Jenkins server
docker ps
# Nên thấy 3 containers:
# - ltweb2-postgres
# - ltweb2-backend
# - ltweb2-frontend

# Kiểm tra logs
docker logs ltweb2-backend
docker logs ltweb2-frontend

# Test endpoints
curl http://localhost:8088/api/auth/login
curl http://localhost:3000/
```

### 8.3. Truy Cập Từ Browser

- Frontend: `http://<vm-ip>:3000`
- Backend API: `http://<vm-ip>:8088/api`
- Swagger (nếu có): `http://<vm-ip>:8088/swagger-ui.html`

---

## Bước 9: Tối Ưu & Bảo Mật

### 9.1. Thêm Environment Variables

Trong Jenkins job config → Pipeline section → thêm:

```groovy
environment {
  JWT_SECRET = credentials('jwt-secret-id')
  DB_PASSWORD = credentials('db-password-id')
}
```

### 9.2. Cấu Hình Nginx Reverse Proxy (Production)

```nginx
# /etc/nginx/conf.d/ltweb2.conf
server {
  listen 80;
  server_name yourdomain.com;

  location / {
    proxy_pass http://localhost:3000;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }

  location /api {
    proxy_pass http://localhost:8088;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

### 9.3. Setup SSL (Let's Encrypt)

```bash
sudo yum install -y certbot python3-certbot-nginx
sudo certbot --nginx -d yourdomain.com
sudo systemctl reload nginx
```

---

## Bước 10: Monitoring & Logging

### 10.1. Xem Jenkins Logs

```bash
sudo journalctl -u jenkins -f
```

### 10.2. Xem Container Logs

```bash
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f postgres
```

### 10.3. Cấu Hình Slack/Email Notifications (Optional)

Trong Jenkinsfile, thêm vào `post` section:

```groovy
post {
  success {
    mail to: 'team@example.com',
         subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
         body: "Good job!"
  }
  failure {
    mail to: 'team@example.com',
         subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
         body: "Check: ${env.BUILD_URL}"
  }
}
```

---

## Troubleshooting Common Issues

### Issue 1: Jenkins Cannot Access GitHub

**Symptoms**: "Failed to connect to repository"

**Solutions**:

```bash
# Test git connection
git ls-remote https://github.com/NguyenThoNgocIT/LT_Web_2.git
# If fails, check DNS/firewall
ping github.com
curl -I https://github.com
```

### Issue 2: Docker Permission Denied

**Solutions**:

```bash
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
# Re-run build
```

### Issue 3: Port Already in Use

**Solutions**:

```bash
# Check what's using port
sudo netstat -tlnp | grep 8088
sudo lsof -i :8088
# Kill process or change port in application.properties
```

### Issue 4: Webhook Not Triggering

**Checklist**:

- ✅ Firewall allows port 8080 from internet
- ✅ Jenkins URL in GitHub webhook is correct
- ✅ "GitHub hook trigger" is enabled in Jenkins job
- ✅ Recent Deliveries show 200 OK

**Test manually**:

```bash
curl -X POST http://<jenkins-ip>:8080/github-webhook/
```

---

## Quick Reference

### Important URLs

- Jenkins: `http://<vm-ip>:8080`
- Frontend: `http://<vm-ip>:3000`
- Backend: `http://<vm-ip>:8088`
- PostgreSQL: `<vm-ip>:5432`

### Important Credentials IDs

- `github-token`: GitHub PAT
- `dockerhub-creds`: DockerHub login
- `github-webhook-secret`: Webhook secret (optional)

### Important Commands

```bash
# Jenkins
sudo systemctl status jenkins
sudo systemctl restart jenkins
sudo tail -f /var/log/jenkins/jenkins.log

# Docker
docker ps
docker compose ps
docker compose logs -f
docker compose down
docker compose up -d

# Firewall
sudo firewall-cmd --list-all
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

---

**Hoàn tất! 🎉**

Giờ bạn có hệ thống CI/CD hoàn chỉnh:

- ✅ Auto build khi push code
- ✅ Run tests (backend + frontend)
- ✅ Build Docker images
- ✅ Push lên DockerHub
- ✅ Deploy tự động với docker-compose
