
package com.example.LT_Web2.controllers;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.repository.UserRepository;
import com.example.LT_Web2.services.CompanyService;
import com.example.LT_Web2.services.JwtService;
import com.example.LT_Web2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller

public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // API: Register user
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();
        try {
            UseModel user = new UseModel();
            String email = userData.get("email");
            String name = userData.get("name");
            String password = userData.get("password");

            if (email == null || email.trim().isEmpty()) {
                response.put("error", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }
            if (name == null || name.trim().isEmpty()) {
                response.put("error", "Name is required");
                return ResponseEntity.badRequest().body(response);
            }
            if (password == null || password.trim().isEmpty()) {
                response.put("error", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }
            if (userService.findByEmail(email) != null) {
                response.put("error", "Email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            user.setEmail(email);
            user.setName(name);
            user.setPassword(passwordEncoder.encode(password));
            Set<String> roles = new HashSet<>();
            roles.add("USER");
            user.setRoles(roles);
            UseModel savedUser = userRepository.save(user);
            // trả về user với roles
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", savedUser.getId());
            userResponse.put("name", savedUser.getName());
            userResponse.put("email", savedUser.getEmail());
            userResponse.put("phone", savedUser.getPhone());
            userResponse.put("roles", savedUser.getRoles());

            response.put("message", "Đăng ký thành công!");
            response.put("user", userData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Lỗi khi đăng ký: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    // API: Login and generate JWT
    // API: Login and generate JWT
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.get("email"), loginData.get("password"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UseModel user = userService.findByEmail(loginData.get("email"));
            String jwt = jwtService.generateToken(user);

            // Trả về cả token và user đầy đủ
            Map<String, Object>  userResponse = new HashMap<>();
            userResponse.put("id", user.getId());
            userResponse.put("name", user.getName());
            userResponse.put("email", user.getEmail());
            userResponse.put("phone", user.getPhone());
            userResponse.put("roles", user.getRoles()); // 👈 THÊM ROLES VÀO ĐÂY

            response.put("message", "Đăng nhập thành công!");
            response.put("token", jwt);
            response.put("user",  userResponse); // 👈 THÊM USER VÀO ĐÂY
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Đăng nhập thất bại: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }
    // ============ API: Add new company via JSON ============
    @PostMapping("/api/company/save")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCompanyApi(
            @RequestBody CompanyModel newCompany,
            Authentication authentication) {   // inject thông tin user đã login
        Map<String, Object> response = new HashMap<>();
        try {
            // Nếu chưa login hoặc không có token hợp lệ
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("error", "Bạn chưa đăng nhập hoặc token không hợp lệ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        String email = authentication.getName();
        UseModel user = userService.findByEmail(email);
            if (user == null) {
                response.put("error", "Không tìm thấy user");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (newCompany.getCompanyName() == null || newCompany.getCompanyName().trim().isEmpty()) {
                response.put("error", "Tên công ty không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            // ✅ Lưu company mới
            companyService.saveCompany(newCompany);

            // ✅ Gắn company vào user hiện tại
            user.setCompany(newCompany);
            userService.saveUser(user);

            response.put("message", "Thêm công ty thành công và gắn vào user!");
            response.put("company", newCompany);
            response.put("user", user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Lỗi khi thêm công ty: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    // ============ API: Get current user profile ============
    @GetMapping("/api/user/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("error", "Unauthorized");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            UseModel user = userService.findByEmail(email);
            if (user == null) {
                response.put("error", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            // Có thể trả cả company nếu cần
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("phone", user.getPhone());
            userData.put("company", user.getCompany());
            if (user.getCompany() != null) {
                Map<String, Object> companyData = new HashMap<>();
                companyData.put("id", user.getCompany().getId());
                companyData.put("name", user.getCompany().getCompanyName());
                userData.put("company", companyData);
            }
            response.put("user", userData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Lỗi khi lấy thông tin người dùng: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ============ API: Update user profile ============
    @PutMapping("/api/user/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUserProfileApi(
            @RequestBody Map<String, Object> updateData,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("error", "Unauthorized");
                return ResponseEntity.status(401).body(response);
            }

            String currentEmail = authentication.getName();
            UseModel existingUser = userService.findByEmail(currentEmail);
            if (existingUser == null) {
                response.put("error", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            // Cập nhật name, phone
            if (updateData.containsKey("name")) {
                existingUser.setName((String) updateData.get("name"));
            }
            if (updateData.containsKey("phone")) {
                existingUser.setPhone((String) updateData.get("phone"));
            }

            // Cập nhật company (nếu có companyId)
            if (updateData.containsKey("companyId")) {
                Long companyId = ((Number) updateData.get("companyId")).longValue();
                if (companyId > 0) {
                    CompanyModel company = companyService.getCompanyById(companyId);
                    if (company == null) {
                        response.put("error", "Company not found");
                        return ResponseEntity.status(404).body(response);
                    }
                    existingUser.setCompany(company);
                } else {
                    existingUser.setCompany(null);
                }
            }

            userService.saveUser(existingUser);
            response.put("message", "Cập nhật thành công!");
            response.put("user", existingUser);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Lỗi khi cập nhật: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}