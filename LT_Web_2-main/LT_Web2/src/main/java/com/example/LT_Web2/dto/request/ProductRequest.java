package com.example.LT_Web2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductRequest {
    @NotBlank private String name;
    @NotNull private BigDecimal price;
    private String description;
    private String imageUrl;
    @NotBlank private String category;
    @NotBlank private String status; // "AVAILABLE" / "OUT_OF_STOCK"


}