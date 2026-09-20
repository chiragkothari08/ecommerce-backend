package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.CreateOrderRequest;
import com.ecommerce.app.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.app.dto.response.OrderResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Order;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    Order createOrder(UUID userId, CreateOrderRequest request);
    OrderResponse getOrder(UUID userId, UUID orderId);
    PageResponse<OrderResponse> listOrders(UUID userId, Pageable pageable);
    void cancelOrder(UUID userId, UUID orderId);
    void reorder(UUID userId, UUID orderId);
    void updateStatus(UUID orderId, UpdateOrderStatusRequest request); // admin
    PageResponse<OrderResponse> listAllOrders(Pageable pageable); // admin
}
