# 🚀 Hướng Dẫn Triển Khai - Nhánh Deploy

> **Hệ Thống Quản Lý Quán Cafe** - Tích hợp Jenkins CI/CD, Prometheus, Grafana và triển khai trên AWS

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.5-green?logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue?logo=react)](https://react.dev)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI/CD-red?logo=jenkins)](https://www.jenkins.io)
[![Docker](https://img.shields.io/badge/Docker-Container-2496ED?logo=docker)](https://www.docker.com)
[![AWS](https://img.shields.io/badge/AWS-Cloud-orange?logo=amazon-aws)](https://aws.amazon.com)
[![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-red?logo=prometheus)](https://prometheus.io)
[![Grafana](https://img.shields.io/badge/Grafana-Visualization-orange?logo=grafana)](https://grafana.com)

## 📌 Mục Lục

1. [🎯 Tổng Quan Dự Án](#-tổng-quan-dự-án)
2. [✨ Tính Năng Chính](#-tính-năng-chính)
3. [🏗️ Kiến Trúc Hệ Thống](#️-kiến-trúc-hệ-thống)
4. [📦 Công Nghệ Sử Dụng](#-công-nghệ-sử-dụng)
5. [🔧 Cài Đặt & Triển Khai](#-cài-đặt--triển-khai)
6. [📊 Monitoring & Logging](#-monitoring--logging)
7. [🔐 Bảo Mật](#-bảo-mật)
8. [📝 API Endpoints](#-api-endpoints)
9. [🐛 Troubleshooting](#-troubleshooting)
10. [📞 Hỗ Trợ](#-hỗ-trợ)

---

## 🎯 Tổng Quan Dự Án

**Hệ Thống Quản Lý Quán Cafe** là một ứng dụng web toàn diện cho phép:

- **👥 Khách Hàng (Users)**: Đặt bàn, đặt hàng, xem lịch sử đơn hàng
- **👨‍💼 Quản Trị Viên (Admin)**: Quản lý sản phẩm, bàn, xác nhận đơn hàng, xem báo cáo doanh thu

Dự án được xây dựng với:

- **Backend**: Spring Boot 3.1.5 (Java 21)
- **Frontend**: React 19 + Tailwind CSS
- **Database**: PostgreSQL 15
- **Deployment**: Docker + AWS
- **CI/CD**: Jenkins Pipeline
- **Monitoring**: Prometheus + Grafana

---

## ✨ Tính Năng Chính

### 👥 Chức Năng Người Dùng (User)

- ✅ **Đăng Ký / Đăng Nhập**
  - JWT Authentication
  - Email verification (tuỳ chọn)
- ✅ **Đặt Bàn**
  - Chọn ngày, giờ, số lượng khách
  - Xem lịch sử đặt bàn
  - Hủy đặt bàn
- ✅ **Đặt Hàng (Order)**
  - Duyệt menu sản phẩm
  - Thêm vào giỏ hàng
  - Thanh toán
  - Xem lịch sử đơn hàng
  - Cập nhật trạng thái đơn hàng real-time

### 👨‍💼 Chức Năng Quản Trị Viên (Admin)

- ✅ **Quản Lý Sản Phẩm**
  - Thêm/Sửa/Xóa sản phẩm
  - Upload hình ảnh
  - Quản lý giá, danh mục
- ✅ **Quản Lý Bàn**
  - Thêm/Sửa/Xóa bàn ăn
  - Cấu hình sức chứa
  - Xem tình trạng bàn real-time
- ✅ **Quản Lý Đơn Hàng**
  - Xem danh sách đơn hàng
  - Xác nhận/Hủy đơn hàng
  - Cập nhật trạng thái (Pending → Confirmed → Completed)
- ✅ **Báo Cáo & Thống Kê**
  - Doanh thu theo ngày, tuần, tháng
  - Số đơn hàng, đặt bàn
  - Sản phẩm bán chạy
  - Biểu đồ tương tác

---

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────────────────┐
│                        AWS Cloud                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Load Balancer / ALB                     │  │
│  └────────────┬─────────────────────────────────────────┘  │
│               │                                             │
│  ┌────────────▼──────────────────────────────────────────┐ │
│  │        Kubernetes / ECS Cluster                      │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │ │
│  │  │   Frontend   │  │   Backend    │  │ PostgreSQL │ │ │
│  │  │  (React)     │  │ (Spring Boot)│  │            │ │ │
│  │  └──────────────┘  └──────────────┘  └────────────┘ │ │
│  └────────────┬─────────────────────────────────────────┘ │
│               │                                             │
│  ┌────────────▼──────────────────────────────────────────┐ │
│  │     Monitoring Stack                                │ │
│  │  ┌──────────────┐  ┌──────────────┐                │ │
│  │  │ Prometheus   │  │   Grafana    │                │ │
│  │  └──────────────┘  └──────────────┘                │ │
│  └──────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘

CI/CD Pipeline (Jenkins)
┌──────────────────────────────────────┐
│  GitHub Repository (Push)            │
│  ↓ (Webhook)                         │
│  Jenkins Pipeline                    │
│  ├─ Build Backend (Maven)            │
│  ├─ Build Frontend (npm)             │
│  ├─ Run Tests                        │
│  ├─ Build Docker Images              │
│  ├─ Push to Container Registry       │
│  └─ Deploy to AWS                    │
└──────────────────────────────────────┘
```

---

## 📦 Công Nghệ Sử Dụng

### Backend

| Thành Phần          | Phiên Bản | Mục Đích              |
| ------------------- | --------- | --------------------- |
| **Java**            | 21        | Ngôn ngữ lập trình    |
| **Spring Boot**     | 3.1.5     | Framework backend     |
| **Spring Security** | 6.1+      | Xác thực & Phân quyền |
| **Spring Data JPA** | 3.1+      | ORM & Database access |
| **PostgreSQL**      | 15        | Cơ sở dữ liệu chính   |
| **JWT**             | 0.11.5    | Token authentication  |
| **Lombok**          | 1.18+     | Code generation       |

### Frontend

| Thành Phần       | Phiên Bản | Mục Đích           |
| ---------------- | --------- | ------------------ |
| **React**        | 19+       | Framework frontend |
| **Tailwind CSS** | 3.3+      | Styling & UI       |
| **Axios**        | 1.x       | HTTP client        |
| **React Router** | 6+        | Routing            |

### DevOps & Monitoring

| Thành Phần         | Phiên Bản | Mục Đích              |
| ------------------ | --------- | --------------------- |
| **Docker**         | 20.10+    | Containerization      |
| **Docker Compose** | 2.0+      | Orchestration (local) |
| **Jenkins**        | 2.400+    | CI/CD Pipeline        |
| **Prometheus**     | 2.40+     | Metrics collection    |
| **Grafana**        | 9.0+      | Metrics visualization |
| **AWS**            | -         | Cloud infrastructure  |

---

## 🔧 Cài Đặt & Triển Khai

### Yêu Cầu Trước Khi Bắt Đầu

```bash
# Backend
- Java 21 SDK
- Maven 3.8.1+

# Frontend
- Node.js 18.x+
- npm 9.x+

# DevOps
- Docker Desktop 20.10+
- Docker Compose 2.0+
- Git

# AWS (cho production)
- AWS Account
- AWS CLI v2
- Configured AWS credentials
```

### 1️⃣ Cài Đặt Môi Trường Local

#### Clone Repository

```bash
git clone <your-repo-url>
cd nguyenthongoc
git checkout deploy  # Switch to deploy branch
```

#### Kiểm Tra Môi Trường

```bash
# Chạy script kiểm tra
bash ci/check-environment.sh
```

#### Thiết Lập Backend

```bash
cd LT_Web_2-main/LT_Web2

# Cài đặt dependencies
mvn clean install

# Chạy ứng dụng (local)
mvn spring-boot:run

# Ứng dụng sẽ chạy tại: http://localhost:8080
```

#### Thiết Lập Frontend

```bash
cd cafe-fe

# Cài đặt dependencies
npm install

# Chạy dev server
npm start

# Frontend sẽ chạy tại: http://localhost:3000
```

### 2️⃣ Build & Chạy với Docker Compose

```bash
# Từ thư mục gốc
docker-compose up -d

# Kiểm tra logs
docker-compose logs -f

# Dừng services
docker-compose down
```

**Services sẽ khả dụng tại:**

- Frontend: http://localhost:80
- Backend API: http://localhost:8080
- PostgreSQL: localhost:5432
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

### 3️⃣ Cấu Hình Jenkins

#### Bước 1: Tạo Jenkins Job

```bash
# Chi tiết xem tại:
cat ci/JENKINS_SETUP.md
```

#### Bước 2: Webhook GitHub

Truy cập **Settings → Webhooks** trong GitHub repository:

- **Payload URL**: `http://jenkins-server:8080/github-webhook/`
- **Content type**: `application/json`
- **Events**: Push events
- **Active**: ✅

#### Bước 3: Configure Pipeline

Jenkinsfile sẽ tự động trigger khi có push:

```groovy
// Pipeline Stages:
1. Checkout Code
2. Test Backend (Maven)
3. Test Frontend (npm)
4. Build Artifacts
5. Build Docker Images
6. Push to Registry
7. Deploy to AWS
```

### 4️⃣ Triển Khai lên AWS (Enterprise-Grade)

#### Kiến Trúc AWS Enterprise

```
┌─────────────────────────────────────────────────────────────┐
│                    AWS Account (ap-southeast-1)             │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │        Internet Gateway                              │  │
│  └────────────┬─────────────────────────────────────────┘  │
│               │                                             │
│  ┌────────────▼──────────────────────────────────────────┐ │
│  │        VPC (10.0.0.0/16)                             │ │
│  │  ┌────────────────────────────────────────────────┐  │ │
│  │  │        Public Subnet 1 (10.0.1.0/24) - AZ-a  │  │ │
│  │  │  ┌──────────────────────────────────────────┐ │  │ │
│  │  │  │  NAT Gateway | Application Load Balancer │ │  │ │
│  │  │  │  EC2 (Frontend) | Jenkins (CI/CD)        │ │  │ │
│  │  │  └──────────────────────────────────────────┘ │  │ │
│  │  └────────────────────────────────────────────────┘  │ │
│  │                                                       │ │
│  │  ┌────────────────────────────────────────────────┐  │ │
│  │  │        Private Subnet 1 (10.0.2.0/24) - AZ-a │  │ │
│  │  │  ┌──────────────────────────────────────────┐ │  │ │
│  │  │  │  EC2 (Backend App)                       │ │  │ │
│  │  │  │  RDS PostgreSQL (Primary)                │ │  │ │
│  │  │  │  ElastiCache (Redis)                     │ │  │ │
│  │  │  └──────────────────────────────────────────┘ │  │ │
│  │  └────────────────────────────────────────────────┘  │ │
│  │                                                       │ │
│  │  ┌────────────────────────────────────────────────┐  │ │
│  │  │    Private Subnet 2 (10.0.3.0/24) - AZ-b    │  │ │
│  │  │  ┌──────────────────────────────────────────┐ │  │ │
│  │  │  │  EC2 (Backend App - Backup)              │ │  │ │
│  │  │  │  RDS PostgreSQL (Standby - Multi-AZ)     │ │  │ │
│  │  │  │  Prometheus & Grafana Monitoring         │ │  │ │
│  │  │  └──────────────────────────────────────────┘ │  │ │
│  │  └────────────────────────────────────────────────┘  │ │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  External Services:                                         │
│  ├─ Route 53 (DNS)                                         │
│  ├─ S3 (Bucket cho uploads)                               │
│  ├─ CloudFront (CDN cho static files)                      │
│  ├─ AWS Secrets Manager (Credentials)                      │
│  └─ CloudWatch (Logs & Monitoring)                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

#### Step 1: VPC & Network Setup

##### 1.1 Tạo VPC

```bash
# Tạo VPC
aws ec2 create-vpc \
  --cidr-block 10.0.0.0/16 \
  --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=cafe-vpc}]' \
  --region ap-southeast-1

# Kết quả: vpc-xxxxxxxxx
export VPC_ID=vpc-xxxxxxxxx

# Enable DNS hostname
aws ec2 modify-vpc-attribute \
  --vpc-id $VPC_ID \
  --enable-dns-hostnames \
  --region ap-southeast-1
```

##### 1.2 Tạo Internet Gateway

```bash
# Tạo Internet Gateway
aws ec2 create-internet-gateway \
  --tag-specifications 'ResourceType=internet-gateway,Tags=[{Key=Name,Value=cafe-igw}]' \
  --region ap-southeast-1

export IGW_ID=igw-xxxxxxxxx

# Attach IGW to VPC
aws ec2 attach-internet-gateway \
  --internet-gateway-id $IGW_ID \
  --vpc-id $VPC_ID \
  --region ap-southeast-1
```

##### 1.3 Tạo Public Subnets

```bash
# Public Subnet 1 (AZ-a)
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.1.0/24 \
  --availability-zone ap-southeast-1a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=cafe-public-subnet-1}]' \
  --region ap-southeast-1

export PUBLIC_SUBNET_1=subnet-xxxxxxxxx

# Public Subnet 2 (AZ-b) - cho redundancy
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.4.0/24 \
  --availability-zone ap-southeast-1b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=cafe-public-subnet-2}]' \
  --region ap-southeast-1

export PUBLIC_SUBNET_2=subnet-yyyyyyyyy
```

##### 1.4 Tạo Private Subnets

```bash
# Private Subnet 1 (AZ-a)
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.2.0/24 \
  --availability-zone ap-southeast-1a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=cafe-private-subnet-1}]' \
  --region ap-southeast-1

export PRIVATE_SUBNET_1=subnet-zzzzzzzzz

# Private Subnet 2 (AZ-b)
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.3.0/24 \
  --availability-zone ap-southeast-1b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=cafe-private-subnet-2}]' \
  --region ap-southeast-1

export PRIVATE_SUBNET_2=subnet-wwwwwwwww
```

##### 1.5 Tạo Elastic IP & NAT Gateway

```bash
# Allocate Elastic IP cho NAT Gateway
aws ec2 allocate-address \
  --domain vpc \
  --tag-specifications 'ResourceType=elastic-ip,Tags=[{Key=Name,Value=cafe-nat-eip}]' \
  --region ap-southeast-1

export NAT_EIP_ALLOCATION=eipalloc-xxxxxxxxx

# Create NAT Gateway trong public subnet
aws ec2 create-nat-gateway \
  --subnet-id $PUBLIC_SUBNET_1 \
  --allocation-id $NAT_EIP_ALLOCATION \
  --tag-specifications 'ResourceType=nat-gateway,Tags=[{Key=Name,Value=cafe-nat}]' \
  --region ap-southeast-1

export NAT_GATEWAY_ID=nat-xxxxxxxxx

# Wait for NAT Gateway to be ready
aws ec2 wait nat-gateway-available \
  --nat-gateway-ids $NAT_GATEWAY_ID \
  --region ap-southeast-1
```

#### Step 2: Route Tables & Routing

##### 2.1 Public Route Table

```bash
# Tạo public route table
aws ec2 create-route-table \
  --vpc-id $VPC_ID \
  --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=cafe-public-rt}]' \
  --region ap-southeast-1

export PUBLIC_RT=rtb-xxxxxxxxx

# Add route to Internet Gateway
aws ec2 create-route \
  --route-table-id $PUBLIC_RT \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id $IGW_ID \
  --region ap-southeast-1

# Associate public subnets
aws ec2 associate-route-table \
  --subnet-id $PUBLIC_SUBNET_1 \
  --route-table-id $PUBLIC_RT \
  --region ap-southeast-1

aws ec2 associate-route-table \
  --subnet-id $PUBLIC_SUBNET_2 \
  --route-table-id $PUBLIC_RT \
  --region ap-southeast-1
```

##### 2.2 Private Route Table

```bash
# Tạo private route table
aws ec2 create-route-table \
  --vpc-id $VPC_ID \
  --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=cafe-private-rt}]' \
  --region ap-southeast-1

export PRIVATE_RT=rtb-yyyyyyyyy

# Add route to NAT Gateway (for internet access)
aws ec2 create-route \
  --route-table-id $PRIVATE_RT \
  --destination-cidr-block 0.0.0.0/0 \
  --nat-gateway-id $NAT_GATEWAY_ID \
  --region ap-southeast-1

# Associate private subnets
aws ec2 associate-route-table \
  --subnet-id $PRIVATE_SUBNET_1 \
  --route-table-id $PRIVATE_RT \
  --region ap-southeast-1

aws ec2 associate-route-table \
  --subnet-id $PRIVATE_SUBNET_2 \
  --route-table-id $PRIVATE_RT \
  --region ap-southeast-1
```

#### Step 3: Security Groups

##### 3.1 ALB Security Group

```bash
# ALB Security Group (from internet)
aws ec2 create-security-group \
  --group-name cafe-alb-sg \
  --description "Security group for Application Load Balancer" \
  --vpc-id $VPC_ID \
  --region ap-southeast-1

export ALB_SG=sg-xxxxxxxxx

# Allow HTTP
aws ec2 authorize-security-group-ingress \
  --group-id $ALB_SG \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0 \
  --region ap-southeast-1

# Allow HTTPS
aws ec2 authorize-security-group-ingress \
  --group-id $ALB_SG \
  --protocol tcp \
  --port 443 \
  --cidr 0.0.0.0/0 \
  --region ap-southeast-1

# Tag it
aws ec2 create-tags \
  --resources $ALB_SG \
  --tags "Key=Name,Value=cafe-alb-sg" \
  --region ap-southeast-1
```

##### 3.2 Frontend EC2 Security Group

```bash
# Frontend EC2 Security Group
aws ec2 create-security-group \
  --group-name cafe-frontend-sg \
  --description "Security group for Frontend EC2" \
  --vpc-id $VPC_ID \
  --region ap-southeast-1

export FRONTEND_SG=sg-yyyyyyyyy

# Allow HTTP from ALB
aws ec2 authorize-security-group-ingress \
  --group-id $FRONTEND_SG \
  --protocol tcp \
  --port 80 \
  --source-group $ALB_SG \
  --region ap-southeast-1

# Allow HTTPS from ALB
aws ec2 authorize-security-group-ingress \
  --group-id $FRONTEND_SG \
  --protocol tcp \
  --port 443 \
  --source-group $ALB_SG \
  --region ap-southeast-1

# Allow SSH from admin IP (thay bằng IP của bạn)
aws ec2 authorize-security-group-ingress \
  --group-id $FRONTEND_SG \
  --protocol tcp \
  --port 22 \
  --cidr YOUR_ADMIN_IP/32 \
  --region ap-southeast-1

# Tag it
aws ec2 create-tags \
  --resources $FRONTEND_SG \
  --tags "Key=Name,Value=cafe-frontend-sg" \
  --region ap-southeast-1
```

##### 3.3 Backend EC2 Security Group

```bash
# Backend EC2 Security Group
aws ec2 create-security-group \
  --group-name cafe-backend-sg \
  --description "Security group for Backend EC2" \
  --vpc-id $VPC_ID \
  --region ap-southeast-1

export BACKEND_SG=sg-zzzzzzzz

# Allow port 8080 from ALB
aws ec2 authorize-security-group-ingress \
  --group-id $BACKEND_SG \
  --protocol tcp \
  --port 8080 \
  --source-group $ALB_SG \
  --region ap-southeast-1

# Allow SSH from bastion/admin
aws ec2 authorize-security-group-ingress \
  --group-id $BACKEND_SG \
  --protocol tcp \
  --port 22 \
  --cidr YOUR_ADMIN_IP/32 \
  --region ap-southeast-1

# Tag it
aws ec2 create-tags \
  --resources $BACKEND_SG \
  --tags "Key=Name,Value=cafe-backend-sg" \
  --region ap-southeast-1
```

##### 3.4 Database Security Group

```bash
# RDS PostgreSQL Security Group
aws ec2 create-security-group \
  --group-name cafe-database-sg \
  --description "Security group for RDS PostgreSQL" \
  --vpc-id $VPC_ID \
  --region ap-southeast-1

export DATABASE_SG=sg-wwwwwww

# Allow port 5432 from Backend EC2
aws ec2 authorize-security-group-ingress \
  --group-id $DATABASE_SG \
  --protocol tcp \
  --port 5432 \
  --source-group $BACKEND_SG \
  --region ap-southeast-1

# Tag it
aws ec2 create-tags \
  --resources $DATABASE_SG \
  --tags "Key=Name,Value=cafe-database-sg" \
  --region ap-southeast-1
```

#### Step 4: EC2 Instances

##### 4.1 Tạo Key Pair

```bash
# Create EC2 Key Pair
aws ec2 create-key-pair \
  --key-name cafe-app-key \
  --query 'KeyMaterial' \
  --output text > cafe-app-key.pem \
  --region ap-southeast-1

# Adjust permissions
chmod 400 cafe-app-key.pem

# Backup key securely
# Lưu ý: Không commit key file vào git!
```

##### 4.2 Launch Frontend EC2

```bash
# Tạo Frontend EC2 trong Public Subnet
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t3.medium \
  --key-name cafe-app-key \
  --security-group-ids $FRONTEND_SG \
  --subnet-id $PUBLIC_SUBNET_1 \
  --associate-public-ip-address \
  --block-device-mappings DeviceName=/dev/xvda,Ebs={VolumeSize=30,VolumeType=gp3,DeleteOnTermination=true} \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=cafe-frontend-ec2}]' \
  --user-data file://ci/user-data-frontend.sh \
  --monitoring Enabled=true \
  --region ap-southeast-1

export FRONTEND_EC2_ID=i-xxxxxxxxx
```

##### 4.3 Launch Backend EC2 (Private Subnet)

```bash
# Backend EC2 trong Private Subnet
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t3.large \
  --key-name cafe-app-key \
  --security-group-ids $BACKEND_SG \
  --subnet-id $PRIVATE_SUBNET_1 \
  --block-device-mappings DeviceName=/dev/xvda,Ebs={VolumeSize=50,VolumeType=gp3,DeleteOnTermination=true} \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=cafe-backend-ec2-1}]' \
  --user-data file://ci/user-data-backend.sh \
  --monitoring Enabled=true \
  --region ap-southeast-1

