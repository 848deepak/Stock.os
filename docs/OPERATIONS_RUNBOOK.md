# Operations Runbook

## Purpose

Provide a repeatable process for safe deployment and verification in AWS.

## Backend Deployment

1. Build backend jar locally
2. Upload jar to EC2 host
3. Replace /opt/inventory-app/app.jar
4. Restart inventory-app service
5. Validate service status and auth health endpoint

## Frontend Deployment

1. Build frontend dist
2. Sync dist to S3 frontend path
3. Upload index.html with no-cache headers
4. Create CloudFront invalidation

## Live Verification

1. GET /api/auth/health
2. Login with admin account
3. Check dashboard stats endpoint
4. Validate products, warehouses, categories, and transactions endpoints

## Security Hygiene

1. Use temporary SSH ingress only when required
2. Revoke temporary security group rules immediately after deployment
3. Prefer least-privilege IAM for runtime operations

## Incident Response Basics

1. Capture service logs from systemd journal
2. Roll back backend jar if startup fails
3. Revalidate health and auth endpoints
4. Re-issue CloudFront invalidation for stale frontend issues
