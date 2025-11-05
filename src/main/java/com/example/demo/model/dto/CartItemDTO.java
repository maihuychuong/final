package com.example.demo.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemDTO {
    Integer id;
    Integer cartId;
    Integer productId;
    Integer quantity;
    LocalDateTime addedAt;
}
