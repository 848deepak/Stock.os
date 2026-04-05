# Inventory Management System - Setup Guide

A production-ready inventory management system with Spring Boot backend and React frontend following industry best practices.

## 📋 Project Structure

```
.
├── inventory-management-system/          # Spring Boot Backend
│   ├── src/main/java/com/inventory/
│   │   ├── model/                        # JPA Entities
│   │   ├── repository/                   # Spring Data JPA Repositories
│   │   ├── service/                      # Business Logic
│   │   ├── controller/                   # REST Endpoints
│   │   ├── dto/                          # Data Transfer Objects
│   │   ├── config/                       # Configuration & Security
│   │   ├── security/                     # JWT & Authentication
│   │   └── exception/                    # Exception Handling
│   ├── src/main/resources/
│   │   ├── application.yml               # Spring Configuration
│   │   └── schema.sql                    # Database Schema
│   └── pom.xml
│
└── frontend/                             # React Frontend
    ├── src/
    │   ├── components/                   # Reusable Components
    │   ├── pages/                        # Page Components
    │   ├── services/                     # API Services
    │   ├── context/                      # React Context
    │   ├── App.jsx                       # Main App Component
    │   └── index.css                     # Tailwind Styles
    ├── package.json
    ├── vite.config.js
    └── index.html
```

## 🚀 Quick Start

### Step 1: Backend Setup

1. **Prerequisites**
   - Java 21+
   - Maven 3.8+
   - MySQL 8+

2. **Configure Database**
   ```bash
   cd inventory-management-system
   ```

   Create MySQL database:
   ```sql
   CREATE DATABASE inventory_db;
   ```

3. **Setup Environment Variables** (optional)
   ```env
   DB_USERNAME=root
   DB_PASSWORD=your_password
   JWT_SECRET=your-super-secret-key-change-this
   ```

4. **Build Backend**
   ```bash
   mvn clean install
   ```

5. **Run Backend**
   ```bash
   mvn spring-boot:run
   ```

   Backend will start on `http://localhost:8080`

### Step 2: Frontend Setup

1. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Start Development Server**
   ```bash
   npm run dev
   ```

   Frontend will run on `http://localhost:3000`

3. **Build for Production**
   ```bash
   npm run build
   ```

## 🔐 Authentication

**Default Credentials** (auto-initialized on first run):
- **Username**: admin
- **Password**: admin1234@
- **Email**: admin@stock.os
- **Role**: ADMIN

The admin user is automatically created by the DataInitializer when the application starts for the first time. The password uses PBKDF2 hashing.

### JWT Token Flow

1. User logs in with credentials
2. Server validates and returns JWT token
3. Token is stored in localStorage
4. All subsequent requests include token in `Authorization: Bearer <token>` header
5. Server validates token for each request

## 📊 Architecture

### Backend Architecture

```
Controller → Service → Repository → Database
    ↓
Exception Handler (Global)
    ↓
Security Filter (JWT)
```

**Layers:**
- **Controller**: REST endpoints with role-based @PreAuthorize
- **Service**: Business logic, transaction management
- **Repository**: Spring Data JPA, database queries
- **DTO**: Clean API contracts, avoid entity exposure

### Security Implementation

- **JWT Authentication**: Stateless token-based auth
- **RBAC**: Role-Based Access Control with three roles:
  - ADMIN: Full system access
  - MANAGER: Inventory management
  - STAFF: View-only access
- **Password Hashing**: BCrypt encryption
- **CORS**: Configured for frontend communication

### Database Schema

**Tables:**
- `roles`: Admin, Manager, Staff
- `users`: User credentials and roles
- `products`: Product catalog with stock levels
- `inventory_transactions`: Stock in/out history

## 🎨 Frontend Features

### Premium UI/UX

