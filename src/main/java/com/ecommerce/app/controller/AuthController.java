package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.*;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.AuthResponse;
import com.ecommerce.app.dto.response.UserResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.AuthService;
import com.ecommerce.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

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
    public ApiResponse<Boolean> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtpAndLogin(request);
        return ApiResponse.ok("OTP verified", true);
    }

    @PostMapping("/login-otp")
    public ApiResponse<AuthResponse> loginWithOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ApiResponse.ok("Login successful", authService.verifyOtpAndLogin(request));
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

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.ok("Logged out successfully", null);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(userService.getProfile(principal.getId()));
    }
}
