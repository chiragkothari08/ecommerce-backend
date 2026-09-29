package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupportTicketRequest {
    @NotBlank
    private String subject;

    @NotBlank
    private String message;
}