- **Design**: Minimalist, Stripe/Notion-inspired
- **Tailwind CSS**: Utility-first styling
- **Framer Motion**: Smooth animations
- **Lucide Icons**: Clean icon set
- **Responsive**: Mobile-first design
- **Dark mode ready**: Easy to extend

### Pages

1. **Login**: Secure authentication
2. **Dashboard**: Key metrics and alerts
3. **Products**: CRUD operations with search/filter
4. **Sidebar Navigation**: Persistent navigation

## 📱 API Endpoints

### Authentication
```
POST   /auth/login                    # Login
GET    /auth/health                   # Health check
```

### Products (Role-protected)
```
GET    /products                      # List all products
GET    /products/{id}                 # Get product by ID
GET    /products/search?keyword=...   # Search products
GET    /products/category/{category}  # Filter by category
GET    /products/low-stock            # Get low stock items
POST   /products                      # Create product (MANAGER+)
PUT    /products/{id}                 # Update product (MANAGER+)
DELETE /products/{id}                 # Delete product (ADMIN)
```

### Inventory Transactions
```
POST   /transactions/stock-in         # Record stock-in
POST   /transactions/stock-out        # Record stock-out
POST   /transactions/adjustment       # Adjust stock
GET    /transactions/history          # Transaction history
GET    /transactions/product/{id}     # Product transactions
```

### Dashboard
```
GET    /dashboard/stats               # Dashboard statistics
```

## 🔧 Configuration

### Backend Configuration (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/inventory_db
    username: root
    password:

  jpa:
    hibernate:
      ddl-auto: update

app:
  jwt:
    secret-key: your-key
    expiration: 86400000  # 24 hours
```

### Frontend Configuration (.env)

```env
VITE_API_URL=http://localhost:8080/api
```

## 🧪 Testing

### Sample API Requests

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin1234@"}'
```

**Get All Products:**
```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/products?page=0&size=10
```

**Create Product:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "sku": "SKU001",
    "category": "Electronics",
    "price": 45000,
    "quantity": 10,
    "reorderLevel": 5
  }'
```

**Stock In:**
```bash
curl -X POST http://localhost:8080/api/transactions/stock-in \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 5,
    "reason": "Purchase from supplier"
  }'
```

## 🔑 Key Features

✅ **JWT Authentication with role-based access control**
✅ **Clean 3-layer architecture**
✅ **Comprehensive error handling**
✅ **Pagination and search**
✅ **Low stock alerts**
✅ **Transaction history**
✅ **Premium UI/UX with animations**
✅ **Responsive design**
✅ **Production-ready code**
✅ **Database schema and sample data**

## 🛡️ Security Best Practices

- ✅ Password hashing (BCrypt)
- ✅ JWT token validation
- ✅ Role-based endpoint protection
- ✅ CORS configuration
- ✅ Input validation with @Valid
- ✅ Global exception handling
- ✅ No hardcoded secrets (environment variables)
- ✅ SQL injection prevention (JPA)

## 📦 Technologies Used

**Backend:**
- Spring Boot 3.3.0
- Spring Data JPA
- Spring Security
- JWT (jjwt)
- MySQL
- Maven

**Frontend:**
- React 18
- Vite
- Tailwind CSS
- Framer Motion
- Axios
- React Router

## 🚀 Production Deployment

### Backend (Docker)
```dockerfile
FROM maven:3.8-openjdk-21 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-slim
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

### Frontend (Nginx)
```nginx
server {
    listen 80;
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }
    location /api {
        proxy_pass http://backend:8080;
    }
}
```

## 📝 Next Steps

1. Configure your MySQL connection
2. Update JWT secret in production
3. Set strong passwords for admin user
4. Configure CORS for your domain
5. Deploy using Docker or cloud platform
6. Set up SSL/TLS certificates
7. Configure CI/CD pipeline

## 📞 Support

For issues or improvements, refer to the code comments and Spring Boot documentation.

---

**Version:** 1.0.0
**Last Updated:** 2026-04-04
