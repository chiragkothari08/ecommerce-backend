package com.ecommerce.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "otp_verifications")
public class OtpVerification extends BaseEntity {

    // NOTE: Java field renamed from "phoneOrEmail" to "identifier" so Spring Data JPA
    // doesn't misparse the method name as "phone" OR "email" (two separate properties).
    // The DB column name is kept the same via @Column(name=...) so no migration change needed.
    @Column(name = "phone_or_email", nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private String purpose; // REGISTER, LOGIN, RESET_PASSWORD
}
