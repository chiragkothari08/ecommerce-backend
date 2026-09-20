package com.ecommerce.app.service;

import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Notification;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    PageResponse<Notification> list(UUID userId, Pageable pageable);
    void markRead(UUID userId, UUID notificationId);
    void markAllRead(UUID userId);
    void delete(UUID userId, UUID notificationId);
    void saveDeviceToken(UUID userId, String token);
    void removeDeviceToken(UUID userId);
    void send(UUID userId, String title, String body, String type, String referenceId);
}
