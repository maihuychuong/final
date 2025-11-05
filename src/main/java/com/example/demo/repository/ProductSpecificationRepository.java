package com.example.demo.repository;

import com.example.demo.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Integer> {
    List<ProductSpecification> findByProductId(Integer productId);
}
