# Project Module API Guide

This document describes all APIs under `/api/v1/projects` for FE integration.

## Base URL

```text
http://localhost:8080
```

## Authentication

All endpoints (except public ones) use JWT Bearer token:

```http
Authorization: Bearer <ACCESS_TOKEN>
```

Roles:

- `STUDENT`: can apply to project.
- `COMMUNITY_LEADER`, `UNI_ADMIN`: can create project, view pending applications, review applications, and view accepted students.

---

## 1) Create Project

- **Method**: `POST`
- **URL**: `/api/v1/projects`
- **Roles**: `COMMUNITY_LEADER`, `UNI_ADMIN`
- **Body**:

```json
{
  "title": "Green Summer Campaign 2026",
  "description": "Support local environmental activities and community outreach.",
  "requiredSkills": "Communication, Teamwork",
  "startTime": "2026-07-01T08:00:00+07:00",
  "endTime": "2026-07-30T17:00:00+07:00",
  "amountOfParticipants": 20
}
```

- **Response**: `201 Created`

```json
{
  "id": 1,
  "title": "Green Summer Campaign 2026",
  "description": "Support local environmental activities and community outreach.",
  "requiredSkills": "Communication, Teamwork",
  "startTime": "2026-07-01T08:00:00",
  "endTime": "2026-07-30T17:00:00",
  "amountOfParticipants": 20,
  "status": "PENDING",
  "leaderId": 2,
  "leaderName": "Leader One",
  "createdAt": "2026-06-01T10:00:00"
}
```

---

## 2) Get All Projects

- **Method**: `GET`
- **URL**: `/api/v1/projects`
- **Roles**: authenticated users
- **Response**: `200 OK`

```json
[
  {
    "id": 1,
    "title": "Green Summer Campaign 2026",
    "description": "Support local environmental activities and community outreach.",
    "requiredSkills": "Communication, Teamwork",
    "startTime": "2026-07-01T08:00:00",
    "endTime": "2026-07-30T17:00:00",
    "amountOfParticipants": 20,
    "status": "PENDING",
    "leaderId": 2,
    "leaderName": "Leader One",
    "createdAt": "2026-06-01T10:00:00"
  }
]
```

---

## 3) Get Project Detail

- **Method**: `GET`
- **URL**: `/api/v1/projects/{id}`
- **Roles**: authenticated users
- **Response**: `200 OK`

Structure is the same as project object above.

---

## 4) Get Projects by Leader

- **Method**: `GET`
- **URL**: `/api/v1/projects/leader/{leaderId}`
- **Roles**: `COMMUNITY_LEADER`, `UNI_ADMIN`
- **Description**: Returns all projects created by a specific leader/admin account.
- **Response**: `200 OK`

```json
[
  {
    "id": 1,
    "title": "Green Summer Campaign 2026",
    "description": "Support local environmental activities and community outreach.",
    "requiredSkills": "Communication, Teamwork",
    "startTime": "2026-07-01T08:00:00",
    "endTime": "2026-07-30T17:00:00",
    "amountOfParticipants": 20,
    "status": "PENDING",
    "leaderId": 2,
    "leaderName": "Leader One",
    "createdAt": "2026-06-01T10:00:00"
  }
]
```

---

## 5) Apply to Project

- **Method**: `POST`
- **URL**: `/api/v1/projects/applications`
- **Roles**: `STUDENT` only
- **Body**:

```json
{
  "projectId": 1
}
```

- **Response**: `201 Created`

```json
{
  "id": 10,
  "projectId": 1,
  "projectTitle": "Green Summer Campaign 2026",
  "studentId": 5,
  "studentName": "Nguyen Van Student",
  "status": "APPLIED",
  "appliedAt": "2026-06-01T11:00:00"
}
```

---

## 6) Get Pending Applications

- **Method**: `GET`
- **URL**: `/api/v1/projects/applications/pending`
- **Roles**: `COMMUNITY_LEADER`, `UNI_ADMIN`
- **Description**: Returns applications with status `APPLIED`.
- **Response**: `200 OK`

```json
[
  {
    "id": 10,
    "projectId": 1,
    "projectTitle": "Green Summer Campaign 2026",
    "studentId": 5,
    "studentName": "Nguyen Van Student",
    "status": "APPLIED",
    "appliedAt": "2026-06-01T11:00:00"
  }
]
```

---

## 7) Review Project Application

- **Method**: `PATCH`
- **URL**: `/api/v1/projects/applications/{applicationId}/status`
- **Roles**: `COMMUNITY_LEADER`, `UNI_ADMIN`
- **Body**:

```json
{
  "status": "ACCEPTED"
}
```

or

```json
{
  "status": "REJECTED"
}
```

- **Response**: `200 OK`

```json
{
  "id": 10,
  "projectId": 1,
  "projectTitle": "Green Summer Campaign 2026",
  "studentId": 5,
  "studentName": "Nguyen Van Student",
  "status": "ACCEPTED",
  "appliedAt": "2026-06-01T11:00:00"
}
```

---

## 8) Get Accepted Students of a Project

- **Method**: `GET`
- **URL**: `/api/v1/projects/{id}/students`
- **Roles**: `COMMUNITY_LEADER`, `UNI_ADMIN`
- **Description**: Returns students whose applications are `ACCEPTED` for this project.
- **Response**: `200 OK`

```json
[
  {
    "studentId": 5,
    "username": "student01",
    "fullName": "Nguyen Van Student",
    "email": "student01@example.com"
  }
]
```

---

## Common Error Responses

### 400 BAD_REQUEST

Examples:

- `Only STUDENT can apply to a project`
- `You have already applied to this project`
- `Only COMMUNITY_LEADER or UNI_ADMIN can review applications`
- `This application has already been reviewed`
- `Review status must be ACCEPTED or REJECTED`
- `Project has reached the participant limit`
- `Project not found`

Format:

```json
{
  "code": "BAD_REQUEST",
  "message": "Project not found",
  "timestamp": "2026-06-01T11:30:00"
}
```

### 401 Unauthorized

Missing or invalid token.

### 403 Forbidden

Role does not have permission for endpoint.

### 422 VALIDATION_ERROR

Invalid request body.