export BACKEND_EC2_ID=i-yyyyyyyyy

# Backend EC2 #2 (Backup) trong Private Subnet 2
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t3.large \
  --key-name cafe-app-key \
  --security-group-ids $BACKEND_SG \
  --subnet-id $PRIVATE_SUBNET_2 \
  --block-device-mappings DeviceName=/dev/xvda,Ebs={VolumeSize=50,VolumeType=gp3,DeleteOnTermination=true} \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=cafe-backend-ec2-2}]' \
  --user-data file://ci/user-data-backend.sh \
  --monitoring Enabled=true \
  --region ap-southeast-1

export BACKEND_EC2_ID_2=i-zzzzzzzz
```

#### Step 5: Application Load Balancer (ALB)

```bash
# Tạo ALB
aws elbv2 create-load-balancer \
  --name cafe-alb \
  --subnets $PUBLIC_SUBNET_1 $PUBLIC_SUBNET_2 \
  --security-groups $ALB_SG \
  --scheme internet-facing \
  --type application \
  --ip-address-type ipv4 \
  --tags "Key=Name,Value=cafe-alb" \
  --region ap-southeast-1

export ALB_ARN=arn:aws:elasticloadbalancing:ap-southeast-1:ACCOUNT_ID:loadbalancer/app/cafe-alb/xxx

