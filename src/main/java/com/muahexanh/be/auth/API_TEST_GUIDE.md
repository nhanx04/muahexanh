# Auth API Test Guide (JWT + Role)

This document describes how to test the current authentication APIs by role: `STUDENT`, `COMMUNITY_LEADER`, and `UNI_ADMIN`.

Base URL:

```text
http://localhost:8080
```

---

## 1) JWT environment configuration

The application reads `.env` via `application.yml`:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration-seconds: ${JWT_EXPIRATION_SECONDS:86400}
```

Example `.env`:

```env
DB_URL=jdbc:postgresql://<host>:5432/<database>?sslmode=require&channel_binding=require
DB_USERNAME=<your_db_username>
DB_PASSWORD=<your_db_password>
JWT_SECRET=<your_base64_jwt_secret>
JWT_EXPIRATION_SECONDS=86400
```

Generate a strong JWT secret in PowerShell:

```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random -Maximum 256}))
```

---

## 2) Endpoint list

### Public endpoints (no token required)

- `POST /api/v1/auth/register/student`
- `POST /api/v1/auth/login`
- `GET  /api/v1/health`

### UNI_ADMIN only

- `POST /api/v1/auth/admin/create-user`

> Student approval APIs were removed. Student registration now creates an active account immediately.

---

## 3) Recommended test flow

### Step A - Student self-registers (instant activation)

**Endpoint**

```http
POST /api/v1/auth/register/student
Content-Type: application/json
```

**Request body**

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

**Expected response**

```json
{
  "message": "Student account created successfully"
}
```

### Step B - Student logs in right after registration

**Endpoint**

```http
POST /api/v1/auth/login
Content-Type: application/json
```

**Request body**

```json
{
  "username": "student01",
  "password": "123456"
}
```

**Expected response**

```json
{
  "accessToken": "<JWT_TOKEN>",
  "tokenType": "Bearer",
  "userId": 2,
  "username": "student01",
  "role": "STUDENT"
}
```

---

### Step C - Admin login

**Endpoint**

```http
POST /api/v1/auth/login
Content-Type: application/json
```

**Request body**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Expected response**

```json
{
  "accessToken": "<JWT_TOKEN>",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "role": "UNI_ADMIN"
}
```

Use token for protected requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

### Step D - Admin creates COMMUNITY_LEADER or UNI_ADMIN

**Endpoint**

```http
POST /api/v1/auth/admin/create-user
Authorization: Bearer <ADMIN_TOKEN>
Content-Type: application/json
```

**Request body (COMMUNITY_LEADER)**

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

**Expected response**

```json
{
  "message": "Account created successfully"
}
```

---

## 4) Common errors (English)

- `400 BAD_REQUEST`
  - `Username already exists`
  - `Email already exists`
  - `Invalid username or password`
  - `Account is not active or has been locked`
  - `This API can only create COMMUNITY_LEADER or UNI_ADMIN accounts`
- `401 Unauthorized`: missing or invalid/expired token.
- `403 Forbidden`: insufficient role.
- `422 VALIDATION_ERROR`: request validation failed.
