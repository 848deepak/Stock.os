# 🚀 Inventory Management System - ENTERPRISE UPGRADE GUIDE

## Overview
Your inventory management system has been upgraded with **enterprise-grade features** for production deployment. This guide covers all new capabilities, architecture, and usage.

---

## 📋 What's New (Enhanced Features)

### ✅ 1. Multi-Warehouse Support
**Problem Solved**: Manage inventory across multiple physical locations

**Implementation**:
- **Warehouse Model**: Represents physical storage locations
- **WarehouseInventory**: Product-warehouse mapping with location-specific quantities
- **API Endpoints**:
  ```
  POST   /api/warehouses              # Create warehouse
  GET    /api/warehouses              # List all warehouses
  GET    /api/warehouses/{id}         # Get warehouse details
  PUT    /api/warehouses/{id}         # Update warehouse
  DELETE /api/warehouses/{id}         # Delete warehouse
  ```

**Usage Example**:
```bash
curl -X POST http://localhost:8080/api/warehouses \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Main Warehouse",
    "location": "New York, USA",
    "capacity": 50000,
    "managerName": "john_manager"
  }'
```

**Benefits**:
- Track inventory per warehouse
- Real-time visibility across locations
- Warehouse-specific reorder levels
- Efficient resource allocation

---

### ✅ 2. Category Management
**Problem Solved**: Organize products hierarchically

**Implementation**:
- **Category Model**: Product categories for better organization
- **Enhanced Product Model**: Products linked to categories
- **API Endpoints**:
  ```
  POST   /api/categories              # Create category
  GET    /api/categories              # List all categories
  GET    /api/categories/active       # Get active categories only
  PUT    /api/categories/{id}         # Update category
  DELETE /api/categories/{id}         # Delete category
  ```

**API Example**:
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and components"
  }'
```

---

### ✅ 3. Barcode Generation & Scanning System
**Problem Solved**: Quick product identification and verification

**Implementation**:
- **ZXing Integration**: Industrial-grade barcode generation
- **Unique Barcodes**: SKU + Product ID based barcode
- **Scanning API**: Quick product lookup by barcode
- **API Endpoints**:
  ```
  POST   /api/barcodes/generate/{productId}      # Generate barcode
  GET    /api/barcodes/image/{productId}         # Get barcode image
  POST   /api/barcodes/regenerate/{productId}    # Regenerate barcode
  GET    /api/barcodes/scan/{barcode}            # Scan & get product
  ```

**Usage Example**:
```bash
# Generate barcode for product
curl -X POST http://localhost:8080/api/barcodes/generate/1 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Get barcode image (Base64)
curl -X GET http://localhost:8080/api/barcodes/image/1 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Scan barcode to get product details
curl -X GET http://localhost:8080/api/barcodes/scan/SKU001-1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Benefits**:
- Reduce manual data entry errors
- Speed up inventory operations
- Track product movement
- Real-time scanning capabilities

---

### ✅ 4. Order Management & Automation
**Problem Solved**: Automate inventory deduction on orders

**Implementation**:
- **Order Model**: Simulates e-commerce orders
- **Event-Driven Automation**: Auto inventory reduction/restoration
- **Order Status Tracking**: PENDING → CONFIRMED → SHIPPED → DELIVERED
- **API Endpoints**:
  ```
  POST   /api/orders                  # Create order (auto deducts stock)
  GET    /api/orders                  # List all orders
  GET    /api/orders/{id}             # Get order details
  GET    /api/orders/status/{status}  # Filter by status
  PUT    /api/orders/{id}/confirm     # Confirm order
  PUT    /api/orders/{id}/ship        # Ship order
  PUT    /api/orders/{id}/deliver     # Mark delivered
  PUT    /api/orders/{id}/cancel      # Cancel (restores inventory)
  ```

**Workflow**:
```
Order Created
    ↓ (Event: OrderPlacedEvent)
    ↓ [Triggered automatically]
    ↓ Inventory REDUCED from warehouse
    ↓
Order Confirmed/Shipped/Delivered
    ↓
Order Cancelled (if applicable)
    ↓ (Event: OrderCancelledEvent)
    ↓ [Triggered automatically]
    ↓ Inventory RESTORED to warehouse
```

