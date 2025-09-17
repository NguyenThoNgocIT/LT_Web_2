
# LT_Web_2

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)  
[![Java Version](https://img.shields.io/badge/Java-17-brightgreen)](#)  
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-blue)](#)

---

## Mục lục

- [Giới thiệu](#giới-thiệu)
- [Tính năng](#tính-năng)
- [Kiến trúc & Công nghệ sử dụng](#kiến-trúc--công-nghệ-sử-dụng)
- [Cài đặt & Chạy dự án locally](#cài-đặt--chạy-dự-án-locally)
- [API endpoints](#api-endpoints)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Quy trình phát triển](#quy-trình-phát-triển)
- [Đóng góp](#đóng-góp)
- [Bản quyền & Giấy phép](#bản-quyền--giấy-phép)

---

## Giới thiệu

**LT_Web_2** là ứng dụng web demo về đăng ký / đăng nhập người dùng (authentication & authorization) sử dụng Spring Boot.

Mục đích:

- Học tập & áp dụng best practices về bảo mật trong web app (Spring Security, UserDetailsService, password hashing)
- Thiết kế modular, dễ mở rộng cho các tính năng tiếp theo: quản lý người dùng, phân quyền, profile, mật khẩu,…

---

## Tính năng

- Đăng ký người dùng mới (signup)
- Đăng nhập (login)
- Ẩn/hiện trang dựa vào quyền người dùng (nếu triển khai sau)
- Templating giao diện đơn giản với Thymeleaf (login, signup)
- Cấu hình bảo mật cơ bản: mã hóa mật khẩu, quản lý session

---

## Kiến trúc & Công nghệ sử dụng

| Thành phần | Công nghệ / Framework | Vai trò |
|------------|------------------------|---------|
| Backend | Java + Spring Boot | Xử lý logic ứng dụng, API, bảo mật |
| Bảo mật | Spring Security | Xác thực người dùng, quản lý phân quyền |
| View / UI | Thymeleaf | Hiển thị form đăng nhập / đăng ký |
| Build / Dependency | Maven hoặc Gradle (tùy) | Quản lý thư viện, biên dịch |
| Database | (chưa rõ / để chọn) | Lưu trữ thông tin người dùng |

---

## Cài đặt & Chạy dự án locally

> Yêu cầu: Java 17+, Maven / Gradle, Git

1. Clone repo về máy

   ```bash
   git clone https://github.com/NguyenThoNgocIT/LT_Web_2.git
   cd LT_Web_2/LT_Web_2-main
Cấu hình file application.properties hoặc application.yml (nếu cần)

Thông tin DB (URL, username, password)

Cấu hình bảo mật (nếu sử dụng OAuth, JWT, v.v.)

Build & chạy

bash
Copy code
mvn clean install
mvn spring-boot:run
Hoặc nếu dùng Gradle:

bash
Copy code
./gradlew build
./gradlew bootRun
Truy cập ứng dụng

Đăng ký: http://localhost:8080/signup

Đăng nhập: http://localhost:8080/login

API Endpoints
Phương thức	URL	Chức năng
GET	/signup	Hiển thị form đăng ký
POST	/signup	Xử lý đăng ký người dùng
GET	/login	Hiển thị form đăng nhập
POST	/login	Xử lý đăng nhập

Lưu ý: Tùy vào cấu hình Spring Security, URL để logout, đổi mật khẩu,… có thể được thêm vào

Cấu trúc thư mục
bash
Copy code
LT_Web_2/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/LT_Web2/
│   │   │       ├── config/              # cấu hình bảo mật & UserDetailsService
│   │   │       ├── controllers/         # các controller như AuthController
│   │   │       ├── model/               # các entity / DTO nếu có
│   │   │       ├── repository/          # layer truy xuất dữ liệu
│   │   │       └── service/             # business logic
│   │   └── resources/
│   │       ├── templates/               # các file Thymeleaf (login, signup,…)
│   │       └── application.properties / yml
├── test/                                # viết unit/integration tests
└── README.md
Quy trình phát triển
Tạo nhánh (branch) mới cho mỗi tính năng hoặc bug fix, theo convention như feature/<tên-feature> hoặc bugfix/<mô-tả-bug>.

Commit thường xuyên & rõ ràng; dùng message commit dạng:

feat: thêm tính năng đăng nhập, fix: sửa lỗi validate email, refactor: tách service

Push nhánh lên remote và tạo Pull Request để review trước khi merge vào main.

Chạy test & kiểm tra build ổn định trước khi deploy.

Đóng góp
Rất hoan nghênh mọi đóng góp từ cộng đồng:

❗ Hãy tạo issue nếu bạn phát hiện bug hoặc muốn đề xuất tính năng mới

🔀 Fork, làm nhánh riêng và gửi pull request

🧪 Viết tests khi thêm tính năng hoặc sửa lỗi

Bản quyền & Giấy phép
MIT License © 2025 Nguyen Tho Ngoc
Bạn có thể sử dụng, sao chép, sửa đổi theo giấy phép MIT. Xem chi tiết tại file LICENSE.

yaml
Copy code

---

Nếu bạn muốn, mình có thể tạo một **README “xịn” hơn** với **ảnh demo, badges CI/CD**, hướng dẫn deploy (Ví dụ lên Heroku / AWS / file Docker), tùy vào nhu cầu. Bạn muốn mình làm như vậy không?
::contentReference[oaicite:0]{index=0}
