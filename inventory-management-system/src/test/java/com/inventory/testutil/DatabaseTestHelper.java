package com.inventory.testutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseTestHelper {
    private static final String H2_URL = "jdbc:h2:mem:inventory_test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";

    private DatabaseTestHelper() {
    }

    public static void configureH2() {
        System.setProperty("inventory.db.url", H2_URL);
        System.setProperty("inventory.db.user", "sa");
        System.setProperty("inventory.db.password", "");
    }

    public static void createSchema() {
        try (Connection conn = DriverManager.getConnection(H2_URL, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS sales_items");
            stmt.execute("DROP TABLE IF EXISTS sales");
            stmt.execute("DROP TABLE IF EXISTS products");
            stmt.execute("DROP TABLE IF EXISTS suppliers");
            stmt.execute("DROP TABLE IF EXISTS users");

            stmt.execute("CREATE TABLE users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, role VARCHAR(20) NOT NULL)");
            stmt.execute("CREATE TABLE suppliers (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, contact VARCHAR(50), address VARCHAR(255))");
            stmt.execute("CREATE TABLE products (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, category VARCHAR(50), price DECIMAL(10,2) NOT NULL, quantity INT NOT NULL, supplier_id INT, expiry_date VARCHAR(20), FOREIGN KEY (supplier_id) REFERENCES suppliers(id))");
            stmt.execute("CREATE TABLE sales (id INT AUTO_INCREMENT PRIMARY KEY, date VARCHAR(32) NOT NULL, total_amount DECIMAL(10,2) NOT NULL, user_id INT, FOREIGN KEY (user_id) REFERENCES users(id))");
            stmt.execute("CREATE TABLE sales_items (id INT AUTO_INCREMENT PRIMARY KEY, sale_id INT, product_id INT, quantity INT NOT NULL, price DECIMAL(10,2) NOT NULL, FOREIGN KEY (sale_id) REFERENCES sales(id), FOREIGN KEY (product_id) REFERENCES products(id))");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize test schema", e);
        }
    }

    public static void seedBaseData() {
        try (Connection conn = DriverManager.getConnection(H2_URL, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'Admin')");
            stmt.execute("INSERT INTO suppliers (name, contact, address) VALUES ('Default Supplier', '99999', 'Main Street')");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to seed test data", e);
        }
    }
}
