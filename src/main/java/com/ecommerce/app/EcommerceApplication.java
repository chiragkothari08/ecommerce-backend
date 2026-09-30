package com.ecommerce.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * E-Commerce Mobile App Backend
 * Stack: Spring Boot 3, PostgreSQL, Redis, JWT + OTP, Razorpay, Cloudinary, Firebase
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
