package com.example.demo.service;

import com.example.demo.entity.RevenueReport;
import com.example.demo.repository.RevenueReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RevenueReportService {
    private final RevenueReportRepository revenueReportRepository;

    public RevenueReportService(RevenueReportRepository revenueReportRepository) {
        this.revenueReportRepository = revenueReportRepository;
    }

    public BigDecimal getLatestMonthRevenue() {
        RevenueReport latestReport = revenueReportRepository.findTopByOrderByReportMonthDesc();
        if (latestReport != null) {
            return latestReport.getTotalRevenue();
        }
        return BigDecimal.ZERO;
    }
}