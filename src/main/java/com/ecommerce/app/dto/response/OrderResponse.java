package com.ecommerce.app.dto.response;

import com.ecommerce.app.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal deliveryFee;
    private BigDecimal tax;
    private BigDecimal total;
    private String paymentMethod;
    private boolean paid;
    private Instant createdAt;
    private List<OrderItemResponse> items;

    @Data
    public static class OrderItemResponse {
        private UUID productId;
        private String productTitle;
        private BigDecimal price;
        private Integer quantity;
    }
}
