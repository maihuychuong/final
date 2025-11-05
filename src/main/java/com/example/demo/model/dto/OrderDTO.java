package com.example.demo.model.dto;

import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.enums.PaymentMethod;
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
public class OrderDTO {
    Integer id;
    Integer userId;
    String fullName;
    String phoneNumber;
    BigDecimal totalPrice;
    OrderStatus status;
    PaymentMethod paymentMethod;
    String shippingAddress;
    Integer shippingMethodId;
    LocalDateTime createdAt;
    String note;
}
