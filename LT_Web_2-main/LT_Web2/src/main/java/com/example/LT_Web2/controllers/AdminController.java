package com.example.LT_Web2.controllers;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.services.CompanyService;
import com.example.LT_Web2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalCompanies", companyService.getAllCompanies().size());
        model.addAttribute("newUser", new UseModel());
        model.addAttribute("newCompany", new CompanyModel());
        return "admin_dashboard";
    }

    // --- User Management ---
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("newUser") UseModel user, @RequestParam(value = "companyId", required = false) Long companyId) {
        // Kiểm tra xem email đã tồn tại chưa
        if (userService.findByEmail(user.getEmail()) != null) {
            return "redirect:/admin/dashboard?error=Email+already+exists";
        }

        if (companyId != null) {
            CompanyModel company = companyService.getCompanyById(companyId);
            user.setCompany(company);
        }
        userService.saveUser(user);
        return "redirect:/admin/dashboard?success=User+saved+successfully";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/dashboard";
    }

    // --- Company Management ---
    @PostMapping("/company/save")
    public String saveCompany(@ModelAttribute("newCompany") CompanyModel company) {
        companyService.saveCompany(company);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/company/delete/{id}")
    public String deleteCompany(@PathVariable("id") Long id) {
        companyService.deleteCompanyById(id);
        return "redirect:/admin/dashboard";
    }
}