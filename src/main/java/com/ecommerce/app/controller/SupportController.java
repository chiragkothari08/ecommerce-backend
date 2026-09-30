package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.SupportMessageRequest;
import com.ecommerce.app.dto.request.SupportTicketRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.SupportMessage;
import com.ecommerce.app.entity.SupportTicket;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.SupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/support/tickets")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping
    public ApiResponse<SupportTicket> create(@Valid @RequestBody SupportTicketRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Support ticket created", supportService.createTicket(principal.getId(), request));
    }

    @GetMapping
    public ApiResponse<List<SupportTicket>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(supportService.listByUser(principal.getId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<SupportTicket> get(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(supportService.getTicket(principal.getId(), id));
    }

    @PostMapping("/{id}/messages")
    public ApiResponse<SupportMessage> addMessage(@PathVariable UUID id, @Valid @RequestBody SupportMessageRequest request,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Message sent", supportService.addMessage(principal.getId(), id, request));
    }
}
