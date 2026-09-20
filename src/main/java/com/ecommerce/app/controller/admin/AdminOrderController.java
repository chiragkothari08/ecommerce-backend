package com.ecommerce.app.controller.admin;

import com.ecommerce.app.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<PageResponse<?>> list(Pageable pageable) {
        return ApiResponse.ok(orderService.listAllOrders(pageable));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateStatus(id, request);
        return ApiResponse.ok("Order status updated", null);
    }
}
