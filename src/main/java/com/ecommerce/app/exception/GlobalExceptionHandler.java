package com.ecommerce.app.exception;

import com.ecommerce.app.dto.response.ApiResponse;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Thrown whenever a request's `?sort=` query param references a field that
     * doesn't exist on the entity being queried (e.g. Swagger's default
     * placeholder value "string", or a frontend typo like "pric" instead of
     * "price"). Without this handler it surfaces as an unhandled 500 on every
     * paginated endpoint (products, orders, reviews, etc.) — this turns it into
     * a clear 400 instead.
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadSortField(PropertyReferenceException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid sort field: '" + ex.getPropertyName() +
                        "'. Check the field name and try again (e.g. sort=price,desc)."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        ApiResponse<Object> response = new ApiResponse<>(false, "Validation failed", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid credentials"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong: " + ex.getMessage()));
    }
}
