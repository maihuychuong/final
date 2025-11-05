package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.ReviewDTO;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.ReviewMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<ReviewDTO> getReviewsByProductId(Integer productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Service
    public ReviewDTO createReview(ReviewDTO reviewDTO, String username) {
        if (reviewDTO.getComment() == null || reviewDTO.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung đánh giá không được để trống.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với username: " + username));

        // Kiểm tra user đã đánh giá sản phẩm này chưa
        boolean hasReviewed = reviewRepository.existsByUserIdAndProductId(user.getId(), reviewDTO.getProductId());
        if (hasReviewed) {
            throw new IllegalStateException("Người dùng đã đánh giá sản phẩm này.");
        }

        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + reviewDTO.getProductId()));

        Review review = ReviewMapper.toEntity(reviewDTO, user, product);
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return ReviewMapper.toDTO(saved);
    }

    public List<ReviewDTO> getReviewsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return reviews.stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void updateReview(ReviewDTO dto, String username) {
        Review review = reviewRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getUsername().equals(username)) {
            throw new SecurityException("Không được chỉnh sửa đánh giá của người khác");
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        reviewRepository.save(review);
    }

    public void deleteReview(Integer id, String username) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getUsername().equals(username)) {
            throw new SecurityException("Không được xoá đánh giá của người khác");
        }

        reviewRepository.delete(review);
    }

    public Review findById(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy đánh giá"));
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }
}
