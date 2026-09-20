package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.ReviewRequest;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Review;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewService {
    PageResponse<Review> listByProduct(UUID productId, Pageable pageable);
    Review addReview(UUID userId, UUID productId, ReviewRequest request);
    void updateReview(UUID userId, UUID reviewId, ReviewRequest request);
    void deleteReview(UUID userId, UUID reviewId);
    void markHelpful(UUID reviewId);
}
