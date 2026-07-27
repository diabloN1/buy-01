# Product Service API Documentation

# Base URL

```text
/products
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

| Role   | Description               |
| ------ | ------------------------- |
| USER   | Can browse products       |
| SELLER | Can manage their products |
| ADMIN  | Can manage all products   |

---

# Data Models

## CreateRequest

Used when creating a new product.

| Field       | Type       | Required | Validation        |
| ----------- | ---------- | -------- | ----------------- |
| name        | String     | Yes      | 3–100 characters  |
| description | String     | Yes      | 3–1000 characters |
| price       | BigDecimal | Yes      | Greater than 0    |
| quantity    | Integer    | Yes      | Minimum 0         |

Example

```json
{
  "name": "Gaming Mouse",
  "description": "Wireless RGB gaming mouse",
  "price": 59.99,
  "quantity": 25
}
```

---

## UpdateRequest

Used when updating an existing product.

| Field       | Type       | Required | Validation        |
| ----------- | ---------- | -------- | ----------------- |
| name        | String     | Yes      | 3–100 characters  |
| description | String     | Yes      | 3–1000 characters |
| price       | BigDecimal | Yes      | Greater than 0    |
| quantity    | Integer    | Yes      | Minimum 0         |

Example

```json
{
  "name": "Gaming Mouse Pro",
  "description": "Updated wireless RGB gaming mouse",
  "price": 69.99,
  "quantity": 30
}
```

---

## ProductImageResponse

Represents an image attached to a product.

| Field | Type   |
| ----- | ------ |
| id    | String |
| url   | String |

Example

```json
{
  "id": "image123",
  "url": "http://localhost:8080/media/images/image123"
}
```

---

## ProductResponse

Represents a complete product.

| Field       | Type                       |
| ----------- | -------------------------- |
| id          | String                     |
| name        | String                     |
| description | String                     |
| images      | List<ProductImageResponse> |
| price       | BigDecimal                 |
| userId      | String                     |
| quantity    | Integer                    |
| createdAt   | LocalDateTime              |

Example

```json
{
  "id": "6857ab34...",
  "name": "Gaming Mouse",
  "description": "Wireless RGB gaming mouse",
  "images": [
    {
      "id": "img001",
      "url": "http://localhost:8080/media/images/img001"
    }
  ],
  "price": 59.99,
  "userId": "user123",
  "quantity": 25,
  "createdAt": "2026-07-27T16:42:18"
}
```

---

## MediaResponse

Internal response received from the Media Service.

| Field       | Type   |
| ----------- | ------ |
| id          | String |
| path        | String |
| productId   | String |
| contentType | String |

---

# Product Endpoints

## Get All Products

**GET**

```text
/products
```

### Authorization

Public

### Query Parameters

Supports Spring Data pagination.

| Parameter | Description                 |
| --------- | --------------------------- |
| page      | Page number                 |
| size      | Number of products per page |
| sort      | Sort field                  |

Example

```text
GET /products?page=0&size=20&sort=createdAt,desc
```

### Response

Returns a paginated list of `ProductResponse`.

---

## Get Product By ID

**GET**

```text
/products/{id}
```

### Authorization

Public

### Response

Returns the requested `ProductResponse`.

---

## Get Products By User

**GET**

```text
/products/user/{userId}
```

### Authorization

Authenticated users

Returns all products created by the specified user.

Supports pagination.

Example

```text
GET /products/user/6857ab34?page=0&size=10
```

---

# Seller & Administrator Endpoints

The following endpoints require either the **SELLER** or **ADMIN** role.

---

## Create Product

**POST**

```text
/products
```

### Authorization

SELLER or ADMIN

### Content-Type

```text
multipart/form-data
```

### Form Data

| Name    | Type   | Description                |
| ------- | ------ | -------------------------- |
| product | JSON   | Product information        |
| images  | File[] | One or more product images |

Example (product part)

```json
{
  "name": "Gaming Mouse",
  "description": "Wireless RGB gaming mouse",
  "price": 59.99,
  "quantity": 25
}
```

### Response

**201 Created**

Returns the created `ProductResponse`.

---

## Update Product

**PUT**

```text
/products/{id}
```

### Authorization

SELLER or ADMIN

### Content-Type

```text
multipart/form-data
```

### Form Data

| Name            | Type         | Required |
| --------------- | ------------ | -------- |
| product         | JSON         | Yes      |
| images          | File[]       | No       |
| deletedImageIds | List<String> | No       |

The endpoint supports:

- Updating product information
- Uploading additional images
- Removing existing images

Example request

```json
{
  "name": "Gaming Mouse Pro",
  "description": "Updated description",
  "price": 69.99,
  "quantity": 30
}
```

### Response

Returns the updated `ProductResponse`.

---

## Delete Product

**DELETE**

```text
/products/{id}
```

### Authorization

SELLER or ADMIN

### Response

**204 No Content**

Deletes the product and its associated images.

---

## Remove Product Image

**DELETE**

```text
/products/{productId}/images/{imageId}
```

### Authorization

SELLER or ADMIN

### Response

**204 No Content**

Removes a single image from the specified product.

---

## Count Products

**GET**

```text
/products/count
```

### Authorization

ADMIN

### Response

```json
125
```

---

# HTTP Status Codes

| Status                    | Description                              |
| ------------------------- | ---------------------------------------- |
| 200 OK                    | Request completed successfully           |
| 201 Created               | Product created successfully             |
| 204 No Content            | Product or image deleted successfully    |
| 400 Bad Request           | Validation failed or malformed request   |
| 401 Unauthorized          | Authentication required or invalid token |
| 403 Forbidden             | User does not have permission            |
| 404 Not Found             | Product not found                        |
| 500 Internal Server Error | Unexpected server error                  |

---

# Authorization Matrix

| Endpoint                                        | USER | SELLER | ADMIN |
| ----------------------------------------------- | :--: | :----: | :---: |
| GET `/products`                                 |  ✓   |   ✓    |   ✓   |
| GET `/products/{id}`                            |  ✓   |   ✓    |   ✓   |
| GET `/products/user/{userId}`                   |  ✓   |   ✓    |   ✓   |
| POST `/products`                                |  ✗   |   ✓    |   ✓   |
| PUT `/products/{id}`                            |  ✗   |   ✓    |   ✓   |
| DELETE `/products/{id}`                         |  ✗   |   ✓    |   ✓   |
| DELETE `/products/{productId}/images/{imageId}` |  ✗   |   ✓    |   ✓   |
| GET `/products/count`                           |  ✗   |   ✗    |   ✓   |

---

# Notes

- Product creation and updates use **`multipart/form-data`** because product information and images are uploaded in a single request.
- The `product` request part must contain a JSON representation of either `CreateRequest` or `UpdateRequest`.
- The `images` request part accepts multiple image files.
- During product updates, the optional `deletedImageIds` parameter allows selective removal of existing images while keeping the remaining ones.
- Product images are stored and managed by the **Media Service**, while the Product Service maintains the association between products and their images.
