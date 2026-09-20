package com.ecommerce.app.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String email;
}
