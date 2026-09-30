package com.ecommerce.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    private String label;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String country;

    private Double latitude;
    private Double longitude;
    private boolean isDefault;
}
