package com.example.LT_Web2.services;

import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.dto.response.ReportResponse;
import com.example.LT_Web2.entity.Order;
import com.example.LT_Web2.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse getOrderDetail(Long id);
    List<OrderResponse> getAllOrders(); // Admin
    List<OrderResponse> getOrdersByCustomer(Long customerId); // Khách
    Order updateOrderStatus(Long id, OrderStatus newStatus);
    ReportResponse getDailyReport();
    ReportResponse getWeeklyReport();
    ReportResponse getMonthlyReport();
}
