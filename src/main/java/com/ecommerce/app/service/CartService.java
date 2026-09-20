package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.AddToCartRequest;
import com.ecommerce.app.dto.request.ApplyCouponRequest;
import com.ecommerce.app.dto.request.UpdateCartItemRequest;
import com.ecommerce.app.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {
    CartResponse getCart(UUID userId);
    CartResponse addItem(UUID userId, AddToCartRequest request);
    CartResponse updateItem(UUID userId, UUID itemId, UpdateCartItemRequest request);
    void removeItem(UUID userId, UUID itemId);
    void clearCart(UUID userId);
    CartResponse applyCoupon(UUID userId, ApplyCouponRequest request);
    void removeCoupon(UUID userId);
}
