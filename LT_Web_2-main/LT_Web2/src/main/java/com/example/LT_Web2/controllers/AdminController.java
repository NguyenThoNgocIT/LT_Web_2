//package com.example.LT_Web2.controllers;
//
//import com.example.LT_Web2.dto.request.OrderRequest;
//import com.example.LT_Web2.dto.request.ReservationRequest;
//import com.example.LT_Web2.dto.response.*;
//import com.example.LT_Web2.entity.*;
//import com.example.LT_Web2.services.*;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * ===================================================================================
// * CONTROLLER CHÍNH - QUẢN LÝ TẤT CẢ CÁC API ENDPOINTS
// * ===================================================================================
// * Cấu trúc:
// * 1. AdminUserController      - Quản lý users (ADMIN only)
// * 2. AdminProductController   - Quản lý sản phẩm/menu (ADMIN only)
// * 3. AdminTableController     - Quản lý bàn (ADMIN only)
// * 4. AdminReservationController - Quản lý đặt bàn (ADMIN only)
// * 5. AdminOrderController     - Quản lý đơn hàng & báo cáo (ADMIN only)
// * 6. UserController           - API cho khách hàng (USER only)
// * ===================================================================================
// */
//@Controller
//public class AdminController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    /**
//     * Helper method: Tạo response chuẩn cho API
//     */
//    private Map<String, Object> buildResponse(String status, String message, Object data, String path) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", status);
//        response.put("message", message);
//        response.put("timestamp", LocalDateTime.now().toString());
//        response.put("path", path);
//        if (data != null) {
//            response.put("data", data);
//        }
//        return response;
//    }
//
//    /**
//     * Helper method: Lấy userId từ SecurityContext
//     */
//    private Long getCurrentUserId() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof User) {
//            return ((User) authentication.getPrincipal()).getId();
//        }
//        throw new IllegalStateException("Không thể lấy userId từ SecurityContext");
//    }
//
//    // ===================================================================================
//    // 1. ADMIN - QUẢN LÝ USERS
//    // ===================================================================================
//
//    /**
//     * API: Lấy danh sách tất cả users
//     * Method: GET
//     * URL: /api/admin/users
//     * Role: ADMIN
//     */
//    @GetMapping("/api/admin/users")
//    @ResponseBody
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<User>> getAllUsersApi() {
//        List<User> users = userService.getAllUsers();
//        return ResponseEntity.ok(users);
//    }
//
//    /**
//     * API: Tạo user mới
//     * Method: POST
//     * URL: /api/admin/users/save
//     * Role: ADMIN
//     * Body: User object
//     */
//    @PostMapping("/api/admin/users/save")
//    @ResponseBody
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> saveUserApi(
//            @RequestBody User user,
//            @RequestParam(value = "companyId", required = false) Long companyId) {
//
//        // Validate input
//        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
//            return ResponseEntity.badRequest()
//                    .body(buildResponse("error", "Email is required", null, "/api/admin/users/save"));
//        }
//        if (user.getName() == null || user.getName().trim().isEmpty()) {
//            return ResponseEntity.badRequest()
//                    .body(buildResponse("error", "Name is required", null, "/api/admin/users/save"));
//        }
//        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
//            return ResponseEntity.badRequest()
//                    .body(buildResponse("error", "Password is required", null, "/api/admin/users/save"));
//        }
//
//        // Kiểm tra email đã tồn tại
//        if (userService.findByEmail(user.getEmail()) != null) {
//            return ResponseEntity.badRequest()
//                    .body(buildResponse("error", "Email already exists", null, "/api/admin/users/save"));
//        }
//
//        // Mã hóa mật khẩu
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        // Đảm bảo user có roles
//        if (user.getRoles() == null || user.getRoles().isEmpty()) {
//            Set<String> defaultRoles = new HashSet<>();
//            defaultRoles.add("USER");
//            user.setRoles(defaultRoles);
//        }
//
//        User savedUser = userService.saveUser(user);
//        return ResponseEntity.ok(buildResponse("success", "User saved successfully", savedUser, "/api/admin/users/save"));
//    }
//
//    /**
//     * API: Cập nhật thông tin user
//     * Method: PUT
//     * URL: /api/admin/users/update/{id}
//     * Role: ADMIN
//     * Body: User object
//     */
//    @PutMapping("/api/admin/users/update/{id}")
//    @ResponseBody
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, String>> updateUserApi(
//            @PathVariable("id") Long id,
//            @RequestBody User updatedUser) {
//
//        Map<String, String> response = new HashMap<>();
//
//        // Validate input
//        if (updatedUser.getName() == null || updatedUser.getName().trim().isEmpty()) {
//            response.put("message", "Name is required");
//            return ResponseEntity.badRequest().body(response);
//        }
//        if (updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
//            response.put("message", "Email is required");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // Lấy user hiện tại
//        User existingUser = userService.getUserById(id);
//        if (existingUser == null) {
//            response.put("message", "User not found");
//            return ResponseEntity.status(404).body(response);
//        }
//
//        // Kiểm tra email trùng
//        User userWithSameEmail = userService.findByEmail(updatedUser.getEmail());
//        if (userWithSameEmail != null && !userWithSameEmail.getId().equals(id)) {
//            response.put("message", "Email already exists");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // Cập nhật
//        existingUser.setName(updatedUser.getName());
//        existingUser.setEmail(updatedUser.getEmail());
//        existingUser.setPhone(updatedUser.getPhone());
//        userService.saveUser(existingUser);
//
//        response.put("message", "User updated successfully");
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * API: Xóa user
//     * Method: DELETE
//     * URL: /api/admin/users/delete/{id}
//     * Role: ADMIN
//     * Note: Không cho phép xóa admin mặc định
//     */
//    @DeleteMapping("/api/admin/users/delete/{id}")
//    @ResponseBody
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> deleteUserApi(@PathVariable("id") Long id) {
//        // Kiểm tra không cho xóa admin mặc định
//        User userToDelete = userService.getUserById(id);
//        if (userToDelete != null && "admin@example.com".equals(userToDelete.getEmail())) {
//            return ResponseEntity.badRequest()
//                    .body(buildResponse("error", "Cannot delete default admin account", null, "/api/admin/users/delete/" + id));
//        }
//
//        userService.deleteUserById(id);
//        return ResponseEntity.ok(buildResponse("success", "User deleted successfully", null, "/api/admin/users/delete/" + id));
//    }
//
//    /**
//     * API: Lấy thống kê dashboard
//     * Method: GET
//     * URL: /api/admin/dashboard
//     * Role: ADMIN
//     */
//    @GetMapping("/api/admin/dashboard")
//    @ResponseBody
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> getDashboardStatsApi() {
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("totalUsers", userService.getAllUsers().size());
//        // TODO: Thêm các thống kê khác (totalTables, totalOrders, revenue...)
//        return ResponseEntity.ok(stats);
//    }
//
//    // ===================================================================================
//    // 2. ADMIN - QUẢN LÝ SẢN PHẨM/MENU
//    // ===================================================================================
//
//    /**
//     * Controller quản lý sản phẩm (menu)
//     * Base URL: /api/admin/products
//     * Role: ADMIN hoặc ROOT
//     */
//    @RestController
//    @RequestMapping("/api/admin/products")
//    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//    @RequiredArgsConstructor
//    public class AdminProductController {
//
//        private final ProductService productService;
//
//        /**
//         * Lấy tất cả sản phẩm
//         * GET /api/admin/products
//         */
//        @GetMapping
//        public ResponseEntity<List<Product>> getAllProducts() {
//            return ResponseEntity.ok(productService.findAll());
//        }
//
//        /**
//         * Tạo sản phẩm mới
//         * POST /api/admin/products
//         */
//        @PostMapping
//        public ResponseEntity<Product> createProduct(@RequestBody Product product) {
//            return ResponseEntity.ok(productService.save(product));
//        }
//
//        /**
//         * Cập nhật sản phẩm
//         * PUT /api/admin/products/{id}
//         */
//        @PutMapping("/{id}")
//        public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
//            product.setId(id);
//            return ResponseEntity.ok(productService.save(product));
//        }
//
//        /**
//         * Xóa sản phẩm
//         * DELETE /api/admin/products/{id}
//         */
//        @DeleteMapping("/{id}")
//        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
//            productService.deleteById(id);
//            return ResponseEntity.noContent().build();
//        }
//    }
//
//    // ===================================================================================
//    // 3. ADMIN - QUẢN LÝ BÀN (TABLES)
//    // ===================================================================================
//
//    /**
//     * Controller quản lý bàn
//     * Base URL: /api/admin/tables
//     * Role: ADMIN hoặc ROOT
//     */
//    @RestController
//    @RequestMapping("/api/admin/tables")
//    @CrossOrigin(origins = "*") // TODO: Giới hạn domain production
//    @RequiredArgsConstructor
//    public class AdminTableController {
//
//        private final TableService tableService;
//
//        /**
//         * Lấy tất cả bàn
//         * GET /api/admin/tables
//         */
//        @GetMapping
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<List<Tables>> getAllTables() {
//            return ResponseEntity.ok(tableService.findAll());
//        }
//
//        /**
//         * Lấy bàn theo trạng thái
//         * GET /api/admin/tables/status/{status}
//         * Params: status = AVAILABLE | RESERVED | OCCUPIED | COMPLETED
//         */
//        @GetMapping("/status/{status}")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<List<Tables>> getTablesByStatus(@PathVariable String status) {
//            TableStatus tableStatus = TableStatus.valueOf(status.toUpperCase());
//            return ResponseEntity.ok(tableService.findByStatus(tableStatus));
//        }
//
//        /**
//         * Tạo bàn mới (có thể tạo nhiều bàn cùng lúc)
//         * POST /api/admin/tables
//         * Body: List<Tables>
//         */
//        @PostMapping
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<List<Tables>> createTables(@Valid @RequestBody List<Tables> tables) {
//            return ResponseEntity.ok(tableService.saveAll(tables));
//        }
//
//        /**
//         * Cập nhật thông tin bàn (tên, vị trí)
//         * PUT /api/admin/tables/{id}
//         * Note: KHÔNG cập nhật trạng thái ở đây
//         */
//        @PutMapping("/{id}")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<Tables> updateTable(
//                @PathVariable Long id,
//                @Valid @RequestBody Tables tableUpdate) {
//            Tables existingTable = tableService.findById(id);
//            existingTable.setName(tableUpdate.getName());
//            existingTable.setLocation(tableUpdate.getLocation());
//            // Không cho phép đổi trạng thái qua endpoint này
//            return ResponseEntity.ok(tableService.save(existingTable));
//        }
//
//        /**
//         * Cập nhật trạng thái bàn (dùng cho check-in, thanh toán, dọn dẹp)
//         * PUT /api/admin/tables/{id}/status
//         * Body: { "status": "AVAILABLE" | "RESERVED" | "OCCUPIED" | "COMPLETED" }
//         */
//        @PutMapping("/{id}/status")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<Tables> updateTableStatus(
//                @PathVariable Long id,
//                @RequestBody Map<String, String> request) {
//
//            String statusStr = request.get("status");
//            if (statusStr == null || statusStr.trim().isEmpty()) {
//                throw new IllegalArgumentException("Trường 'status' không được để trống");
//            }
//
//            TableStatus newStatus = TableStatus.valueOf(statusStr.toUpperCase());
//            Tables updatedTable = tableService.updateStatus(id, newStatus);
//            return ResponseEntity.ok(updatedTable);
//        }
//
//        /**
//         * Xóa bàn (chỉ khi ở trạng thái AVAILABLE)
//         * DELETE /api/admin/tables/{id}
//         */
//        @DeleteMapping("/{id}")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
//            tableService.deleteById(id);
//            return ResponseEntity.noContent().build();
//        }
//    }
//
//    // ===================================================================================
//    // 4. ADMIN - QUẢN LÝ ĐẶT BÀN (RESERVATIONS)
//    // ===================================================================================
//
//    /**
//     * Controller quản lý đặt bàn
//     * Base URL: /api/admin/reservations
//     * Role: ADMIN hoặc ROOT
//     */
//    @RestController
//    @RequestMapping("/api/admin/reservations")
//    @RequiredArgsConstructor
//    public class AdminReservationController {
//
//        private final ReservationService reservationService;
//
//        /**
//         * Admin xem tất cả đặt bàn
//         * GET /api/admin/reservations
//         */
//        @GetMapping
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<List<Reservation>> getAllReservations() {
//            return ResponseEntity.ok(reservationService.getAllReservations());
//        }
//
//        /**
//         * Admin xác nhận đặt bàn
//         * PUT /api/admin/reservations/{id}/confirm
//         */
//        @PutMapping("/{id}/confirm")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<Reservation> confirmReservation(@PathVariable Long id) {
//            return ResponseEntity.ok(reservationService.confirmReservation(id));
//        }
//
//        /**
//         * Admin hủy đặt bàn
//         * PUT /api/admin/reservations/{id}/cancel
//         */
//        @PutMapping("/{id}/cancel")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<Reservation> cancelReservation(@PathVariable Long id) {
//            return ResponseEntity.ok(reservationService.cancelReservation(id));
//        }
//    }
//
//    // ===================================================================================
//    // 5. ADMIN - QUẢN LÝ ĐỢN HÀNG & BÁO CÁO (ORDERS)
//    // ===================================================================================
//
//    /**
//     * Controller quản lý đơn hàng & báo cáo
//     * Base URL: /api/admin/orders
//     * Role: ADMIN hoặc ROOT
//     */
//    @RestController
//    @RequestMapping("/api/admin/orders")
//    @RequiredArgsConstructor
//    public class AdminOrderController {
//
//        private final OrderService orderService;
//
//        /**
//         * Admin xem tất cả đơn hàng
//         * GET /api/admin/orders
//         */
//        @GetMapping
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<List<OrderResponse>> getAllOrders() {
//            return ResponseEntity.ok(orderService.getAllOrders());
//        }
//
//        /**
//         * Admin xem chi tiết đơn hàng
//         * GET /api/admin/orders/{id}
//         */
//        @GetMapping("/{id}")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long id) {
//            return ResponseEntity.ok(orderService.getOrderDetail(id));
//        }
//
//        /**
//         * Admin cập nhật trạng thái đơn hàng
//         * PUT /api/admin/orders/{id}/status
//         * Body: { "status": "PREPARING" | "SERVED" | "COMPLETED" | "CANCELLED" }
//         */
//        @PutMapping("/{id}/status")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<Order> updateOrderStatus(
//                @PathVariable Long id,
//                @RequestBody Map<String, String> request) {
//            String statusStr = request.get("status");
//            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
//            Order updated = orderService.updateOrderStatus(id, status);
//            return ResponseEntity.ok(updated);
//        }
//
//        /**
//         * Báo cáo theo ngày
//         * GET /api/admin/orders/report/daily
//         */
//        @GetMapping("/report/daily")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<ReportResponse> getDailyReport() {
//            return ResponseEntity.ok(orderService.getDailyReport());
//        }
//
//        /**
//         * Báo cáo theo tuần
//         * GET /api/admin/orders/report/weekly
//         */
//        @GetMapping("/report/weekly")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<ReportResponse> getWeeklyReport() {
//            return ResponseEntity.ok(orderService.getWeeklyReport());
//        }
//
//        /**
//         * Báo cáo theo tháng
//         * GET /api/admin/orders/report/monthly
//         */
//        @GetMapping("/report/monthly")
//        @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
//        public ResponseEntity<ReportResponse> getMonthlyReport() {
//            return ResponseEntity.ok(orderService.getMonthlyReport());
//        }
//    }
//
//    // ===================================================================================
//    // 6. USER - API CHO KHÁCH HÀNG
//    // ===================================================================================
//
//    /**
//     * Controller cho khách hàng (USER role)
//     * Base URL: /api/user
//     * Role: USER only
//     *
//     * ⚠️ LƯU Ý: ĐÃ LOẠI BỎ CÁC ROUTE TRÙNG LẶP:
//     * - /api/reservations (đã có trong AdminReservationController)
//     * - /api/orders (đã có trong AdminOrderController)
//     *
//     * ✅ TẤT CẢ API USER ĐỀU BẮT ĐẦU BẰNG /api/user/*
//     */
//    @RestController
//    @RequestMapping("/api/user")
//    @RequiredArgsConstructor
//    public class UserController {
//
//        private final TableService tableService;
//        private final ReservationService reservationService;
//        private final ProductService productService;
//        private final OrderService orderService;
//
//        /**
//         * 1. Khách xem danh sách bàn trống
//         * GET /api/user/tables/available
//         */
//        @GetMapping("/tables/available")
//        @PreAuthorize("hasRole('USER')")
//        public ResponseEntity<List<TableResponse>> getAvailableTables() {
//            List<Tables> tables = tableService.findByStatus(TableStatus.AVAILABLE);
//            List<TableResponse> response = tables.stream()
//                    .map(TableResponse::new)
//                    .collect(Collectors.toList());
//            return ResponseEntity.ok(response);
//        }
//
//        /**
//         * 2. Khách đặt bàn
//         * POST /api/user/reservations
//         */
//        @PostMapping("/reservations")
//        @PreAuthorize("hasRole('USER')")
//        public ResponseEntity<ReservationResponse> createReservation(
//                @Valid @RequestBody ReservationRequest request) {
//            Long userId = getCurrentUserId();
//            Reservation reservation = reservationService.createReservation(request, userId);
//            return ResponseEntity.ok(new ReservationResponse(reservation));
//        }
//
//        /**
//         * 3. Khách xem lịch sử đặt bàn của mình
//         * GET /api/user/reservations/my
//         */
//        @GetMapping("/reservations/my")
//        @PreAuthorize("hasRole('USER')")
//        public ResponseEntity<List<ReservationResponse>> getMyReservations() {
//            Long userId = getCurrentUserId();
//            List<Reservation> reservations = reservationService.getReservationsByCustomer(userId);
//            List<ReservationResponse> response = reservations.stream()
//                    .map(ReservationResponse::new)
//                    .toList();
//            return ResponseEntity.ok(response);
//        }
//
//        /**
//         * 4. Khách hủy đặt bàn của mình
//         * PUT /api/user/reservations/{id}/cancel
//         */
//        @PutMapping("/reservations/{id}/cancel")
//        @PreAuthorize("hasRole('USER')")
//        public ResponseEntity<Reservation> cancelMyReservation(@PathVariable Long id) {
//            // TODO: Thêm logic kiểm tra reservation có thuộc về user hiện tại không
//            return ResponseEntity.ok(reservationService.cancelReservation(id));
//        }
//
//        /**
//         * 5. Khách xem menu (danh sách món)
//         * GET /api/user/menu
//         */
//        @GetMapping("/menu")
//        @PreAuthorize("hasRole('USER')")
//        public ResponseEntity<List<ProductResponse>> getMenu() {
//            List<Product> products = productService.findByStatus(ProductStatus.AVAILABLE);
//            List<ProductResponse> response = products.stream()
//                    .map(ProductResponse::new)
//                    .collect(Collectors.toList());
//            return ResponseEntity.ok(response);
//        }
//
//        /**
//         * 6. Khách tạo đơn hàng (order món)
//         * POST /api/user/orders
//         */
//        @PostMapping("/orders")
//        @PreAuthorize("hasRole('USER')")
//        public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
//            Long userId = getCurrentUserId();
//            Order order = orderService.createOrder(request, userId);
//            return ResponseEntity.ok(new OrderResponse(order, orderService.getOrderItems(order.getId())));
//        }
//
//        /**
//         * 7. Khách xem danh sách đơn hàng của mình
//         * GET /api/user/orders/my-orders
//         */
//        @GetMapping("/orders/my-orders")
//        @PreAuthorize("hasRole('USER')")
//        public ResponseEntity<List<OrderResponse>> getMyOrders() {
//            Long userId = getCurrentUserId();
//            return ResponseEntity.ok(orderService.getOrdersByCustomer(userId));
//        }
//
//        /**
//         * 8. Khách xem chi tiết đơn hàng
//         * GET /api/user/orders/{id}
//         */
//        @GetMapping("/orders/{id}")
//        @PreAuthorize("hasRole('USER')")
//        public ResponseEntity<OrderResponse> getMyOrderDetail(@PathVariable Long id) {
//            // TODO: Thêm logic kiểm tra order có thuộc về user hiện tại không
//            return ResponseEntity.ok(orderService.getOrderDetail(id));
//        }
//
//        /**
//         * Helper: Lấy userId từ SecurityContext
//         */
//        private Long getCurrentUserId() {
//            var auth = SecurityContextHolder.getContext().getAuthentication();
//            if (auth != null && auth.getPrincipal() instanceof User) {
//                return ((User) auth.getPrincipal()).getId();
//            }
//            throw new IllegalStateException("User not authenticated");
//        }
//    }
//}
///**
// * 2. ✅ CẤU TRÚC URL :
// *    Admin APIs:
// *    - /api/admin/users/*           - Quản lý users
// *    - /api/admin/products/*        - Quản lý sản phẩm
// *    - /api/admin/tables/*          - Quản lý bàn
// *    - /api/admin/reservations/*    - Quản lý đặt bàn
// *    - /api/admin/orders/*          - Quản lý đơn hàng & báo cáo
// *    User APIs:
// *    - /api/user/tables/available   - Xem bàn trống
// *    - /api/user/reservations/*     - Đặt bàn & xem lịch sử
// *    - /api/user/menu               - Xem menu
// *    - /api/user/orders/*           - Order món & xem đơn hàng
// * 3. ✅ PHÂN QUYỀN RÕ RÀNG:
// *    - Admin: hasAnyRole('ROOT', 'ADMIN')
// *    - User: hasRole('USER')
// * 4. ✅ NESTED CONTROLLERS:
// *    - Tất cả @RestController đều nested bên trong AdminController
// *    - Dễ quản lý và maintain
// * ===================================================================================
// */