# Get ALB DNS
export ALB_DNS=$(aws elbv2 describe-load-balancers \
  --load-balancer-arns $ALB_ARN \
  --query 'LoadBalancers[0].DNSName' \
  --output text \
  --region ap-southeast-1)

echo "ALB DNS: $ALB_DNS"

# Tạo Target Group cho Frontend
aws elbv2 create-target-group \
  --name cafe-frontend-tg \
  --protocol HTTP \
  --port 80 \
  --vpc-id $VPC_ID \
  --health-check-protocol HTTP \
  --health-check-path /index.html \
  --health-check-interval-seconds 30 \
  --health-check-timeout-seconds 5 \
  --healthy-threshold-count 2 \
  --unhealthy-threshold-count 3 \
  --matcher HttpCode=200 \
  --region ap-southeast-1

export FRONTEND_TG_ARN=arn:aws:elasticloadbalancing:ap-southeast-1:ACCOUNT_ID:targetgroup/cafe-frontend-tg/xxx

# Register Frontend EC2
aws elbv2 register-targets \
  --target-group-arn $FRONTEND_TG_ARN \
  --targets Id=$FRONTEND_EC2_ID \
  --region ap-southeast-1

# Tạo Target Group cho Backend
aws elbv2 create-target-group \
  --name cafe-backend-tg \
  --protocol HTTP \
  --port 8080 \
  --vpc-id $VPC_ID \
  --health-check-protocol HTTP \
  --health-check-path /actuator/health \
  --health-check-interval-seconds 30 \
  --health-check-timeout-seconds 5 \
  --healthy-threshold-count 2 \
  --unhealthy-threshold-count 3 \
  --matcher HttpCode=200 \
  --region ap-southeast-1

