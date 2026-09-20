package com.ecommerce.app.controller.admin;

import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.OrderStatus;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Lightweight analytics built directly from JPA repositories.
 * For heavier reporting, consider dedicated read-optimized queries or a
 * reporting DB/warehouse fed by CDC from Postgres.
 */
@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
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

    @GetMapping("/sales")
    public ApiResponse<Map<String, Object>> sales() {
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.isPaid())
                .map(o -> o.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ApiResponse.ok(Map.of("totalRevenue", totalRevenue));
    }

    @GetMapping("/orders")
    public ApiResponse<Map<String, Long>> orderAnalytics() {
        var all = orderRepository.findAll();
        return ApiResponse.ok(Map.of(
                "placed", all.stream().filter(o -> o.getStatus() == OrderStatus.PLACED).count(),
                "delivered", all.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count(),
                "cancelled", all.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count()
        ));
    }

    @GetMapping("/products")
    public ApiResponse<Map<String, Object>> productAnalytics() {
        return ApiResponse.ok(Map.of("totalProducts", productRepository.count()));
    }

    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> userAnalytics() {
        return ApiResponse.ok(Map.of("totalUsers", userRepository.count()));
    }

    @GetMapping("/revenue")
    public ApiResponse<Map<String, Object>> revenueAnalytics() {
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.isPaid())
                .map(o -> o.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ApiResponse.ok(Map.of("totalRevenue", totalRevenue));
    }
}
