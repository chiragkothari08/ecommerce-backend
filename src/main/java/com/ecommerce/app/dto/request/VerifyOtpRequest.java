package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank
    private String phone;

    @NotBlank @Size(min = 6, max = 6)
    private String otp;

    private String purpose = "LOGIN";
}
