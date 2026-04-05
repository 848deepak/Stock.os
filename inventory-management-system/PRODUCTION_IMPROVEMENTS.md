# Production Improvements

## Implemented
- SQL aggregation moved from Java loops to database queries.
- Date range filtering moved to SQL in report flow.
- Sales flow is now transactional for atomicity.
- Added indexing guidance directly in schema script.

## Recommended Next Step: Connection Pooling
Use HikariCP for production to reduce connection setup overhead.

Suggested settings:
- maximumPoolSize: 20
- minimumIdle: 5
- connectionTimeout: 30000
- idleTimeout: 600000
- maxLifetime: 1800000

## Recommended Next Step: Caching
Add Caffeine cache for read-heavy lookup endpoints:
- Product by ID cache (TTL 5 minutes)
- Supplier by ID cache (TTL 10 minutes)

Cache invalidation points:
- Invalidate Product cache on add/update/delete and on stock changes.
- Invalidate Supplier cache on add/update/delete.

## Observability
- Keep java.util.logging enabled at INFO in production.
- Route logs to centralized logging (ELK/OpenSearch/Loki) if deployed as service.
