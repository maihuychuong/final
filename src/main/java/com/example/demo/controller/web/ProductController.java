package com.example.demo.controller.web;

import com.example.demo.entity.Product;
import com.example.demo.model.dto.ReviewDTO;
import com.example.demo.service.NotificationService;
import com.example.demo.service.ProductService;
import com.example.demo.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;
    private final NotificationService notificationService;

    public ProductController(ProductService productService, ReviewService reviewService, NotificationService notificationService) {
        this.productService = productService;
        this.reviewService = reviewService;
        this.notificationService = notificationService;
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "brands", required = false) String[] brandsArray,
            @RequestParam(value = "priceRange", required = false) String priceRange,
            @RequestParam(value = "discount", required = false) String discount,
            @RequestParam(value = "stock", required = false) String stock,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, Principal principal) {

        List<String> brandsFinal = (brandsArray == null) ? new ArrayList<>() : Arrays.asList(brandsArray);
        List<Product> allProducts = productService.filterProducts(keyword, brandsFinal, priceRange, stock);

        // Lọc theo giảm giá
        if ("yes".equals(discount)) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getDiscountPercent() != null && p.getDiscountPercent() > 0)
                    .collect(Collectors.toList());
        } else if ("no".equals(discount)) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getDiscountPercent() == null || p.getDiscountPercent() == 0)
                    .collect(Collectors.toList());
        }

        // Lọc theo còn hàng / hết hàng
        if ("in-stock".equals(stock)) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getStock() != null && p.getStock() > 0)
                    .collect(Collectors.toList());
        } else if ("out-of-stock".equals(stock)) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getStock() == null || p.getStock() == 0)
                    .collect(Collectors.toList());
        }

        // Pagination
        int pageSize = 12;
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        page = Math.min(Math.max(page, 1), totalPages > 0 ? totalPages : 1);

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalProducts);

        List<Product> productsPage = fromIndex < totalProducts ? allProducts.subList(fromIndex, toIndex) : new ArrayList<>();

        // Truyền giá trị vào view
        model.addAttribute("products", productsPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("brands", brandsFinal);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("discount", discount);
        model.addAttribute("stock", stock);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activePage", "products");
        notificationService.addNotificationsToModel(model, principal);

        Set<String> allBrands = productService.getAllProducts().stream()
                .map(Product::getBrand)
                .collect(Collectors.toSet());
        model.addAttribute("allBrands", allBrands);

        return "products";
    }

    @GetMapping("/product-detail/{id}")
    public String detailProduct(@PathVariable int id, Model model, Principal principal) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.getDiscountPercent() == null) {
                product.setDiscountPercent(0);
            }
            System.out.println("Sản phẩm: " + product.getName() + ", Giá: " + product.getPrice() + ", Chiết khấu: " + product.getDiscountPercent());
            model.addAttribute("product", product);
            model.addAttribute("activePage", "products");
            model.addAttribute("reviews", reviewService.getReviewsByProductId(id));
            notificationService.addNotificationsToModel(model, principal);
            return "product-detail";
        } else {
            System.out.println("Không tìm thấy sản phẩm với id = " + id);
            return "redirect:/products";
        }
    }
}
