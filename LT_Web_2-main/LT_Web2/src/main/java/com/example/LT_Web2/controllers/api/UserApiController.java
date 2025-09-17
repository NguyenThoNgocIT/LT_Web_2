package com.example.LT_Web2.controllers.api;

import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    // Lấy danh sách tất cả user
    @GetMapping
    public List<UseModel> getAllUsers() {
        return userService.getAllUsers();
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public UseModel getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Tạo user mới
    @PostMapping
    public UseModel createUser(@RequestBody UseModel user) {
        return userService.saveUser(user);
    }

    // Cập nhật user theo ID
    @PutMapping("/{id}")
    public UseModel updateUser(@PathVariable Long id, @RequestBody UseModel user) {
        return userService.updateUser(id, user);
    }

    // Xóa user theo ID
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
