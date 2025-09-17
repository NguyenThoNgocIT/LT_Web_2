package com.example.LT_Web2.repository;

import com.example.LT_Web2.models.UseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UseModel, Long> {
    Optional<UseModel> findByEmail(String email);
}
