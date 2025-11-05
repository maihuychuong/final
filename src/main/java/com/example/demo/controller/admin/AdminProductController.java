package com.example.demo.controller.admin;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public AdminProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    // ProductAdminController.java
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-add";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute("product") Product product, Model model) {
        boolean hasError = false;

        // Kiểm tra giá không âm và không bằng 0
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("priceError", "Giá phải lớn hơn 0");
            hasError = true;
        }

        // Kiểm tra giảm giá
        if (product.getDiscountPercent() != null && product.getDiscountPercent() < 0) {
            model.addAttribute("discountError", "Phần trăm giảm giá không được âm.");
            hasError = true;
        }

        // Kiểm tra tồn kho
        if (product.getStock() != null && product.getStock() < 0) {
            model.addAttribute("stockError", "Tồn kho không được âm.");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("product", product); // Gán lại product để giữ dữ liệu nhập
            return "product-add"; // Quay lại form nếu có lỗi
        }

        product.setCreatedAt(LocalDateTime.now());
        productRepository.save(product);
        return "redirect:/admin?activeTab=products";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Integer id, Model model) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            return "product-edit"; // file thymeleaf admin/product-edit.html
        }
        return "redirect:/admin?activeTab=products";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute Product updatedProduct) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product existingProduct = productOpt.get();

            // Giữ lại thông tin không được sửa
            updatedProduct.setId(existingProduct.getId());
            updatedProduct.setCreatedAt(existingProduct.getCreatedAt());

            // Trường updatedAt sẽ được tự động cập nhật nhờ @UpdateTimestamp
            productRepository.save(updatedProduct);
        }

        return "redirect:/admin?activeTab=products";
    }

    // Xóa sản phẩm theo id
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }
        return "redirect:/admin?activeTab=products";
    }

}
