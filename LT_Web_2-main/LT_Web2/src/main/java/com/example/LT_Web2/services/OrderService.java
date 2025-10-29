package com.example.LT_Web2.services;

import com.example.LT_Web2.dto.request.OrderRequest;
import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.dto.response.ReportResponse;
import com.example.LT_Web2.entity.Order;
import com.example.LT_Web2.entity.OrderItem;
import com.example.LT_Web2.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse getOrderDetail(Long id);

    Order createOrder(OrderRequest request, Long customerId);

    List<OrderItem> getOrderItems(Long orderId);

    List<OrderResponse> getAllOrders(); // Admin

    List<OrderResponse> getOrdersByCustomer(Long customerId); // Khách

    Order updateOrderStatus(Long id, OrderStatus newStatus);

    ReportResponse getDailyReport();

    ReportResponse getWeeklyReport();

    ReportResponse getMonthlyReport();

    /**
     * Thống kê doanh thu theo kiểu (day/week/month) và khoảng thời gian
     * 
     * @param type: "day", "week", "month"
     * @param from: ngày bắt đầu (có thể null)
     * @param to:   ngày kết thúc (có thể null)
     * @return Map<String, Object> gồm các trường: labels, values, total
     */
    java.util.Map<String, Object> getRevenueStatistics(String type, java.time.LocalDate from, java.time.LocalDate to);
}
