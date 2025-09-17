package com.example.LT_Web2.controllers;

import com.example.LT_Web2.models.CompanyModel;
import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.services.CompanyService;
import com.example.LT_Web2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;
    private final CompanyService companyService;

    @Autowired
    public UserController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    // Redirect to user/add when accessing root
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/user/add";
    }

    // Show form to add a new user
    @GetMapping("/user/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new UseModel());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("companies", companyService.getAllCompanies()); // Thêm list company
        return "add_user";
    }

    @PostMapping("/user/add")
    public String addUser(@ModelAttribute("user") UseModel user, @RequestParam("companyId") Long companyId) {
        CompanyModel company = companyService.getCompanyById(companyId);
        user.setCompany(company);
        userService.saveUser(user);
        return "redirect:/user/add";
    }


    // Show form to edit an existing user
    @GetMapping("/user/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        UseModel user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("companies", companyService.getAllCompanies());
        return "edit_user";
    }

    // Handle form submission to update an existing user
    // @PostMapping("/user/update")
    // public String updateUser(@ModelAttribute("user") UseModel user,
    //                          @RequestParam("companyId") Long companyId) {
    //     CompanyModel company = companyService.getCompanyById(companyId);
    //     user.setCompany(company);
    //     userService.saveUser(user);
    //     return "redirect:/user/add";
    // }


    // Delete a user by ID
    // @GetMapping("/user/delete/{id}")
    // public String deleteUser(@PathVariable("id") Long id) {
    //     userService.deleteUserById(id);
    //     return "redirect:/user/add";
    // }
    
    // Show form to add a new company
    @GetMapping("/company/add")
    public String showAddCompanyForm(Model model) {
        model.addAttribute("company", new CompanyModel());
        return "add_company";
    }

    // Handle form submission to add a new company
    @PostMapping("/company/add")
    public String addCompany(@ModelAttribute("company") CompanyModel company) {
        companyService.saveCompany(company);
        return "redirect:/user/add";
    }
    // Delete a company by ID
    @GetMapping("/company/delete/{id}")
    public String deleteCompany(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            companyService.deleteCompanyById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa công ty thành công!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/add";
    }
}