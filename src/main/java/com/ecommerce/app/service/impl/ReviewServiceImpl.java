package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.ReviewRequest;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.dto.response.ReviewResponse;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.Review;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.ReviewRepository;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> listByProduct(UUID productId, Pageable pageable) {
        // @Transactional keeps the Hibernate session open for this whole method,
        // so mapping to ReviewResponse (which touches review.getProduct()/getUser())
        // doesn't blow up with a LazyInitializationException — open-in-view is
        // disabled project-wide, so this would otherwise fail once the request
        // left the service layer.
        Page<Review> page = reviewRepository.findByProductId(productId, pageable);
        return PageResponse.from(page.map(ReviewResponse::from));
    }

    @Override
    @Transactional
    public ReviewResponse addReview(UUID userId, UUID productId, ReviewRequest request) {
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw ApiException.conflict("You have already reviewed this product");
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> ApiException.notFound("Product not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);

        recalculateProductRating(product);
        return ReviewResponse.from(review);
    }

    @Override
    @Transactional
    public void updateReview(UUID userId, UUID reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> ApiException.notFound("Review not found"));
        if (!review.getUser().getId().equals(userId)) throw ApiException.forbidden("Not your review");
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        reviewRepository.save(review);
        recalculateProductRating(review.getProduct());
    }

    @Override
    @Transactional
    public void deleteReview(UUID userId, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> ApiException.notFound("Review not found"));
        if (!review.getUser().getId().equals(userId)) throw ApiException.forbidden("Not your review");
        Product product = review.getProduct();
        reviewRepository.delete(review);
        recalculateProductRating(product);
    }

    @Override
    @Transactional
    public void markHelpful(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> ApiException.notFound("Review not found"));
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    private void recalculateProductRating(Product product) {
        List<Review> reviews = reviewRepository.findByProductId(product.getId());
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        product.setAvgRating(Math.round(avg * 10.0) / 10.0);
        product.setReviewCount(reviews.size());
        productRepository.save(product);
    }
}
