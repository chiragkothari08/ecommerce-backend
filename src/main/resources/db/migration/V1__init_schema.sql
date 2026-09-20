-- ============================================================
-- E-Commerce Mobile App — Initial Database Schema (PostgreSQL)
-- Flyway migration V1
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- for gen_random_uuid()

-- ---------- USERS ----------
CREATE TABLE users (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name               VARCHAR(255) NOT NULL,
    email              VARCHAR(255) UNIQUE,
    phone              VARCHAR(20) UNIQUE,
    password_hash      VARCHAR(255),
    role               VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    profile_image_url  VARCHAR(500),
    email_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    blocked            BOOLEAN NOT NULL DEFAULT FALSE,
    google_id          VARCHAR(255),
    fcm_device_token   VARCHAR(500),
    created_at         TIMESTAMP NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);

-- ---------- REFRESH TOKENS ----------
CREATE TABLE refresh_tokens (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token        VARCHAR(512) NOT NULL UNIQUE,
    expiry_date  TIMESTAMP NOT NULL,
    revoked      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- ---------- OTP VERIFICATIONS ----------
CREATE TABLE otp_verifications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_or_email  VARCHAR(255) NOT NULL,
    otp_hash        VARCHAR(255) NOT NULL,
    expires_at      TIMESTAMP NOT NULL,
    attempts        INT NOT NULL DEFAULT 0,
    verified        BOOLEAN NOT NULL DEFAULT FALSE,
    purpose         VARCHAR(30) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_otp_phone_purpose ON otp_verifications(phone_or_email, purpose);

-- ---------- CATEGORIES ----------
CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150) NOT NULL,
    slug        VARCHAR(150),
    image_url   VARCHAR(500),
    parent_id   UUID REFERENCES categories(id) ON DELETE SET NULL,
    sort_order  INT NOT NULL DEFAULT 0,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------- PRODUCTS ----------
