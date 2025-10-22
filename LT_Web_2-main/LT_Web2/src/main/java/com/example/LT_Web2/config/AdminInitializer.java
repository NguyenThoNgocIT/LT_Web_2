package com.example.LT_Web2.config;

import com.example.LT_Web2.enity.User;
import com.example.LT_Web2.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.HashSet;
import java.util.Set;
@Configuration
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@example.com");
                admin.setName("Admin User");
                admin.setPassword(bCryptPasswordEncoder.encode("admin123"));
                Set<String> roles = new HashSet<>();
                roles.add("ADMIN");
                admin.setRoles(roles);
                userRepository.save(admin);
            }
        };
    }
}