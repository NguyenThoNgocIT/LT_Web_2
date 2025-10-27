package com.example.LT_Web2.controllers.user;

import java.util.List;
import java.util.stream.Collectors;
import com.example.LT_Web2.dto.response.ProductResponse;
import com.example.LT_Web2.entity.Product;
import com.example.LT_Web2.entity.ProductStatus;
import com.example.LT_Web2.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/menu")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getMenu() {
        List<Product> products = productService.findByStatus(ProductStatus.AVAILABLE);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}