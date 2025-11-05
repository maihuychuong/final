package com.example.demo.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpecificationDTO {
    Integer id;
    Integer productId;
    Integer specTypeId;
    String specValue;
}
