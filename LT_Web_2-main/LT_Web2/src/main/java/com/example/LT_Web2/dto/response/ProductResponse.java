package com.example.LT_Web2.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private String category;
    private String status;

    public ProductResponse(com.example.LT_Web2.entity.Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescriptions();
        this.imageUrl = product.getImageUrl();
        this.category = product.getCategory();
        this.status = product.getStatus() != null ? product.getStatus().name() : null;
    }

    // getters
}