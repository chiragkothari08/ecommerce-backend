package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.CreateOrderRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody CreateOrderRequest request,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        Order order = orderService.createOrder(principal.getId(), request);
        return ApiResponse.ok("Order created", Map.of(
                "orderId", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "total", order.getTotal(),
                "status", order.getStatus()
        ));
    }

    @GetMapping
    public ApiResponse<PageResponse<?>> list(@AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return ApiResponse.ok(orderService.listOrders(principal.getId(), pageable));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<?> get(@PathVariable UUID orderId, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(orderService.getOrder(principal.getId(), orderId));
    }

    @GetMapping("/{orderId}/tracking")
    public ApiResponse<?> tracking(@PathVariable UUID orderId, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(orderService.getOrder(principal.getId(), orderId));
    }

    @GetMapping("/{orderId}/status")
    public ApiResponse<?> status(@PathVariable UUID orderId, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(orderService.getOrder(principal.getId(), orderId).getStatus());
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable UUID orderId, @AuthenticationPrincipal UserPrincipal principal) {
        orderService.cancelOrder(principal.getId(), orderId);
        return ApiResponse.ok("Order cancelled", null);
    }

    @PostMapping("/{orderId}/return")
    public ApiResponse<Void> requestReturn(@PathVariable UUID orderId, @AuthenticationPrincipal UserPrincipal principal) {
        // Redirects to the dedicated Returns flow which requires a reason payload.
        return ApiResponse.ok("Use POST /api/returns with { orderId, reason } to request a return", null);
    }

    @PostMapping("/{orderId}/reorder")
    public ApiResponse<Void> reorder(@PathVariable UUID orderId, @AuthenticationPrincipal UserPrincipal principal) {
        orderService.reorder(principal.getId(), orderId);
        return ApiResponse.ok("Items added back to cart", null);
    }

    @GetMapping("/{orderId}/invoice")
    public ApiResponse<?> invoice(@PathVariable UUID orderId, @AuthenticationPrincipal UserPrincipal principal) {
        // Generate a PDF invoice here (e.g. using a PDF library) and return a download URL.
        return ApiResponse.ok(orderService.getOrder(principal.getId(), orderId));
    }
}
