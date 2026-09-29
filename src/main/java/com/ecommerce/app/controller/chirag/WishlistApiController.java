package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.WishlistItemRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Module 4: Wishlist
 * GET    /wishlist
 * POST   /wishlist/items
 * DELETE /wishlist/items/:id   (id = productId)
 */
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistApiController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<?>> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(wishlistService.getWishlist(principal.getId()));
    }

    @PostMapping("/items")
    public ApiResponse<Void> addItem(@Valid @RequestBody WishlistItemRequest request,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        wishlistService.addToWishlist(principal.getId(), request.getProductId());
        return ApiResponse.ok("Added to wishlist", null);
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> removeItem(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        wishlistService.removeFromWishlist(principal.getId(), id);
        return ApiResponse.ok("Removed from wishlist", null);
    }
}
