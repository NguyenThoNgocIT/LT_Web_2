package com.example.LT_Web2.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "table_id")
        private Tables table;

        @ManyToOne
        @JoinColumn(name = "customer_id")
        private User customer;

        private BigDecimal totalAmount;
        private LocalDateTime createdAt = LocalDateTime.now();

        @Enumerated(EnumType.STRING)
        private OrderStatus status = OrderStatus.PENDING;
    }
