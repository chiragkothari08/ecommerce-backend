package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.AddToCartRequest;
import com.ecommerce.app.dto.request.UpdateCartItemRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.CartResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Module 5: Cart
 * GET    /cart                (items + price breakdown + delivery estimate)
 * POST   /cart/items          (add item with selected size/color variant)
 * PUT    /cart/items/:id      (update quantity)
 * DELETE /cart/items/:id      (remove item)
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartApiController {

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

    @PutMapping("/items/{id}")
    public ApiResponse<CartResponse> updateItem(@PathVariable UUID id, @Valid @RequestBody UpdateCartItemRequest request,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Cart updated", cartService.updateItem(principal.getId(), id, request));
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> removeItem(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        cartService.removeItem(principal.getId(), id);
        return ApiResponse.ok("Item removed", null);
    }
}
