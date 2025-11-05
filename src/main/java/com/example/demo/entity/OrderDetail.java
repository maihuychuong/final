package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private BigDecimal price; // Giá gốc lúc đặt hàng (có thể là giá chưa giảm)

    private BigDecimal finalPrice; // Giá sau giảm

    public BigDecimal getTotalPrice() {
        // Tính tổng theo giá sau giảm
        if (finalPrice != null) {
            return finalPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
