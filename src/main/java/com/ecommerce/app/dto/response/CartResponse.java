package com.ecommerce.app.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CartResponse {
    private UUID cartId;
    private List<CartItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String appliedCouponCode;

    @Data
    public static class CartItemResponse {
        private UUID itemId;
        private UUID productId;
        private String productTitle;
        private String productImage;
        private UUID variantId;
        private String variantLabel;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal lineTotal;
    }
}
