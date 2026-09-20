package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.CreatePaymentOrderRequest;
import com.ecommerce.app.dto.request.RefundRequest;
import com.ecommerce.app.dto.request.VerifyPaymentRequest;

import java.util.Map;
import java.util.UUID;

public interface PaymentService {
    Map<String, Object> createRazorpayOrder(UUID userId, CreatePaymentOrderRequest request);
    void verifyPayment(UUID userId, VerifyPaymentRequest request);
    void handleWebhook(String payload, String signatureHeader);
    void refund(RefundRequest request);
    Map<String, Object> getPaymentStatus(UUID paymentId);
}
