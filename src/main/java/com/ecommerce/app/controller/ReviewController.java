package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.ReviewRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PutMapping("/api/reviews/{reviewId}")
    public ApiResponse<Void> update(@PathVariable UUID reviewId, @Valid @RequestBody ReviewRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        reviewService.updateReview(principal.getId(), reviewId, request);
        return ApiResponse.ok("Review updated", null);
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    public ApiResponse<Void> delete(@PathVariable UUID reviewId, @AuthenticationPrincipal UserPrincipal principal) {
        reviewService.deleteReview(principal.getId(), reviewId);
        return ApiResponse.ok("Review deleted", null);
    }

    @PostMapping("/api/reviews/{reviewId}/helpful")
    public ApiResponse<Void> markHelpful(@PathVariable UUID reviewId) {
        reviewService.markHelpful(reviewId);
        return ApiResponse.ok("Marked as helpful", null);
    }
}
