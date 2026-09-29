package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {
    @NotBlank
    private String phone; // 10 digit phone

    private String purpose = "LOGIN"; // REGISTER, LOGIN, RESET_PASSWORD
}
