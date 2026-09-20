package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.AddressRequest;
import com.ecommerce.app.dto.response.AddressResponse;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressResponse>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(addressService.list(principal.getId()));
    }

    @PostMapping
    public ApiResponse<AddressResponse> create(@Valid @RequestBody AddressRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Address added", addressService.create(principal.getId(), request));
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> get(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(addressService.get(principal.getId(), id));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@PathVariable UUID id, @Valid @RequestBody AddressRequest request,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Address updated", addressService.update(principal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        addressService.delete(principal.getId(), id);
        return ApiResponse.ok("Address deleted", null);
    }

    @PatchMapping("/{id}/default")
    public ApiResponse<Void> setDefault(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        addressService.setDefault(principal.getId(), id);
        return ApiResponse.ok("Default address updated", null);
    }
}
