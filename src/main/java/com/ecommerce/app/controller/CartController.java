package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.AddToCartRequest;
import com.ecommerce.app.dto.request.ApplyCouponRequest;
import com.ecommerce.app.dto.request.UpdateCartItemRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.CartResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(cartService.getCart(principal.getId()));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addItem(@Valid @RequestBody AddToCartRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Item added to cart", cartService.addItem(principal.getId(), request));
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<CartResponse> updateItem(@PathVariable UUID itemId, @Valid @RequestBody UpdateCartItemRequest request,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Cart updated", cartService.updateItem(principal.getId(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Void> removeItem(@PathVariable UUID itemId, @AuthenticationPrincipal UserPrincipal principal) {
        cartService.removeItem(principal.getId(), itemId);
        return ApiResponse.ok("Item removed", null);
    }

    @DeleteMapping
    public ApiResponse<Void> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ApiResponse.ok("Cart cleared", null);
    }

    @PostMapping("/coupon")
    public ApiResponse<CartResponse> applyCoupon(@Valid @RequestBody ApplyCouponRequest request,
                                                  @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Coupon applied", cartService.applyCoupon(principal.getId(), request));
    }

    @DeleteMapping("/coupon")
    public ApiResponse<Void> removeCoupon(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.removeCoupon(principal.getId());
        return ApiResponse.ok("Coupon removed", null);
    }

    @GetMapping("/price-breakdown")
    public ApiResponse<CartResponse> priceBreakdown(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(cartService.getCart(principal.getId()));
    }

    @GetMapping("/delivery-estimate")
    public ApiResponse<Map<String, String>> deliveryEstimate() {
        return ApiResponse.ok(Map.of("estimate", "3-5 business days"));
    }
}
