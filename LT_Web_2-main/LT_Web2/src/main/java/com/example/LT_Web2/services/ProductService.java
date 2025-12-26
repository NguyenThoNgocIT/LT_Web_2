package com.example.LT_Web2.services;

import com.example.LT_Web2.entity.Product;
import com.example.LT_Web2.entity.ProductStatus;

import java.util.List;

public interface ProductService{
    Product save(Product product);
    Product findById(Long id);
    List<Product> findAll();
    List<Product> findByStatus(ProductStatus status);
    void deleteById(Long id);

}