export BACKEND_TG_ARN=arn:aws:elasticloadbalancing:ap-southeast-1:ACCOUNT_ID:targetgroup/cafe-backend-tg/xxx

# Register Backend EC2s
aws elbv2 register-targets \
  --target-group-arn $BACKEND_TG_ARN \
  --targets Id=$BACKEND_EC2_ID Id=$BACKEND_EC2_ID_2 \
  --region ap-southeast-1

# Tạo Listener cho ALB
aws elbv2 create-listener \
  --load-balancer-arn $ALB_ARN \
  --protocol HTTP \
  --port 80 \
  --default-actions Type=forward,TargetGroupArn=$FRONTEND_TG_ARN \
  --region ap-southeast-1

# Tạo listener rule cho /api/ -> backend
aws elbv2 create-rule \
  --listener-arn arn:aws:elasticloadbalancing:ap-southeast-1:ACCOUNT_ID:listener/app/cafe-alb/xxx/yyy \
  --priority 1 \
  --conditions Field=path-pattern,Values=/api/* \
  --actions Type=forward,TargetGroupArn=$BACKEND_TG_ARN \
  --region ap-southeast-1
```

#### Step 6: RDS PostgreSQL (Multi-AZ)

```bash
# Tạo RDS Security Group Inbound rule (đã làm ở bước 3.4)

# Create RDS Subnet Group
aws rds create-db-subnet-group \
  --db-subnet-group-name cafe-db-subnet \
  --db-subnet-group-description "Subnet group for Cafe PostgreSQL" \
  --subnet-ids $PRIVATE_SUBNET_1 $PRIVATE_SUBNET_2 \
  --tags "Key=Name,Value=cafe-db-subnet" \
  --region ap-southeast-1

# Tạo RDS Instance (Multi-AZ)
aws rds create-db-instance \
  --db-instance-identifier cafe-postgres-prod \
  --db-instance-class db.t3.medium \
  --engine postgres \
  --engine-version 15.3 \
  --allocated-storage 100 \
  --storage-type gp3 \
  --master-username postgres \
  --master-user-password YourSecurePassword123! \
  --db-subnet-group-name cafe-db-subnet \
  --vpc-security-group-ids $DATABASE_SG \
  --multi-az \
  --backup-retention-period 30 \
  --preferred-backup-window "03:00-04:00" \
  --preferred-maintenance-window "sun:04:00-sun:05:00" \
  --db-name cafe_shop \
  --enable-cloudwatch-logs-exports postgresql \
  --enable-iam-database-authentication \
  --storage-encrypted \
  --deletion-protection \
  --region ap-southeast-1

# Wait for RDS to be available
aws rds wait db-instance-available \
  --db-instance-identifier cafe-postgres-prod \
  --region ap-southeast-1

# Get RDS Endpoint
export RDS_ENDPOINT=$(aws rds describe-db-instances \
  --db-instance-identifier cafe-postgres-prod \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text \
  --region ap-southeast-1)

echo "RDS Endpoint: $RDS_ENDPOINT"
```

#### Step 7: Store Credentials in AWS Secrets Manager

```bash
# Store database credentials
aws secretsmanager create-secret \
  --name cafe/postgres/master \
  --description "PostgreSQL master credentials" \
  --secret-string '{"username":"postgres","password":"YourSecurePassword123!","engine":"postgres","host":"'$RDS_ENDPOINT'","port":5432,"dbname":"cafe_shop"}' \
  --region ap-southeast-1

# Store JWT Secret
aws secretsmanager create-secret \
  --name cafe/jwt/secret \
  --description "JWT Secret Key" \
  --secret-string "your-super-secret-jwt-key-min-32-chars" \
  --region ap-southeast-1

# Store DockerHub credentials
aws secretsmanager create-secret \
  --name cafe/dockerhub/credentials \
  --description "DockerHub credentials" \
  --secret-string '{"username":"your-dockerhub-user","password":"your-token"}' \
  --region ap-southeast-1
```

#### Step 8: Deploy Applications

##### 8.1 Frontend Deployment (via User Data)

Tạo file `ci/user-data-frontend.sh`:

```bash
#!/bin/bash
set -e

# Update system
apt-get update && apt-get upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Install Docker Compose
apt-get install -y docker-compose

# Clone repository
cd /opt
git clone https://github.com/your-repo/nguyenthongoc.git
cd nguyenthongoc
git checkout deploy

# Pull credentials from Secrets Manager
aws secretsmanager get-secret-value \
  --secret-id cafe/dockerhub/credentials \
  --region ap-southeast-1 \
  --query SecretString --output text | jq -r '.password' | \
  docker login --username $(aws secretsmanager get-secret-value \
    --secret-id cafe/dockerhub/credentials \
    --region ap-southeast-1 \
    --query SecretString --output text | jq -r '.username') \
  --password-stdin

# Pull and run Docker image
docker pull nguyenthongoc/ltweb2-frontend:latest
docker run -d \
  --name ltweb2-frontend \
  --restart always \
  -p 80:80 \
  -v /opt/nginx.conf:/etc/nginx/nginx.conf:ro \
  nguyenthongoc/ltweb2-frontend:latest

# Enable CloudWatch agent
wget https://s3.amazonaws.com/aws-cloudwatch/downloads/latest/awscloudwatch-agent/ubuntu/amd64/AmazonCloudWatchAgent.zip
unzip AmazonCloudWatchAgent.zip
./install.sh
```

##### 8.2 Backend Deployment (via User Data)

Tạo file `ci/user-data-backend.sh`:

```bash
#!/bin/bash
set -e

# Update system
apt-get update && apt-get upgrade -y

# Install Java 21
apt-get install -y openjdk-21-jdk

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Get credentials from Secrets Manager
DB_SECRET=$(aws secretsmanager get-secret-value \
  --secret-id cafe/postgres/master \
  --region ap-southeast-1 \
  --query SecretString --output text)

export DB_HOST=$(echo $DB_SECRET | jq -r '.host')
export DB_PORT=$(echo $DB_SECRET | jq -r '.port')
export DB_NAME=$(echo $DB_SECRET | jq -r '.dbname')
export DB_USER=$(echo $DB_SECRET | jq -r '.username')
export DB_PASSWORD=$(echo $DB_SECRET | jq -r '.password')

# Clone and deploy
cd /opt
git clone https://github.com/your-repo/nguyenthongoc.git
cd nguyenthongoc
git checkout deploy

# Pull Docker image
docker pull nguyenthongoc/ltweb2-backend:latest
docker run -d \
  --name ltweb2-backend \
  --restart always \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME" \
  -e SPRING_DATASOURCE_USERNAME="$DB_USER" \
  -e SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD" \
  -e JWT_SECRET="$(aws secretsmanager get-secret-value --secret-id cafe/jwt/secret --region ap-southeast-1 --query SecretString --output text)" \
  nguyenthongoc/ltweb2-backend:latest

# Setup CloudWatch
wget https://s3.amazonaws.com/aws-cloudwatch/downloads/latest/awscloudwatch-agent/ubuntu/amd64/AmazonCloudWatchAgent.zip
unzip AmazonCloudWatchAgent.zip
./install.sh
```

#### Step 9: DNS & Certificate (Route 53 + ACM)

```bash
# Create SSL Certificate (nếu có domain)
aws acm request-certificate \
  --domain-name yourdomain.com \
  --subject-alternative-names www.yourdomain.com \
  --validation-method DNS \
  --region ap-southeast-1

# Get Hosted Zone ID
export HOSTED_ZONE_ID=$(aws route53 list-hosted-zones-by-name \
  --dns-name yourdomain.com \
  --query 'HostedZones[0].Id' \
  --output text)

# Create DNS record pointing to ALB
aws route53 change-resource-record-sets \
  --hosted-zone-id $HOSTED_ZONE_ID \
  --change-batch '{
    "Changes": [{
      "Action": "CREATE",
      "ResourceRecordSet": {
        "Name": "yourdomain.com",
        "Type": "A",
        "AliasTarget": {
          "HostedZoneId": "Z1LMS91P8CMLE5",
          "DNSName": "'$ALB_DNS'",
          "EvaluateTargetHealth": true
        }
      }
    }]
  }' \
  --region ap-southeast-1
```

#### Step 10: Kiểm Tra Deployment

```bash
# Kiểm tra VPC
aws ec2 describe-vpcs --vpc-ids $VPC_ID --region ap-southeast-1

# Kiểm tra Security Groups
aws ec2 describe-security-groups --group-ids $ALB_SG $BACKEND_SG $FRONTEND_SG $DATABASE_SG --region ap-southeast-1

# Kiểm tra EC2 Instances
aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=cafe-*" \
  --query 'Reservations[*].Instances[*].[InstanceId,InstanceType,State.Name,PrivateIpAddress,PublicIpAddress]' \
  --region ap-southeast-1

# Kiểm tra ALB Status
aws elbv2 describe-load-balancers \
  --load-balancer-arns $ALB_ARN \
  --region ap-southeast-1

# Kiểm tra Target Groups Health
aws elbv2 describe-target-health \
  --target-group-arn $FRONTEND_TG_ARN \
  --region ap-southeast-1

# Kiểm tra RDS
aws rds describe-db-instances \
  --db-instance-identifier cafe-postgres-prod \
  --query 'DBInstances[0].[DBInstanceStatus,MultiAZ,LatestRestorableTime]' \
  --region ap-southeast-1

# Test connectivity
curl -i http://$ALB_DNS
curl -i http://$ALB_DNS/api/products
```

#### Step 11: Cleanup (nếu cần)

```bash
# Delete EC2 instances
aws ec2 terminate-instances --instance-ids $FRONTEND_EC2_ID $BACKEND_EC2_ID $BACKEND_EC2_ID_2 --region ap-southeast-1

# Delete ALB
aws elbv2 delete-load-balancer --load-balancer-arn $ALB_ARN --region ap-southeast-1

# Delete Target Groups
aws elbv2 delete-target-group --target-group-arn $FRONTEND_TG_ARN --region ap-southeast-1
aws elbv2 delete-target-group --target-group-arn $BACKEND_TG_ARN --region ap-southeast-1

# Delete RDS
aws rds delete-db-instance --db-instance-identifier cafe-postgres-prod --skip-final-snapshot --region ap-southeast-1

# Delete VPC
aws ec2 detach-internet-gateway --internet-gateway-id $IGW_ID --vpc-id $VPC_ID --region ap-southeast-1
aws ec2 delete-internet-gateway --internet-gateway-id $IGW_ID --region ap-southeast-1
aws ec2 delete-vpc --vpc-id $VPC_ID --region ap-southeast-1
```

#### Chuẩn Bị Environment

```bash
# Configure AWS CLI
aws configure

# Set AWS credentials
export AWS_ACCESS_KEY_ID=your-key
export AWS_SECRET_ACCESS_KEY=your-secret
export AWS_REGION=ap-southeast-1
```

#### Deploy Script

```bash
# Chạy deploy script
bash ci/deploy.sh

# Hoặc thông qua Jenkins Pipeline
# Pipeline sẽ tự động deploy sau khi test passed
```

#### Kiểm Tra Deployment

```bash
# Xem EC2 instances
aws ec2 describe-instances --query 'Reservations[*].Instances[*].[InstanceId,PublicIpAddress,State.Name]'

# SSH vào instance (qua NAT/Bastion)
ssh -i cafe-app-key.pem ubuntu@<private-ip>

# Kiểm tra docker containers
docker ps

# Xem logs
docker logs <container-id>
```

---

## 📊 Monitoring & Logging

### Prometheus Setup

**Endpoints được monitor:**

```yaml
# Backend metrics
- http://backend:8080/actuator/prometheus

# Database metrics
- PostgreSQL exporter: localhost:9187

# System metrics
- Node exporter: localhost:9100
```

**Cấu hình tại:** `ci/prometheus.yml`

```bash
# Access Prometheus
http://<server-ip>:9090
```

### Grafana Setup

**Datasource đã cấu hình:**

- Prometheus: http://prometheus:9090

**Dashboards:**

- Application Metrics Dashboard
- System Performance Dashboard
- Database Metrics Dashboard

**Access Grafana:**

```
URL: http://<server-ip>:3000
Default Username: admin
Default Password: admin
```

**Import Dashboard:**

1. Truy cập Grafana → Dashboards → Import
2. Upload file: `ci/grafana-provisioning/dashboards/grafana-dashboard.json`

### Key Metrics Monitored

```
📊 Application Metrics
- HTTP request rate & latency
- Active connections
- Error rate
- JVM memory & garbage collection

🗄️ Database Metrics
- Connection pool utilization
- Query performance
- Slow queries
- Transaction count

💻 System Metrics
- CPU usage
- Memory utilization
- Disk I/O
- Network traffic
```

### Setting Up Alerts

```yaml
# Example Alert Rule (prometheus.yml)
groups:
  - name: cafe-alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
        for: 5m
        annotations:
          summary: "High error rate detected"

      - alert: DatabaseDown
        expr: pg_up == 0
        for: 1m
        annotations:
          summary: "PostgreSQL is down"
```

---

## 🔐 Bảo Mật

### Authentication & Authorization

- **JWT Token** cho API requests
- **Session** cho web interface
- **Role-Based Access Control (RBAC)**
  - `USER`: Khách hàng bình thường
  - `ADMIN`: Quản trị viên

### CORS Configuration

```java
// Spring Security config
corsConfigurationSource()
  .allowedOrigins("http://localhost:3000", "https://yourdomain.com")
  .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
  .allowedHeaders("*")
  .allowCredentials(true)
```

### Environment Variables (Production)

```bash
# Database
DB_HOST=rds-instance.amazonaws.com
DB_PORT=5432
DB_NAME=cafe_shop
DB_USER=postgres
DB_PASSWORD=${SECURE_PASSWORD}

# JWT
JWT_SECRET=${SECURE_JWT_SECRET}
JWT_EXPIRATION=86400000

# AWS
AWS_REGION=ap-southeast-1
S3_BUCKET=cafe-app-uploads
```

### SSL/TLS Certificate

```bash
# Sử dụng AWS Certificate Manager
aws acm request-certificate \
  --domain-name yourdomain.com \
  --validation-method DNS
```

---

## 📝 API Endpoints

### Authentication

```bash
# Đăng ký
POST /api/auth/register
Content-Type: application/json
{
  "username": "user@example.com",
  "password": "secure_password",
  "fullName": "John Doe"
}

# Đăng nhập
POST /api/auth/login
Content-Type: application/json
{
  "username": "user@example.com",
  "password": "secure_password"
}
Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "user@example.com",
    "role": "USER"
  }
}
```

### Product Management (Admin)

```bash
# Get all products
GET /api/products

# Create product
POST /api/products
Authorization: Bearer {token}
Content-Type: application/json
{
  "name": "Espresso",
  "description": "Classic Italian espresso",
  "price": 25000,
  "category": "COFFEE"
}

# Update product
PUT /api/products/{id}
Authorization: Bearer {token}

# Delete product
DELETE /api/products/{id}
Authorization: Bearer {token}
```

### Table Management (Admin)

```bash
# Get all tables
GET /api/tables

# Create table
POST /api/tables
Authorization: Bearer {token}
{
  "tableNumber": 1,
  "capacity": 4,
  "status": "AVAILABLE"
}

# Update table
PUT /api/tables/{id}
Authorization: Bearer {token}

# Delete table
DELETE /api/tables/{id}
Authorization: Bearer {token}
```

### Booking Management (User)

```bash
# Create booking
POST /api/bookings
Authorization: Bearer {token}
{
  "tableId": 1,
  "bookingDate": "2024-12-26",
  "bookingTime": "19:00",
  "guestCount": 4,
  "notes": "Birthday celebration"
}

# Get user's bookings
GET /api/bookings/my-bookings
Authorization: Bearer {token}

# Cancel booking
DELETE /api/bookings/{id}
Authorization: Bearer {token}
```

### Order Management

```bash
# Create order
POST /api/orders
Authorization: Bearer {token}
{
  "items": [
    {"productId": 1, "quantity": 2},
    {"productId": 3, "quantity": 1}
  ],
  "totalPrice": 75000,
  "notes": "Extra hot"
}

# Get user's orders
GET /api/orders/my-orders
Authorization: Bearer {token}

# Admin: Get all orders
GET /api/orders
Authorization: Bearer {admin-token}

# Admin: Update order status
PUT /api/orders/{id}/status
Authorization: Bearer {admin-token}
{
  "status": "CONFIRMED"
}
```

### Statistics & Reports (Admin)

```bash
# Daily revenue
GET /api/reports/revenue/daily?date=2024-12-26
Authorization: Bearer {admin-token}

# Monthly revenue
GET /api/reports/revenue/monthly?year=2024&month=12
Authorization: Bearer {admin-token}

# Top selling products
GET /api/reports/top-products?limit=10
Authorization: Bearer {admin-token}

# Orders statistics
GET /api/reports/orders/stats?startDate=2024-12-01&endDate=2024-12-31
Authorization: Bearer {admin-token}
```

---

## 🐛 Troubleshooting

### Problem: Docker Compose không start

```bash
# Kiểm tra logs
docker-compose logs

# Xóa containers và restart
docker-compose down -v
docker-compose up -d

# Kiểm tra network
docker network ls
```

### Problem: Database connection error

```bash
# Kiểm tra PostgreSQL status
docker exec ltweb2-postgres pg_isready

# Kiểm tra environment variables
docker-compose config | grep -A5 postgres

# Xem database logs
docker logs ltweb2-postgres
```

### Problem: Frontend không load API

```bash
# Kiểm tra CORS settings
curl -i -X OPTIONS http://localhost:8080/api/products \
  -H "Origin: http://localhost:3000"

# Xem backend logs
docker logs ltweb2-backend

# Kiểm tra network trong docker-compose
docker network inspect app-network
```

### Problem: Jenkins Pipeline failed

```bash
# Kiểm tra Jenkins logs
docker logs jenkins-server

# Xem detailed build logs
# Jenkins UI → Job → Build Number → Console Output

# Kiểm tra Docker credentials
docker login -u <username> -p <password>
```

### Problem: Prometheus không collect metrics

```bash
# Kiểm tra backend metrics endpoint
curl http://localhost:8080/actuator/prometheus

# Verify prometheus scrape config
curl http://localhost:9090/api/v1/targets

# Xem prometheus logs
docker logs prometheus
```

---

## 📱 Giao Diện Ứng Dụng

### User Interface

- **Login/Register Page**: Xác thực người dùng
- **Home Page**: Hiển thị menu, khuyến mãi
- **Menu Page**: Duyệt sản phẩm, thêm vào giỏ
- **Booking Page**: Đặt bàn
- **Order History**: Lịch sử đơn hàng
- **Reservation History**: Lịch sử đặt bàn

### Admin Dashboard

- **Dashboard**: Thống kê tổng quan
- **Product Management**: CRUD sản phẩm
- **Table Management**: CRUD bàn ăn
- **Order Management**: Xác nhận/Hủy đơn
- **Reports**: Báo cáo doanh thu
- **User Management**: Quản lý tài khoản người dùng

---

## 📞 Hỗ Trợ

### Tài Liệu Liên Quan

- [CI/CD Documentation](./CI_CD_README.md)
- [Jenkins Setup Guide](./ci/JENKINS_SETUP.md)
- [Backend README](./LT_Web_2-main/LT_Web2/README.md)
- [Frontend README](./cafe-fe/README.md)

### Liên Hệ

```
📧 Email: your-email@example.com
💬 Slack: #cafe-app-team
📞 Phone: +84 xxx xxxx xxx
```

### Thông Tin Dự Án

```
Repository: https://github.com/your-username/nguyenthongoc
Branch: deploy
Deployment: AWS ap-southeast-1
Status: 🟢 Production
Last Updated: 2024-12-26
```

---

## 📜 License

MIT License - xem file [LICENSE](./LICENSE) để chi tiết

---

## 🎉 Bắt Đầu

1. **Clone repository**: `git clone <url>`
2. **Checkout deploy branch**: `git checkout deploy`
3. **Chạy local**: `docker-compose up -d`
4. **Access application**:
   - Frontend: http://localhost
   - Backend: http://localhost:8080
   - Monitoring: http://localhost:3000 (Grafana)

**Chúc mừng! Bạn đã sẵn sàng deploy ứng dụng Cafe Management System** 🚀
