package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStockRequest {
    @NotNull
    private Integer stock;
}
