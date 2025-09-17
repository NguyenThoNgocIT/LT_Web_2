package com.example.LT_Web2.controllers;

import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UseModel());
        return "signup"; // file signup.html trong templates
    }

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String login() {
        return "login"; // file login.html trong templates
    }

    // Xử lý đăng ký
    @PostMapping("/process-register")
    public String processRegister(UseModel user) {
        // Mã hóa mật khẩu trước khi lưu DB
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Gán role mặc định
        Set<String> roles = new HashSet<>();
        roles.add("USER"); // sẽ được map thành ROLE_USER trong UseModel
        user.setRoles(roles);

        // Lưu vào DB
        userRepository.save(user);

        // Sau khi đăng ký thành công → chuyển về trang login
        return "redirect:/login";
    }
}
