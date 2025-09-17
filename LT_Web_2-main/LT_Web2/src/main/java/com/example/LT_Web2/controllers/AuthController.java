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

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UseModel());
        return "signup";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }



    @PostMapping("/process-register")
    public String processRegister(UseModel user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);
        userRepository.save(user);
        return "redirect:/login";
    }
}