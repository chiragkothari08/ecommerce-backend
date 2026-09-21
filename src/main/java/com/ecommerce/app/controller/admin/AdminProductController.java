package com.ecommerce.app.controller.admin;

import com.ecommerce.app.dto.request.ProductRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<Map<String, UUID>> create(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok("Product created", Map.of("id", productService.createProduct(request)));
    }

    @GetMapping
    public ApiResponse<PageResponse<?>> list(@RequestParam(required = false) String category,
                                              @RequestParam(required = false) String brand,
                                              Pageable pageable) {
        return ApiResponse.ok(productService.listProducts(category, brand, null, null, null, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> get(@PathVariable UUID id) {
        return ApiResponse.ok(productService.getProduct(id, null));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        productService.updateProduct(id, request);
        return ApiResponse.ok("Product updated", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ApiResponse.ok("Product deleted", null);
    }
}
