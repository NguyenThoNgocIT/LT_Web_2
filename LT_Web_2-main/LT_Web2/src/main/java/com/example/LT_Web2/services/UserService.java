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

    // Save or update a user
    public UseModel saveUser(UseModel user) {
        return userRepository.save(user);
    }

    // Get all users
    public List<UseModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by ID
    public UseModel getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    // Delete a user by ID
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}