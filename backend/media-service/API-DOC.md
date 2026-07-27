# Media Service API Documentation

## Overview

The **Media Service** is responsible for storing, retrieving, and deleting image files used throughout the marketplace. Images are stored in object storage (MinIO) while their metadata is persisted in the database.

### Responsibilities

- Image upload
- Image retrieval
- Image deletion
- User media listing
- Media statistics

---

# Base URL

```text
/media/images
```

---

# Authentication

The service uses **JWT Bearer Authentication**.

Protected endpoints require:

```text
Authorization: Bearer <access_token>
```

---

# Roles

| Role   | Description                  |
| ------ | ---------------------------- |
| USER   | Can view images              |
| SELLER | Can upload and manage images |
| ADMIN  | Full access                  |

---

# Data Model

## Media

Represents an uploaded image.

| Field       | Type   | Description                            |
| ----------- | ------ | -------------------------------------- |
| id          | String | Unique media identifier                |
| path        | String | Object storage key/path                |
| productId   | String | Associated product (optional)          |
| userId      | String | Owner of the image (optional)          |
| contentType | String | MIME type (e.g. image/png, image/jpeg) |

Example

```json
{
  "id": "6858bc23...",
  "path": "d5a3b44f-2d2d-4e65-8d6a.png",
  "productId": "684a4d12...",
  "userId": "683b7ef1...",
  "contentType": "image/png"
}
```

---

# Media Endpoints

## Upload Image

**POST**

```text
/media/images
```

### Authorization

SELLER or ADMIN

### Content-Type

```text
multipart/form-data
```

### Form Data

| Name      | Type   | Required | Description                    |
| --------- | ------ | -------- | ------------------------------ |
| image     | File   | Yes      | Image file to upload           |
| productId | String | No       | Associate image with a product |
| userId    | String | No       | Optional user identifier       |

> **Note:** In the current implementation, the `userId` request parameter is accepted but not used. The service calls `mediaService.upload(image, productId)`.

### Response

**201 Created**

```json
{
  "id": "6858bc23...",
  "path": "d5a3b44f-2d2d-4e65-8d6a.png",
  "productId": "684a4d12...",
  "userId": null,
  "contentType": "image/png"
}
```

---

## Download Image

**GET**

```text
/media/images/{id}
```

### Authorization

Public

### Response

Returns the image file.

Headers include:

| Header        | Value                     |
| ------------- | ------------------------- |
| Content-Type  | Original image MIME type  |
| Cache-Control | max-age=2592000 (30 days) |

Example

```
GET /media/images/6858bc23...
```

Returns the binary image (`image/png`, `image/jpeg`, etc.).

---

## Delete Image

**DELETE**

```text
/media/images/{id}
```

### Authorization

SELLER or ADMIN

### Response

**204 No Content**

Deletes the image from both object storage and the metadata database.

---

## Get Media By User

**GET**

```text
/media/images/user/{userId}
```

### Authorization

SELLER or ADMIN

Returns a paginated list of media belonging to the specified user.

Supports Spring pagination.

Example

```text
GET /media/images/user/683b7ef1?page=0&size=20
```

Response

```json
{
  "content": [
    {
      "id": "...",
      "path": "...",
      "productId": "...",
      "userId": "...",
      "contentType": "image/png"
    }
  ],
  "totalElements": 15,
  "totalPages": 1
}
```

---

## Count Media

**GET**

```text
/media/images/count
```

### Authorization

SELLER or ADMIN

### Response

```json
42
```

---

# HTTP Status Codes

| Status                    | Description                              |
| ------------------------- | ---------------------------------------- |
| 200 OK                    | Request completed successfully           |
| 201 Created               | Image uploaded successfully              |
| 204 No Content            | Image deleted successfully               |
| 400 Bad Request           | Invalid image or malformed request       |
| 401 Unauthorized          | Authentication required or invalid token |
| 403 Forbidden             | User does not have permission            |
| 404 Not Found             | Media not found                          |
| 500 Internal Server Error | Unexpected server error                  |

---

# Authorization Matrix

| Endpoint                          | USER | SELLER | ADMIN |
| --------------------------------- | :--: | :----: | :---: |
| GET `/media/images/{id}`          |  ✓   |   ✓    |   ✓   |
| POST `/media/images`              |  ✗   |   ✓    |   ✓   |
| DELETE `/media/images/{id}`       |  ✗   |   ✓    |   ✓   |
| GET `/media/images/user/{userId}` |  ✗   |   ✓    |   ✓   |
| GET `/media/images/count`         |  ✗   |   ✓    |   ✓   |

---

# Notes

- Images are stored in **MinIO** object storage, while their metadata is stored in the database.
- The service supports image retrieval through direct streaming, preserving the original `Content-Type`.
- Download responses include a **30-day Cache-Control** header to improve client-side performance.
- Product images are typically uploaded by the **Product Service**, which associates uploaded media with products using the `productId` parameter.
- Although the upload endpoint accepts both `productId` and `userId`, the current implementation only passes `productId` to the service layer. If user ownership is required, the implementation should be updated to persist the `userId` as well.
