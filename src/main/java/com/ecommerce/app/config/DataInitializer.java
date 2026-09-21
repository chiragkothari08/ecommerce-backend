package com.ecommerce.app.config;

import com.ecommerce.app.entity.Role;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Seed default Admin User if not exists
        final String adminEmail = "admin@stylehub.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName("StyleHub Admin");
            admin.setEmail(adminEmail);
            admin.setPhone("9999999999");
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setEmailVerified(true);
            admin.setPhoneVerified(true);
            userRepository.save(admin);
            log.info("Default Admin account created successfully: {} (Role: ADMIN)", adminEmail);
        } else {
            // Ensure the user has ROLE_ADMIN
            userRepository.findByEmail(adminEmail).ifPresent(user -> {
                if (user.getRole() != Role.ADMIN) {
                    user.setRole(Role.ADMIN);
                    userRepository.save(user);
                    log.info("Updated existing user {} to ROLE_ADMIN", adminEmail);
                }
            });
        }
    }
}
