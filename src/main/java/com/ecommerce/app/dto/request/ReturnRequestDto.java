package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReturnRequestDto {
    @NotNull
    private UUID orderId;

    @NotBlank
    private String reason;
}
