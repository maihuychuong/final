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
public class ProductSalesReportDTO {
    Integer id;
    Integer productId;
    String reportMonth;
    Integer totalQuantitySold;
    BigDecimal totalRevenue;
    LocalDateTime createdAt;
}
