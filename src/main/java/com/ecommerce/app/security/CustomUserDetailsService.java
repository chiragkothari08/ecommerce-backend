package com.ecommerce.app.security;

import com.ecommerce.app.entity.User;
import com.ecommerce.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdOrEmail) throws UsernameNotFoundException {
        User user;
        try {
            UUID id = UUID.fromString(userIdOrEmail);
            user = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userIdOrEmail));
        } catch (IllegalArgumentException e) {
            user = userRepository.findByEmail(userIdOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userIdOrEmail));
        }
        return new UserPrincipal(user);
    }
}
