# Database — E-Commerce Mobile App

**Engine:** PostgreSQL 14+
**Migration tool:** Flyway (runs automatically on app startup from `src/main/resources/db/migration/`)

This folder holds a **standalone copy** of the schema so you (or a DBA) can
inspect/run it independently of the Spring Boot app.

## Files

| File | Purpose |
|---|---|
| `schema.sql` | Full DDL — identical to `V1__init_schema.sql` used by Flyway. Run this directly if you want to provision a DB by hand instead of letting the app do it. |
| `seed_data.sql` | Optional sample data (admin user, categories, products, a coupon) for local development. **Not** run automatically. |

## Quick start (manual)

```bash
createdb ecommerce_db
psql -U postgres -d ecommerce_db -f schema.sql
psql -U postgres -d ecommerce_db -f seed_data.sql   # optional
```

If you let the Spring Boot app manage migrations instead (recommended), just
create an empty database and start the app — Flyway applies `schema.sql`
(as `V1__init_schema.sql`) automatically.

## Entity-relationship overview

```
users ──< refresh_tokens
users ──< otp_verifications (by phone/email, not FK)
users ──< addresses
users ──< cart (1:1) ──< cart_items >── products
users ──< wishlist >── products
users ──< orders ──< order_items >── products
orders ──< payments
orders ──< returns
users ──< reviews >── products
users ──< notifications
users ──< support_tickets ──< support_messages
categories ──< categories (self-referencing parent/child)
categories ──< products ──< product_images
products ──< product_variants
users ──< recently_viewed >── products
coupons  (looked up by code, not a strict FK from cart/orders)
```

## Tables (from the PRD's "Suggested Database Tables")

`users, roles(→ role enum column), products, product_variants, categories,
product_images, cart, cart_items, wishlist, addresses, orders, order_items,
payments, coupons, reviews, returns, notifications, recently_viewed,
support_tickets, support_messages, refresh_tokens`

Notes on deviations from the literal PRD list:
- `roles` was implemented as a `role VARCHAR` enum column on `users`
  (`CUSTOMER`, `ADMIN`, `SUPPORT_AGENT`) rather than a separate join table,
  since the PRD only needed simple role-based access, not many-to-many roles.
- Added `otp_verifications` (needed to support the OTP endpoints securely —
  never store raw OTPs) and `product_images` as its own table (needed for
  multiple images per product with ordering).

## Key design choices

- **UUID primary keys** everywhere (`gen_random_uuid()`) — safer to expose in
  REST URLs than sequential integers, and merge-friendly across environments.
- **Soft delete** for `products` and `categories` (an `active` flag) instead
  of hard deletes, so historical orders/reviews referencing them stay intact.
- **Price snapshots** on `order_items` (`product_title_snapshot`,
  `price_snapshot`) so historical orders don't change if a product's price or
  name is edited later.
- **Coupon usage tracking** via `usage_count` / `usage_limit` on `coupons`.
- Timestamps (`created_at`, `updated_at`) on every table via Hibernate/JPA
  auditing (`BaseEntity` + `@EnableJpaAuditing`).

## Environment variables used by the app to connect

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecommerce_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
```
