package com.example.LT_Web2.services.impl;

import com.example.LT_Web2.entity.Product;
import com.example.LT_Web2.entity.ProductStatus;
import com.example.LT_Web2.exception.ResourceNotFoundException;
import com.example.LT_Web2.repository.ProductRepository;
import com.example.LT_Web2.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    public Product save (Product product) {
        if(product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) == 0){
            throw new IllegalArgumentException("Giá phải lớn hơn 0");
        }
        return productRepository.save(product);
    }
    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    @Override
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm để xóa với ID: " + id);
        }
        productRepository.deleteById(id);
    }
}

