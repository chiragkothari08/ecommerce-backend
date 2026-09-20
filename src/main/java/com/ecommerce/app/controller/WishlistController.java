package com.ecommerce.app.controller;

import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<?>> get(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(wishlistService.getWishlist(principal.getId()));
    }

    @PostMapping("/{productId}")
    public ApiResponse<Void> add(@PathVariable UUID productId, @AuthenticationPrincipal UserPrincipal principal) {
        wishlistService.addToWishlist(principal.getId(), productId);
        return ApiResponse.ok("Added to wishlist", null);
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> remove(@PathVariable UUID productId, @AuthenticationPrincipal UserPrincipal principal) {
        wishlistService.removeFromWishlist(principal.getId(), productId);
        return ApiResponse.ok("Removed from wishlist", null);
    }

    @PostMapping("/{productId}/move-to-cart")
    public ApiResponse<Void> moveToCart(@PathVariable UUID productId, @AuthenticationPrincipal UserPrincipal principal) {
        wishlistService.moveToCart(principal.getId(), productId);
        return ApiResponse.ok("Moved to cart", null);
    }
}
