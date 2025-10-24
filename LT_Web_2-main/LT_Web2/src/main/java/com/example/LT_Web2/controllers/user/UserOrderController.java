package com.example.LT_Web2.controllers.user;

import com.example.LT_Web2.dto.request.OrderRequest;
import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.entity.Order;
import com.example.LT_Web2.entity.User;
import com.example.LT_Web2.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller dành cho KHÁCH HÀNG (USER)
 * - Tạo đơn hàng
 * - Xem đơn hàng của mình
 * - Xem chi tiết đơn hàng
 */
@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;

    /**
     * Tạo đơn hàng mới
     * POST /api/user/orders
     * Body: OrderRequest
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        Long userId = getCurrentUserId();
        Order order = orderService.createOrder(request, userId);
        return ResponseEntity.ok(new OrderResponse(order, orderService.getOrderItems(order.getId())));
    }

    /**
     * Xem tất cả đơn hàng của mình
     * GET /api/user/orders
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(orderService.getOrdersByCustomer(userId));
    }

    /**
     * Xem chi tiết đơn hàng
     * GET /api/user/orders/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long id) {
        // Kiểm tra quyền: chỉ xem được đơn của mình
        Long userId = getCurrentUserId();
        OrderResponse order = orderService.getOrderDetail(id);
        if (!order.getCustomerId().equals(userId)) {
            throw new SecurityException("Bạn không có quyền xem đơn hàng này");
        }
        return ResponseEntity.ok(order);
    }

    // Helper: Lấy ID người dùng hiện tại từ JWT
    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return ((User) auth.getPrincipal()).getId();
        }
        throw new IllegalStateException("User not authenticated");
    }
}