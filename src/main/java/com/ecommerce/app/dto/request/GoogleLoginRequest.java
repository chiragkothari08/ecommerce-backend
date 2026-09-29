package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    @NotBlank
    private String idToken; // Google ID token from Flutter google_sign_in
}
