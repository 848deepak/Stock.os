# Technical Specification

## System Architecture

1. Frontend: React + Vite SPA
2. Backend: Spring Boot 3.3 REST APIs
3. Database: PostgreSQL (AWS profile)
4. Delivery: S3 website origin + CloudFront + EC2 backend

## Core API Domains

1. auth
2. products
3. categories
4. warehouses
5. transactions
6. dashboard
7. barcodes

## Security

1. JWT-based authentication
2. Role-based authorization with ADMIN, MANAGER, STAFF
3. CORS enabled through app config

## Production Routing

1. CloudFront distribution forwards /api/* to EC2 origin
2. Frontend static assets served from S3 origin

## Data Seeding Notes

1. Startup initializer ensures role setup
2. Admin credentials are explicitly set to required product demo values
3. Production seed records created via authenticated live APIs

## Build Commands

Backend:

mvn clean package -DskipTests

Frontend:

npm run build

## Deployment Steps

1. Upload frontend dist to S3 origin path
2. Invalidate CloudFront paths
3. Copy backend jar to EC2
4. Restart inventory-app service
5. Validate health and protected endpoints
