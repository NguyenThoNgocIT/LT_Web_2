package com.example.LT_Web2.entity;

public enum OrderStatus {
    PENDING,        // Mới tạo
    PREPARING,      // Đang chế biến
    SERVED,         // Đã phục vụ
    COMPLETED,      // Hoàn thành (thanh toán xong)
    CANCELLED
}
