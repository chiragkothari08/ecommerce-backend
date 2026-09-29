package com.ecommerce.app.controller.admin;

import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.Role;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<Page<User>> list(Pageable pageable) {
        return ApiResponse.ok(userRepository.findAll(pageable));
    }

    @PutMapping("/{id}")
    @Transactional
    public ApiResponse<Void> update(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        User user = userRepository.findById(id).orElseThrow(() -> ApiException.notFound("User not found"));
        if (updates.containsKey("name")) user.setName((String) updates.get("name"));
        if (updates.containsKey("role")) user.setRole(Role.valueOf((String) updates.get("role")));
        userRepository.save(user);
        return ApiResponse.ok("User updated", null);
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ApiResponse<Void> updateStatus(@PathVariable UUID id, @RequestBody Map<String, Boolean> body) {
        User user = userRepository.findById(id).orElseThrow(() -> ApiException.notFound("User not found"));
        user.setBlocked(Boolean.TRUE.equals(body.get("blocked")));
        userRepository.save(user);
        return ApiResponse.ok("User status updated", null);
    }
}
