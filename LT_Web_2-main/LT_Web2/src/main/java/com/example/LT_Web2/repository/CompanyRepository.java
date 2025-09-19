package com.example.LT_Web2.repository;

import com.example.LT_Web2.models.CompanyModel;
import org.springframework.data.jpa.repository.JpaRepository;

// Đây là interface Repository cho CompanyModel
// JpaRepository cung cấp sẵn các phương thức CRUD cơ bản:
// save(), findById(), findAll(), deleteById(), ...
// Service sẽ gọi Repository này để thao tác DB
public interface CompanyRepository extends JpaRepository<CompanyModel, Long> {
}
