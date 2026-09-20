package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.DeviceTokenRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Notification;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<PageResponse<Notification>> list(@AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return ApiResponse.ok(notificationService.list(principal.getId(), pageable));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markRead(principal.getId(), id);
        return ApiResponse.ok("Marked as read", null);
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllRead(principal.getId());
        return ApiResponse.ok("All marked as read", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.delete(principal.getId(), id);
        return ApiResponse.ok("Notification deleted", null);
    }

    @PostMapping("/device-token")
    public ApiResponse<Void> saveDeviceToken(@Valid @RequestBody DeviceTokenRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.saveDeviceToken(principal.getId(), request.getDeviceToken());
        return ApiResponse.ok("Device token saved", null);
    }

    @DeleteMapping("/device-token")
    public ApiResponse<Void> removeDeviceToken(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.removeDeviceToken(principal.getId());
        return ApiResponse.ok("Device token removed", null);
    }
}
