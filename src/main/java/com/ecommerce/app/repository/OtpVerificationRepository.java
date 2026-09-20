package com.ecommerce.app.repository;

import com.ecommerce.app.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {
    Optional<OtpVerification> findTopByIdentifierAndPurposeOrderByCreatedAtDesc(String identifier, String purpose);
}
