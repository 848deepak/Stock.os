# Seeding And Demo Accounts

## Intent

Provide realistic baseline data for evaluation while keeping production behavior API-driven and role-safe.

## Seeded Production Data

1. Categories: Electronics, Consumables, Packing, Retail
2. Warehouses: Main Warehouse, Secondary Warehouse
3. Products: LIVE-SKU-1001 to LIVE-SKU-1004
4. Transactions: stock-in, stock-out, and adjustment entries across products

## Demo Accounts

1. Admin
- Username: admin
- Email: admin@stock.os
- Password: admin1234@

2. Teacher Demo
- Username: teacherdemo
- Email: teacherdemo@stock.os
- Password: Demo1234@
- Role: STAFF

## Policy

1. Demo account exists for presentation only.
2. New non-admin users should not receive forced mock datasets.
3. Dashboard and analytics should prioritize live API data.

## Verification Commands (via API)

1. Login with admin and teacher demo accounts
2. Check dashboard stats endpoint for non-zero transaction metrics
3. Fetch transactions history and products list
