package com.example.demo.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO {
    Integer id;
    String name;
    String brand;
    BigDecimal price;
    Integer discountPercent;
    Integer stock;
    String description;
    String imageUrl;
    Integer categoryId;
}
