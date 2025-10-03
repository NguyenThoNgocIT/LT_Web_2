package com.example.LT_Web2.controllers;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.services.CompanyService;
import com.example.LT_Web2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
// ⚠️ KHÔNG dùng @RequestMapping("/admin") ở mức class
// → để có thể định nghĩa riêng /admin/... (web) và /api/admin/... (API)
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CompanyService companyService;

    // =============== WEB ROUTES (Session-based, HTML) ===============

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalCompanies", companyService.getAllCompanies().size());
        model.addAttribute("newUser", new UseModel());
        model.addAttribute("newCompany", new CompanyModel());
        return "admin_dashboard";
    }
    @PostMapping("/admin/users/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveUserWeb(
            @ModelAttribute("newUser") UseModel user,
            @RequestParam(value = "companyId", required = false) Long companyId,
            RedirectAttributes redirectAttributes) {

        if (user.getId() != null && user.getId() > 0) {
            // === CẬP NHẬT ===
            UseModel existingUser = userService.getUserById(user.getId());
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng để cập nhật");
                return "redirect:/admin/dashboard";
            }

            // Kiểm tra email trùng (trừ khi là email của chính user đó)
            UseModel userWithSameEmail = userService.findByEmail(user.getEmail());
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(existingUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Email đã tồn tại");
                return "redirect:/admin/dashboard";
            }

            // Cập nhật các field cơ bản
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());

            // ✅ XỬ LÝ MẬT KHẨU: chỉ cập nhật nếu có nhập
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            // → Nếu không nhập, giữ nguyên existingUser.getPassword() (mật khẩu cũ)

            // Cập nhật công ty
            if (companyId != null) {
                CompanyModel company = companyService.getCompanyById(companyId);
                existingUser.setCompany(company);
            } else {
                existingUser.setCompany(null);
            }

            userService.saveUser(existingUser);
            redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
        } else {
            // === TẠO MỚI ===
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email is required");
                return "redirect:/admin/dashboard";
            }
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Name is required");
                return "redirect:/admin/dashboard";
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Password is required");
                return "redirect:/admin/dashboard";
            }
            if (userService.findByEmail(user.getEmail()) != null) {
                redirectAttributes.addFlashAttribute("error", "Email already exists");
                return "redirect:/admin/dashboard";
            }

            // Mã hóa mật khẩu khi tạo mới
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if (companyId != null) {
                CompanyModel company = companyService.getCompanyById(companyId);
                user.setCompany(company);
            }
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");
        }

        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUserWeb(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/company/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCompanyWeb(@ModelAttribute("newCompany") CompanyModel company) {
        companyService.saveCompany(company);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/company/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCompanyWeb(@PathVariable("id") Long id) {
        companyService.deleteCompanyById(id);
        return "redirect:/admin/dashboard";
    }

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
            @RequestBody UseModel user,
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

        if (companyId != null) {
            CompanyModel company = companyService.getCompanyById(companyId);
            if (company == null) {
                return ResponseEntity.badRequest()
                        .body(buildResponse("error", "Company not found", null, "/api/admin/users/save"));
            }
            user.setCompany(company);
        }

        UseModel savedUser = userService.saveUser(user);
        return ResponseEntity.ok(buildResponse("success", "User saved successfully", savedUser, "/api/admin/users/save"));
    }

    @DeleteMapping("/api/admin/users/delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUserApi(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(buildResponse("success", "User deleted successfully", null, "/api/admin/users/delete/" + id));
    }

    @PostMapping("/api/admin/company/save")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveCompanyApi(@RequestBody CompanyModel company) {
        if (company.getCompanyName() == null || company.getCompanyName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(buildResponse("error", "Company name is required", null, "/api/admin/company/save"));
        }
        CompanyModel savedCompany = companyService.saveCompany(company);
        return ResponseEntity.ok(buildResponse("success", "Company saved successfully", savedCompany, "/api/admin/company/save"));
    }

    @DeleteMapping("/api/admin/company/delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCompanyApi(@PathVariable("id") Long id) {
        companyService.deleteCompanyById(id);
        return ResponseEntity.ok(buildResponse("success", "Company deleted successfully", null, "/api/admin/company/delete/" + id));
    }
    @PutMapping("/api/admin/users/update/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateUserApi(
            @PathVariable("id") Long id,
            @RequestBody UseModel updatedUser) {

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
        UseModel existingUser = userService.getUserById(id);
        if (existingUser == null) {
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }

        // Kiểm tra email trùng (nếu đổi email)
        UseModel userWithSameEmail = userService.findByEmail(updatedUser.getEmail());
        if (userWithSameEmail != null && !userWithSameEmail.getId().equals(id)) {
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // Cập nhật thông tin
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());

        // Nếu có companyId trong body → cập nhật company
        if (updatedUser.getCompany() != null && updatedUser.getCompany().getId() != null) {
            CompanyModel company = companyService.getCompanyById(updatedUser.getCompany().getId());
            if (company == null) {
                response.put("message", "Company not found");
                return ResponseEntity.badRequest().body(response);
            }
            existingUser.setCompany(company);
        } else {
            existingUser.setCompany(null);
        }

        userService.saveUser(existingUser);
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

}
