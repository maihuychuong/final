package com.example.demo.util;

import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.model.dto.ReviewDTO;

import java.time.LocalDateTime;

public class ReviewMapper {
    public static ReviewDTO toDTO(Review review) {
        if (review == null) return null;
        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .username(review.getUser() != null ? review.getUser().getUsername() : null)
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .productName(review.getProduct() != null ? review.getProduct().getName() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static Review toEntity(ReviewDTO dto, User user, Product product) {
        if (dto == null) return null;
        return Review.builder()
                .id(dto.getId())
                .user(user)
                .product(product)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .build();
    }
}
