package com.example.LT_Web2.controllers;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.repository.UserRepository;
import com.example.LT_Web2.services.CompanyService;
import com.example.LT_Web2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
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

    // Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UseModel());
        return "signup";
    }

    // Show login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Process user registration
    @PostMapping("/process-register")
    public String processRegister(@ModelAttribute("user") UseModel user, RedirectAttributes redirectAttributes) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Đăng ký thành công!");
        return "redirect:/login";
    }

    // Show user profile page with edit and add company modals
    @GetMapping("/user/profile")
    public String showUserProfile(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            UseModel user = userService.findByEmail(email);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("companies", companyService.getAllCompanies());
                model.addAttribute("newCompany", new CompanyModel()); // For the add company modal
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin người dùng");
                return "error";
            }
        } else {
            return "redirect:/login";
        }
        return "profile_user";
    }

    // Update user profile (handles both inline form and modal)
    @PostMapping("/user/profile")
    public String updateUserProfile(@ModelAttribute("user") UseModel user,
                                    @RequestParam(value = "companyId", required = false) Long companyId,
                                    RedirectAttributes redirectAttributes) {
        UseModel existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setPhone(user.getPhone());
            if (companyId != null && companyId > 0) {
                CompanyModel company = companyService.getCompanyById(companyId);
                existingUser.setCompany(company);
            } else {
                existingUser.setCompany(null);
            }
            userService.saveUser(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
        }
        return "redirect:/user/profile";
    }

    // Add new company (handles the add company modal)
    @PostMapping("/user/company/save")
    public String addNewCompany(@ModelAttribute("newCompany") CompanyModel newCompany,
                                RedirectAttributes redirectAttributes) {
        try {
            companyService.saveCompany(newCompany);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm công ty thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm công ty: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }
}