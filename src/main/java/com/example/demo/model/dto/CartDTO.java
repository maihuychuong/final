package com.example.demo.model.dto;

import com.example.demo.model.enums.CartStatus;
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
public class CartDTO {
    Integer id;
    Integer userId;
    BigDecimal totalPrice;
    CartStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
