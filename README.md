# E-Commerce Mobile App — Backend

Full REST API backend for the E-Commerce Mobile App PRD/SRS, built with the
exact stack specified in the document:

**Flutter (client, not included) ⇄ Java Spring Boot ⇄ PostgreSQL ⇄ Redis ⇄ Cloudinary ⇄ Firebase ⇄ Razorpay ⇄ Google Maps**

---

## 1. Project layout

```
ecommerce-backend/
├── pom.xml                          # Maven dependencies
├── docker-compose.yml               # Postgres + Redis for local dev
├── database/                        # Standalone DB files (see database/README.md)
│   ├── schema.sql                   # Full DDL (same as the Flyway migration)
│   ├── seed_data.sql                # Optional sample data
│   └── README.md
└── src/main/
    ├── resources/
    │   ├── application.yml          # All configuration (env-var driven)
    │   └── db/migration/
    │       └── V1__init_schema.sql  # Flyway migration (auto-applied on boot)
    └── java/com/ecommerce/app/
        ├── EcommerceApplication.java
        ├── config/                  # Security, JWT filter wiring, CORS, Razorpay, Cloudinary, OpenAPI, JPA auditing
        ├── entity/                  # JPA entities — one class per DB table
        ├── repository/              # Spring Data JPA repositories
        ├── dto/
        │   ├── request/             # Request payload DTOs (validated with jakarta.validation)
        │   └── response/            # Response payload DTOs
        ├── service/                 # Service interfaces
        ├── service/impl/            # Business logic implementations
        ├── security/                # JwtUtil, JwtAuthenticationFilter, UserPrincipal, UserDetailsService
        ├── exception/                # ApiException + GlobalExceptionHandler (RFC-ish JSON error responses)
        └── controller/               # REST controllers (public + authenticated)
            └── admin/                 # Admin-only controllers (require ROLE_ADMIN)
```

This mirrors the module breakdown from your API doc: Auth, Home, Products,
Cart, Wishlist, Addresses/Checkout, Payments, Orders, Returns, Profile,
Coupons, Notifications, Reviews/Support, Admin, Admin Analytics,
Upload/Location — each as its own controller + service + (where needed)
entity/repository, per the "Recommended Backend Structure" section of the PRD.

---

## 2. Running it locally

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker (for Postgres + Redis) — or your own local instances

### Steps

```bash
# 1. Start Postgres + Redis
docker compose up -d

# 2. Set required environment variables (or edit application.yml directly)
export JWT_SECRET="a-long-random-secret-at-least-32-bytes"
export RAZORPAY_KEY_ID="rzp_test_xxxxxxxx"
export RAZORPAY_KEY_SECRET="xxxxxxxxxxxxxxxx"
export RAZORPAY_WEBHOOK_SECRET="xxxxxxxxxxxxxxxx"
export CLOUDINARY_CLOUD_NAME="your_cloud_name"
export CLOUDINARY_API_KEY="your_api_key"
export CLOUDINARY_API_SECRET="your_api_secret"
export GOOGLE_MAPS_API_KEY="your_maps_key"

# 3. Run
./mvnw spring-boot:run
```

Flyway will automatically create every table in `database/schema.sql` on
first boot. The API is then live at `http://localhost:8080`, with Swagger UI
at `http://localhost:8080/swagger-ui.html`.

### Test credentials (if you loaded `seed_data.sql`)
- Admin: `admin@ecommerce.test` / `Admin@123`

---

## 3. Authentication

- **JWT access token** (15 min) + **refresh token** (7 days), per the PRD's
  "JWT + Refresh Tokens" security requirement.
- **OTP login** (`/api/auth/send-otp`, `/api/auth/verify-otp`) — OTPs are
  bcrypt-hashed at rest and expire after 5 minutes; wire in an SMS gateway
  (MSG91, Twilio, etc.) at the marked `TODO` in `AuthServiceImpl.sendOtp()`.
- **Google login** endpoint is present but needs a Google ID-token verifier
  wired in (`AuthServiceImpl.googleLogin()`) — left as a clear extension
  point rather than guessed at, since it needs your real OAuth client ID.
- Protected endpoints expect: `Authorization: Bearer <accessToken>`
- Admin endpoints (`/api/admin/**`) require `ROLE_ADMIN`.

---

## 4. Payments (Razorpay)

Flow implemented exactly as described in the PRD's "Payment Flow" section:

```
Flutter → POST /api/payments/create-order → Razorpay order created
        → Razorpay Checkout (client-side) → customer pays
        → Flutter → POST /api/payments/verify (verifies HMAC signature)
        → Razorpay → POST /api/payments/webhook (server-to-server confirmation)
```

Refunds: `POST /api/payments/refund`.

---

## 5. What's fully implemented vs. left as an extension point

**Fully implemented:** registration/login/OTP/refresh/logout, product catalog
+ search + filtering, cart (add/update/remove/coupon), wishlist, addresses,
order creation with stock validation + coupon + delivery-fee calculation,
Razorpay order creation/verification/webhook/refund, reviews with rating
aggregation, returns workflow, notifications (in-app), support tickets,
full admin CRUD for products/categories/coupons/inventory/orders/users/returns,
basic admin analytics, Cloudinary image upload, Google Maps geocoding proxy.

**Left as clearly-marked extension points** (things that need your real
credentials/vendor accounts to be meaningful, so they're stubbed rather than
faked): SMS OTP delivery, Google Sign-In token verification, email sending
(password reset), Firebase push dispatch (device tokens are stored; the
actual `FirebaseMessaging.send()` call is a one-line addition), PDF invoice
generation. Search each with `grep -rn "TODO\|NOTE:" src/` to find them.

---

## 6. API surface

The endpoint list matches your `DOC-20260816-WA0011.pdf` mapping document
1:1 — every route in that document has a corresponding controller method
here. See the controllers under `controller/` and `controller/admin/` for
the definitive, current list, or browse `/swagger-ui.html` once running.

## 7. Two API path styles, same backend

This project ships **two sets of controllers that both call the exact same
services** — so there's only one implementation of any business logic, just
two different URL naming conventions on top of it:

- **`controller/*.java` + `controller/admin/*.java`** — the original set,
  prefixed with `/api/...` (e.g. `/api/auth/login`, `/api/cart/items`).
- **`controller/chirag/*.java`** — a second, shorter-path set matching a
  simplified ~32-36 endpoint breakdown (e.g. `/auth/login`, `/cart/items`,
  no `/api` prefix). Useful if a stakeholder-facing spec expects those exact
  names.

Both sets are live at the same time — pick whichever URL style you want to
call from Flutter/Postman, or drop the one you don't need. `SecurityConfig`
already has auth rules for both prefixes.
