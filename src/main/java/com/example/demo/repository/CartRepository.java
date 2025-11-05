package com.example.demo.repository;

import com.example.demo.entity.Cart;
import com.example.demo.model.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser_IdAndStatus(Integer userId, CartStatus status);
    List<Cart> findByUserId(Integer userId);
}
