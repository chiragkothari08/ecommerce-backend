# E-Commerce Mobile App — Complete API List

Ek hi file me pura API list — saare endpoints, method, aur kis controller file
me hain wo bhi bataya gaya hai. Base URL local run pe: `http://localhost:8080`

Auth wale endpoints me header chahiye: `Authorization: Bearer <accessToken>`
(jo `/api/auth/login` ya `/api/auth/register` se milta hai).

---

## 1. Authentication APIs
**File:** `controller/AuthController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register new user |
| POST | `/api/auth/login` | No | Email/phone + password login |
| POST | `/api/auth/send-otp` | No | Send OTP to phone |
| POST | `/api/auth/verify-otp` | No | Verify OTP |
| POST | `/api/auth/login-otp` | No | Login using OTP |
| POST | `/api/auth/google` | No | Google login |
| POST | `/api/auth/forgot-password` | No | Forgot password |
| POST | `/api/auth/reset-password` | No | Reset password |
| POST | `/api/auth/refresh-token` | No | Refresh access token |
| POST | `/api/auth/logout` | No | Logout |
| GET | `/api/auth/me` | Yes | Current logged-in user |

## 2. Home APIs
**File:** `controller/HomeController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/home` | Optional | Complete home screen data |
| GET | `/api/banners` | No | Hero banners |
| GET | `/api/categories` | No | Categories list |
| GET | `/api/products/featured` | No | Featured products |
| GET | `/api/products/flash-sale` | No | Flash-sale products |
| GET | `/api/products/recommended` | Optional | Recommended products |
| GET | `/api/products/recently-viewed` | Yes | Recently viewed products |

## 3. Product & Search APIs
**File:** `controller/ProductController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/products` | No | Product listing (pagination, filters: category, brand, minPrice, maxPrice, sort) |
| GET | `/api/products/{id}` | Optional | Product details |
| GET | `/api/products/{id}/variants` | No | Product variants |
| GET | `/api/products/{id}/reviews` | No | Product reviews (paginated) |
| POST | `/api/products/{id}/reviews` | Yes | Add review |
| GET | `/api/products/{id}/related` | No | Related products |
| GET | `/api/products/search?query=` | No | Search products |
| GET | `/api/search/suggestions?query=` | No | Search suggestions |
| GET | `/api/search/filters` | No | Available filters |

## 4. Cart APIs
**File:** `controller/CartController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/cart` | Yes | Get user's cart |
| POST | `/api/cart/items` | Yes | Add product to cart |
| PUT | `/api/cart/items/{itemId}` | Yes | Update quantity |
| DELETE | `/api/cart/items/{itemId}` | Yes | Remove item |
| DELETE | `/api/cart` | Yes | Clear cart |
| POST | `/api/cart/coupon` | Yes | Apply coupon |
| DELETE | `/api/cart/coupon` | Yes | Remove coupon |
| GET | `/api/cart/price-breakdown` | Yes | Price breakdown |
| GET | `/api/cart/delivery-estimate` | Yes | Delivery estimate |

## 5. Wishlist APIs
**File:** `controller/WishlistController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/wishlist` | Yes | Get wishlist |
| POST | `/api/wishlist/{productId}` | Yes | Add product |
| DELETE | `/api/wishlist/{productId}` | Yes | Remove product |
| POST | `/api/wishlist/{productId}/move-to-cart` | Yes | Move wishlist item to cart |

## 6. Address APIs
**File:** `controller/AddressController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/addresses` | Yes | Get addresses |
| POST | `/api/addresses` | Yes | Add address |
| GET | `/api/addresses/{id}` | Yes | Get single address |
| PUT | `/api/addresses/{id}` | Yes | Update address |
| DELETE | `/api/addresses/{id}` | Yes | Delete address |
| PATCH | `/api/addresses/{id}/default` | Yes | Set default address |

## 7. Checkout APIs
**File:** `controller/CheckoutController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| POST | `/api/checkout/validate` | Yes | Validate cart |
| GET | `/api/checkout/delivery-options` | Yes | Delivery options |
| POST | `/api/checkout/calculate` | Yes | Calculate final amount |
| GET | `/api/checkout/summary` | Yes | Checkout summary |

## 8. Payment APIs (Razorpay)
**File:** `controller/PaymentController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| POST | `/api/payments/create-order` | Yes | Create Razorpay order |
| POST | `/api/payments/verify` | Yes | Verify payment signature |
| GET | `/api/payments/{paymentId}` | Yes | Payment details |
| POST | `/api/payments/webhook` | No (signed) | Razorpay webhook |
| POST | `/api/payments/refund` | Yes (admin) | Refund payment |
| GET | `/api/payments/{paymentId}/status` | Yes | Payment status |

## 9. Order & Tracking APIs
**File:** `controller/OrderController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| POST | `/api/orders` | Yes | Create order |
| GET | `/api/orders` | Yes | User's orders (paginated) |
| GET | `/api/orders/{orderId}` | Yes | Order details |
| GET | `/api/orders/{orderId}/tracking` | Yes | Track order |
| GET | `/api/orders/{orderId}/status` | Yes | Current status |
| POST | `/api/orders/{orderId}/cancel` | Yes | Cancel order |
| POST | `/api/orders/{orderId}/return` | Yes | Return request pointer (use `/api/returns`) |
| POST | `/api/orders/{orderId}/reorder` | Yes | Reorder |
| GET | `/api/orders/{orderId}/invoice` | Yes | Invoice |

