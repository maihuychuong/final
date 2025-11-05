package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_sales_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSalesReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "report_month")
    private String reportMonth;

    @Column(name = "total_quantity_sold")
    private Integer totalQuantitySold;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
