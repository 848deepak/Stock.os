# Inventory Management System for Retail Businesses

## Project Overview
A professional, scalable inventory system for retail shops, built with Java (Core + Swing), MySQL, and JDBC. Features include authentication, product/stock/supplier/sales management, reports, notifications, smart analytics, barcode simulation, export, dark mode, and multi-user support.

## Architecture
- MVC pattern
- DAO and Service layers
- Swing GUI
- MySQL database

## Database Schema
See `sql/inventory_schema.sql` for full table definitions.

## Features
- Admin/Staff authentication
- Product CRUD, search, inventory view
- Real-time stock tracking, low stock alerts
- Supplier CRUD, product-supplier linking
- Sales/invoice creation, auto stock update, sales history
- Reports: sales, most sold, low stock
- Notifications: low/out-of-stock
- Smart features: inventory prediction, reorder suggestion, demand trends
- Advanced: barcode simulation, export to PDF/CSV, dark mode, multi-user

## SaaS Conversion & Monetization
- Move backend to cloud, web frontend (React/Spring Boot)
- Multi-tenant DB, subscription billing
- Target: retail shops, wholesalers
- Pricing: tiered subscription, free trial
- Deployment: desktop → web → cloud

## Setup
1. Install JDK 17+ and Maven 3.9+
2. Run MySQL schema from `sql/inventory_schema.sql`
3. Configure database credentials via environment variables:
	- `DB_URL=jdbc:mysql://localhost:3306/inventory_db`
	- `DB_USER=your_user`
	- `DB_PASSWORD=your_password`
4. Build the application:
	- `mvn clean compile`
5. Run tests:
	- `mvn test`
6. Launch GUI login frame:
	- `mvn -DskipTests exec:java -Dexec.mainClass=com.inventory.view.LoginFrame`

## Testing Coverage
- Unit tests for auth, product validation, stock validation, and smart analytics
- Integration tests for DAO CRUD operations and failure scenarios
- End-to-end workflow test: add supplier + product + sale + stock/report validation

## Production Notes
- Passwords support PBKDF2 hashes (`pbkdf2$salt$hash`) with legacy plaintext compatibility
- Sales creation is transactional (sale + sale items + stock deduction)
- DAO layer uses prepared statements and structured exception propagation
- Added SQL indexes for report and lookup performance

## Future Scope
- Mobile app
- AI-powered analytics
- Integration with POS, e-commerce

## Contact
For freelance, startup, or resume use.

---

**This project is ready for FAANG-level internships, freelancing, and MVP launches.**
