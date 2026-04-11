# Auth API Test Guide (JWT + Role)

Tài liệu này hướng dẫn test toàn bộ API auth theo từng role: `STUDENT`, `COMMUNITY_LEADER`, `UNI_ADMIN`.

Base URL mặc định:

```text
http://localhost:8080
```

---

## 1) Cấu hình biến môi trường JWT

Ứng dụng đọc `.env` qua `application.yml`:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration-seconds: ${JWT_EXPIRATION_SECONDS:86400}
```

### Nội dung `.env` mẫu

```env
DB_URL=jdbc:postgresql://<host>:5432/<database>?sslmode=require&channel_binding=require
DB_USERNAME=<your_db_username>
DB_PASSWORD=<your_db_password>
JWT_SECRET=<your_base64_jwt_secret>
JWT_EXPIRATION_SECONDS=86400
```

### Cách tạo JWT_SECRET nhanh (PowerShell)

```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random -Maximum 256}))
```

Copy kết quả và gán vào `JWT_SECRET`.

---

## 2) Danh sách endpoint

### Public (không cần token)
- `POST /api/v1/auth/register/student`
- `POST /api/v1/auth/login`
- `GET  /api/v1/health`

### UNI_ADMIN
- `POST /api/v1/auth/admin/create-user`

### UNI_ADMIN hoặc COMMUNITY_LEADER
- `GET  /api/v1/auth/applications/pending`
- `POST /api/v1/auth/applications/{id}/review`

---

## 3) Test theo luồng chuẩn

## Bước A - Student gửi đơn đăng ký

**Endpoint**

```http
POST /api/v1/auth/register/student
Content-Type: application/json
```

**Body JSON**

```json
{
  "username": "student01",
  "password": "123456",
  "fullName": "Nguyen Van Student",
  "email": "student01@example.com",
  "phoneNumber": "0901234567",
  "address": "HCMC",
  "abilitiesDescription": "Java, Spring Boot"
}
```

**Response mẫu**

```json
{
  "message": "Đã gửi đơn đăng ký, chờ duyệt"
}
```

---

## Bước B - Admin đăng nhập lấy token

**Endpoint**

```http
POST /api/v1/auth/login
Content-Type: application/json
```

**Body JSON**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response mẫu**

```json
{
  "accessToken": "<JWT_TOKEN>",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "role": "UNI_ADMIN"
}
```

Lưu token để dùng cho các API protected:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## Bước C - Admin tạo COMMUNITY_LEADER / UNI_ADMIN

**Endpoint**

```http
POST /api/v1/auth/admin/create-user
Authorization: Bearer <ADMIN_TOKEN>
Content-Type: application/json
```

**Body JSON tạo COMMUNITY_LEADER**

```json
{
  "username": "leader01",
  "password": "123456",
  "role": "COMMUNITY_LEADER",
  "fullName": "Leader One",
  "email": "leader01@example.com",
  "phoneNumber": "0901111111",
  "address": "Can Tho",
  "organizationName": "Youth Union"
}
```

**Body JSON tạo UNI_ADMIN**

```json
{
  "username": "admin02",
  "password": "123456",
  "role": "UNI_ADMIN",
  "fullName": "Admin Two",
  "email": "admin02@example.com",
  "phoneNumber": "0902222222",
  "address": "Hanoi",
  "organizationName": "University Office"
}
```

**Response mẫu**

```json
{
  "message": "Tạo tài khoản thành công"
}
```

---

## Bước D - UNI_ADMIN hoặc COMMUNITY_LEADER xem đơn chờ duyệt

**Endpoint**

```http
GET /api/v1/auth/applications/pending
Authorization: Bearer <ADMIN_OR_LEADER_TOKEN>
```

**Response mẫu**

```json
[
  {
    "id": 10,
    "username": "student01",
    "fullName": "Nguyen Van Student",
    "email": "student01@example.com",
    "status": "PENDING",
    "reviewerNote": null,
    "reviewedBy": null,
    "createdAt": "2026-01-01T10:00:00",
    "updatedAt": "2026-01-01T10:00:00"
  }
]
```

---

## Bước E - Duyệt / từ chối đơn STUDENT

**Endpoint**

```http
POST /api/v1/auth/applications/{id}/review
Authorization: Bearer <ADMIN_OR_LEADER_TOKEN>
Content-Type: application/json
```

### E1. Duyệt đơn

**Body JSON**

```json
{
  "approved": true,
  "reviewerNote": "Đạt yêu cầu"
}
```

Khi duyệt thành công, hệ thống tạo account `users.role = STUDENT` và `user_profiles` tương ứng.

### E2. Từ chối đơn

**Body JSON**

```json
{
  "approved": false,
  "reviewerNote": "Thiếu thông tin"
}
```

**Response mẫu**

```json
{
  "id": 10,
  "username": "student01",
  "fullName": "Nguyen Van Student",
  "email": "student01@example.com",
  "status": "APPROVED",
  "reviewerNote": "Đạt yêu cầu",
  "reviewedBy": 2,
  "createdAt": "2026-01-01T10:00:00",
  "updatedAt": "2026-01-01T10:05:00"
}
```

---

## 4) Test login cho STUDENT sau khi được duyệt

**Endpoint**

```http
POST /api/v1/auth/login
Content-Type: application/json
```

**Body JSON**

```json
{
  "username": "student01",
  "password": "123456"
}
```

**Kết quả mong đợi**: trả về token với `role = STUDENT`.

---

## 5) Lỗi thường gặp

- `401 Unauthorized`: thiếu token hoặc token sai/expired.
- `403 Forbidden`: role không đủ quyền.
- `400 BAD_REQUEST`: username/email đã tồn tại hoặc dữ liệu không hợp lệ.
- `422 VALIDATION_ERROR`: fail validate request body.

