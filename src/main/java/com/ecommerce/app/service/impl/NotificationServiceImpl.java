package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Notification;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.NotificationRepository;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    // NOTE: inject a FirebaseMessagingService bean here to actually push to devices
    // via the `fcmDeviceToken` stored on User, using the firebase-admin SDK.

    @Override
    public PageResponse<Notification> list(UUID userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public void markRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ApiException.notFound("Notification not found"));
        if (!notification.getUser().getId().equals(userId)) throw ApiException.forbidden("Not your notification");
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged()).forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ApiException.notFound("Notification not found"));
        if (!notification.getUser().getId().equals(userId)) throw ApiException.forbidden("Not your notification");
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void saveDeviceToken(UUID userId, String token) {
        User user = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));
        user.setFcmDeviceToken(token);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeDeviceToken(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));
        user.setFcmDeviceToken(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void send(UUID userId, String title, String body, String type, String referenceId) {
        User user = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notificationRepository.save(notification);

        // TODO: if user.getFcmDeviceToken() != null, dispatch a push via FirebaseMessaging.
    }
}
