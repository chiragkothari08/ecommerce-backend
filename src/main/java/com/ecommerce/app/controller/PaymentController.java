package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.CreatePaymentOrderRequest;
import com.ecommerce.app.dto.request.RefundRequest;
import com.ecommerce.app.dto.request.VerifyPaymentRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ApiResponse<Map<String, Object>> createOrder(@Valid @RequestBody CreatePaymentOrderRequest request,
                                                          @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(paymentService.createRazorpayOrder(principal.getId(), request));
    }

    @PostMapping("/verify")
    public ApiResponse<Void> verify(@Valid @RequestBody VerifyPaymentRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        paymentService.verifyPayment(principal.getId(), request);
        return ApiResponse.ok("Payment verified successfully", null);
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<Map<String, Object>> get(@PathVariable UUID paymentId) {
        return ApiResponse.ok(paymentService.getPaymentStatus(paymentId));
    }

    @PostMapping("/webhook")
    public ApiResponse<Void> webhook(@RequestBody String payload,
                                      @RequestHeader("X-Razorpay-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ApiResponse.ok(null);
    }

    @PostMapping("/refund")
    public ApiResponse<Void> refund(@Valid @RequestBody RefundRequest request) {
        paymentService.refund(request);
        return ApiResponse.ok("Refund initiated", null);
    }

    @GetMapping("/{paymentId}/status")
    public ApiResponse<Map<String, Object>> status(@PathVariable UUID paymentId) {
        return ApiResponse.ok(paymentService.getPaymentStatus(paymentId));
    }
}
