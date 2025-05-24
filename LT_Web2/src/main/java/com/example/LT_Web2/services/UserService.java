package com.example.LT_Web2.services;

import com.example.LT_Web2.models.UseModel;
import com.example.LT_Web2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Lưu user
    public UseModel saveUser(UseModel user) {
        return userRepository.save(user);
    }

    // Lấy danh sách tất cả user
    public List<UseModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Xoá user theo id
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
