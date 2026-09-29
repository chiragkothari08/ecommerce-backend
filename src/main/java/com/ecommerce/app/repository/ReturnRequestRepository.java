package com.ecommerce.app.repository;

import com.ecommerce.app.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, UUID> {
    List<ReturnRequest> findByUserId(UUID userId);
}
