package com.example.demo.controller.admin;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    private final UserRepository userRepository;

    public AdminCustomerController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Xem chi tiết khách hàng theo ID, trả về trang Thymeleaf
    @GetMapping("/{id}")
    public String viewCustomerDetail(@PathVariable Integer id, Model model) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            model.addAttribute("customer", userOptional.get());
            return "customer-detail";  // tên file HTML Thymeleaf
        } else {
            return "redirect:/admin/customers?error=notfound";
        }
    }

    // Khoá tài khoản
    @PostMapping("/{id}/lock")
    public String lockCustomer(@PathVariable Integer id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsLocked(true);
            userRepository.save(user);
        }

        return "redirect:/admin/customers/" + id;
    }

    // Mở khoá tài khoản
    @PostMapping("/{id}/unlock")
    public String unlockCustomer(@PathVariable Integer id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsLocked(false);
            userRepository.save(user);
        }

        return "redirect:/admin/customers/" + id;
    }

}
