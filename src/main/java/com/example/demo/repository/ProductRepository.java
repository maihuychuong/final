package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Lấy sản phẩm theo giá trong khoảng và hãng nằm trong danh sách
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.brand IN :brands")
    List<Product> findByPriceBetweenAndBrandIn(
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("brands") List<String> brands);

    // Lấy sản phẩm theo giá trong khoảng, không xét hãng
    List<Product> findByPriceBetween(Long minPrice, Long maxPrice);

    // Lấy sản phẩm theo hãng (list)
    List<Product> findByBrandIn(List<String> brands);

    List<Product> findTopByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"specifications", "specifications.specType"})
    Optional<Product> findById(Integer id);

    List<Product> findByNameContainingIgnoreCase(String name, Sort sort);
    List<Product> findAll(Sort sort);
}
