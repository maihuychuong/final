package com.example.demo.controller.customer;

import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.model.dto.ReviewDTO;
import com.example.demo.service.ReviewService;
import com.example.demo.service.UserService;
import com.example.demo.util.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final ReviewService reviewService;

    // Hiển thị trang profile
    @GetMapping
    public String showProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<ReviewDTO> reviews = reviewService.getReviewsByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);

        if (reviews.isEmpty()) {
            model.addAttribute("noReviewsMessage", "Bạn chưa có đánh giá nào.");
        }

        return "profile";
    }



    // Xử lý cập nhật profile
    @PostMapping
    public String updateProfile(@ModelAttribute User userForm, Principal principal, Model model) {
        try {
            User currentUser = userService.findByUsername(principal.getName());
            userService.updateUserProfile(currentUser.getId(), userForm.getFullName(), userForm.getEmail(), userForm.getPhoneNumber());
            model.addAttribute("success", "Cập nhật thông tin thành công");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        User updatedUser = userService.findByUsername(principal.getName());
        model.addAttribute("user", updatedUser);
        return "profile";
    }

    @PostMapping("/reviews/add")
    public String submitReview(@ModelAttribute ReviewDTO reviewDTO, Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        try {
            reviewService.createReview(reviewDTO, principal.getName());
            redirectAttributes.addFlashAttribute("reviewSuccess", "Đánh giá đã được gửi thành công.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reviewError", "Có lỗi xảy ra khi gửi đánh giá.");
        }

        return "redirect:/product-detail/" + reviewDTO.getProductId();
    }

    // Hiển thị form sửa đánh giá
    @GetMapping("/reviews/edit/{id}")
    public String showEditReviewForm(@PathVariable("id") Integer id, Model model, Principal principal) {
        // Lấy review theo id
        var review = reviewService.findById(id);

        // Kiểm tra quyền sửa: chỉ chủ đánh giá mới được sửa
        if (!review.getUser().getUsername().equals(principal.getName())) {
            // Có thể trả về lỗi hoặc chuyển về trang profile
            return "redirect:/profile";
        }

        ReviewDTO reviewDTO = ReviewMapper.toDTO(review);

        model.addAttribute("reviewDTO", reviewDTO);
        return "review-edit"; // tên file HTML form sửa đánh giá
    }

    // Xử lý cập nhật đánh giá
    @PostMapping("/reviews/update")
    public String updateReview(@ModelAttribute ReviewDTO reviewDTO, Principal principal) {
        // Lấy review gốc để kiểm tra quyền sửa
        var review = reviewService.findById(reviewDTO.getId());

        if (!review.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/profile";
        }

        // Cập nhật nội dung đánh giá
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        // Có thể cập nhật thời gian sửa nếu cần
        // review.setCreatedAt(LocalDateTime.now());

        reviewService.save(review);  // Bạn cần có method save trong ReviewService

        // Quay lại trang profile hoặc trang chi tiết sản phẩm
        return "redirect:/profile";
    }

    // Xử lý xoá đánh giá
    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") Integer id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReview(id, principal.getName());
            redirectAttributes.addFlashAttribute("reviewSuccess", "Đã xoá đánh giá.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reviewError", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}
