package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @Min(1)
    private Integer quantity;
}
