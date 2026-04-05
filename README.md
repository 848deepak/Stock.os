# Stock.os

Stock.os is an inventory command center for small and mid-size operations teams.

It helps teams control stock movement, avoid stock-outs, and act faster with one system for products, transactions, dashboards, and barcode workflows.

## Product Summary

Stock.os is designed for three business outcomes:

1. Inventory confidence: accurate product and movement visibility.
2. Operational speed: fast stock-in, stock-out, adjustments, and barcode lookup.
3. Decision clarity: role-based dashboards that highlight what needs action.

## Who This Is For

1. Admin: configures system, manages data, and oversees all operations.
2. Manager: drives day-to-day inventory performance and replenishment.
3. Staff: executes stock operations with secure scoped access.

## Current Live Environment

1. Frontend URL: https://d15os9kcan23ap.cloudfront.net
2. API base: https://d15os9kcan23ap.cloudfront.net/api
3. CloudFront distribution: E1YKXYS6CAYRVP
4. Backend host: EC2 in ap-south-1 (routed via CloudFront /api/* behavior)

## Demo Accounts

Use these only for demonstration and validation.

1. Admin demo account
- Username: admin
- Email: admin@stock.os
- Password: admin1234@

2. Teacher demo account
- Username: teacherdemo
- Email: teacherdemo@stock.os
- Password: Demo1234@
- Role: STAFF

## What Is Seeded In Production

Production now includes live operational records to power charts for all roles.

1. Categories: Electronics, Consumables, Packing, Retail
2. Warehouses: Main Warehouse (Mumbai), Secondary Warehouse (Bengaluru)
3. Products: 4 live SKUs (LIVE-SKU-1001 to LIVE-SKU-1004)
4. Transactions: stock-in, stock-out, and adjustment records across products

## Feature Scope

1. Authentication and role-based authorization (JWT + RBAC)
2. Product lifecycle management
3. Inventory transaction tracking
4. Warehouse views
5. Dashboard analytics
6. Barcode generation and scan lookup
7. Cloud deployment-ready frontend and backend

## Product Behavior Notes

1. Dashboards are API-driven.
2. New users do not see hardcoded mock data.
3. Admin has controlled fallback behavior only when datasets are empty.

## Repository Structure

1. frontend: React app
2. inventory-management-system: Spring Boot API
3. AWS and delivery docs at repository root

## Quick Start For Local Development

### Backend

1. Go to inventory-management-system
2. Build and run:

mvn clean package
mvn spring-boot:run

### Frontend

1. Go to frontend
2. Install and run:

npm install
npm run dev

## Deployment Overview

1. Frontend artifacts are published to S3 and served by CloudFront.
2. Backend jar is deployed to EC2 and managed via systemd (inventory-app).
3. CloudFront route /api/* forwards API traffic to EC2 origin.

## Documentation Index

Product-focused docs:

1. docs/PRODUCT_BRIEF.md
2. docs/PRD.md
3. docs/USER_PERSONAS.md
4. docs/RELEASE_PLAN.md

Engineering and operations docs:

1. docs/TECHNICAL_SPEC.md
2. docs/OPERATIONS_RUNBOOK.md
3. docs/SEEDING_AND_DEMO_ACCOUNTS.md

Legacy and implementation artifacts are retained for traceability.

## Non-Goals (Current Phase)

1. Multi-tenant isolation
2. Full procurement workflow automation
3. Native mobile applications

## License

This repository is currently maintained as a project delivery baseline by 848deepak.
