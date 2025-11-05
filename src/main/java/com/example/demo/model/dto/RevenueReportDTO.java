package com.example.demo.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueReportDTO {
    Integer id;
    String reportMonth; // vd: "2025-04"
    BigDecimal totalRevenue;
    Integer totalOrders;
    LocalDateTime createdAt;
}
