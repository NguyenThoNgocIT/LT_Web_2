package com.example.LT_Web2.controllers;

import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Khi mở trình duyệt sẽ vào đây luôn
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/user/add";
    }

    @GetMapping("/user/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new UseModel());
        model.addAttribute("users", userService.getAllUsers()); // gửi danh sách user
        return "add_user";
    }

    @PostMapping("/user/add")
    public String addUser(@ModelAttribute("user") UseModel user) {
        userService.saveUser(user);
        return "redirect:/user/add";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/user/add";
    }
}
