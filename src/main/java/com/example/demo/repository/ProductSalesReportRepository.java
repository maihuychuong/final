package com.example.demo.repository;

import com.example.demo.entity.ProductSalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductSalesReportRepository extends JpaRepository<ProductSalesReport, Integer> {

    List<ProductSalesReport> findByReportMonth(String reportMonth);
    List<ProductSalesReport> findByReportMonthStartingWith(String year);

    // 🆕 Tìm tháng mới nhất có báo cáo
    @Query("SELECT MAX(r.reportMonth) FROM ProductSalesReport r")
    String findLatestReportMonth();

    // 🆕 Lấy 5 sản phẩm bán chạy nhất trong tháng đó
    List<ProductSalesReport> findTop5ByReportMonthOrderByTotalQuantitySoldDesc(String reportMonth);
}
