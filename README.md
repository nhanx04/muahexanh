# MuaHeXanh Backend (Spring Boot + PostgreSQL)

Dự án backend khởi tạo bằng **Spring Boot 3** sử dụng:

- Spring Web
- Spring Data JPA
- PostgreSQL
- Bean Validation

## Cấu hình biến môi trường (.env)

Dự án đã được cấu hình đọc file `.env` tự động qua:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

Tạo file `.env` ở thư mục gốc dự án với nội dung:

```env
DB_URL=jdbc:postgresql://ep-falling-snow-amgz9gdu-pooler.c-5.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
DB_USERNAME=neondb_owner
DB_PASSWORD=<your_password>
```

- Swagger/OpenAPI

## 1. Yêu cầu môi trường

- Java 17+
- Maven 3.9+
- PostgreSQL 13+

## 2. Cấu trúc chính

```text
src/main/java/com/muahexanh/be
├── MuaHexanhBeApplication.java
├── common
│   ├── ApiError.java
│   ├── BaseEntity.java
│   └── GlobalExceptionHandler.java
├── config
│   ├── JpaAuditingConfig.java
│   └── OpenApiConfig.java
├── health
│   └── HealthController.java
└── user
    ├── User.java
    ├── UserController.java
    ├── UserRepository.java
    ├── UserService.java
    └── dto
        ├── CreateUserRequest.java
        └── UserResponse.java
```

## 3. Cấu hình database

File cấu hình: `src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/muahexanh_db
    username: postgres
    password: postgres
```

Bạn cần tạo database trước:

```sql
CREATE DATABASE muahexanh_db;
```

> Nếu user/password khác, hãy sửa lại trong `application.yml`.

## 4. Chạy dự án

### Cách 1: Chạy bằng Maven

```bash
mvn spring-boot:run
```

### Cách 2: Build rồi chạy jar

```bash
mvn clean package
java -jar target/muahexanh-be-0.0.1-SNAPSHOT.jar
```

Ứng dụng chạy mặc định tại: `http://localhost:8080`

## 5. API mẫu

### Health check

- `GET /api/v1/health`

Ví dụ response:

```json
{
  "status": "UP",
  "service": "muahexanh-be",
  "timestamp": "2026-01-01T10:00:00"
}
```

### User APIs

#### Tạo user

- `POST /api/v1/users`

Body:

```json
{
  "email": "test@example.com",
  "fullName": "Nguyen Van A"
}
```

#### Lấy danh sách user

- `GET /api/v1/users`

## 6. Swagger/OpenAPI

Sau khi chạy app, mở:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## 7. Ghi chú phát triển

- `ddl-auto: update` đang dùng cho môi trường dev.
- Khi lên production nên chuyển sang migration tool (Flyway/Liquibase).
- Đã bật global exception handler và validation cơ bản.