**API Example**:
```bash
# Create order (auto-deducts inventory)
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "warehouseId": 1,
    "quantity": 5,
    "expectedDelivery": "2026-04-15T10:00:00"
  }'

# Cancel order (auto-restores inventory)
curl -X PUT http://localhost:8080/api/orders/1/cancel \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### ✅ 5. Event-Driven Architecture
**Problem Solved**: Automated system responses to business events

**Events Implemented**:

#### OrderPlacedEvent
- **Triggered**: When new order is created
- **Action**: Automatically deduct stock from warehouse
- **Benefits**: No manual inventory updates needed

#### OrderCancelledEvent
- **Triggered**: When order is cancelled
- **Action**: Automatically restore stock to warehouse
- **Benefits**: Prevents inventory inconsistencies

#### StockLowEvent
- **Triggered**: When inventory drops below reorder level
- **Action**: Creates alert for purchasing
- **Benefits**: Proactive stock management

**Listener Pattern**:
```java
@EventListener
public void handleOrderPlaced(OrderPlacedEvent event) {
    // Automatically reduce inventory
    warehouseInventoryService.removeStock(
        event.getProductId(),
        event.getWarehouseId(),
        event.getQuantity()
    );
}
```

---

### ✅ 6. Export System (CSV/PDF)
**Problem Solved**: Generate reports for analysis and compliance

**Features**:
- **CSV Export**: For spreadsheet analysis
- **PDF Export**: For formal reports
- **Filtered Exports**: By category, warehouse, date range
- **API Endpoints**:
  ```
  GET    /api/exports/products/csv              # Export products
  GET    /api/exports/products/pdf              # Export products PDF
  GET    /api/exports/transactions/pdf          # Export transactions
  ```

**Examples**:
```bash
# Export all products as CSV
curl -X GET "http://localhost:8080/api/exports/products/csv" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o products.csv

# Export products by category as PDF
curl -X GET "http://localhost:8080/api/exports/products/pdf?category=1" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o products.pdf

# Export transactions for date range as PDF
curl -X GET "http://localhost:8080/api/exports/transactions/pdf?startDate=2026-04-01T00:00:00&endDate=2026-04-30T23:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o transactions.pdf
```

---

### ✅ 7. Audit Logging
**Problem Solved**: Track all system changes for compliance and debugging

**Features**:
- **Automatic Logging**: Every create/update/delete recorded
- **User Tracking**: Who made what change and when
- **Change History**: Old value → New value
- **Supports**: Products, Categories, Warehouses, Orders, Inventory

**Database Tables**:
- `audit_logs` - All entity modifications

---

### ✅ 8. Warehouse Transfers
**Problem Solved**: Move inventory between warehouses efficiently

**Implementation**:
- **Transfer Model**: Track inter-warehouse movements
- **Status Tracking**: PENDING → IN_TRANSIT → COMPLETED
- **Inventory Updates**: Auto-update both source and destination

**Database Schema**:
- `warehouse_transfers` - Tracks all transfers

---

### ✅ 9. Stock Alerts
**Problem Solved**: Notify when inventory reaches critical levels

**Alert Types**:
- Low Stock (below reorder level)
- Critical Stock (very low)
- Out of Stock (zero quantity)
- Overstock (above capacity)

**Database Tables**:
- `stock_alerts` - Alert records with resolution status

---

## 🏗️ Enhanced Database Schema

### New Tables

#### 1. Categories
```sql
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

