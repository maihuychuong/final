package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    public List<Product> filterProducts(String keyword, List<String> brands, String priceRange, String stock) {
        List<Product> products = getAllProducts();

        if (keyword != null && !keyword.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }

        if (brands != null && !brands.isEmpty()) {
            products = products.stream()
                    .filter(p -> brands.contains(p.getBrand()))
                    .toList();
        }

        if (priceRange != null && !priceRange.isEmpty()) {
            String[] parts = priceRange.split("-");
            if (parts.length == 2) {
                try {
                    BigDecimal min = new BigDecimal(parts[0]);
                    BigDecimal max = new BigDecimal(parts[1]);
                    products = products.stream()
                            .filter(p -> p.getPrice() != null &&
                                    p.getPrice().compareTo(min) >= 0 &&
                                    p.getPrice().compareTo(max) <= 0)
                            .toList();
                } catch (NumberFormatException ignored) {}
            }
        }

        if (stock != null && !stock.isEmpty()) {
            if ("in-stock".equals(stock)) {
                products = products.stream()
                        .filter(p -> p.getStock() != null && p.getStock() > 0)
                        .toList();
            } else if ("out-of-stock".equals(stock)) {
                products = products.stream()
                        .filter(p -> p.getStock() == null || p.getStock() == 0)
                        .toList();
            }
        }

        return products;
    }

    public List<Product> getFeaturedProducts(int limit) {
        return productRepository.findTopByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Integer id) {
        productRepository.deleteById(id);
    }
}
