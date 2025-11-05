package com.example.demo.repository;

import com.example.demo.entity.SpecType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecTypeRepository extends JpaRepository<SpecType, Integer> {
    Optional<SpecType> findByName(String name);
}

