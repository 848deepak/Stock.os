# Stock.os - Inventory Command Center

![Status](https://img.shields.io/badge/status-production-green) ![License](https://img.shields.io/badge/license-proprietary-blue)

Stock.os is an enterprise-grade inventory command center for small and mid-size operations teams. It enables teams to control stock movement, avoid stock-outs, and act faster with one integrated system for products, transactions, dashboards, and barcode workflows.

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [User Roles](#user-roles)
- [Live Environment](#live-environment)
- [Production Seed Data](#production-seed-data)
- [Quick Start](#quick-start)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Deployment Architecture](#deployment-architecture)
- [Repository Structure](#repository-structure)
- [Documentation](#documentation)
- [Non-Goals](#non-goals)
- [Support & Contact](#support--contact)

---

## Overview

Stock.os is designed to deliver three core business outcomes:

1. **Inventory Confidence**: Accurate product and movement visibility across all warehouses
2. **Operational Speed**: Fast stock-in, stock-out, adjustments, and barcode lookup operations
3. **Decision Clarity**: Role-based dashboards that highlight what needs immediate action

---

## Key Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control (RBAC)
- **Product Lifecycle Management**: Full CRUD operations for product catalog and suppliers
- **Inventory Tracking**: Real-time transaction tracking for all stock movements
- **Multi-Warehouse Support**: Manage inventory across multiple warehouse locations
- **Analytics Dashboards**: Interactive dashboards with business intelligence
- **Barcode Integration**: Generate barcodes and perform quick scan lookups
- **Cloud-Native Architecture**: Fully containerized and cloud deployment-ready
- **Multi-User Support**: Concurrent user support with role-based permissions

---

## User Roles

The system supports three primary user roles:

| Role | Responsibilities |
|------|------------------|
| **Admin** | System configuration, data management, operations oversight, user management |
| **Manager** | Day-to-day inventory performance monitoring, replenishment decisions, reporting |
| **Staff** | Stock operations execution, data entry, barcode scanning with scoped access |

---

## Live Environment

The production environment is live and fully operational:

| Component | Details |
|-----------|---------|
| Frontend URL | https://d15os9kcan23ap.cloudfront.net |
| API Base URL | https://d15os9kcan23ap.cloudfront.net/api |
| CDN Distribution | CloudFront (ID: E1YKXYS6CAYRVP) |
| Backend Infrastructure | EC2 in AWS ap-south-1 region (routed via CloudFront /api/* behavior) |

---

## Production Seed Data

Production environment includes comprehensive operational records for dashboard analytics:

| Category | Details |
|----------|---------|
| **Categories** | Electronics, Consumables, Packing, Retail |
| **Warehouses** | Main Warehouse (Mumbai), Secondary Warehouse (Bengaluru) |
| **Live SKUs** | 4 operational products (LIVE-SKU-1001 to LIVE-SKU-1004) |
| **Transactions** | Stock-in, stock-out, and adjustment records across all products |

**Important Notes**:
- Dashboards are entirely API-driven with no hardcoded mock data for new users
- Admin users have controlled fallback behavior only when datasets are empty
- All seed data supports production-level analytics and reporting

---

## Quick Start

### Prerequisites

- **Backend**: Java 21+, Maven 3.8+, MySQL 8+
- **Frontend**: Node.js 18+, npm 8+
- **All Environments**: Git, SSH client

### Backend Setup

```bash
# Navigate to backend directory
cd inventory-management-system

# Build the application
mvn clean package

# Run the Spring Boot application
mvn spring-boot:run

# Backend will be available at http://localhost:8080
```

### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Frontend will be available at http://localhost:3000
```

For detailed setup instructions, see [SETUP_GUIDE.md](SETUP_GUIDE.md)

---

## Deployment Architecture

The production deployment follows a three-tier architecture:

```
┌─────────────────────────────────────┐
│     CloudFront CDN                  │
│   (E1YKXYS6CAYRVP)                 │
├─────────────────────────────────────┤
│ / → S3 (Frontend Assets)            │
│ /api/* → EC2 Backend (ap-south-1)  │
└─────────────────────────────────────┘
        ↓
┌─────────────────────────────────────┐
│      EC2 Instance (ap-south-1)      │
│   ├─ Spring Boot API (systemd)      │
│   └─ RDS MySQL Database             │
└─────────────────────────────────────┘
```

**Deployment Components**:
- Frontend artifacts published to S3 and served by CloudFront
- Backend JAR deployed to EC2 and managed via systemd service (inventory-app)
- CloudFront intelligently routes API requests to EC2 backend
- All communication is encrypted in transit

---

## Repository Structure

```
miniproject6thsem/
├── README.md                              # This file
├── SETUP_GUIDE.md                         # Local development setup
├── DEVELOPMENT_GUIDE.md                   # Feature development guide
├── AWS_DEPLOYMENT_GUIDE.md                # AWS deployment instructions
├── API_TESTING_GUIDE.md                   # API testing procedures
├── VERIFICATION_CHECKLIST.md              # Production verification
├── .env.template                          # Environment variables template
│
├── frontend/                              # React Frontend Application
│   ├── src/
│   │   ├── components/                    # Reusable UI components
│   │   ├── pages/                         # Page components
│   │   ├── services/                      # API client service
│   │   ├── context/                       # React Context (auth, etc.)
│   │   ├── App.jsx
│   │   └── index.css                      # Tailwind CSS styles
│   ├── package.json
│   └── vite.config.js                     # Vite configuration
│
├── inventory-management-system/           # Spring Boot Backend
│   ├── src/main/java/com/inventory/
│   │   ├── controller/                    # REST API endpoints
│   │   ├── service/                       # Business logic
│   │   ├── model/                         # JPA entities
│   │   ├── dao/                           # Data access layer
│   │   ├── exception/                     # Custom exceptions
│   │   └── util/                          # Utility classes
│   ├── src/main/resources/
│   │   ├── application.yml                # Spring configuration
│   │   ├── application-aws.yml            # AWS profile
│   │   └── schema.sql                     # Database schema
│   ├── sql/inventory_schema.sql           # Full schema definition
│   ├── pom.xml                            # Maven configuration
│   └── Dockerfile                         # Container configuration
│
├── docs/                                  # Documentation
│   ├── PRD.md                             # Product requirement document
│   ├── PRODUCT_BRIEF.md                   # Executive summary
│   ├── TECHNICAL_SPEC.md                  # Technical architecture
│   ├── USER_PERSONAS.md                   # User research
│   ├── RELEASE_PLAN.md                    # Release roadmap
│   ├── OPERATIONS_RUNBOOK.md              # Operations procedures
│   └── SEEDING_AND_DEMO_ACCOUNTS.md       # Demo account setup
│
├── aws-setup.sh                           # AWS infrastructure provisioning
└── docker-compose.yml                     # Local Docker environment
```

---

## Documentation

Complete documentation is organized by audience and purpose:

### Product & Business Documentation
- [docs/PRODUCT_BRIEF.md](docs/PRODUCT_BRIEF.md) - Executive product overview
- [docs/PRD.md](docs/PRD.md) - Product requirements and specifications
- [docs/USER_PERSONAS.md](docs/USER_PERSONAS.md) - User research and personas
- [docs/RELEASE_PLAN.md](docs/RELEASE_PLAN.md) - Release roadmap and timeline

### Technical & Engineering Documentation
- [docs/TECHNICAL_SPEC.md](docs/TECHNICAL_SPEC.md) - System architecture and design
- [docs/OPERATIONS_RUNBOOK.md](docs/OPERATIONS_RUNBOOK.md) - Operations and monitoring
- [docs/SEEDING_AND_DEMO_ACCOUNTS.md](docs/SEEDING_AND_DEMO_ACCOUNTS.md) - Demo data setup

### Deployment & Development Guides
- [SETUP_GUIDE.md](SETUP_GUIDE.md) - Local development environment setup
- [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - Feature development guidelines
- [AWS_DEPLOYMENT_GUIDE.md](AWS_DEPLOYMENT_GUIDE.md) - Production AWS deployment
- [API_TESTING_GUIDE.md](API_TESTING_GUIDE.md) - API testing procedures

---

## Non-Goals (Current Phase)

The following are explicitly out of scope for this release:

- Multi-tenant isolation and SaaS conversion
- Full procurement workflow automation
- Native mobile applications (web access supported)
- Advanced supply chain integration
- Predictive analytics and ML features

---

## Support & Contact

For issues, questions, or contributions, please refer to the appropriate guide:

- **Development**: See [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)
- **Deployment Issues**: See [AWS_DEPLOYMENT_GUIDE.md](AWS_DEPLOYMENT_GUIDE.md)
- **Operations**: See [docs/OPERATIONS_RUNBOOK.md](docs/OPERATIONS_RUNBOOK.md)
- **Testing**: See [API_TESTING_GUIDE.md](API_TESTING_GUIDE.md)

---

**Repository Status**: Production - v1.0.0
**Last Updated**: April 2026
**License**: Proprietary - This repository is maintained as a product baseline.
