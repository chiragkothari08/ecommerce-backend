package com.ecommerce.app.controller.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.app.dto.request.ProductRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.ProductImage;
import com.ecommerce.app.entity.ProductVariant;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.ProductImageRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.ProductVariantRepository;
import com.ecommerce.app.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final Cloudinary cloudinary;

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

    /**
     * Upload a photo and attach it to a product — optionally tagged to one
     * specific color/size variant. Pass variantId to make this photo show up
     * ONLY when the frontend has that variant selected (e.g. "Red" photos).
     * Omit variantId for a general photo shown regardless of color.
     */
    @PostMapping("/{id}/images")
    @Transactional
    public ApiResponse<Map<String, Object>> addImage(@PathVariable UUID id,
                                                       @RequestParam("file") MultipartFile file,
                                                       @RequestParam(required = false) UUID variantId,
                                                       @RequestParam(required = false, defaultValue = "0") int sortOrder) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found"));

        ProductVariant variant = null;
        if (variantId != null) {
            variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> ApiException.notFound("Variant not found"));
            if (!variant.getProduct().getId().equals(id)) {
                throw ApiException.badRequest("That variant does not belong to this product");
            }
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "ecommerce/products"));

            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setVariant(variant);
            image.setUrl((String) uploadResult.get("secure_url"));
            image.setCloudinaryPublicId((String) uploadResult.get("public_id"));
            image.setSortOrder(sortOrder);
            image = productImageRepository.save(image);

            return ApiResponse.ok("Image added", Map.of(
                    "imageId", image.getId(),
                    "url", image.getUrl(),
                    "variantId", variant != null ? variant.getId() : "null (general image)"
            ));
        } catch (IOException e) {
            throw ApiException.badRequest("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/images/{imageId}")
    @Transactional
    public ApiResponse<Void> deleteImage(@PathVariable UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> ApiException.notFound("Image not found"));
        try {
            if (image.getCloudinaryPublicId() != null) {
                cloudinary.uploader().destroy(image.getCloudinaryPublicId(), ObjectUtils.emptyMap());
            }
        } catch (IOException ignored) {
            // Cloudinary-side delete failing shouldn't block removing our own DB record
        }
        productImageRepository.delete(image);
        return ApiResponse.ok("Image deleted", null);
    }
}