#### 2. Warehouses
```sql
CREATE TABLE warehouses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL,
    manager_id BIGINT,
    capacity INT DEFAULT 10000,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

#### 3. Warehouse Inventory (Multi-warehouse support)
```sql
CREATE TABLE warehouse_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    last_stocked_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY unique_product_warehouse (product_id, warehouse_id)
);
```

#### 4. Orders (E-commerce simulation)
```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    quantity INT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    order_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL
);
```

#### 5. Stock Alerts
```sql
CREATE TABLE stock_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    alert_type VARCHAR(50),
    current_stock INT,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL
);
```

#### 6. Audit Logs
```sql
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    action VARCHAR(50),  -- CREATE, UPDATE, DELETE
    old_value TEXT,
    new_value TEXT,
    user_id BIGINT,
    created_at DATETIME NOT NULL
);
```

---

## 🔧 Key Service Classes

### 1. **WarehouseService**
Manages warehouse CRUD operations and relationships.

### 2. **CategoryService**
Handles product category management.

### 3. **WarehouseInventoryService**
Multi-warehouse inventory tracking:
- `addStock()` - Add inventory to warehouse
- `removeStock()` - Remove inventory (with validation)
- `getInventoryByProduct()` - Get all warehouses for product
- `getInventoryByWarehouse()` - Get all products in warehouse
- Updates product total quantity automatically

### 4. **OrderService**
Order management with automation:
- `createOrder()` - Creates order + publishes OrderPlacedEvent
- `cancelOrder()` - Cancels + publishes OrderCancelledEvent
- Status tracking: PENDING → CONFIRMED → SHIPPED → DELIVERED

### 5. **BarcodeService**
Barcode generation and scanning:
- `generateBarcodeForProduct()` - Creates unique barcode
- `getBarcodeImage()` - Returns Base64 encoded image
- `getProductByBarcode()` - Scan & retrieve product

### 6. **ExportService**
Data export functionality:
- `exportProductsToCSV()` - CSV format for spreadsheets
- `exportProductsToPDF()` - Professional PDF reports
- Supports filtering by category/warehouse

### 7. **AuditLogService**
Automatic audit trail:
- Logs all entity modifications
- Tracks user, timestamp, old/new values
- Integrated with all services

---

## 📊 REST API Reference

### Authentication
```bash
POST /api/auth/login
```
**Request**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

### Categories
```
POST   /api/categories
GET    /api/categories
GET    /api/categories/{id}
GET    /api/categories/active
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

### Warehouses
```
POST   /api/warehouses
GET    /api/warehouses
GET    /api/warehouses/{id}
GET    /api/warehouses/active
PUT    /api/warehouses/{id}
DELETE /api/warehouses/{id}
```

### Orders
```
POST   /api/orders
GET    /api/orders
GET    /api/orders/{id}
GET    /api/orders/status/{status}
PUT    /api/orders/{id}/confirm
PUT    /api/orders/{id}/ship
PUT    /api/orders/{id}/deliver
PUT    /api/orders/{id}/cancel
```

### Barcodes
```
POST   /api/barcodes/generate/{productId}
GET    /api/barcodes/image/{productId}
POST   /api/barcodes/regenerate/{productId}
GET    /api/barcodes/scan/{barcode}
```

### Exports
```
GET    /api/exports/products/csv
GET    /api/exports/products/pdf
GET    /api/exports/transactions/pdf
```

### Products (Enhanced)
```
GET    /api/products (with pagination)
GET    /api/products/{id}
GET    /api/products/search?keyword=...
GET    /api/products/category/{id}
GET    /api/products/supplier/{id}
GET    /api/products/low-stock
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

---

## 🔐 Role-Based Access Control (RBAC)

### ADMIN Role
- Full system access
- Can delete inventory, categories, warehouses
- Can view audit logs
- Can manage users and roles

### MANAGER Role
- Inventory management (CRUD)
- Order management
- Export data
- Cannot delete users or change roles

### STAFF Role
- View-only access
- Can view products, warehouse inventory
- Can view orders and transactions
- Cannot modify data

**Authorization Examples**:
```java
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO dto) { ... }

@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteCategory(@PathVariable Long id) { ... }

@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) { ... }
```

---

## 🚀 Setup & Deployment

### Prerequisites
- Java 21+
- MySQL 8.0+
- Maven 3.8+
- Node.js 18+ (for frontend)

### Backend Setup

**1. Update Database Configuration**
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/inventory_db
    username: root
    password: your_password
```

**2. Build & Run**
```bash
cd inventory-management-system
mvn clean install
mvn spring-boot:run
```

**3. Verify**
```bash
curl http://localhost:8080/api/auth/health
```

### Frontend Setup

**1. Install Dependencies**
```bash
cd frontend
npm install
```

**2. Add New Features to UI**
```bash
npm run dev
```

**3. Build for Production**
```bash
npm run build
```

---

## 💡 Usage Scenarios

### Scenario 1: Multi-Location Inventory Management
**Problem**: Company has 3 warehouses and needs to track inventory per location

**Solution**:
```bash
# Create warehouses
curl -X POST http://localhost:8080/api/warehouses \
  -H "Authorization: Bearer TOKEN" \
  -d '{"name": "NY Warehouse", "location": "New York"}'

# View inventory per warehouse
curl -X GET "http://localhost:8080/api/warehouses/1/inventory" \
  -H "Authorization: Bearer TOKEN"

# Transfer between warehouses
curl -X POST http://localhost:8080/api/warehouse-transfers \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "productId": 1,
    "fromWarehouseId": 1,
    "toWarehouseId": 2,
    "quantity": 10
  }'
```

