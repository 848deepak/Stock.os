# 🗂️ Complete API Reference - Enterprise Edition

## Base URL
```
http://localhost:8080/api
```

## Authentication
All endpoints require JWT token (except `/auth/login` and `/auth/health`)
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## **AUTH Endpoints** (Public)

### Login
```
POST /auth/login
Content-Type: application/json

Request:
{
  "username": "admin",
  "password": "admin1234@"
}

Response:
{
  "token": "eyJhbGc...",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

### Health Check
```
GET /auth/health
Response: { "status": "UP" }
```

---

## **CATEGORIES** (New)

### Create Category
```
POST /categories
Authorization: Bearer TOKEN
Content-Type: application/json
Role: ADMIN, MANAGER

{
  "name": "Electronics",
  "description": "Electronic devices"
}
```

### Get All Categories
```
GET /categories
Authorization: Bearer TOKEN
Role: ADMIN, MANAGER, STAFF

Response: [{
  "id": 1,
  "name": "Electronics",
  "description": "...",
  "isActive": true,
  "createdAt": "2026-04-04T10:00:00",
  "updatedAt": "2026-04-04T10:00:00"
}]
```

### Get Active Categories Only
```
GET /categories/active
```

### Get Category by ID
```
GET /categories/{id}
```

### Update Category
```
PUT /categories/{id}

{
  "name": "Electronics Updated",
  "description": "...",
  "isActive": true
}
```

### Delete Category
```
DELETE /categories/{id}
Role: ADMIN only
```

---

## **WAREHOUSES** (New - Multi-location Support)

### Create Warehouse
```
POST /warehouses
Role: ADMIN, MANAGER

{
  "name": "Main Warehouse",
  "location": "New York, USA",
  "capacity": 50000,
  "managerName": "john_manager"
}
```

### List All Warehouses
```
GET /warehouses
```

### Get Active Warehouses
```
GET /warehouses/active
```

### Get Warehouse by ID
```
GET /warehouses/{id}
```

### Update Warehouse
```
PUT /warehouses/{id}

{
  "name": "Updated Warehouse",
  "location": "...",
  "capacity": 60000,
  "isActive": true
}
```

### Delete Warehouse
```
DELETE /warehouses/{id}
Role: ADMIN only
```

---

## **BARCODES** (New - Scanning System)

### Generate Barcode for Product
```
POST /barcodes/generate/{productId}
Role: ADMIN, MANAGER

Response:
{
  "barcode": "SKU001-1",
  "productId": "1"
}
```

### Get Barcode Image (Base64)
```
GET /barcodes/image/{productId}

Response:
{
  "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
}
```

### Regenerate Barcode
```
POST /barcodes/regenerate/{productId}
```

### Scan Barcode & Get Product
```
GET /barcodes/scan/{barcode}
Role: ADMIN, MANAGER, STAFF

Response:
{
  "id": 1,
  "name": "Laptop",
  "sku": "SKU001",
  "price": 45000,
  "quantity": 50,
  "category": "Electronics"
}
```

---

## **ORDERS** (New - Order Automation)

### Create Order (Auto-deducts inventory)
```
POST /orders
Role: ADMIN, MANAGER
Content-Type: application/json

{
  "productId": 1,
  "warehouseId": 1,
  "quantity": 5,
  "expectedDelivery": "2026-04-15T10:00:00"
}

Response:
{
  "id": 1,
  "orderNumber": "ORD-A1B2C3D4",
  "productId": 1,
  "productName": "Laptop",
  "warehouseId": 1,
  "quantity": 5,
  "status": "PENDING",
  "orderDate": "2026-04-04T10:00:00"
}
```

### Get All Orders
```
GET /orders
```

### Get Order by ID
```
GET /orders/{id}
```

### Get Orders by Status
```
GET /orders/status/{status}
Status values: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
```

### Confirm Order
```
PUT /orders/{id}/confirm
Role: ADMIN, MANAGER
Response: Updated order with status CONFIRMED
```

### Ship Order
```
PUT /orders/{id}/ship
Role: ADMIN, MANAGER
Response: Updated order with status SHIPPED
```

### Deliver Order
```
PUT /orders/{id}/deliver
Role: ADMIN, MANAGER
Response: Updated order with status DELIVERED
```

### Cancel Order (Auto-restores inventory)
```
PUT /orders/{id}/cancel
Role: ADMIN, MANAGER
Response: Updated order with status CANCELLED
Note: Automatically restores inventory to warehouse
```

---

## **EXPORTS** (New - CSV/PDF Reports)

### Export Products as CSV
```
GET /exports/products/csv
Role: ADMIN, MANAGER
Query Params:
  - category: (Optional) category ID for filtering
