package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.ReturnRequestDto;
import com.ecommerce.app.entity.ReturnRequest;

import java.util.List;
import java.util.UUID;

public interface ReturnService {
    ReturnRequest create(UUID userId, ReturnRequestDto request);
    List<ReturnRequest> listByUser(UUID userId);
    void cancel(UUID userId, UUID returnId);
    List<ReturnRequest> listAll(); // admin
    void approve(UUID returnId, String remarks); // admin
    void reject(UUID returnId, String remarks); // admin
}
