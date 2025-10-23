package com.example.LT_Web2.repository;

import com.example.LT_Web2.entity.Product;
import com.example.LT_Web2.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(ProductStatus status);
}
