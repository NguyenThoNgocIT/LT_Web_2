package com.example.LT_Web2.dto.response;

import com.example.LT_Web2.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderItemResponse {
    private String productName;
    private int quantity;
    private BigDecimal price;

    public OrderItemResponse(OrderItem item) {
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
        this.price = item.getPriceAtOrder();
    }

}
