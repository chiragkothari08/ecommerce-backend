package com.ecommerce.app.dto.response;

import com.ecommerce.app.entity.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private String role;

    public static UserResponse from(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setProfileImageUrl(user.getProfileImageUrl());
        r.setRole(user.getRole().name());
        return r;
    }
}
