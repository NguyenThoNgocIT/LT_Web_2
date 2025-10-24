package com.example.LT_Web2.controllers.admin;

import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.dto.response.ReportResponse;
import com.example.LT_Web2.entity.Order;
import com.example.LT_Web2.entity.OrderStatus;
import com.example.LT_Web2.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetail(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @GetMapping("/report/daily")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<ReportResponse> getDailyReport() {
        return ResponseEntity.ok(orderService.getDailyReport());
    }

    @GetMapping("/report/weekly")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<ReportResponse> getWeeklyReport() {
        return ResponseEntity.ok(orderService.getWeeklyReport());
    }

    @GetMapping("/report/monthly")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<ReportResponse> getMonthlyReport() {
        return ResponseEntity.ok(orderService.getMonthlyReport());
    }
}