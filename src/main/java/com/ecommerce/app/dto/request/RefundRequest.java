package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RefundRequest {
    @NotNull
    private UUID paymentId;

    private BigDecimal amount; // null = full refund

    private String reason;
}
