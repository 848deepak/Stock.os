# API Testing Guide

## Base URL
```
http://localhost:8080/api
```

## Authentication

All endpoints (except `/auth/login`) require the `Authorization` header:
```
Authorization: Bearer <JWT_TOKEN>
```

## 🔐 Authentication Endpoints

### 1. Login
**POST** `/auth/login`

Request:
```json
{
  "username": "admin",
  "password": "admin1234@"
}
```

Response (201):
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "role": "ADMIN",
  "userId": 1
}
```

Save the `token` value for subsequent requests.

### 2. Health Check
**GET** `/auth/health` (No Auth Required)

Response:
```json
{
  "status": "ok",
  "message": "Inventory Management System API is running"
}
```

---

## 📦 Product Endpoints

### 1. List All Products
**GET** `/products?page=0&size=10&sortBy=id&direction=DESC`

Response:
```json
{
  "content": [
    {
      "id": 1,
      "name": "Laptop",
      "sku": "SKU001",
      "description": "Dell Inspiron 15",
      "category": "Electronics",
      "price": 45000.00,
      "quantity": 50,
      "reorderLevel": 10,
      "isActive": true,
      "isLowStock": false,
      "createdAt": "2026-04-04T10:00:00"
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "currentPage": 0
}
```

### 2. Get Product by ID
**GET** `/products/{id}`

Example: `GET /products/1`

Response:
```json
{
  "id": 1,
  "name": "Laptop",
  "sku": "SKU001",
  "category": "Electronics",
  "price": 45000.00,
  "quantity": 50,
  "isLowStock": false
}
```

### 3. Search Products
**GET** `/products/search?keyword=laptop&page=0&size=10`

Response: Same as List All Products

### 4. Get Low Stock Products
**GET** `/products/low-stock`

Response:
```json
[
  {
    "id": 5,
    "name": "Mouse",
    "sku": "SKU004",
    "quantity": 5,
    "reorderLevel": 20,
    "isLowStock": true
  }
]
```

### 5. Get Products by Category
**GET** `/products/category/Electronics?page=0&size=10`

### 6. Create Product
**POST** `/products` (Requires MANAGER or ADMIN role)

Request:
```json
{
  "name": "Desktop Computer",
  "sku": "SKU006",
  "description": "High-performance desktop",
  "category": "Electronics",
  "price": 75000.00,
  "quantity": 15,
  "reorderLevel": 5,
  "expiryDate": null
}
```

Response (201):
```json
{
  "id": 6,
  "name": "Desktop Computer",
  "sku": "SKU006",
  "category": "Electronics",
  "price": 75000.00,
  "quantity": 15,
  "createdAt": "2026-04-04T10:30:00"
}
```

### 7. Update Product
**PUT** `/products/{id}` (Requires MANAGER or ADMIN role)

Example: `PUT /products/1`

Request:
```json
{
  "name": "Laptop - Updated",
  "sku": "SKU001",
  "category": "Electronics",
  "price": 48000.00,
  "quantity": 45,
  "reorderLevel": 10
}
```

### 8. Delete Product
**DELETE** `/products/{id}` (Requires ADMIN role only)

Example: `DELETE /products/1`

Response: 204 No Content

---

## 📊 Inventory Transaction Endpoints

### 1. Stock In (Add Stock)
**POST** `/transactions/stock-in` (Requires MANAGER or ADMIN role)

Request:
```json
{
  "productId": 1,
  "quantity": 20,
  "reason": "Purchase from supplier ABC"
}
```

Response (201):
```json
{
  "id": 1,
  "productId": 1,
  "productName": "Laptop",
  "type": "STOCK_IN",
  "quantity": 20,
  "reason": "Purchase from supplier ABC",
  "performedByUsername": "admin",
  "referenceId": "12345678-1234-1234-1234-123456789012",
  "createdAt": "2026-04-04T10:45:00"
}
```

### 2. Stock Out (Remove Stock)
**POST** `/transactions/stock-out` (Requires MANAGER or ADMIN role)

Request:
```json
{
  "productId": 1,
  "quantity": 5,
  "reason": "Sale to customer #12345"
}
```

Error if insufficient stock:
```json
{
  "status": 400,
  "message": "Insufficient stock. Available: 45, Requested: 50"
}
```

### 3. Stock Adjustment
**POST** `/transactions/adjustment` (Requires MANAGER or ADMIN role)

Request:
```json
{
  "productId": 1,
  "quantity": 40,
  "reason": "Physical count adjustment"
}
```

### 4. Get Transaction History
**GET** `/transactions/history?page=0&size=10`

Response:
```json
{
  "content": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop",
      "type": "STOCK_IN",
      "quantity": 20,
      "reason": "Purchase from supplier",
      "performedByUsername": "admin",
      "createdAt": "2026-04-04T10:45:00",
      "referenceId": "ABC123"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0
}
```

### 5. Get Product Transaction History
**GET** `/transactions/product/{productId}?page=0&size=10`

Example: `GET /transactions/product/1`

---

## 📈 Dashboard Endpoints

### Get Dashboard Statistics
**GET** `/dashboard/stats`

Response:
```json
{
  "totalProducts": 5,
  "lowStockProducts": 2,
  "recentTransactions": 15
}
```

---

## ❌ Error Responses

### Validation Error (422)
```json
{
  "status": 422,
  "errors": {
    "name": "Product name is required",
    "price": "Price must be greater than 0"
  },
  "timestamp": "2026-04-04T10:50:00"
}
```

### Not Found (404)
```json
{
  "status": 404,
  "message": "Product not found with id: 999",
  "timestamp": "2026-04-04T10:50:00"
}
```

### Unauthorized (401)
```json
{
  "status": 401,
  "message": "Invalid JWT token"
}
```

### Forbidden (403)
```json
{
  "status": 403,
  "message": "Access denied"
}
```

### Business Error (400)
```json
{
  "status": 400,
  "message": "Product with SKU SKU001 already exists",
  "timestamp": "2026-04-04T10:50:00"
}
```

---

## 🔑 Role-Based Access

| Endpoint | GET | POST | PUT | DELETE |
|----------|-----|------|-----|--------|
| `/products` | STAFF+ | MANAGER+ | MANAGER+ | ADMIN |
| `/transactions` | STAFF+ | MANAGER+ | - | - |
| `/dashboard` | STAFF+ | - | - | - |

**Roles Hierarchy:** ADMIN ⊃ MANAGER ⊃ STAFF

---

## 🧪 Complete API Test Sequence

### Step 1: Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin1234@"}'
```

Copy the `token` from response.

### Step 2: Get Products
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Step 3: Create Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Product",
    "sku": "NEW001",
    "category": "Test",
    "price": 1000,
    "quantity": 10
  }'
```

### Step 4: Record Stock In
```bash
curl -X POST http://localhost:8080/api/transactions/stock-in \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 5,
    "reason": "Test addition"
  }'
```

### Step 5: Get Dashboard Stats
```bash
curl -X GET http://localhost:8080/api/dashboard/stats \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📌 Notes

- All timestamps are in ISO 8601 format (UTC)
- Prices are in the base currency (₹)
- Pagination starts at page 0
- Default page size is 10 items
- JWT tokens expire after 24 hours
- Deleted products are soft-deleted (is_active = false)
- Low stock is defined by: quantity ≤ reorderLevel

---

## 🔗 Useful Links

- Spring Boot Docs: https://spring.io/projects/spring-boot
- JWT Introduction: https://jwt.io
- REST API Best Practices: https://restfulapi.net
- HTTP Status Codes: https://httpwg.org/specs/rfc9110.html

