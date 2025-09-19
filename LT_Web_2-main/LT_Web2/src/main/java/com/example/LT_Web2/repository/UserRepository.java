package com.example.LT_Web2.repository;

import com.example.LT_Web2.models.UseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repository cho User (UseModel)
// JpaRepository cung cấp sẵn các phương thức CRUD:
// save(), findById(), findAll(), deleteById(), ...
// Thêm phương thức tùy chỉnh findByEmail() để tìm user theo email
public interface UserRepository extends JpaRepository<UseModel, Long> {
    Optional<UseModel> findByEmail(String email);
}
