package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spec_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
}

