package com.example.demo.controller.web;

import com.example.demo.entity.Notification;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductSalesReport;
import com.example.demo.model.dto.ReviewDTO;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.enums.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.*;
import com.example.demo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class WebController {
    private final ProductService productService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JavaMailSender mailSender;
    private final ReviewService reviewService;
    private final NotificationService notificationService;
    private final ProductSalesReportService productSalesReportService;

    public WebController(ProductService productService, UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService, JavaMailSender mailSender, ReviewService reviewService, NotificationService notificationService, ProductSalesReportService productSalesReportService) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.mailSender = mailSender;
        this.reviewService = reviewService;
        this.notificationService = notificationService;
        this.productSalesReportService = productSalesReportService;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        List<Product> highlightedProducts = productService.getFeaturedProducts(8);
        model.addAttribute("products", highlightedProducts);
        model.addAttribute("activePage", "home");
        notificationService.addNotificationsToModel(model, principal);
        List<ProductSalesReport> top5BestSellingProducts = productSalesReportService.getTop5BestSellingProductsLastMonth();
        model.addAttribute("top5BestSellingProducts", top5BestSellingProducts);
        return "index";
    }




    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        model.addAttribute("activePage", "login");
        if (error != null) {
            model.addAttribute("errorMessage", error);
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDTO());
        model.addAttribute("activePage", "register");
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("user") UserDTO dto,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "register");
            return "register";
        }

        // Kiểm tra thủ công độ dài mật khẩu
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            model.addAttribute("activePage", "register");
            model.addAttribute("user", dto);
            return "register";
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            model.addAttribute("activePage", "register");
            model.addAttribute("user", dto);
            return "register";
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            model.addAttribute("activePage", "register");
            model.addAttribute("user", dto);
            return "register";
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            model.addAttribute("error", "Email đã được sử dụng!");
            model.addAttribute("activePage", "register");
            model.addAttribute("user", dto);
            return "register";
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .isLocked(false)
                .build();

        userRepository.save(user);

        List<User> admins = userRepository.findByRole(Role.ADMIN);
        String title = "Khách hàng mới đăng ký";
        String content = "Khách hàng " + user.getFullName() + " (username: " + user.getUsername() + ") vừa đăng ký tài khoản.";

        for (User admin : admins) {
            notificationService.createNotification(admin, title, content);
        }

        return "redirect:/login?success";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("activePage", "forgot-password");
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpServletRequest request, Model model) {
        User user = userService.findByEmail(email);
        model.addAttribute("activePage", "forgot-password");

        if (user == null) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống.");
            return "forgot-password";  // Trả về trang cùng với lỗi
        }

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user, token);

        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        boolean isSent = userService.sendPasswordResetEmail(user, appUrl, token);

        if (isSent) {
            model.addAttribute("success", "Email đặt lại mật khẩu đã được gửi, vui lòng kiểm tra hộp thư.");
            return "forgot-password";
        } else {
            model.addAttribute("error", "Có lỗi xảy ra khi gửi email, vui lòng thử lại.");
            return "forgot-password";
        }
    }


    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam String token, Model model) {
        if (userService.isValidToken(token)) {
            model.addAttribute("token", token);
            model.addAttribute("activePage", "reset-password");
            return "reset-password";
        }
        model.addAttribute("activePage", "forgot-password");
        return "redirect:/forgot-password?error";
    }

    @PostMapping("/reset-password")
    public String handleReset(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword,
            Model model) {

        if (newPassword.length() < 6) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự.");
            model.addAttribute("activePage", "reset-password");
            return "reset-password";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", "Mật khẩu mới và xác nhận không khớp!");
            model.addAttribute("activePage", "reset-password");
            return "reset-password";
        }

        if (userService.resetPassword(token, newPassword)) {
            return "redirect:/login?resetSuccess";
        }

        model.addAttribute("token", token);
        model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn!");
        model.addAttribute("activePage", "reset-password");
        return "reset-password";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("activePage", "change-password");
        return "change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword,
            Model model,
            Principal principal) {

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu mới và xác nhận không khớp!");
            model.addAttribute("activePage", "change-password");
            return "change-password";
        }

        User user = userService.findByUsername(principal.getName());

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("errorMessage", "Mật khẩu cũ không đúng!");
            model.addAttribute("activePage", "change-password");
            return "change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        model.addAttribute("successMessage", "Đổi mật khẩu thành công!");
        model.addAttribute("activePage", "change-password");
        return "change-password";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model, Principal principal) {
        model.addAttribute("activePage", "about");
        notificationService.addNotificationsToModel(model, principal);
        return "about";
    }

    @GetMapping("/contact")
    public String showContactPage(Model model, Principal principal) {
        model.addAttribute("activePage", "contact");
        notificationService.addNotificationsToModel(model, principal);
        return "contact";
    }

    @PostMapping("/contact")
    public String handleContact(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String message,
            Model model) {
        if (name.isBlank() || email.isBlank() || message.isBlank()) {
            model.addAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin!");
            model.addAttribute("activePage", "contact");
            return "contact";
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("maihuychuong78@gmail.com"); // Your support email
            mail.setFrom(email); // User's email
            mail.setSubject("Contact Form Submission from " + name);
            mail.setText("Name: " + name + "\nEmail: " + email + "\nMessage: " + message);
            mailSender.send(mail);
            model.addAttribute("successMessage", "Tin nhắn đã được gửi thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Không thể gửi tin nhắn. Vui lòng thử lại sau.");
            e.printStackTrace(); // Log the error for debugging
        }
        model.addAttribute("activePage", "contact");
        return "contact";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Notification> notifications = notificationService.getNotificationsForUser(user);

        if (notifications != null) {
            for (Notification noti : notifications) {
                if (noti.getIsRead() == null || !noti.getIsRead()) {
                    noti.setIsRead(true);
                    notificationService.save(noti); // cập nhật lại trong DB
                }
            }
        }

        // Sau khi đánh dấu tất cả đã đọc, unreadCount = 0
        long unreadCount = 0;

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("activePage", "notifications");
        return "notifications";
    }
}