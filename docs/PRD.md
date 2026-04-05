# Product Requirements Document (PRD)

## 1. Objective

Deliver a cloud-hosted inventory management product that enables secure operations, accurate stock tracking, and actionable dashboard insights.

## 2. Scope

### In Scope

1. Authentication and role management
2. Product and category management
3. Warehouse management
4. Inventory transactions (stock-in, stock-out, adjustment)
5. Dashboard analytics and feed
6. Barcode generation and scan lookup
7. CloudFront + EC2 production deployment

### Out of Scope

1. Multi-tenant billing
2. Vendor procurement automation
3. Native mobile applications

## 3. User Stories

1. As an admin, I can manage inventory master data and users.
2. As a manager, I can track stock movement and act on low stock.
3. As a staff member, I can execute inventory operations within role permissions.
4. As a teacher/reviewer, I can use a dedicated demo account to validate workflows.

## 4. Functional Requirements

1. User login returns JWT and role context.
2. Product APIs support create, read, update, delete, and search.
3. Transaction APIs record movement with audit metadata.
4. Dashboard APIs provide product count, low-stock count, and recent transaction signals.
5. Barcode APIs support scan and image/generation workflows.
6. UI pages must use real API data and avoid global hardcoded mock datasets.

## 5. Non-Functional Requirements

1. Secure auth and role authorization checks on protected endpoints.
2. Production deployment through AWS with CDN in front of API and frontend.
3. Recoverable backend deployment with service restart and health validation.
4. Basic observability through service status and endpoint checks.

## 6. Acceptance Criteria

1. Admin account login works with requested credentials.
2. Teacher demo account can log in successfully.
3. Dashboard charts render with production transaction data.
4. Warehouses, categories, and products APIs return production records.
5. Scanner lookup resolves seeded SKUs via API.
6. New users do not receive forced mock data views.

## 7. Risks and Mitigations

1. IAM gaps: use least-privilege policies and explicit deployment checks.
2. Cache staleness: enforce CloudFront invalidation after frontend deploy.
3. Data drift: maintain seed scripts and document seed intent.
