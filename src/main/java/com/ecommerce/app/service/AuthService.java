package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.*;
import com.ecommerce.app.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void sendOtp(SendOtpRequest request);
    AuthResponse verifyOtpAndLogin(VerifyOtpRequest request);
    AuthResponse googleLogin(GoogleLoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void logout(String refreshToken);
}
