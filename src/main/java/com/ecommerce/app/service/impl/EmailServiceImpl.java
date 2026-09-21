package com.ecommerce.app.service.impl;

import com.ecommerce.app.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:admin.medinexa@gmail.com}")
    private String fromEmail;

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to StyleHub! 🛍️");
            message.setText("Hi " + name + ",\n\nWelcome to StyleHub! Your account has been created successfully.\nHappy Shopping!\n\nBest Regards,\nStyleHub Team");
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("StyleHub - Your Verification Code");
            message.setText("Hello,\n\nYour StyleHub OTP verification code is: " + otp + "\n\nThis code will expire in 5 minutes.\nDo not share this code with anyone.\n\nStyleHub Team");
            mailSender.send(message);
            log.info("OTP email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("StyleHub - Password Reset Request");
            message.setText("Hello,\n\nWe received a request to reset your password. Use the token or code below to proceed:\n\n" + resetToken + "\n\nIf you did not request this, please ignore this email.\n\nStyleHub Team");
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }
}
