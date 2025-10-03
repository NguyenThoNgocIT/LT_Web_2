# 🚀 LT_Web2 – Quản Lý Người Dùng & Công Ty (Spring Boot + JWT + Thymeleaf)

> **Một hệ thống quản trị nội bộ** cho phép quản lý người dùng, công ty, phân quyền theo vai trò, tích hợp xác thực JWT cho API và giao diện web thân thiện dành cho admin.

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2+-green?logo=spring)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-blue?logo=java)](https://www.oracle.com/java/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1+-orange?logo=html5)](https://www.thymeleaf.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-brightgreen)](https://jwt.io/)
[![License](https://img.shields.io/badge/License-MIT-purple)](LICENSE)

## 📌 Mục lục

- [✨ Tính năng chính](#-tính-năng-chính)
- [📦 Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [⚙️ Cài đặt & Chạy ứng dụng](#️-cài-đặt--chạy-ứng-dụng)
- [🔐 Phân quyền & Bảo mật](#-phân-quyền--bảo-mật)
- [📡 API Endpoints](#-api-endpoints)
- [🖥️ Giao diện Web](#️-giao-diện-web)
- [🧪 Kiểm thử](#-kiểm-thử)
- [📝 Ghi chú triển khai](#-ghi-chú-triển-khai)
- [📄 Giấy phép](#-giấy-phép)

## ✨ Tính năng chính

- ✅ **Xác thực người dùng**:
    - Đăng ký / Đăng nhập (web & API)
    - JWT token cho API, session cho giao diện web
- ✅ **Quản lý hồ sơ người dùng**:
    - Xem, cập nhật thông tin cá nhân
    - Gắn người dùng với công ty
- ✅ **Quản lý công ty**:
    - Thêm/sửa/xóa công ty (admin)
- ✅ **Phân quyền theo vai trò**:
    - `USER`: Truy cập profile cá nhân
    - `ADMIN`: Quản lý toàn bộ hệ thống
- ✅ **Giao diện admin dashboard**:
    - Thống kê số liệu
    - CRUD người dùng & công ty qua modal
- ✅ **Bảo mật chuẩn**:
    - Mã hóa mật khẩu (BCrypt)
    - CSRF disabled cho API (stateless), enabled cho web nếu cần
    - Phản hồi lỗi 401/403 rõ ràng cho API

## 📦 Công nghệ sử dụng

| Loại | Công nghệ |
|------|----------|
| **Backend** | Spring Boot 3.2+, Spring Security, Spring Data JPA |
| **Frontend** | Thymeleaf, Bootstrap 5, Font Awesome |
| **Cơ sở dữ liệu** | H2 (dev), dễ chuyển sang PostgreSQL/MySQL |
| **Xác thực** | JWT (API), Session (Web) |
| **Mã hóa** | BCryptPasswordEncoder |
| **Build** | Maven |
| **Testing** | JUnit 5, Mockito |

## ⚙️ Cài đặt & Chạy ứng dụng

### Yêu cầu
- Java 17+
- Maven 3.6+
- IDE (IntelliJ IDEA, VS Code, v.v.)

### Các bước

```bash
# 1. Clone repository
git clone https://github.com/your-username/LT_Web2.git
cd LT_Web2

# 2. Build ứng dụng
./mvnw clean package

# 3. Chạy ứng dụng
./mvnw spring-boot:run

# 🚀 Spring Boot App - User & Company Management

## 👤 Tài khoản mặc định (dev)

| Email              | Mật khẩu  | Quyền  |
|--------------------|-----------|--------|
| admin@example.com  | admin123  | ADMIN  |
| user@example.com   | user123   | USER   |

⚠️ *Dữ liệu được khởi tạo tự động qua `data.sql` khi dùng H2.*

---

## 🔐 Phân quyền & Bảo mật

### Web (Session-based)
| Đường dẫn            | Quyền truy cập         |
|----------------------|------------------------|
| `/login`, `/register` | Public |
| `/user/**`           | USER hoặc ADMIN |
| `/admin/**`          | ADMIN |

### API (JWT-based)
| Endpoint        | Quyền truy cập         |
|-----------------|------------------------|
| `/api/auth/**`  | Public (login/register) |
| `/api/user/**`  | JWT + role USER/ADMIN |
| `/api/admin/**` | JWT + role ADMIN |

---

## ⚠️ Xử lý lỗi API

| Mã lỗi | Ý nghĩa |
|--------|---------|
| **401 Unauthorized** | Thiếu hoặc token không hợp lệ |
| **403 Forbidden**    | Đã xác thực nhưng không đủ quyền |
| **400 Bad Request**  | Dữ liệu đầu vào không hợp lệ |

---

## 📡 API Endpoints

### 🔑 Xác thực
| Method | Endpoint             | Mô tả |
|--------|----------------------|-------|
| POST   | `/api/auth/register` | Đăng ký người dùng mới |
| POST   | `/api/auth/login`    | Đăng nhập → nhận JWT |

### 👤 Người dùng (USER)
| Method | Endpoint             | Mô tả |
|--------|----------------------|-------|
| GET    | `/api/user/profile`  | Lấy profile người dùng hiện tại |
| PUT    | `/api/user/profile`  | Cập nhật profile |
| POST   | `/api/company/save`  | Thêm công ty mới |

### 👨‍💼 Admin (ADMIN)
| Method | Endpoint                         | Mô tả |
|--------|----------------------------------|-------|
| POST   | `/api/admin/users/save`          | Tạo người dùng |
| PUT    | `/api/admin/users/update/{id}`   | Cập nhật người dùng |
| DELETE | `/api/admin/users/delete/{id}`   | Xóa người dùng |
| POST   | `/api/admin/company/save`        | Tạo công ty |
| PUT    | `/api/admin/company/update/{id}` | Cập nhật công ty |
| DELETE | `/api/admin/company/delete/{id}` | Xóa công ty |

🔹 Gửi kèm header:  
## 🖥️ Giao diện Web

| Trang               | Đường dẫn |
|---------------------|-----------|
| Đăng nhập / Đăng ký | `/login`, `/register` |
| Profile người dùng  | `/user/profile` |
| Admin Dashboard     | `/admin/dashboard` |

- Quản lý **người dùng & công ty** qua modal  
- Hỗ trợ **tạo / sửa / xóa** trực tiếp trên giao diện  
- Responsive: **desktop & tablet**
🧪 Kiểm thử
Test bằng Postman
Import collection: LT_Web2.postman_collection.json (nếu có)

Test unit
bash


1
./mvnw test
📝 Ghi chú triển khai
Production:
Thay H2 bằng PostgreSQL/MySQL
Cấu hình application-prod.yml
Bật HTTPS, CORS nếu cần
JWT Secret: Đặt biến môi trường JWT_SECRET (mặc định: ltweb2-secret-key)
Logging: Dùng SLF4J + Logback (xem src/main/resources/logback-spring.xml)
📄 Giấy phép
Dự án này được cấp phép theo MIT License — xem chi tiết tại LICENSE .

💡 Phát triển bởi: [Nguyễn Thọ Ngọc and Phi Đen]
📧 Liên hệ: nguyenthongoc22072204@gmail.com
🌐 Phiên bản: 1.0.0 "