package com.example.LT_Web2.services;

import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Thêm hoặc cập nhật user
    public UseModel saveUser(UseModel user) {
        return userRepository.save(user);
    }

    // Lấy tất cả user
    public List<UseModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Tìm user theo ID
    public UseModel getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + id));
    }

    // Xóa user theo ID
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User không tồn tại với ID: " + id);
        }
        userRepository.deleteById(id);
    }
    public UseModel findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
