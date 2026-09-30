package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class WishlistItemRequest {
    @NotNull
    private UUID productId;
}
