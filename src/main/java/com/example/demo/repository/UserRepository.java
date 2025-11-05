package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);

    // Tìm theo role và tên hoặc email (ignore case)
    @Query("SELECT u FROM User u WHERE u.role = :role AND (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findByRoleAndFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            @Param("role") Role role,
            @Param("keyword") String fullNameKeyword,
            @Param("keyword") String emailKeyword);

    long countByRole(Role role);
}
