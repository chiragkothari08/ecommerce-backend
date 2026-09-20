package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.CreatePaymentOrderRequest;
import com.ecommerce.app.dto.request.RefundRequest;
import com.ecommerce.app.dto.request.VerifyPaymentRequest;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.OrderStatus;
import com.ecommerce.app.entity.Payment;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.repository.PaymentRepository;
import com.ecommerce.app.service.NotificationService;
import com.ecommerce.app.service.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${app.razorpay.webhook-secret}")
    private String webhookSecret;

    @Override
    @Transactional
    public Map<String, Object> createRazorpayOrder(UUID userId, CreatePaymentOrderRequest request) {
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), userId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));

        try {
            JSONObject options = new JSONObject();
            // Razorpay expects amount in the smallest currency unit (paise for INR)
            options.put("amount", order.getTotal().multiply(BigDecimal.valueOf(100)).intValue());
            options.put("currency", "INR");
            options.put("receipt", order.getOrderNumber());

            com.razorpay.Order rzpOrder = razorpayClient.orders.create(options);

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setRazorpayOrderId(rzpOrder.get("id"));
            payment.setAmount(order.getTotal());
            payment.setStatus(Payment.PaymentStatus.CREATED);
            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", rzpOrder.get("id"));
            response.put("amount", options.get("amount"));
            response.put("currency", "INR");
            response.put("orderId", order.getId());
            return response;
        } catch (RazorpayException e) {
            throw ApiException.badRequest("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void verifyPayment(UUID userId, VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> ApiException.notFound("Payment record not found"));

        Order order = payment.getOrder();
        if (!order.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("This payment does not belong to the current user");
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, webhookSecretOrKeySecret());

            if (!isValid) {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("Signature verification failed");
                paymentRepository.save(payment);
                throw ApiException.badRequest("Payment signature verification failed");
            }

            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            order.setPaid(true);
            orderRepository.save(order);

            notificationService.send(userId, "Payment Successful",
                    "Payment for order " + order.getOrderNumber() + " was successful.", "ORDER_UPDATE", order.getId().toString());
        } catch (RazorpayException e) {
            throw ApiException.badRequest("Signature verification error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signatureHeader) {
        try {
            boolean valid = Utils.verifyWebhookSignature(payload, signatureHeader, webhookSecret);
            if (!valid) {
                throw ApiException.badRequest("Invalid webhook signature");
            }
            JSONObject json = new JSONObject(payload);
            String event = json.optString("event");

            if ("payment.captured".equals(event)) {
                JSONObject paymentEntity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                String rzpOrderId = paymentEntity.optString("order_id");
                paymentRepository.findByRazorpayOrderId(rzpOrderId).ifPresent(payment -> {
                    payment.setStatus(Payment.PaymentStatus.SUCCESS);
                    payment.setRazorpayPaymentId(paymentEntity.optString("id"));
                    paymentRepository.save(payment);
                    Order order = payment.getOrder();
                    order.setPaid(true);
                    orderRepository.save(order);
                });
            } else if ("payment.failed".equals(event)) {
                JSONObject paymentEntity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                String rzpOrderId = paymentEntity.optString("order_id");
                paymentRepository.findByRazorpayOrderId(rzpOrderId).ifPresent(payment -> {
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setFailureReason(paymentEntity.optString("error_description"));
                    paymentRepository.save(payment);
                });
            }
        } catch (RazorpayException e) {
            throw ApiException.badRequest("Webhook signature verification failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void refund(RefundRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> ApiException.notFound("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.SUCCESS) {
            throw ApiException.badRequest("Only successful payments can be refunded");
        }

        try {
            JSONObject refundRequest = new JSONObject();
            BigDecimal amount = request.getAmount() != null ? request.getAmount() : payment.getAmount();
            refundRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());

            razorpayClient.payments.refund(payment.getRazorpayPaymentId(), refundRequest);

            boolean full = request.getAmount() == null || amount.compareTo(payment.getAmount()) == 0;
            payment.setStatus(full ? Payment.PaymentStatus.REFUNDED : Payment.PaymentStatus.PARTIALLY_REFUNDED);
            paymentRepository.save(payment);

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.RETURNED);
            orderRepository.save(order);
        } catch (RazorpayException e) {
            throw ApiException.badRequest("Refund failed: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getPaymentStatus(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> ApiException.notFound("Payment not found"));
        Map<String, Object> map = new HashMap<>();
        map.put("paymentId", payment.getId());
        map.put("status", payment.getStatus());
        map.put("amount", payment.getAmount());
        map.put("razorpayPaymentId", payment.getRazorpayPaymentId());
        return map;
    }

    // Razorpay's Utils.verifyPaymentSignature expects the KEY SECRET, not the webhook secret.
    // Kept as a separate accessor in case the two are configured to different values.
    private String webhookSecretOrKeySecret() {
        return razorpayKeySecretFallback();
    }

    @Value("${app.razorpay.key-secret}")
    private String razorpayKeySecret;

    private String razorpayKeySecretFallback() {
        return razorpayKeySecret;
    }
}
