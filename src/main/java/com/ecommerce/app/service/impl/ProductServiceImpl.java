package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.ProductRequest;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.dto.response.ProductResponse;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.RecentlyViewed;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.CategoryRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.RecentlyViewedRepository;
import com.ecommerce.app.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RecentlyViewedRepository recentlyViewedRepository;

    @Override
    public PageResponse<ProductResponse> listProducts(String category, String brand, Double minPrice, Double maxPrice, String sort, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));
            if (category != null) predicates.add(cb.equal(root.get("category").get("id"), UUID.fromString(category)));
            if (brand != null) predicates.add(cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
            if (minPrice != null) predicates.add(cb.ge(root.get("price"), BigDecimal.valueOf(minPrice)));
            if (maxPrice != null) predicates.add(cb.le(root.get("price"), BigDecimal.valueOf(maxPrice)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Product> page = productRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(ProductResponse::from));
    }

    @Override
    @Transactional
    public ProductResponse getProduct(UUID id, UUID userId) {
        Product product = productRepository.findById(id).orElseThrow(() -> ApiException.notFound("Product not found"));

        if (userId != null) {
            RecentlyViewed rv = recentlyViewedRepository.findByUserIdAndProductId(userId, id).orElseGet(() -> {
                RecentlyViewed r = new RecentlyViewed();
                User u = new User();
                u.setId(userId);
                r.setUser(u);
                r.setProduct(product);
                return r;
            });
            recentlyViewedRepository.save(rv);
        }
        return ProductResponse.from(product);
    }

    @Override
    public List<ProductResponse> getFeatured() {
        return productRepository.findByFeaturedTrueAndActiveTrue().stream().map(ProductResponse::from).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getFlashSale() {
        return productRepository.findByFlashSaleTrueAndActiveTrue().stream().map(ProductResponse::from).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getRecommended(UUID userId) {
        // Simple placeholder: newest active products.
        // Swap in a real recommendation engine (collaborative filtering, embeddings, etc.) later.
        return productRepository.findTop10ByActiveTrueOrderByCreatedAtDesc().stream()
                .map(ProductResponse::from).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getRecentlyViewed(UUID userId) {
        return recentlyViewedRepository.findTop10ByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(rv -> ProductResponse.from(rv.getProduct())).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getRelated(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> ApiException.notFound("Product not found"));
        if (product.getCategory() == null) return List.of();
        return productRepository.findByCategoryIdAndActiveTrue(product.getCategory().getId()).stream()
                .filter(p -> !p.getId().equals(productId))
                .limit(10)
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<ProductResponse> search(String query, Pageable pageable) {
        Specification<Product> spec = (root, cq, cb) -> cb.and(
                cb.isTrue(root.get("active")),
                cb.or(
                        cb.like(cb.lower(root.get("title")), "%" + query.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + query.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("brand")), "%" + query.toLowerCase() + "%")
                )
        );
        Page<Product> page = productRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(ProductResponse::from));
    }

    @Override
    @Transactional
    public UUID createProduct(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return productRepository.save(product).getId();
    }

    @Override
    @Transactional
    public void updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> ApiException.notFound("Product not found"));
        applyRequest(product, request);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> ApiException.notFound("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStock(request.getStock());
        product.setFeatured(request.isFeatured());
        product.setFlashSale(request.isFlashSale());
        product.setFlashSaleEndTime(request.getFlashSaleEndTime());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> ApiException.notFound("Category not found"));
            product.setCategory(category);
        }
    }
}
