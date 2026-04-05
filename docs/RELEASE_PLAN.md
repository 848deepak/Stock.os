# Release Plan

## Release Name

Stock.os Production Baseline R1

## Release Goals

1. Stabilize cloud-hosted inventory workflows
2. Ensure API-driven dashboards and scanner behavior
3. Provide demo-ready account and data setup

## Milestones

1. M1: CloudFront API origin and behavior routing completed
2. M2: Backend deployment and auth account fixes completed
3. M3: Frontend production build and CDN cache refresh completed
4. M4: Production seed dataset and demo account validated

## Validation Checklist

1. Auth health endpoint reachable
2. Admin login with admin@stock.os credentials works
3. Teacher demo account login works
4. Products, warehouses, categories, and transactions endpoints return expected data
5. Dashboard stats reflect seeded transactions

## Rollback Strategy

1. Backend: restore previous app.jar backup and restart inventory-app service
2. Frontend: republish previous dist bundle to S3 and invalidate CloudFront
3. Data: keep seed operations append-only and avoid destructive resets in production

## Post-Release Tasks

1. Review dashboard behavior by role
2. Collect usage observations for next milestone
3. Prioritize enhancements for reporting and operations automation
