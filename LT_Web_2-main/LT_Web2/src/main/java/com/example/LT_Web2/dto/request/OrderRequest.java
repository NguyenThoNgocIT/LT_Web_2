package com.example.LT_Web2.dto.request;

import lombok.Data;

import java.util.List;
@Data
public class OrderRequest {
    private Long tableId;
    private List<OrderItemRequest> items;
    private String note;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }
}
