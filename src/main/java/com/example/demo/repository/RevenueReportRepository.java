package com.example.demo.repository;

import com.example.demo.entity.RevenueReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RevenueReportRepository extends JpaRepository<RevenueReport, Integer> {
    List<RevenueReport> findByReportMonth(String reportMonth);
    List<RevenueReport> findByReportMonthStartingWith(String year);
    RevenueReport findTopByOrderByReportMonthDesc();
}
