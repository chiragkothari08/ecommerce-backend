package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.ChangePasswordRequest;
import com.ecommerce.app.dto.request.UpdateProfileRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.dto.response.UserResponse;
import com.ecommerce.app.entity.Notification;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.NotificationService;
import com.ecommerce.app.service.OrderService;
import com.ecommerce.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<UserResponse> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(userService.getProfile(principal.getId()));
    }

    @PutMapping
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                    @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Profile updated", userService.updateProfile(principal.getId(), request));
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        userService.changePassword(principal.getId(), request);
        return ApiResponse.ok("Password changed successfully", null);
    }

    @PostMapping("/profile-image")
    public ApiResponse<String> updateProfileImage(@RequestParam("file") MultipartFile file,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Profile image updated", userService.updateProfileImage(principal.getId(), file));
    }

    @GetMapping("/orders")
    public ApiResponse<PageResponse<?>> orders(@AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return ApiResponse.ok(orderService.listOrders(principal.getId(), pageable));
    }

    @GetMapping("/notifications")
    public ApiResponse<PageResponse<Notification>> notifications(@AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return ApiResponse.ok(notificationService.list(principal.getId(), pageable));
    }
}
