package com.example.LT_Web2.controllers.user;

import com.example.LT_Web2.dto.request.OrderRequest;
import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.entity.Order;
import com.example.LT_Web2.entity.User;
import com.example.LT_Web2.services.OrderService;
import com.example.LT_Web2.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtService jwtService;

    /**
     * Tạo đơn hàng mới
     * POST /api/user/orders
     * Body: OrderRequest
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        Order order = orderService.createOrder(request, userId);
        return ResponseEntity.ok(new OrderResponse(order, orderService.getOrderItems(order.getId())));
    }

    /**
     * Xem tất cả đơn hàng của mình
     * GET /api/user/orders
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        return ResponseEntity.ok(orderService.getOrdersByCustomer(userId));
    }

    /**
     * Xem chi tiết đơn hàng
     * GET /api/user/orders/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long id, HttpServletRequest httpRequest) {
        // Kiểm tra quyền: chỉ xem được đơn của mình
        Long userId = getCurrentUserId(httpRequest);
        OrderResponse order = orderService.getOrderDetail(id);
        if (!order.getCustomerId().equals(userId)) {
            throw new SecurityException("Bạn không có quyền xem đơn hàng này");
        }
        return ResponseEntity.ok(order);
    }

    // Helper: Lấy ID người dùng hiện tại từ JWT token
    private Long getCurrentUserId(HttpServletRequest request) {
        // Try to get from JWT token first
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Long userId = jwtService.extractUserId(token);
                if (userId != null) {
                    System.out.println("✅ Extracted userId from JWT: " + userId);
                    return userId;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Cannot extract userId from JWT: " + e.getMessage());
            }
        }

        // Fallback to SecurityContext
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            System.out.println("✅ Got userId from SecurityContext: " + user.getId());
            return user.getId();
        }

        // Log chi tiết để debug
        System.err.println("❌ Cannot get user ID. Auth: " + auth);
        if (auth != null) {
            System.err.println("Principal type: " + auth.getPrincipal().getClass().getName());
            System.err.println("Principal: " + auth.getPrincipal());
        }
        throw new IllegalStateException("User not authenticated or principal is not User type");
    }
}