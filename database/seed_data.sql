-- ============================================================
-- Sample seed data for local development / testing only.
-- Run manually against your dev database — NOT applied by Flyway.
-- psql -U postgres -d ecommerce_db -f database/seed_data.sql
-- ============================================================

-- Admin user (password: Admin@123 — bcrypt hash below, change before real use)
INSERT INTO users (id, name, email, phone, password_hash, role, email_verified, phone_verified)
VALUES (gen_random_uuid(), 'Super Admin', 'admin@ecommerce.test', '9999999999',
        '$2a$10$7QJZ0m1G8y0m3v3f0i8oCOe1c9m8b0Zt8s7yJXk8kQe0nQ8h1u1Fu', -- bcrypt("Admin@123")
        'ADMIN', TRUE, TRUE);

-- Categories
INSERT INTO categories (id, name, slug, sort_order, active) VALUES
 (gen_random_uuid(), 'Shirts', 'shirts', 1, TRUE),
 (gen_random_uuid(), 'Shoes', 'shoes', 2, TRUE),
 (gen_random_uuid(), 'Trousers', 'trousers', 3, TRUE),
 (gen_random_uuid(), 'Bags', 'bags', 4, TRUE);

-- Sample products (pick a category id from above after insert, or just leave category_id null for a quick smoke test)
INSERT INTO products (id, title, description, brand, price, discount_price, stock, active, featured, flash_sale)
VALUES
 (gen_random_uuid(), 'Men Luxury Shirt', 'Premium cotton shirt for everyday wear.', 'Levi''s', 1499.00, 999.00, 50, TRUE, TRUE, FALSE),
 (gen_random_uuid(), 'Men Luxury Hoodie', 'Warm and stylish hoodie.', 'Nike', 2499.00, 1799.00, 30, TRUE, TRUE, TRUE),
 (gen_random_uuid(), 'Orbit Sneakers Low', 'Comfortable everyday sneakers.', 'Fila', 3999.00, 2990.00, 20, TRUE, FALSE, TRUE);

-- Sample coupon
INSERT INTO coupons (id, code, description, discount_type, discount_value, min_order_value, max_discount_amount, active)
VALUES (gen_random_uuid(), 'WELCOME10', '10% off on your first order', 'PERCENTAGE', 10.00, 500.00, 300.00, TRUE);
