package com.ecommerce.app.controller.admin;

import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.ReturnRequest;
import com.ecommerce.app.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/returns")
@RequiredArgsConstructor
public class AdminReturnController {

    private final ReturnService returnService;

    @GetMapping
    public ApiResponse<List<ReturnRequest>> list() {
        return ApiResponse.ok(returnService.listAll());
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        returnService.approve(id, body != null ? body.get("remarks") : null);
        return ApiResponse.ok("Return approved", null);
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        returnService.reject(id, body != null ? body.get("remarks") : null);
        return ApiResponse.ok("Return rejected", null);
    }
}
