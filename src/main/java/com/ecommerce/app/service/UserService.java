package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.ChangePasswordRequest;
import com.ecommerce.app.dto.request.UpdateProfileRequest;
import com.ecommerce.app.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {
    UserResponse getProfile(UUID userId);
    UserResponse updateProfile(UUID userId, UpdateProfileRequest request);
    void changePassword(UUID userId, ChangePasswordRequest request);
    String updateProfileImage(UUID userId, MultipartFile file);
}