CREATE TABLE products (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    category_id     UUID REFERENCES categories(id) ON DELETE SET NULL,
    brand           VARCHAR(150),
    price           NUMERIC(10,2) NOT NULL,
    discount_price  NUMERIC(10,2),
    stock           INT NOT NULL DEFAULT 0,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    featured        BOOLEAN NOT NULL DEFAULT FALSE,
    flash_sale      BOOLEAN NOT NULL DEFAULT FALSE,
    avg_rating      DOUBLE PRECISION DEFAULT 0,
    review_count    INT DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_products_title ON products(title);
CREATE INDEX idx_products_category ON products(category_id);

-- ---------- PRODUCT IMAGES ----------
CREATE TABLE product_images (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id            UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url                   VARCHAR(500) NOT NULL,
    cloudinary_public_id  VARCHAR(255),
    sort_order            INT NOT NULL DEFAULT 0,
    created_at            TIMESTAMP NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_product_images_product ON product_images(product_id);

-- ---------- PRODUCT VARIANTS ----------
CREATE TABLE product_variants (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    size            VARCHAR(50),
    color           VARCHAR(50),
    sku             VARCHAR(100),
    price_override  NUMERIC(10,2),
    stock           INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_variants_product ON product_variants(product_id);

-- ---------- RECENTLY VIEWED ----------
CREATE TABLE recently_viewed (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id  UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_recently_viewed_user ON recently_viewed(user_id);

-- ---------- CART ----------
CREATE TABLE cart (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    applied_coupon_code    VARCHAR(50),
    created_at             TIMESTAMP NOT NULL DEFAULT now(),
    updated_at             TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE cart_items (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id     UUID NOT NULL REFERENCES cart(id) ON DELETE CASCADE,
    product_id  UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    variant_id  UUID REFERENCES product_variants(id) ON DELETE SET NULL,
    quantity    INT NOT NULL DEFAULT 1,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);

-- ---------- WISHLIST ----------
CREATE TABLE wishlist (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id  UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_wishlist_user_product UNIQUE (user_id, product_id)
);

-- ---------- ADDRESSES ----------
CREATE TABLE addresses (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    label          VARCHAR(50),
    full_name      VARCHAR(255) NOT NULL,
    phone          VARCHAR(20) NOT NULL,
    address_line1  VARCHAR(255) NOT NULL,
    address_line2  VARCHAR(255),
    city           VARCHAR(100) NOT NULL,
    state          VARCHAR(100) NOT NULL,
    zip_code       VARCHAR(20) NOT NULL,
    country        VARCHAR(100) NOT NULL,
    latitude       DOUBLE PRECISION,
    longitude      DOUBLE PRECISION,
    is_default     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_addresses_user ON addresses(user_id);

-- ---------- COUPONS ----------
CREATE TABLE coupons (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                 VARCHAR(50) NOT NULL UNIQUE,
    description          VARCHAR(255),
    discount_type        VARCHAR(20) NOT NULL,   -- PERCENTAGE | FLAT
    discount_value       NUMERIC(10,2) NOT NULL,
    min_order_value      NUMERIC(10,2),
    max_discount_amount  NUMERIC(10,2),
    valid_from           TIMESTAMP,
    valid_until          TIMESTAMP,
    usage_limit          INT,
    usage_count          INT NOT NULL DEFAULT 0,
    active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------- ORDERS ----------
CREATE TABLE orders (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number      VARCHAR(50) NOT NULL UNIQUE,
    user_id           UUID NOT NULL REFERENCES users(id),
    address_id        UUID REFERENCES addresses(id),
    subtotal          NUMERIC(10,2) NOT NULL,
    discount          NUMERIC(10,2) NOT NULL DEFAULT 0,
    delivery_fee      NUMERIC(10,2) NOT NULL DEFAULT 0,
    tax               NUMERIC(10,2) NOT NULL DEFAULT 0,
    total             NUMERIC(10,2) NOT NULL,
    coupon_code       VARCHAR(50),
    status            VARCHAR(30) NOT NULL DEFAULT 'PLACED',
    payment_method    VARCHAR(30),
    paid              BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

CREATE TABLE order_items (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id               UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id             UUID NOT NULL REFERENCES products(id),
    variant_id             UUID REFERENCES product_variants(id),
    product_title_snapshot VARCHAR(255) NOT NULL,
    price_snapshot         NUMERIC(10,2) NOT NULL,
    quantity               INT NOT NULL,
    created_at             TIMESTAMP NOT NULL DEFAULT now(),
    updated_at             TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_order_items_order ON order_items(order_id);

-- ---------- PAYMENTS ----------
CREATE TABLE payments (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id             UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    razorpay_order_id    VARCHAR(100) NOT NULL,
    razorpay_payment_id  VARCHAR(100),
    razorpay_signature   VARCHAR(255),
    amount               NUMERIC(10,2) NOT NULL,
    status               VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    method               VARCHAR(30),
    failure_reason       VARCHAR(500),
    created_at           TIMESTAMP NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_rzp_order ON payments(razorpay_order_id);

-- ---------- RETURNS ----------
CREATE TABLE returns (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id       UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    user_id        UUID NOT NULL REFERENCES users(id),
    reason         TEXT NOT NULL,
    status         VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    admin_remarks  VARCHAR(500),
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_returns_user ON returns(user_id);

-- ---------- REVIEWS ----------
CREATE TABLE reviews (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id     UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating         INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment        TEXT,
    helpful_count  INT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_reviews_product ON reviews(product_id);

-- ---------- NOTIFICATIONS ----------
CREATE TABLE notifications (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title         VARCHAR(255) NOT NULL,
    body          TEXT,
    type          VARCHAR(50),
    reference_id  VARCHAR(100),
    read          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_notifications_user ON notifications(user_id);

-- ---------- SUPPORT ----------
CREATE TABLE support_tickets (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    subject     VARCHAR(255) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_support_tickets_user ON support_tickets(user_id);

CREATE TABLE support_messages (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id   UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    sender_id   UUID NOT NULL REFERENCES users(id),
    message     TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_support_messages_ticket ON support_messages(ticket_id);