## 10. Returns APIs
**File:** `controller/ReturnController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| POST | `/api/returns` | Yes | Create return request |
| GET | `/api/returns` | Yes | User's returns |
| GET | `/api/returns/{returnId}` | Yes | Return details |
| POST | `/api/returns/{returnId}/cancel` | Yes | Cancel return |

## 11. Profile / User APIs
**File:** `controller/UserController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/users/me` | Yes | Get profile |
| PUT | `/api/users/me` | Yes | Update profile |
| PUT | `/api/users/me/password` | Yes | Change password |
| POST | `/api/users/me/profile-image` | Yes | Update profile image (multipart) |
| GET | `/api/users/me/orders` | Yes | User orders |
| GET | `/api/users/me/notifications` | Yes | User notifications |

## 12. Coupon APIs
**File:** `controller/CouponController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/coupons` | No | Available coupons |
| POST | `/api/coupons/validate` | No | Validate coupon |
| POST | `/api/cart/coupon` | Yes | Apply coupon (see Cart APIs) |

## 13. Notification APIs
**File:** `controller/NotificationController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/notifications` | Yes | Get notifications (paginated) |
| PATCH | `/api/notifications/{id}/read` | Yes | Mark notification read |
| PATCH | `/api/notifications/read-all` | Yes | Mark all as read |
| DELETE | `/api/notifications/{id}` | Yes | Delete notification |
| POST | `/api/notifications/device-token` | Yes | Save Firebase device token |
| DELETE | `/api/notifications/device-token` | Yes | Remove device token |

## 14. Reviews & Support APIs
**Files:** `controller/ReviewController.java`, `controller/SupportController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| GET | `/api/products/{productId}/reviews` | No | Get product reviews |
| POST | `/api/products/{productId}/reviews` | Yes | Create review |
| PUT | `/api/reviews/{reviewId}` | Yes | Edit review |
| DELETE | `/api/reviews/{reviewId}` | Yes | Delete review |
| POST | `/api/reviews/{reviewId}/helpful` | No | Mark review helpful |
| POST | `/api/support/tickets` | Yes | Create support ticket |
| GET | `/api/support/tickets` | Yes | User support tickets |
| GET | `/api/support/tickets/{id}` | Yes | Ticket details |
| POST | `/api/support/tickets/{id}/messages` | Yes | Send support message |

## 15. Admin APIs
**Files:** `controller/admin/AdminProductController.java`, `AdminCategoryController.java`,
`AdminInventoryController.java`, `AdminOrderController.java`, `AdminCouponController.java`,
`AdminUserController.java`, `AdminReturnController.java`
(all require `ROLE_ADMIN`)

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/admin/products` | Create product |
| GET | `/api/admin/products` | List products |
| GET | `/api/admin/products/{id}` | Product details |
| PUT | `/api/admin/products/{id}` | Update product |
| DELETE | `/api/admin/products/{id}` | Delete (deactivate) product |
| POST | `/api/admin/categories` | Create category |
| GET | `/api/admin/categories` | List categories |
| PUT | `/api/admin/categories/{id}` | Update category |
| DELETE | `/api/admin/categories/{id}` | Delete category |
| GET | `/api/admin/inventory` | Inventory list |
| PATCH | `/api/admin/inventory/{productId}` | Update stock |
| GET | `/api/admin/orders` | All orders |
| PATCH | `/api/admin/orders/{id}/status` | Update order status |
| POST | `/api/admin/coupons` | Create coupon |
| GET | `/api/admin/coupons` | List coupons |
| PUT | `/api/admin/coupons/{id}` | Update coupon |
| DELETE | `/api/admin/coupons/{id}` | Delete coupon |
| GET | `/api/admin/users` | List/manage users |
| PUT | `/api/admin/users/{id}` | Update user |
| PATCH | `/api/admin/users/{id}/status` | Block/unblock user |
| GET | `/api/admin/returns` | Return requests |
| POST | `/api/admin/returns/{id}/approve` | Approve return |
| POST | `/api/admin/returns/{id}/reject` | Reject return |

## 16. Admin Analytics APIs
**File:** `controller/admin/AdminAnalyticsController.java`

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/admin/analytics/dashboard` | Dashboard summary |
| GET | `/api/admin/analytics/sales` | Sales reports |
| GET | `/api/admin/analytics/orders` | Order analytics |
| GET | `/api/admin/analytics/products` | Product analytics |
| GET | `/api/admin/analytics/users` | User analytics |
| GET | `/api/admin/analytics/revenue` | Revenue analytics |

## 17. Upload / Location APIs
**Files:** `controller/UploadController.java`, `controller/LocationController.java`

| Method | Endpoint | Auth Required | Purpose |
|---|---|---|---|
| POST | `/api/uploads/image` | Yes | Upload image (multipart, Cloudinary) |
| POST | `/api/uploads/product-images` | Yes (admin) | Upload multiple product images |
| DELETE | `/api/uploads/{publicId}` | Yes (admin) | Delete stored image |
| GET | `/api/locations/geocode?address=` | No | Geocode address (Google Maps) |
| GET | `/api/locations/reverse-geocode?lat=&lng=` | No | Reverse geocode |
| GET | `/api/delivery/estimate?lat=&lng=` | No | Delivery/location estimate |

---

## Total: 90+ endpoints across 24 controllers

Live, interactive version (once the app is running):
`http://localhost:8080/swagger-ui.html`
