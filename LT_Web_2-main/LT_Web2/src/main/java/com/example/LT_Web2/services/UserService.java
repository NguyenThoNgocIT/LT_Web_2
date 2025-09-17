package com.example.LT_Web2.services;

import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Tạo mới hoặc lưu user
    public UseModel saveUser(UseModel user) {
        // Mã hóa mật khẩu trước khi lưu
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // Lấy tất cả user
    public List<UseModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo ID
    public UseModel getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    // Cập nhật user theo ID
    public UseModel updateUser(Long id, UseModel user) {
        UseModel existingUser = getUserById(id);
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        // Nếu password được truyền lên, mã hóa và cập nhật
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // Nếu UseModel có thêm các trường khác, cũng cập nhật ở đây
        // existingUser.setRole(user.getRole());
        // existingUser.setPhone(user.getPhone());
        return userRepository.save(existingUser);
    }

    // Xóa user theo ID
    public void deleteUser(Long id) {
        // Kiểm tra tồn tại trước khi xóa
        getUserById(id);
        userRepository.deleteById(id);
    }
}
