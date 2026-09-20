package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminReturnActionRequest {
    @NotBlank
    private String action; // "approve" or "reject"

    private String remarks;
}
