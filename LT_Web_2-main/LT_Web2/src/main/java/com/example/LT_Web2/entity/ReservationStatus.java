package com.example.LT_Web2.entity;

public enum ReservationStatus {
    PENDING,      // Đã đặt, chờ xác nhận
    CONFIRMED,    // Admin đã xác nhận
    CANCELLED,    // Khách hoặc admin hủy
    NO_SHOW       // Khách không đến (hệ thống tự động)
}
