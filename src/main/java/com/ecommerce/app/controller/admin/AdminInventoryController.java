package com.ecommerce.app.controller.admin;

import com.ecommerce.app.dto.request.ProductRequest;
import com.ecommerce.app.dto.request.UpdateStockRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.dto.response.ProductResponse;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final ProductRepository productRepository;

    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> list(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        return ApiResponse.ok(PageResponse.from(page.map(ProductResponse::from)));
    }

    @PatchMapping("/{productId}")
    @Transactional
    public ApiResponse<Void> updateStock(@PathVariable UUID productId, @Valid @RequestBody UpdateStockRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> ApiException.notFound("Product not found"));
        product.setStock(request.getStock());
        productRepository.save(product);
        return ApiResponse.ok("Stock updated", null);
    }
}
