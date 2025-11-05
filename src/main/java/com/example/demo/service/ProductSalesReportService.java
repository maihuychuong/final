package com.example.demo.service;

import com.example.demo.entity.ProductSalesReport;
import com.example.demo.repository.ProductSalesReportRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSalesReportService {
    private final ProductSalesReportRepository productSalesReportRepository;

    public ProductSalesReportService(ProductSalesReportRepository productSalesReportRepository) {
        this.productSalesReportRepository = productSalesReportRepository;
    }

    public List<ProductSalesReport> getAllProductSalesReports() {
        return productSalesReportRepository.findAll();
    }

    public List<ProductSalesReport> getTop5BestSellingProductsLastMonth() {
        String latestMonth = productSalesReportRepository.findLatestReportMonth();
        if (latestMonth != null) {
            return productSalesReportRepository.findTop5ByReportMonthOrderByTotalQuantitySoldDesc(latestMonth);
        }
        return new ArrayList<>();
    }

    public String getLatestReportMonth() {
        return productSalesReportRepository.findLatestReportMonth();
    }
}
