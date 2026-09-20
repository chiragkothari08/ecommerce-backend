package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.*;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.dto.response.ProductResponse;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Module 8: Admin Dashboard APIs
 * CRUD /admin/products
 * CRUD /admin/categories
 * GET  /admin/orders
 * PUT  /admin/orders/:id/status
 * POST /admin/coupons
 * GET  /admin/users
 * GET  /admin/analytics
 * PUT  /admin/returns/:id
 *
 * NOTE: All endpoints here require ROLE_ADMIN — enforced in SecurityConfig
 * via .requestMatchers("/admin/**").hasAnyRole("ADMIN") (add this matcher
 * alongside the existing "/api/admin/**" one if you keep both API sets
 * running side by side).
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final ReturnService returnService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    // ---------- Products (CRUD) ----------

    @PostMapping("/products")
    public ApiResponse<Map<String, UUID>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok("Product created", Map.of("id", productService.createProduct(request)));
    }

    @GetMapping("/products")
    public ApiResponse<PageResponse<?>> listProducts(@RequestParam(required = false) String category,
                                                      @RequestParam(required = false) String brand,
                                                      Pageable pageable) {
        return ApiResponse.ok(productService.listProducts(category, brand, null, null, null, pageable));
    }

    @PutMapping("/products/{id}")
    public ApiResponse<Void> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        productService.updateProduct(id, request);
        return ApiResponse.ok("Product updated", null);
    }

    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ApiResponse.ok("Product deleted", null);
    }

    @PatchMapping("/products/{id}/stock")
    public ApiResponse<Void> updateStock(@PathVariable UUID id, @Valid @RequestBody UpdateStockRequest request) {
        var product = productRepository.findById(id).orElseThrow(() -> ApiException.notFound("Product not found"));
        product.setStock(request.getStock());
        productRepository.save(product);
        return ApiResponse.ok("Stock updated", null);
    }

    // ---------- Categories (CRUD) ----------

    @PostMapping("/categories")
    public ApiResponse<Map<String, UUID>> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.ok("Category created", Map.of("id", categoryService.create(request)));
    }

    @GetMapping("/categories")
    public ApiResponse<List<Category>> listCategories() {
        return ApiResponse.ok(categoryService.listActive());
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<Void> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        categoryService.update(id, request);
        return ApiResponse.ok("Category updated", null);
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
        return ApiResponse.ok("Category deleted", null);
    }

    // ---------- Orders ----------

    @GetMapping("/orders")
    public ApiResponse<PageResponse<?>> listAllOrders(Pageable pageable) {
        return ApiResponse.ok(orderService.listAllOrders(pageable));
    }

    @PutMapping("/orders/{id}/status")
    public ApiResponse<Void> updateOrderStatus(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateStatus(id, request);
        return ApiResponse.ok("Order status updated", null);
    }

    // ---------- Coupons ----------

    @PostMapping("/coupons")
    public ApiResponse<Map<String, UUID>> createCoupon(@Valid @RequestBody CouponRequest request) {
        return ApiResponse.ok("Coupon created", Map.of("id", couponService.create(request)));
    }

    // ---------- Users ----------

    @GetMapping("/users")
    public ApiResponse<Page<User>> listUsers(Pageable pageable) {
        return ApiResponse.ok(userRepository.findAll(pageable));
    }

    // ---------- Analytics ----------

    @GetMapping("/analytics")
    public ApiResponse<Map<String, Object>> analytics() {
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.isPaid())
                .map(o -> o.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ApiResponse.ok(Map.of(
                "totalOrders", totalOrders,
                "totalUsers", totalUsers,
                "totalProducts", totalProducts,
                "totalRevenue", totalRevenue
        ));
    }

    // ---------- Returns ----------

    @PutMapping("/returns/{id}")
    public ApiResponse<Void> actOnReturn(@PathVariable UUID id, @Valid @RequestBody AdminReturnActionRequest request) {
        if ("approve".equalsIgnoreCase(request.getAction())) {
            returnService.approve(id, request.getRemarks());
        } else if ("reject".equalsIgnoreCase(request.getAction())) {
            returnService.reject(id, request.getRemarks());
        } else {
            throw ApiException.badRequest("action must be 'approve' or 'reject'");
        }
        return ApiResponse.ok("Return " + request.getAction() + "d", null);
    }
}
