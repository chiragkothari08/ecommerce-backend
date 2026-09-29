package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.*;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.AuthResponse;
import com.ecommerce.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Module 1: Authentication & User Management
 * POST /auth/register
 * POST /auth/login
 * POST /auth/send-otp
 * POST /auth/verify-otp
 * POST /auth/google
 * POST /auth/forgot-password
 * POST /auth/reset-password
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Registered successfully", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login successful", authService.login(request));
    }

    @PostMapping("/send-otp")
    public ApiResponse<Void> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.sendOtp(request);
        return ApiResponse.ok("OTP sent successfully", null);
    }

    @PostMapping("/verify-otp")
    public ApiResponse<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ApiResponse.ok("OTP verified, login successful", authService.verifyOtpAndLogin(request));
    }

    @PostMapping("/google")
    public ApiResponse<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return ApiResponse.ok("Login successful", authService.googleLogin(request));
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.ok("If an account exists, a reset link has been sent", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok("Password reset successfully", null);
    }
}
