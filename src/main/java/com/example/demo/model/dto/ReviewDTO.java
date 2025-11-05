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
public class ReviewDTO {
    Integer id;
    Integer userId;
    Integer productId;
    String productName;
    Integer rating;
    String comment;
    String username;
    LocalDateTime createdAt;
}
