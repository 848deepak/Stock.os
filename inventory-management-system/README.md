# Inventory Management System - Backend API

![Language](https://img.shields.io/badge/language-Java-orange) ![Build Tool](https://img.shields.io/badge/build-Maven-red) ![Framework](https://img.shields.io/badge/framework-Spring_Boot-green) ![Database](https://img.shields.io/badge/database-MySQL-blue)

A professional, enterprise-grade inventory management system for retail businesses, built with Java 21, Spring Boot, MySQL, and JDBC. Provides comprehensive inventory management features including authentication, product/stock/supplier/sales management, analytics, barcode simulation, reporting, and dark mode support.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Building & Running](#building--running)
- [Testing](#testing)
- [API Endpoints](#api-endpoints)
- [Advanced Features](#advanced-features)
- [Production Deployment](#production-deployment)
- [Future Roadmap](#future-roadmap)
- [Support & Contribution](#support--contribution)

---

## Overview

The Inventory Management System provides a complete, production-ready backend API for retail businesses to manage inventory across multiple warehouses, track stock movements, manage suppliers, process sales, and analyze trends.

**Key Metrics**:
- Supports multi-user concurrent access with role-based authorization
- Real-time stock tracking with automated low-stock alerts
- Transactional sales processing with auto stock deduction
- Performance-optimized database with strategic indexing

---

## Architecture

The system follows a layered MVC architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         REST API Layer                  │
│    (Spring Boot Controllers)            │
├─────────────────────────────────────────┤
│         Service Layer                   │
│   (Business Logic & Transactions)       │
├─────────────────────────────────────────┤
│         DAO/Repository Layer            │
│  (Data Access & Persistence)            │
├─────────────────────────────────────────┤
│         MySQL Database                  │
│   (Normalized Schema with Indexes)      │
└─────────────────────────────────────────┘
```

**Design Principles**:
- **Separation of Concerns**: Clean division between presentation, business logic, and data layers
- **Transaction Safety**: ACID-compliant sales processing
- **Exception Handling**: Structured exception propagation with meaningful error messages
- **Security**: PBKDF2 password hashing with legacy plaintext compatibility
- **Performance**: SQL indexes on frequently queried columns for report operations

---

## Key Features

### Core Inventory Management
- ✅ Product CRUD operations with inventory levels
- ✅ Real-time stock tracking and movement history
- ✅ Multi-warehouse support for distributed operations
- ✅ Automatic low-stock and out-of-stock notifications
- ✅ Supplier management and product-supplier linking
- ✅ Product search and filtering with advanced criteria

### Sales & Transactions
- ✅ Invoice creation with multiple line items
- ✅ Automatic stock deduction on sale completion
- ✅ Sales history and transaction audit trail
- ✅ Transactional processing ensuring data consistency
- ✅ Sales performance metrics per product/category

### Analytics & Reporting
- ✅ Sales reports by time period and category
- ✅ Most sold products ranking
- ✅ Low stock and stockout alerts
- ✅ Inventory movement analytics
- ✅ Demand trend analysis and predictions
- ✅ Reorder recommendations based on historical data

### Advanced Features
- ✅ Barcode simulation and quick lookup
- ✅ PDF and CSV export capabilities
- ✅ Dark mode support for extended work sessions
- ✅ Multi-user role-based access control
- ✅ Admin/Staff authentication with session management
- ✅ Responsive UI with Swing GUI framework

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 21 LTS |
| **Framework** | Spring Boot | 3.2+ |
| **ORM** | JPA/Hibernate | Standard |
| **Database** | MySQL | 8.0+ |
| **Access Method** | JDBC | 4.3+ |
| **Build Tool** | Maven | 3.9+ |
| **GUI** | Java Swing | JDK 21 |
| **Testing** | JUnit 5, Mockito | Latest |

---

## Prerequisites

### System Requirements
- **Operating System**: Windows, macOS, or Linux
- **RAM**: Minimum 4GB (8GB recommended)
- **Disk Space**: 2GB free space for application and dependencies

### Required Software
- **Java Development Kit (JDK)**: Version 21 or later
  - [Download OpenJDK 21](https://openjdk.org/projects/jdk/21/)
  - Verify: `java -version`
- **Apache Maven**: Version 3.8+ or later
  - [Download Maven](https://maven.apache.org/download.cgi)
  - Verify: `mvn -v`
- **MySQL Server**: Version 8.0 or later
  - [Download MySQL](https://dev.mysql.com/downloads/mysql/)
  - Verify: `mysql --version`

### Verification Script
```bash
# Verify all prerequisites
java -version          # Should show Java 21+
mvn -v                # Should show Maven 3.8+
mysql --version       # Should show MySQL 8.0+
```

---

## Setup & Installation

### Step 1: Clone Repository
```bash
git clone https://github.com/yourusername/miniproject6thsem.git
cd miniproject6thsem/inventory-management-system
```

### Step 2: Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Execute schema creation
SOURCE sql/inventory_schema.sql;
EXIT;

# Verify database created
mysql -u root -p -e "SHOW DATABASES LIKE 'inventory%';"
```

### Step 3: Environment Configuration
```bash
# Copy environment template
cp ../.env.template .env

# Edit configuration file with your values
# On macOS/Linux:
nano .env

# Required environment variables:
export DB_URL=jdbc:mysql://localhost:3306/inventory_db
export DB_USER=root
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your-32-character-secret-key-min-32
```

### Step 4: Maven Build
```bash
# Clean and build
mvn clean install

# Build with tests
mvn clean install

# Build skipping tests (faster, for initial setup)
mvn clean install -DskipTests
```

---

## Database Schema

The system uses a normalized relational schema optimized for inventory operations.

### Key Tables
- **Users**: Authentication and role management (admin/staff)
- **Products**: Product catalog with UOM and category
- **Stock**: Inventory levels per warehouse
- **Transactions**: Complete audit trail of all stock movements
- **Sales/SalesItems**: Invoice records and line items
- **Suppliers**: Supplier information and relationships
- **Categories**: Product classification
- **Warehouses**: Physical location management

### Schema Location
```
sql/inventory_schema.sql        # Full schema with indexes
src/main/resources/schema.sql   # DDL statements
```

For detailed schema documentation, refer to [TECHNICAL_SPEC.md](../../docs/TECHNICAL_SPEC.md)

---

## Configuration

### Application Properties
Configuration is managed through `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: inventory-management-system
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/inventory_db}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: update
```

### Profile-Specific Configuration
- `application-dev.yml`: Local development environment
- `application-aws.yml`: AWS production environment
- `application-test.yml`: Test environment with H2 database

---

## Building & Running

### Development Mode
```bash
# Run with SpringBoot Maven plugin
mvn spring-boot:run

# Application starts on http://localhost:8080
# API base path: http://localhost:8080/api/
```

### Launch GUI Application
```bash
# Run the Swing GUI login form
mvn -DskipTests exec:java -Dexec.mainClass=com.inventory.view.LoginFrame

# Default credentials:
# Username: admin
# Password: admin123
```

### Production Build
```bash
# Create production JAR
mvn clean package -DskipTests

# Output: target/inventory-management-system-1.0.0.jar
# Run: java -jar target/inventory-management-system-1.0.0.jar
```

---

## Testing

### Unit Test Coverage
The system includes comprehensive unit tests for:
- Authentication and password validation
- Product entity validation
- Stock level validation and calculations
- Smart analytics and prediction algorithms
- Exception handling and error propagation

### Run Tests
```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run specific test method
mvn test -Dtest=ProductServiceTest#testCreateProduct
```

### Integration Tests
- DAO CRUD operations validation
- Database transaction rollback scenarios
- Supplier-Product relationship integrity
- Sales processing with stock updates

### End-to-End Workflow Test
Complete workflow validation:
1. Add supplier
2. Add product
3. Create sale transaction
4. Verify stock deduction
5. Validate report generation

---

## API Endpoints

### Authentication
```
POST   /api/auth/login              # User login
POST   /api/auth/logout             # User logout
GET    /api/auth/validate           # Token validation
```

### Products
```
GET    /api/products                # List all products
POST   /api/products                # Create new product
GET    /api/products/{id}           # Get product details
PUT    /api/products/{id}           # Update product
DELETE /api/products/{id}           # Delete product
GET    /api/products/search?q=...   # Search products
```

### Inventory
```
GET    /api/inventory               # Get inventory levels
GET    /api/inventory/{productId}   # Get specific inventory
POST   /api/inventory/adjust        # Adjust stock levels
GET    /api/inventory/low           # Get low-stock items
```

### Sales
```
GET    /api/sales                   # List all sales
POST   /api/sales                   # Create sales invoice
GET    /api/sales/{id}              # Get sales details
GET    /api/sales/report/period     # Sales by period
```

### Reporting
```
GET    /api/reports/sales           # Sales report
GET    /api/reports/popular         # Most sold products
GET    /api/reports/trends          # Demand trends
GET    /api/reports/export?format=pdf  # Export report
```

For complete API documentation, see [COMPLETE_API_REFERENCE.md](../../COMPLETE_API_REFERENCE.md)

---

## Advanced Features

### Security
- **Password Hashing**: PBKDF2 with salt-based hashing
- **Legacy Support**: Plaintext password compatibility for migration
- **Role-Based Access**: Admin and Staff roles with granular permissions
- **Session Management**: Secure token-based sessions

### Performance Optimization
- **Database Indexes**: Strategic indexing on report and lookup columns
- **Connection Pooling**: Optimized MySQL connection management
- **Query Optimization**: Prepared statements preventing SQL injection
- **Caching**: Computed results cached where appropriate

### Data Integrity
- **Transactional Processing**: ACID-compliant sale operations
- **Foreign Key Constraints**: Referential integrity enforcement
- **Audit Trail**: Complete history of all modifications
- **Error Recovery**: Rollback mechanisms for failed transactions

---

## Production Deployment

### Pre-deployment Checklist
- [ ] Database created and tested
- [ ] Environment variables configured
- [ ] Application.yml updated with production values
- [ ] SSL/TLS certificates in place
- [ ] Backup strategy implemented
- [ ] Monitoring and alerting configured

### Deployment Steps
```bash
# Build production JAR
mvn clean package -DskipTests -P production

# Deploy to server
scp target/inventory-management-system-1.0.0.jar user@server:/opt/app/

# Start application
ssh user@server 'cd /opt/app && java -jar inventory-management-system-1.0.0.jar'
```

For AWS deployment, see [AWS_DEPLOYMENT_GUIDE.md](../../AWS_DEPLOYMENT_GUIDE.md)

---

## Future Roadmap

### Planned Enhancements
- **Mobile App**: Native iOS/Android inventory management
- **AI Analytics**: Machine learning-based demand forecasting
- **POS Integration**: Point-of-sale system connectivity
- **E-commerce Integration**: Online store integration
- **Advanced Procurement**: Automated purchase order generation
- **Multi-tenant SaaS**: Platform for multiple business operations

### Technology Upgrades
- Microservices architecture migration
- Event-driven architecture for scalability
- GraphQL API layer
- Real-time data synchronization with WebSockets

---

## Support & Contribution

### Getting Help
- Review [DEVELOPMENT_GUIDE.md](../../DEVELOPMENT_GUIDE.md) for feature development
- Check [API_TESTING_GUIDE.md](../../API_TESTING_GUIDE.md) for API testing procedures
- See [TECHNICAL_SPEC.md](../../docs/TECHNICAL_SPEC.md) for architecture details

### Development Setup
For local development with feature extensions, follow [DEVELOPMENT_GUIDE.md](../../DEVELOPMENT_GUIDE.md)

---

**Project Status**: Production Ready v1.0.0  
**Java Version**: 21 LTS  
**Last Updated**: April 2026  
**Maintainer**: Development Team  
**License**: Proprietary
