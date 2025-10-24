package com.example.LT_Web2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductRequest {
    @NotBlank private String name;
    @NotNull private BigDecimal price;
    private String description;
    private String imageUrl;
    @NotBlank private String category;
    @NotBlank private String status; // "AVAILABLE" / "OUT_OF_STOCK"

    // getters, setters
}