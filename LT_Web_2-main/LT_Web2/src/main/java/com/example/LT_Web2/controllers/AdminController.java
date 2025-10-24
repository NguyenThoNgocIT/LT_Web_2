package com.example.LT_Web2.controllers;

import com.example.LT_Web2.dto.request.ReservationRequest;
import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.dto.response.ReportResponse;
import com.example.LT_Web2.dto.response.ReservationResponse;
import com.example.LT_Web2.entity.*;
import com.example.LT_Web2.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
// ⚠️ KHÔNG dùng @RequestMapping("/admin") ở mức class
// → để có thể định nghĩa riêng /admin/... (web) và /api/admin/... (API)
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // =============== API ROUTES (JWT-based, JSON) ===============
    private Map<String, Object> buildResponse(String status, String message, Object data, String path) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", path);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }

    @PostMapping("/api/admin/users/save")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveUserApi(
            @RequestBody User user,
            @RequestParam(value = "companyId", required = false) Long companyId) {

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(buildResponse("error", "Email is required", null, "/api/admin/users/save"));
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(buildResponse("error", "Name is required", null, "/api/admin/users/save"));
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(buildResponse("error", "Password is required", null, "/api/admin/users/save"));
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest()
                    .body(buildResponse("error", "Email already exists", null, "/api/admin/users/save"));
        }

        // ✅ THÊM DÒNG NÀY ĐỂ MÃ HÓA MẬT KHẨU
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ ĐẢM BẢO USER CÓ ROLES
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<String> defaultRoles = new HashSet<>();
            defaultRoles.add("USER"); // hoặc "ADMIN" nếu muốn tạo admin
            user.setRoles(defaultRoles);
        }

        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(buildResponse("success", "User saved successfully", savedUser, "/api/admin/users/save"));
    }

    @DeleteMapping("/api/admin/users/delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUserApi(@PathVariable("id") Long id) {
        // 👇 THÊM KIỂM TRA KHÔNG CHO XÓA TÀI KHOẢN ADMIN MẶC ĐỊNH
        User userToDelete = userService.getUserById(id);
        if (userToDelete != null && "admin@example.com".equals(userToDelete.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(buildResponse("error", "Cannot delete default admin account", null, "/api/admin/users/delete/" + id));
        }

        userService.deleteUserById(id);
        return ResponseEntity.ok(buildResponse("success", "User deleted successfully", null, "/api/admin/users/delete/" + id));
    }

    @PutMapping("/api/admin/users/update/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateUserApi(
            @PathVariable("id") Long id,
            @RequestBody User updatedUser) {

        Map<String, String> response = new HashMap<>();

        // Kiểm tra dữ liệu đầu vào
        if (updatedUser.getName() == null || updatedUser.getName().trim().isEmpty()) {
            response.put("message", "Name is required");
            return ResponseEntity.badRequest().body(response);
        }
        if (updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }

        // Lấy user hiện tại từ DB
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }

        // Kiểm tra email trùng (nếu đổi email)
        User userWithSameEmail = userService.findByEmail(updatedUser.getEmail());
        if (userWithSameEmail != null && !userWithSameEmail.getId().equals(id)) {
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // Cập nhật thông tin
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        userService.saveUser(existingUser);
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    // =============== API: Get all users ===============
    @GetMapping("/api/admin/users")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsersApi() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // =============== API: Get dashboard stats ===============
    @GetMapping("/api/admin/dashboard")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardStatsApi() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getAllUsers().size());
        return ResponseEntity.ok(stats);
    }

    // =============== API: products ===============
    @RestController
    @RequestMapping("/api/admin/products")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public class ProductController {

        @Autowired
        private ProductService productService;

        @PostMapping
        public ResponseEntity<Product> create(@RequestBody Product product) {
            return ResponseEntity.ok(productService.save(product));
        }

        @PutMapping("/{id}")
        public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
            product.setId(id);
            return ResponseEntity.ok(productService.save(product));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
            productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping
        public ResponseEntity<List<Product>> getAll() {
            return ResponseEntity.ok(productService.findAll());
        }
    }

    // =============== API: api manager table ===============
    @RestController
    @RequestMapping("/api/admin/tables")
    @CrossOrigin(origins = "*") // Có thể giới hạn theo domain production
    @RequiredArgsConstructor
    public class TableController {

        private final TableService tableService;

        /**
         * Lấy danh sách tất cả bàn
         */
        @GetMapping
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<List<Tables>> getAllTables() {
            return ResponseEntity.ok(tableService.findAll());
        }

        /**
         * Lấy bàn theo trạng thái (AVAILABLE, RESERVED, OCCUPIED, COMPLETED)
         */
        @GetMapping("/status/{status}")
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<List<Tables>> getTablesByStatus(@PathVariable String status) {
            TableStatus tableStatus = TableStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(tableService.findByStatus(tableStatus));
        }

        /**
         * Tạo bàn mới
         */
        @PostMapping
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity <List<Tables>> createTables(
                @Valid
                @RequestBody
                List<Tables> table) {
            return ResponseEntity.ok(tableService.saveAll(table));
        }

        /**
         * Cập nhật thông tin bàn (tên, vị trí) — KHÔNG cập nhật trạng thái ở đây
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<Tables> updateTable(
                @PathVariable Long id,
                @Valid @RequestBody Tables tableUpdate) {
            Tables existingTable = tableService.findById(id);
            existingTable.setName(tableUpdate.getName());
            existingTable.setLocation(tableUpdate.getLocation());
            // Không cho phép đổi trạng thái qua endpoint này
            return ResponseEntity.ok(tableService.save(existingTable));
        }

        /**
         * CẬP NHẬT TRẠNG THÁI BÀN — dùng cho nghiệp vụ (check-in, thanh toán, dọn dẹp...)
         */
        @PutMapping("/{id}/status")
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<Tables> updateTableStatus(
                @PathVariable Long id,
                @RequestBody Map<String, String> request) {

            String statusStr = request.get("status");
            if (statusStr == null || statusStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Trường 'status' không được để trống");
            }

            TableStatus newStatus = TableStatus.valueOf(statusStr.toUpperCase());
            Tables updatedTable = tableService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updatedTable);
        }

        /**
         * Xóa bàn (chỉ khi ở trạng thái AVAILABLE)
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
            tableService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }

    // =============== API: api manager quản lí đặt bàn reservation ===============
    @RestController
    @RequestMapping("/api/reservations")
    @RequiredArgsConstructor
    public class ReservationController {

        private final ReservationService reservationService;

        // 🧑‍ Khách đặt bàn
        @PostMapping
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
            Long currentUserId = getCurrentUserId();
            Reservation reservation = reservationService.createReservation(request, currentUserId);
            return ResponseEntity.ok(new ReservationResponse(reservation));
        }

        // 👁️‍ Khách xem lịch sử đặt bàn của mình
        @GetMapping("/my")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<List<ReservationResponse>> getMyReservations() {
            Long userId = getCurrentUserId();
            List<Reservation> reservations = reservationService.getReservationsByCustomer(userId);
            List<ReservationResponse> response = reservations.stream()
                    .map(ReservationResponse::new)
                    .toList();
            return ResponseEntity.ok(response);
        }

        // 👨‍💼 Admin: xem tất cả
        @GetMapping
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<List<Reservation>> getAllReservations() {
            return ResponseEntity.ok(reservationService.getAllReservations());
        }

        // ✅ Admin xác nhận
        @PutMapping("/{id}/confirm")
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<Reservation> confirm(@PathVariable Long id) {
            return ResponseEntity.ok(reservationService.confirmReservation(id));
        }

        // ❌ Admin hoặc khách hủy
        @PutMapping("/{id}/cancel")
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN', 'USER')")
        public ResponseEntity<Reservation> cancel(@PathVariable Long id) {
            return ResponseEntity.ok(reservationService.cancelReservation(id));
        }

        // 🔒 Helper: lấy ID user hiện tại từ SecurityContext (bạn cần implement)
        private Long getCurrentUserId() {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                return ((User) authentication.getPrincipal()).getId();
            }
            throw new IllegalStateException("Không thể lấy userId từ SecurityContext");
        }
    }
    // =============== API: api manager quản lí oder and báo cáo   ===============
    @RestController
    @RequestMapping("/api/orders")
    @RequiredArgsConstructor
    public class OrderController {

        private final OrderService orderService;

        // 👨‍💼 Admin: xem tất cả đơn hàng
        @GetMapping
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<List<OrderResponse>> getAllOrders() {
            return ResponseEntity.ok(orderService.getAllOrders());
        }

        // 👁️‍ Khách: xem đơn hàng của mình
        @GetMapping("/my")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<List<OrderResponse>> getMyOrders() {
            Long userId = getCurrentUserId();
            return ResponseEntity.ok(orderService.getOrdersByCustomer(userId));
        }

        // 👁️‍ Xem chi tiết đơn hàng
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN', 'USER')")
        public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long id) {
            return ResponseEntity.ok(orderService.getOrderDetail(id));
        }

        // 👨‍💼 Admin: cập nhật trạng thái
        @PutMapping("/{id}/status")
        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
        public ResponseEntity<Order> updateOrderStatus(
                @PathVariable Long id,
                @RequestBody Map<String, String> request) {
            String statusStr = request.get("status");
            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
            Order updated = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updated);
        }

        // 📊 Báo cáo
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

        // Helper
        private Long getCurrentUserId() {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                return ((User) auth.getPrincipal()).getId();
            }
            throw new IllegalStateException("User not authenticated");
        }
    }
}
