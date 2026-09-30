package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotNull
    private UUID addressId;

    @NotBlank
    private String paymentMethod; // UPI, CARD, NETBANKING, WALLET, COD

    private String couponCode;
}
