package com.ecommerce.app.dto.request;

import com.ecommerce.app.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;
}
