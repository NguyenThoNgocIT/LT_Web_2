package com.example.LT_Web2.dto.response;

import com.example.LT_Web2.entity.Order;
import com.example.LT_Web2.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderResponse {
    private Long id;
    private String tableName;
    private String customerName;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String status;
    private Long customerId;
    private List<OrderItemResponse> items;

    public OrderResponse(Order order, List<OrderItem> items) {
        this.id = order.getId();
        this.tableName = order.getTable() != null ? order.getTable().getName() : "N/A";
        this.customerName = order.getCustomer() != null ? order.getCustomer().getName() : "Ẩn danh";
        this.totalAmount = order.getTotalAmount();
        this.createdAt = order.getCreatedAt();
        this.customerId = order.getCustomer().getId();
        this.status = order.getStatus().name();
        this.items = items.stream().map(OrderItemResponse::new).toList();
    }
}
