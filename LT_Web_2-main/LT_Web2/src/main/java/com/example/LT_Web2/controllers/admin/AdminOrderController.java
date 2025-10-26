package com.example.LT_Web2.controllers.admin;

import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.dto.response.ReportResponse;
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
@CrossOrigin(origins = "*") // Cho phép FE (ví dụ http://localhost:3000) gọi API. Thay bằng domain cụ thể khi deploy.
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
    /**
     * Cập nhật trạng thái order.
     * Request body: { "status": "PENDING" | "CONFIRMED" | "DELIVERED" | ... }
     * Trả về 200 cùng Order đã cập nhật, hoặc 400 nếu thiếu/không hợp lệ giá trị status.
     */
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        if (statusStr == null || statusStr.trim().isEmpty()) {
            // Trả về 400 nếu client không gửi field 'status'
            return ResponseEntity.badRequest().body("Missing or empty 'status' field");
        }
        try {
            OrderStatus status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
            return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
        } catch (IllegalArgumentException ex) {
            // valueOf ném IllegalArgumentException nếu không tìm thấy enum
            return ResponseEntity.badRequest().body("Invalid status value: " + statusStr);
        }
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