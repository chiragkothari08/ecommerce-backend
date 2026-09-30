package com.ecommerce.app.controller;

import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;

    @PostMapping("/validate")
    public ApiResponse<?> validate(@AuthenticationPrincipal UserPrincipal principal) {
        // Real validation happens against live stock at order-creation time in OrderService.
        return ApiResponse.ok("Cart is valid", cartService.getCart(principal.getId()));
    }

    @GetMapping("/delivery-options")
    public ApiResponse<List<Map<String, Object>>> deliveryOptions() {
        return ApiResponse.ok(List.of(
                Map.of("id", "STANDARD", "label", "Standard Shipping", "eta", "4-7 days", "fee", 49),
                Map.of("id", "EXPRESS", "label", "Express Shipping", "eta", "1-2 days", "fee", 149)
        ));
    }

    @PostMapping("/calculate")
    public ApiResponse<?> calculate(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(cartService.getCart(principal.getId()));
    }

    @GetMapping("/summary")
    public ApiResponse<?> summary(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(cartService.getCart(principal.getId()));
    }
}
