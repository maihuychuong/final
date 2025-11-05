package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "revenue_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_month")
    private String reportMonth;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

