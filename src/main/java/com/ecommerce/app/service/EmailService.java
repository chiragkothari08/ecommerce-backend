package com.ecommerce.app.service;

public interface EmailService {
    void sendWelcomeEmail(String toEmail, String name);
    void sendOtpEmail(String toEmail, String otp);
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
