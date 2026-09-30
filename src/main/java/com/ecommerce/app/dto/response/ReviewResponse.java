package com.ecommerce.app.dto.response;

import com.ecommerce.app.entity.Review;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ReviewResponse {
    private UUID id;
    private UUID productId;
    private UUID userId;
    private String userName;
    private Integer rating;
    private String comment;
    private Integer helpfulCount;
    private Instant createdAt;

    public static ReviewResponse from(Review review) {
        ReviewResponse r = new ReviewResponse();
        r.setId(review.getId());
        r.setProductId(review.getProduct().getId());
        r.setUserId(review.getUser().getId());
        r.setUserName(review.getUser().getName());
        r.setRating(review.getRating());
        r.setComment(review.getComment());
        r.setHelpfulCount(review.getHelpfulCount());
        r.setCreatedAt(review.getCreatedAt());
        return r;
    }
}
