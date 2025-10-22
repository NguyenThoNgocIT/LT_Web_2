package com.example.LT_Web2.controllers;

import com.example.LT_Web2.enity.User;
import com.example.LT_Web2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private Map<String, Object> buildResponse(String status, String message, Object data,String path ) {
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
}
