package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.AddressRequest;
import com.ecommerce.app.dto.request.UpdateProfileRequest;
import com.ecommerce.app.dto.response.AddressResponse;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.UserResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.AddressService;
import com.ecommerce.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Module 2: User Profile & Addresses
 * GET  /user/profile
 * PUT  /user/profile
 * GET  /user/addresses
 * POST /user/addresses
 * PUT  /user/addresses/:id
 * DELETE /user/addresses/:id
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileApiController {

    private final UserService userService;
    private final AddressService addressService;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(userService.getProfile(principal.getId()));
    }

    @PutMapping("/profile")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                    @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Profile updated", userService.updateProfile(principal.getId(), request));
    }

    @GetMapping("/addresses")
    public ApiResponse<List<AddressResponse>> getAddresses(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(addressService.list(principal.getId()));
    }

    @PostMapping("/addresses")
    public ApiResponse<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request,
                                                    @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Address added", addressService.create(principal.getId(), request));
    }

    @PutMapping("/addresses/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable UUID id, @Valid @RequestBody AddressRequest request,
                                                       @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Address updated", addressService.update(principal.getId(), id, request));
    }

    @DeleteMapping("/addresses/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        addressService.delete(principal.getId(), id);
        return ApiResponse.ok("Address deleted", null);
    }
}
