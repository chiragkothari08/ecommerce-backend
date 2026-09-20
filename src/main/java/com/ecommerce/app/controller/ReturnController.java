package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.ReturnRequestDto;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.ReturnRequest;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    public ApiResponse<ReturnRequest> create(@Valid @RequestBody ReturnRequestDto request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Return request created", returnService.create(principal.getId(), request));
    }

    @GetMapping
    public ApiResponse<List<ReturnRequest>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(returnService.listByUser(principal.getId()));
    }

    @GetMapping("/{returnId}")
    public ApiResponse<ReturnRequest> get(@PathVariable UUID returnId, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(returnService.listByUser(principal.getId()).stream()
                .filter(r -> r.getId().equals(returnId)).findFirst().orElse(null));
    }

    @PostMapping("/{returnId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable UUID returnId, @AuthenticationPrincipal UserPrincipal principal) {
        returnService.cancel(principal.getId(), returnId);
        return ApiResponse.ok("Return cancelled", null);
    }
}
