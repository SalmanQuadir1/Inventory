-- Insert default admin user (Password is 'admin' encrypted with BCrypt)
INSERT INTO users (username, password, name, email, role, enabled, created_at, created_by) 
VALUES ('admin', '$2a$10$l8wZgxscJ4nU/CYaamnjmOaNmh11uUGBaLdlpnTU98KjXNzI9mGJ6', 'System Administrator', 'admin@fghstore.com', 'ADMIN', true, NOW(), 'SYSTEM')
ON DUPLICATE KEY UPDATE password='$2a$10$l8wZgxscJ4nU/CYaamnjmOaNmh11uUGBaLdlpnTU98KjXNzI9mGJ6';

-- Insert default pharmacist
INSERT INTO users (username, password, name, email, role, enabled, created_at, created_by) 
VALUES ('pharma', '$2a$10$l8wZgxscJ4nU/CYaamnjmOaNmh11uUGBaLdlpnTU98KjXNzI9mGJ6', 'Head Manager', 'pharma@fghstore.com', 'PHARMACIST', true, NOW(), 'SYSTEM')
ON DUPLICATE KEY UPDATE password='$2a$10$l8wZgxscJ4nU/CYaamnjmOaNmh11uUGBaLdlpnTU98KjXNzI9mGJ6';

-- Insert default staff
INSERT INTO users (username, password, name, email, role, enabled, created_at, created_by) 
VALUES ('staff', '$2a$10$l8wZgxscJ4nU/CYaamnjmOaNmh11uUGBaLdlpnTU98KjXNzI9mGJ6', 'Sales Staff', 'staff@fghstore.com', 'STAFF', true, NOW(), 'SYSTEM')
ON DUPLICATE KEY UPDATE password='$2a$10$l8wZgxscJ4nU/CYaamnjmOaNmh11uUGBaLdlpnTU98KjXNzI9mGJ6';

-- Insert some dummy suppliers
INSERT INTO suppliers (id, supplier_name, contact_person, phone, email, address, created_at, created_by)
VALUES (1, 'Apex Solutions Ltd', 'Sarah Johnson', '9876543210', 'sarah@apexsolutions.com', '123 Business Ave, Mumbai', NOW(), 'SYSTEM')
ON DUPLICATE KEY UPDATE supplier_name='Apex Solutions Ltd';

INSERT INTO suppliers (id, supplier_name, contact_person, phone, email, address, created_at, created_by)
VALUES (2, 'Global Logistics', 'John Doe', '8765432109', 'john@globallogistics.com', '45 Logistics Blvd, Delhi', NOW(), 'SYSTEM')
ON DUPLICATE KEY UPDATE supplier_name='Global Logistics';

-- Insert some products
INSERT INTO products (id, product_name, short_description, manufacturer, category, batch_number, expiry_date, purchase_price, selling_price, quantity, reorder_level, supplier_id, created_at, created_by)
VALUES (1, 'Ergonomic Office Chair', 'High-back ergonomic chair', 'ComfortSeat', 'Furniture', 'BCH-2023-01', '2029-12-31', 1500.00, 2500.00, 50, 10, 1, NOW(), 'SYSTEM')
ON DUPLICATE KEY UPDATE product_name='Ergonomic Office Chair';

INSERT INTO products (id, product_name, short_description, manufacturer, category, batch_number, expiry_date, purchase_price, selling_price, quantity, reorder_level, supplier_id, created_at, created_by)
VALUES (2, 'Mechanical Keyboard', 'RGB Mechanical Keyboard', 'TechKeys', 'Electronics', 'BCH-2023-45', '2028-06-30', 500.00, 850.00, 100, 20, 2, NOW(), 'SYSTEM')
ON DUPLICATE KEY UPDATE product_name='Mechanical Keyboard';

INSERT INTO products (id, product_name, short_description, manufacturer, category, batch_number, expiry_date, purchase_price, selling_price, quantity, reorder_level, supplier_id, created_at, created_by)
VALUES (3, 'Wireless Mouse', 'Silent Wireless Mouse', 'TechKeys', 'Electronics', 'BCH-2023-09', '2027-11-01', 450.00, 600.00, 150, 20, 2, NOW(), 'SYSTEM')
ON DUPLICATE KEY UPDATE product_name='Wireless Mouse';
