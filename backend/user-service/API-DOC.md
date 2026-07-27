# User Service API Documentation

## Overview

The **User Service** is responsible for user authentication, account management, profile management, and administrator user operations.

### Responsibilities

- User registration
- User authentication
- JWT token refresh
- User profile management
- Avatar upload/removal
- Administrative user management

---

# Base URL

```
/users
```

Authentication endpoints:

```
/users/auth
```

---

# Authentication

The service uses **JWT Bearer Authentication**.

Protected endpoints require:

```
Authorization: Bearer <access_token>
```

---

# Roles

| Role   | Description              |
| ------ | ------------------------ |
| USER   | Regular marketplace user |
| SELLER | Seller account           |
| ADMIN  | System administrator     |

---

# Data Models

## RegisterRequest

Used during account registration.

| Field    | Type   | Required | Validation      |
| -------- | ------ | -------- | --------------- | ---------------------- |
| name     | String | Yes      | 3–25 characters |
| email    | String | Yes      | Valid email     |
| password | String | Yes      | 8–30 characters |
| role     | USER   | SELLER   | Yes             | Must be USER or SELLER |

Example

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "SELLER"
}
```

---

## LoginRequest

| Field    | Type   | Required |
| -------- | ------ | -------- |
| email    | String | Yes      |
| password | String | Yes      |

Example

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

---

## RefreshTokenRequest

| Field        | Type   |
| ------------ | ------ |
| refreshToken | String |

Example

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## CreateUserRequest (Admin)

| Field    | Type   |
| -------- | ------ |
| name     | String |
| email    | String |
| password | String |

---

## UpdateUserRequest

| Field | Type   |
| ----- | ------ |
| name  | String |

Example

```json
{
  "name": "Updated Name"
}
```

---

## UserAvatarResponse

| Field | Type   |
| ----- | ------ |
| id    | String |
| url   | String |

---

## UserResponse

Represents a complete user profile.

| Field     | Type               |
| --------- | ------------------ |
| id        | String             |
| name      | String             |
| email     | String             |
| role      | Role               |
| avatar    | UserAvatarResponse |
| createdAt | LocalDateTime      |

Example

```json
{
  "id": "684ab12d...",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "SELLER",
  "avatar": {
    "id": "media123",
    "url": "http://localhost:8080/media/images/media123"
  },
  "createdAt": "2026-07-27T14:20:33"
}
```

---

## UserWidgetResponse

Lightweight user information.

| Field  | Type               |
| ------ | ------------------ |
| id     | String             |
| name   | String             |
| avatar | UserAvatarResponse |

---

## AuthResponse

Returned after successful authentication.

| Field        | Type         |
| ------------ | ------------ |
| accessToken  | String       |
| refreshToken | String       |
| user         | UserResponse |

---

## ErrorResponse

Returned when an error occurs.

| Field     | Type          |
| --------- | ------------- |
| timestamp | LocalDateTime |
| status    | Integer       |
| error     | String        |
| message   | String        |

Example

```json
{
  "timestamp": "2026-07-27T15:42:18",
  "status": 404,
  "error": "Not Found",
  "message": "User not found."
}
```

---

# Authentication Endpoints

## Register User

**POST**

```
/users/auth/register
```

### Authorization

Public

### Request

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

### Success Response

**201 Created**

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "user": {
    ...
  }
}
```

---

## Login

**POST**

```
/users/auth/login
```

### Authorization

Public

### Request

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Success Response

**200 OK**

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "user": {
    ...
  }
}
```

---

## Refresh Access Token

**POST**

```
/users/auth/refresh
```

### Authorization

Requires a valid refresh token.

### Request

```json
{
  "refreshToken": "..."
}
```

### Success Response

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "user": {
    ...
  }
}
```

---

# User Endpoints

## Get Current User

**GET**

```
/users/me
```

### Authorization

Authenticated users

### Response

**200 OK**

Returns the authenticated user's profile.

---

## Update Current User

**PUT**

```
/users/me
```

### Authorization

Authenticated users

### Request

```json
{
  "name": "New Name"
}
```

### Response

Updated `UserResponse`.

---

## Upload Avatar

**POST**

```
/users/me/avatar
```

### Authorization

Authenticated users

### Content-Type

```
multipart/form-data
```

### Form Data

| Name  | Type |
| ----- | ---- |
| image | File |

### Response

Updated `UserResponse`.

---

## Delete Avatar

**DELETE**

```
/users/me/avatar
```

### Authorization

Authenticated users

### Response

**204 No Content**

---

## Get User Widget

**GET**

```
/users/widget/{id}
```

### Authorization

Public

### Response

Returns a `UserWidgetResponse`.

---

# Administrator Endpoints

These endpoints require the **ADMIN** role.

---

## Create User

**POST**

```
/users
```

### Authorization

ADMIN

### Request

```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "password": "password123"
}
```

### Response

**201 Created**

Returns the created `UserResponse`.

---

## Get All Users

**GET**

```
/users
```

### Authorization

ADMIN

### Query Parameters

Supports Spring pagination.

| Parameter | Description   |
| --------- | ------------- |
| page      | Page number   |
| size      | Page size     |
| sort      | Sorting field |

Example

```
GET /users?page=0&size=20&sort=name
```

Returns a paginated list of `UserResponse` objects.

---

## Get User by ID

**GET**

```
/users/{id}
```

### Authorization

ADMIN

Returns a `UserResponse`.

---

## Update User

**PUT**

```
/users/{id}
```

### Authorization

ADMIN

Updates the specified user.

---

## Delete User

**DELETE**

```
/users/{id}
```

### Authorization

ADMIN

### Response

**204 No Content**

---

## Count Users

**GET**

```
/users/count
```

### Authorization

ADMIN

### Response

```json
152
```

---

# HTTP Status Codes

| Status                    | Description                                     |
| ------------------------- | ----------------------------------------------- |
| 200 OK                    | Request completed successfully                  |
| 201 Created               | Resource created successfully                   |
| 204 No Content            | Resource deleted successfully                   |
| 400 Bad Request           | Validation failed or invalid request            |
| 401 Unauthorized          | Authentication required or invalid token        |
| 403 Forbidden             | Insufficient permissions                        |
| 404 Not Found             | Requested resource not found                    |
| 409 Conflict              | Resource already exists (e.g., duplicate email) |
| 500 Internal Server Error | Unexpected server error                         |

---

# Authorization Matrix

| Endpoint                    | USER | SELLER | ADMIN |
| --------------------------- | :--: | :----: | :---: |
| POST `/users/auth/register` |  ✓   |   ✓    |   ✓   |
| POST `/users/auth/login`    |  ✓   |   ✓    |   ✓   |
| POST `/users/auth/refresh`  |  ✓   |   ✓    |   ✓   |
| GET `/users/me`             |  ✓   |   ✓    |   ✓   |
| PUT `/users/me`             |  ✓   |   ✓    |   ✓   |
| POST `/users/me/avatar`     |  ✓   |   ✓    |   ✓   |
| DELETE `/users/me/avatar`   |  ✓   |   ✓    |   ✓   |
| GET `/users/widget/{id}`    |  ✓   |   ✓    |   ✓   |
| POST `/users`               |  ✗   |   ✗    |   ✓   |
| GET `/users`                |  ✗   |   ✗    |   ✓   |
| GET `/users/{id}`           |  ✗   |   ✗    |   ✓   |
| PUT `/users/{id}`           |  ✗   |   ✗    |   ✓   |
| DELETE `/users/{id}`        |  ✗   |   ✗    |   ✓   |
| GET `/users/count`          |  ✗   |   ✗    |   ✓   |