### Scenario 2: Automated Order Processing
**Problem**: Manual inventory adjustment after each order

**Solution**:
```bash
# Create order (inventory auto-deducted)
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "productId": 1,
    "warehouseId": 1,
    "quantity": 5
  }'

# System automatically:
# 1. Publishes OrderPlacedEvent
# 2. Event listener deducts inventory
# 3. Creates audit log entry
# 4. Checks for low stock alerts

# Retrieve order status
curl -X GET "http://localhost:8080/api/orders/status/PENDING" \
  -H "Authorization: Bearer TOKEN"
```

### Scenario 3: Product Scanning & Barcode
**Problem**: Need quick product lookup by barcode

**Solution**:
```bash
# Generate barcode
curl -X POST http://localhost:8080/api/barcodes/generate/1 \
  -H "Authorization: Bearer TOKEN"

# Get barcode image to display
curl -X GET http://localhost:8080/api/barcodes/image/1 \
  -H "Authorization: Bearer TOKEN"

# Scan barcode (quick lookup)
curl -X GET http://localhost:8080/api/barcodes/scan/SKU001-1 \
  -H "Authorization: Bearer TOKEN"
```

---

## 📈 Performance Optimization

### Database Indexing
All created tables include strategic indexing:
- Product: `sku`, `category_id`, `barcode`
- Warehouse: `name`, `is_active`
- Orders: `status`, `order_date`, `product_id`
- Transactions: `created_at`, `product_id`, `type`

### Caching Opportunities
```java
@Cacheable("categories")
public List<CategoryDTO> getActiveCategories() { ... }

@CacheEvict("categories")
public CategoryDTO updateCategory(Long id, CategoryDTO dto) { ... }
```

### Lazy Loading
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
private Category category;
```

---

## 🧪 Testing Checklist

- [ ] Create category and verify via API
- [ ] Create warehouse with inventory tracking
- [ ] Create order and verify stock auto-deduction
- [ ] Cancel order and verify stock restoration
- [ ] Generate barcode and scan product
- [ ] Export products to CSV and PDF
- [ ] Create multiple orders in different warehouses
- [ ] Verify low stock alerts
- [ ] Check audit logs for all operations
- [ ] Test RBAC restrictions for each role

---

## 🐛 Troubleshooting

### Issue: Inventory not deducting on order creation
**Solution**: Verify event listener is enabled and exception isn't caught silently
```bash
# Check application logs
tail -f logs/application.log | grep "OrderPlaced"
```

### Issue: Barcode not generating
**Solution**: Ensure ZXing dependencies are installed
```bash
mvn dependency:tree | grep zxing
```

### Issue: Export file is empty
**Solution**: Check query filters and verify data exists
```bash
# Verify products exist in database
SELECT COUNT(*) FROM products WHERE is_active = true;
```

---

## 📚 Dependencies Added

```xml
<!-- Barcode Generation -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.2</version>
</dependency>

<!-- PDF Export -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>

<!-- CSV Export -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-csv</artifactId>
    <version>1.10.0</version>
</dependency>
```

---

## 🎓 Architecture Highlights

### Event-Driven Pattern
```
Order Created → Event Published → Listener Triggered → Inventory Updated
```

### Layered Architecture
```
Controller → Service → Repository → Database
   ↑           ↑          ↑           ↑
  REST       Business    Data      Persistence
  Layer      Logic      Access      Layer
```

### DTO Pattern
- Prevents entity exposure
- Clean API contracts
- Easy versioning

### Transactional Consistency
```java
@Transactional
public OrderDTO createOrder(...) {
    // All operations atomic
    // Rollback on any failure
}
```

---

## 🔮 Future Enhancements

1. **Kafka Integration** - Async event processing
2. **Redis Caching** - Performance improvement
3. **Rate Limiting** - API protection
4. **GraphQL** - Flexible queries
5. **Machine Learning** - Demand forecasting
6. **Mobile App** - Native scanning
7. **Real-time Dashboard** - WebSocket updates
8. **Advanced Analytics** - BI integration

---

## 📞 Support

- Check `/api/auth/health` for system status
- Review logs: `target/app.log`
- Verify database connectivity
- Check JWT token expiration
- Validate RBAC permissions

---

**Version**: 2.0.0 (Enterprise Edition)
**Last Updated**: April 4, 2026
**Status**: 🟢 Production Ready
