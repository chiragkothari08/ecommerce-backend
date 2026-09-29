package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.*;
import com.ecommerce.app.dto.response.AuthResponse;
import com.ecommerce.app.dto.response.UserResponse;
import com.ecommerce.app.entity.*;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.OtpVerificationRepository;
import com.ecommerce.app.repository.RefreshTokenRepository;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.security.JwtUtil;
import com.ecommerce.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("Email already registered");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw ApiException.conflict("Phone already registered");
        }

        User user = new User();
        user.setName((request.getFirstName() + " " + (request.getLastName() == null ? "" : request.getLastName())).trim());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailOrPhone(request.getEmailOrPhone(), request.getEmailOrPhone())
                .orElseThrow(() -> ApiException.unauthorized("Invalid credentials"));

        if (user.isBlocked()) {
            throw ApiException.forbidden("Account is blocked. Contact support.");
        }
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid credentials");
        }
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void sendOtp(SendOtpRequest request) {
        String otp = generateOtp();
        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setIdentifier(request.getPhone());
        otpEntity.setOtpHash(passwordEncoder.encode(otp));
        otpEntity.setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        otpEntity.setPurpose(request.getPurpose());
        otpVerificationRepository.save(otpEntity);

        // In production: integrate with SMS gateway (e.g. MSG91, Twilio) here.
        // For now the OTP is logged server-side for development/testing purposes.
        System.out.println("[DEV] OTP for " + request.getPhone() + " (" + request.getPurpose() + "): " + otp);
    }

    @Override
    @Transactional
    public AuthResponse verifyOtpAndLogin(VerifyOtpRequest request) {
        OtpVerification otpEntity = otpVerificationRepository
                .findTopByIdentifierAndPurposeOrderByCreatedAtDesc(request.getPhone(), request.getPurpose())
                .orElseThrow(() -> ApiException.badRequest("OTP not requested for this number"));

        if (otpEntity.isVerified()) {
            throw ApiException.badRequest("OTP already used");
        }
        if (otpEntity.getExpiresAt().isBefore(Instant.now())) {
            throw ApiException.badRequest("OTP expired");
        }
        if (otpEntity.getAttempts() >= 5) {
            throw ApiException.badRequest("Too many attempts. Request a new OTP.");
        }
        if (!passwordEncoder.matches(request.getOtp(), otpEntity.getOtpHash())) {
            otpEntity.setAttempts(otpEntity.getAttempts() + 1);
            otpVerificationRepository.save(otpEntity);
            throw ApiException.badRequest("Incorrect OTP");
        }

        otpEntity.setVerified(true);
        otpVerificationRepository.save(otpEntity);

        User user = userRepository.findByPhone(request.getPhone()).orElseGet(() -> {
            User u = new User();
            u.setName("User");
            u.setPhone(request.getPhone());
            u.setPhoneVerified(true);
            u.setRole(Role.CUSTOMER);
            return userRepository.save(u);
        });
        user.setPhoneVerified(true);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        // NOTE: In production, verify request.getIdToken() using Google's
        // GoogleIdTokenVerifier (com.google.api-client) against app.google.oauth-client-id,
        // then extract email/name/googleId from the verified payload.
        throw ApiException.badRequest("Google login verification not configured. Wire up GoogleIdTokenVerifier with app.google.oauth-client-id.");
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> ApiException.unauthorized("Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpiryDate().isBefore(Instant.now())) {
            throw ApiException.unauthorized("Refresh token expired. Please login again.");
        }

        User user = stored.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        return new AuthResponse(newAccessToken, stored.getToken(), UserResponse.from(user));
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            // In production: generate a signed reset token and email it via spring-boot-starter-mail.
            System.out.println("[DEV] Password reset requested for: " + user.getEmail());
        });
        // Always respond success-like (handled at controller) to avoid email enumeration.
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // NOTE: wire this up to validate request.getResetToken() (e.g. a short-lived JWT
        // emailed in forgotPassword) and then update the corresponding user's password.
        throw ApiException.badRequest("Reset token validation not wired to an email provider yet.");
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpirationMs()));
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenValue, UserResponse.from(user));
    }

    private String generateOtp() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }
}
