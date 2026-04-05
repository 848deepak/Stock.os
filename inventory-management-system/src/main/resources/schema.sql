-- Create Database
CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

-- Create roles table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create categories table (new)
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create warehouses table (new)
CREATE TABLE warehouses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL,
    manager_id BIGINT,
    capacity INT DEFAULT 10000,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (manager_id) REFERENCES users(id),
    INDEX idx_name (name),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create suppliers table (new)
CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create products table (enhanced)
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    category_id BIGINT,
    supplier_id BIGINT,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    reorder_level INT DEFAULT 10,
    expiry_date DATETIME,
    barcode VARCHAR(255) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(50),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    INDEX idx_sku (sku),
    INDEX idx_category_id (category_id),
    INDEX idx_is_active (is_active),
    INDEX idx_barcode (barcode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create warehouse_inventory table (new - for multi-warehouse support)
CREATE TABLE warehouse_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    reorder_level INT DEFAULT 10,
    last_stocked_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_product_warehouse (product_id, warehouse_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create inventory_transactions table (enhanced)
CREATE TABLE inventory_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    type VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    reason TEXT,
    user_id BIGINT,
    reference_id VARCHAR(36),
    notes TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_product_id (product_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create orders table (new - for order simulation and automation)
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    quantity INT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    order_date DATETIME NOT NULL,
    expected_delivery DATETIME,
    actual_delivery DATETIME,
    notes TEXT,
    created_by BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_order_number (order_number),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create audit_logs table (new - for audit trail)
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    action VARCHAR(50),
    old_value TEXT,
    new_value TEXT,
    user_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_entity_type (entity_type),
    INDEX idx_entity_id (entity_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create stock_alerts table (new - for low stock alerts)
CREATE TABLE stock_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    alert_type VARCHAR(50),
    current_stock INT,
    reorder_level INT,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    resolved_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    INDEX idx_product_id (product_id),
    INDEX idx_is_resolved (is_resolved),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create warehouse_transfers table (new - for inter-warehouse transfers)
CREATE TABLE warehouse_transfers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    from_warehouse_id BIGINT NOT NULL,
    to_warehouse_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_by BIGINT,
    created_at DATETIME NOT NULL,
    completed_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (from_warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (to_warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default roles
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Administrator - Full system access'),
    ('MANAGER', 'Manager - Inventory management access'),
    ('STAFF', 'Staff - View-only access');

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, email, role_id, is_active, created_at, updated_at) VALUES
    ('admin', '$2a$10$slYQmyNdGzin7olVN2edi.Sg8DT0/LewKJfxWruWR2p.7MKm9HJGK', 'admin@inventory.com', 1, TRUE, NOW(), NOW());

-- Insert sample categories
INSERT INTO categories (name, description, is_active, created_at, updated_at) VALUES
    ('Electronics', 'Electronic devices and components', TRUE, NOW(), NOW()),
    ('Furniture', 'Office furniture and seating', TRUE, NOW(), NOW()),
    ('Accessories', 'Miscellaneous office accessories', TRUE, NOW(), NOW()),
    ('Supplies', 'Office supplies and consumables', TRUE, NOW(), NOW());

-- Insert sample warehouses
INSERT INTO warehouses (name, location, capacity, is_active, created_at, updated_at) VALUES
    ('Main Warehouse', 'New York, USA', 50000, TRUE, NOW(), NOW()),
    ('Secondary Warehouse', 'Los Angeles, USA', 30000, TRUE, NOW(), NOW()),
    ('Transit Hub', 'Chicago, USA', 20000, TRUE, NOW(), NOW());

-- Insert sample suppliers
INSERT INTO suppliers (name, email, phone, address, is_active, created_at, updated_at) VALUES
    ('Tech Distributors Inc', 'sales@techdist.com', '+1-800-123-4567', '123 Tech Ave, Silicon Valley, CA', TRUE, NOW(), NOW()),
    ('Furniture World', 'orders@furnitureworld.com', '+1-800-234-5678', '456 Furniture Rd, Charlotte, NC', TRUE, NOW(), NOW()),
    ('Office Plus', 'contact@officeplus.com', '+1-800-345-6789', '789 Supply Lane, Atlanta, GA', TRUE, NOW(), NOW());

-- Insert sample products (enhanced with category and supplier)
INSERT INTO products (name, sku, description, category_id, supplier_id, price, quantity, reorder_level, is_active, created_at, updated_at, created_by) VALUES
    ('Laptop', 'SKU001', 'Dell Inspiron 15', 1, 1, 45000.00, 50, 10, TRUE, NOW(), NOW(), 'admin'),
    ('Office Chair', 'SKU002', 'Ergonomic office chair', 2, 2, 8000.00, 20, 5, TRUE, NOW(), NOW(), 'admin'),
    ('Monitor', 'SKU003', '24-inch LCD Monitor', 1, 1, 15000.00, 15, 5, TRUE, NOW(), NOW(), 'admin'),
    ('Mouse', 'SKU004', 'Wireless mouse', 3, 1, 500.00, 100, 20, TRUE, NOW(), NOW(), 'admin'),
    ('Keyboard', 'SKU005', 'Mechanical keyboard', 3, 1, 3000.00, 30, 10, TRUE, NOW(), NOW(), 'admin');

-- Insert warehouse inventory mappings
INSERT INTO warehouse_inventory (product_id, warehouse_id, quantity, reorder_level, last_stocked_at, created_at, updated_at) VALUES
    (1, 1, 30, 10, NOW(), NOW(), NOW()),
    (1, 2, 15, 10, NOW(), NOW(), NOW()),
    (1, 3, 5, 10, NOW(), NOW(), NOW()),
    (2, 1, 15, 5, NOW(), NOW(), NOW()),
    (2, 2, 5, 5, NOW(), NOW(), NOW()),
    (3, 1, 10, 5, NOW(), NOW(), NOW()),
    (3, 2, 5, 5, NOW(), NOW(), NOW()),
    (4, 1, 60, 20, NOW(), NOW(), NOW()),
    (4, 2, 40, 20, NOW(), NOW(), NOW()),
    (5, 1, 20, 10, NOW(), NOW(), NOW()),
    (5, 2, 10, 10, NOW(), NOW(), NOW());
