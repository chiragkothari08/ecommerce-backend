package com.ecommerce.app.service;

import com.ecommerce.app.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    List<ProductResponse> getWishlist(UUID userId);
    void addToWishlist(UUID userId, UUID productId);
    void removeFromWishlist(UUID userId, UUID productId);
    void moveToCart(UUID userId, UUID productId);
}
