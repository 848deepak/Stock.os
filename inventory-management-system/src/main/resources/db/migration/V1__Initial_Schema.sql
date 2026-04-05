-- Create ENUM types
CREATE TYPE user_role AS ENUM ('ADMIN', 'MANAGER', 'STAFF');
CREATE TYPE transaction_type AS ENUM ('INFLOW', 'OUTFLOW', 'ADJUSTMENT', 'TRANSFER');

-- Create tables
CREATE TABLE IF NOT EXISTS roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role_id INTEGER REFERENCES roles(role_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id SERIAL PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL UNIQUE,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(15),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    country VARCHAR(50),
    postal_code VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    product_id SERIAL PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id INTEGER REFERENCES categories(category_id),
    supplier_id INTEGER REFERENCES suppliers(supplier_id),
    unit_price DECIMAL(10, 2) NOT NULL,
    reorder_level INTEGER DEFAULT 10,
    reorder_quantity INTEGER DEFAULT 50,
    barcode VARCHAR(50) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS warehouses (
    warehouse_id SERIAL PRIMARY KEY,
    warehouse_name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL,
    manager_id INTEGER REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS warehouse_inventory (
    warehouse_inventory_id SERIAL PRIMARY KEY,
    warehouse_id INTEGER NOT NULL REFERENCES warehouses(warehouse_id),
    product_id INTEGER NOT NULL REFERENCES products(product_id),
    quantity_on_hand INTEGER DEFAULT 0,
    quantity_reserved INTEGER DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(warehouse_id, product_id)
);

CREATE TABLE IF NOT EXISTS inventory_transactions (
    transaction_id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(product_id),
    warehouse_id INTEGER REFERENCES warehouses(warehouse_id),
    transaction_type transaction_type NOT NULL,
    quantity INTEGER NOT NULL,
    user_id INTEGER REFERENCES users(user_id),
    remarks TEXT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    order_id SERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id INTEGER REFERENCES users(user_id),
    warehouse_id INTEGER REFERENCES warehouses(warehouse_id),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expected_delivery_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING',
    total_amount DECIMAL(12, 2),
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS warehouse_transfers (
    transfer_id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(product_id),
    from_warehouse_id INTEGER NOT NULL REFERENCES warehouses(warehouse_id),
    to_warehouse_id INTEGER NOT NULL REFERENCES warehouses(warehouse_id),
    quantity INTEGER NOT NULL,
    transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    user_id INTEGER REFERENCES users(user_id),
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stock_alerts (
    alert_id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(product_id),
    warehouse_id INTEGER REFERENCES warehouses(warehouse_id),
    alert_type VARCHAR(50) NOT NULL,
    current_stock INTEGER,
    threshold INTEGER,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    log_id SERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id INTEGER NOT NULL,
    action VARCHAR(50) NOT NULL,
    user_id INTEGER REFERENCES users(user_id),
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_products_code ON products(product_code);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_supplier ON products(supplier_id);
CREATE INDEX idx_inventory_product ON inventory_transactions(product_id);
CREATE INDEX idx_inventory_warehouse ON inventory_transactions(warehouse_id);
CREATE INDEX idx_inventory_date ON inventory_transactions(transaction_date);
CREATE INDEX idx_inventory_type ON inventory_transactions(transaction_type);
CREATE INDEX idx_warehouse_inv_product ON warehouse_inventory(product_id);
CREATE INDEX idx_warehouse_inv_warehouse ON warehouse_inventory(warehouse_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_warehouse ON orders(warehouse_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_transfers_from ON warehouse_transfers(from_warehouse_id);
CREATE INDEX idx_transfers_to ON warehouse_transfers(to_warehouse_id);
CREATE INDEX idx_transfers_product ON warehouse_transfers(product_id);
CREATE INDEX idx_stock_alerts_product ON stock_alerts(product_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_date ON audit_logs(created_at);

-- Insert default roles
INSERT INTO roles (role_name) VALUES ('ADMIN'), ('MANAGER'), ('STAFF') ON CONFLICT DO NOTHING;

-- Insert default admin user (password: Admin@123 - must be hashed in production)
-- Hash this password using BCrypt before inserting in production
-- INSERT INTO users (username, password, email, role_id) 
-- VALUES ('admin', '$2a$10$...', 'admin@inventory.com', 1);

COMMIT;
