package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.*;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.OrderService;
import com.ecommerce.app.service.PaymentService;
import com.ecommerce.app.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Module 7: Checkout, Orders & Tracking
 * POST /orders/checkout                    - initialize order, calculate totals
 * POST /payments/razorpay/create-order      - create Razorpay order
 * POST /payments/razorpay/verify            - verify payment signature
 * GET  /orders                              - order history
 * GET  /orders/:id                          - order detail + tracking timeline
 * POST /orders/:id/return                   - request return
 */
@RestController
@RequiredArgsConstructor
public class CheckoutOrderApiController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ReturnService returnService;

    @PostMapping("/orders/checkout")
    public ApiResponse<Map<String, Object>> checkout(@Valid @RequestBody CreateOrderRequest request,
                                                       @AuthenticationPrincipal UserPrincipal principal) {
        Order order = orderService.createOrder(principal.getId(), request);
        return ApiResponse.ok("Order placed successfully", Map.of(
                "orderId", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "subtotal", order.getSubtotal(),
                "discount", order.getDiscount(),
                "deliveryFee", order.getDeliveryFee(),
                "tax", order.getTax(),
                "total", order.getTotal(),
                "status", order.getStatus()
        ));
    }

    @PostMapping("/payments/razorpay/create-order")
    public ApiResponse<Map<String, Object>> createRazorpayOrder(@Valid @RequestBody CreatePaymentOrderRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(paymentService.createRazorpayOrder(principal.getId(), request));
    }

    @PostMapping("/payments/razorpay/verify")
    public ApiResponse<Void> verifyRazorpayPayment(@Valid @RequestBody VerifyPaymentRequest request,
                                                    @AuthenticationPrincipal UserPrincipal principal) {
        paymentService.verifyPayment(principal.getId(), request);
        return ApiResponse.ok("Payment verified successfully", null);
    }

    @GetMapping("/orders")
    public ApiResponse<PageResponse<?>> getOrderHistory(@AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return ApiResponse.ok(orderService.listOrders(principal.getId(), pageable));
    }

    @GetMapping("/orders/{id}")
    public ApiResponse<?> getOrderDetail(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        // Response includes order.status which the app renders as the
        // Placed -> Packed -> Shipped -> Out for Delivery -> Delivered timeline.
        return ApiResponse.ok(orderService.getOrder(principal.getId(), id));
    }

    @PostMapping("/orders/{id}/return")
    public ApiResponse<?> requestReturn(@PathVariable UUID id, @Valid @RequestBody ReturnItemRequest request,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        ReturnRequestDto dto = new ReturnRequestDto();
        dto.setOrderId(id);
        dto.setReason(request.getReason());
        return ApiResponse.ok("Return requested", returnService.create(principal.getId(), dto));
    }
}