Headers:
  Content-Disposition: attachment; filename="products.csv"
  Content-Type: text/plain
```

### Export Products as PDF
```
GET /exports/products/pdf
Query Params:
  - category: (Optional) category ID
Headers:
  Content-Disposition: attachment; filename="products.pdf"
  Content-Type: application/pdf
```

### Export Transactions as PDF
```
GET /exports/transactions/pdf
Query Params (Required):
  - startDate: 2026-04-01T00:00:00
  - endDate: 2026-04-30T23:59:59
Headers:
  Content-Disposition: attachment; filename="transactions.pdf"
  Content-Type: application/pdf
```

---

## **PRODUCTS** (Enhanced)

### Get All Products (with pagination)
```
GET /products?page=0&size=10&sort=name,desc
```

### Search Products
```
GET /products/search?keyword=laptop&page=0&size=10
```

### Get Products by Category
```
GET /products?category=Electronics
GET /products/category/{categoryId}
```

### Get Products by Supplier
```
GET /products/supplier/{supplierId}
```

### Get Low Stock Products
```
GET /products/low-stock
```

### Get Product by ID
```
GET /products/{id}
```

### Create Product
```
POST /products
Role: ADMIN, MANAGER

{
  "name": "Laptop",
  "sku": "SKU001",
  "description": "Dell Inspiron 15",
  "categoryId": 1,
  "supplierId": 1,
  "price": 45000.00,
  "quantity": 50,
  "reorderLevel": 10
}
```

### Update Product
```
PUT /products/{id}

{
  "name": "Laptop Updated",
  "price": 46000.00,
  "quantity": 45
}
```

### Delete Product
```
DELETE /products/{id}
Role: ADMIN only
```

---

## **TRANSACTIONS** (Enhanced with warehouse)

### Stock In (Add inventory)
```
POST /transactions/stock-in
Role: ADMIN, MANAGER

{
  "productId": 1,
  "warehouseId": 1,
  "quantity": 20,
  "reason": "Restock from supplier"
}
```

### Stock Out (Remove inventory)
```
POST /transactions/stock-out
Role: ADMIN, MANAGER

{
  "productId": 1,
  "warehouseId": 1,
  "quantity": 5,
  "reason": "Manual adjustment"
}
```

### Get Transaction History
```
GET /transactions/history?page=0&size=10
GET /transactions/history?productId=1
GET /transactions/history?warehouseId=1
GET /transactions/history?type=STOCK_IN
```

---

## **DASHBOARD** (Enhanced)

### Get Dashboard Statistics
```
GET /dashboard/stats
Response:
{
  "totalProducts": 100,
  "totalValue": 5000000,
  "lowStockCount": 5,
  "warehouseCount": 3,
  "totalWarehouses": 3,
  "outOfStockCount": 2
}
```

---

## **New Database Entities**

### Categories
- Product categorization
- Hierarchical organization
- Query-friendly structure

### Warehouses
- Multi-location support
- Capacity tracking
- Manager assignment

### Warehouse Inventory
- Product-warehouse mapping
- Location-specific quantities
- Independent reorder levels per warehouse

### Orders
- E-commerce order simulation
- Automatic inventory deduction
- Status tracking (5 states)

### Stock Alerts
- Low stock notifications
- Critical inventory alerts
- Resolvable alert system

### Audit Logs
- Track all modifications
- User attribution
- Change history (before/after)

### Warehouse Transfers
- Inter-warehouse movements
- Status tracking
- Automatic inventory updates

---

## **Status Codes**

```
200 OK                  - Successful retrieval/update
201 Created            - Resource created
204 No Content         - Successful deletion
400 Bad Request        - Invalid input
401 Unauthorized       - Missing/invalid token
403 Forbidden          - Insufficient permissions
404 Not Found          - Resource not found
409 Conflict           - Inventory conflict (e.g., insufficient stock)
500 Internal Error     - Server error
```

---

## **Error Response Format**

```json
{
  "timestamp": "2026-04-04T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient stock. Available: 10, Requested: 20",
  "path": "/api/orders"
}
```

---

## **Sample cURL Commands**

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Create Category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Electronics","description":"Electronics devices"}'
```

### Create Order (Auto-deduct inventory)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"warehouseId":1,"quantity":5}'
```

### Scan Barcode
```bash
curl -X GET http://localhost:8080/api/barcodes/scan/SKU001-1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Export Products
```bash
curl -X GET "http://localhost:8080/api/exports/products/csv" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o products.csv
```

---

**Version**: 2.0.0
**Last Updated**: April 4, 2026
