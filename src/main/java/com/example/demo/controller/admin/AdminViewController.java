package com.example.demo.controller.admin;

import com.example.demo.entity.*;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.enums.Role;
import com.example.demo.repository.*;
import com.example.demo.service.ProductSalesReportService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminViewController {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RevenueReportRepository revenueReportRepository;
    private final ProductSalesReportRepository productSalesReportRepository;
    private final ProductSalesReportService productSalesReportService;

    public AdminViewController(ProductRepository productRepository, OrderRepository orderRepository,
                               UserRepository userRepository, RevenueReportRepository revenueReportRepository, ProductSalesReportRepository productSalesReportRepository, ProductSalesReportService productSalesReportService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.revenueReportRepository = revenueReportRepository;
        this.productSalesReportRepository = productSalesReportRepository;
        this.productSalesReportService = productSalesReportService;
    }

    @GetMapping("/admin")
    public String showAdminDashboard(
            // Dashboard
            @RequestParam(name = "activeTab", defaultValue = "dashboard") String activeTab,

            // Sản phẩm
            @RequestParam(name = "productKeyword", required = false) String productKeyword,
            @RequestParam(name = "productSort", defaultValue = "desc") String productSort,

            // Đơn hàng
            @RequestParam(name = "status", required = false) String statusStr,
            @RequestParam(name = "createdDate", required = false) String createdDateStr,
            @RequestParam(name = "orderSort", defaultValue = "desc") String orderSort,

            // Khách hàng
            @RequestParam(name = "customerKeyword", required = false) String customerKeyword,

            // Bộ lọc báo cáo
            @RequestParam(name = "filterYear", required = false) String filterYear,
            @RequestParam(name = "filterMonth", required = false) String filterMonth,

            Model model) {
        try {
            // --- Dashboard
            long totalProducts = productRepository.count();
            long totalOrders = orderRepository.count();
            long totalCustomers = userRepository.countByRole(Role.CUSTOMER);

            Map<String, Integer> orderStatusCount = new HashMap<>();
            orderStatusCount.put("pending", orderRepository.countByStatus(OrderStatus.PENDING));
            orderStatusCount.put("confirmed", orderRepository.countByStatus(OrderStatus.CONFIRMED));
            orderStatusCount.put("shipped", orderRepository.countByStatus(OrderStatus.SHIPPED));
            orderStatusCount.put("delivered", orderRepository.countByStatus(OrderStatus.DELIVERED));
            orderStatusCount.put("cancelled", orderRepository.countByStatus(OrderStatus.CANCELLED));

            // --- Sản phẩm ---
            Sort productSortOrder = productSort.equalsIgnoreCase("asc") ?
                    Sort.by("createdAt").ascending() :
                    Sort.by("createdAt").descending();

            List<Product> products = (productKeyword != null && !productKeyword.trim().isEmpty()) ?
                    productRepository.findByNameContainingIgnoreCase(productKeyword.trim(), productSortOrder) :
                    productRepository.findAll(productSortOrder);

            // --- Đơn hàng ---
            Sort orderSortOrder = orderSort.equalsIgnoreCase("asc") ?
                    Sort.by("createdAt").ascending() :
                    Sort.by("createdAt").descending();

            OrderStatus status = null;
            if (statusStr != null && !statusStr.isEmpty()) {
                try {
                    status = OrderStatus.valueOf(statusStr.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    model.addAttribute("error", "Trạng thái đơn hàng không hợp lệ: " + statusStr);
                }
            }

            LocalDate createdDate = null;
            if (createdDateStr != null && !createdDateStr.isEmpty()) {
                try {
                    createdDate = LocalDate.parse(createdDateStr);
                } catch (DateTimeParseException e) {
                    model.addAttribute("error", "Ngày tạo không hợp lệ: " + createdDateStr);
                }
            }

            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            if (createdDate != null) {
                startDate = createdDate.atStartOfDay();
                endDate = createdDate.atTime(23, 59, 59, 999999999);
            }

            List<Order> orders;
            if (status == null && createdDate == null) {
                orders = orderRepository.findAll(orderSortOrder);
            } else {
                orders = orderRepository.findByFilters(status, startDate, endDate, orderSortOrder);
            }

            // --- Khách hàng ---
            List<User> customers;
            if (customerKeyword != null && !customerKeyword.trim().isEmpty()) {
                customers = userRepository.findByRoleAndFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        Role.CUSTOMER, customerKeyword.trim(), customerKeyword.trim());
            } else {
                customers = userRepository.findByRole(Role.CUSTOMER);
            }

            // --- Báo cáo ---
            List<RevenueReport> revenueReports;
            List<ProductSalesReport> productSalesReports;

            if (filterYear != null && !filterYear.isEmpty()) {
                if (filterMonth != null && !filterMonth.isEmpty()) {
                    // Lọc theo cả năm và tháng
                    String filterMonthYear = filterYear + "-" + filterMonth;
                    revenueReports = revenueReportRepository.findByReportMonth(filterMonthYear);
                    productSalesReports = productSalesReportRepository.findByReportMonth(filterMonthYear);
                } else {
                    // Lọc chỉ theo năm
                    revenueReports = revenueReportRepository.findByReportMonthStartingWith(filterYear);
                    productSalesReports = productSalesReportRepository.findByReportMonthStartingWith(filterYear);
                }
            } else {
                // Không lọc
                revenueReports = revenueReportRepository.findAll();
                productSalesReports = productSalesReportRepository.findAll();
            }

            // Lấy doanh thu tháng gần nhất
            RevenueReport latestRevenueReport = revenueReportRepository.findTopByOrderByReportMonthDesc();
            BigDecimal latestMonthRevenue = (latestRevenueReport != null) ? latestRevenueReport.getTotalRevenue() : BigDecimal.ZERO;

            // --- Lấy 5 sản phẩm bán chạy nhất tháng gần nhất ---
            String latestMonth = productSalesReportService.getLatestReportMonth();
            List<ProductSalesReport> top5BestSellingProducts = productSalesReportService.getTop5BestSellingProductsLastMonth();

            model.addAttribute("top5BestSellingProducts", top5BestSellingProducts);
            model.addAttribute("latestReportMonth", latestMonth);


            // Add model attributes
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("totalCustomers", totalCustomers);
            model.addAttribute("orderStatusCount", orderStatusCount);

            model.addAttribute("products", products);
            model.addAttribute("productKeyword", productKeyword);
            model.addAttribute("sort", productSort);

            model.addAttribute("orders", orders);
            model.addAttribute("selectedStatus", statusStr);
            model.addAttribute("createdDate", createdDateStr);
            model.addAttribute("orderSort", orderSort);

            model.addAttribute("customers", customers);
            model.addAttribute("customerKeyword", customerKeyword);

            model.addAttribute("revenueReports", revenueReports);
            model.addAttribute("productSalesReports", productSalesReports);
            model.addAttribute("totalRevenue", latestMonthRevenue);

            // Lấy danh sách revenueReports sắp xếp theo reportMonth tăng dần
            List<RevenueReport> sortedRevenueReports = revenueReportRepository.findAll(Sort.by("reportMonth").ascending());

            // Chuẩn bị danh sách label và data
            List<String> revenueLabels = new ArrayList<>();
            List<BigDecimal> revenueData = new ArrayList<>();

            for (RevenueReport report : sortedRevenueReports) {
                revenueLabels.add(report.getReportMonth());
                revenueData.add(report.getTotalRevenue());
            }

            // Đưa vào model
            model.addAttribute("revenueLabels", revenueLabels);
            model.addAttribute("revenueData", revenueData);

            model.addAttribute("filterYear", filterYear);
            model.addAttribute("filterMonth", filterMonth);

            model.addAttribute("activeTab", activeTab);

            return "admin";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải trang admin: " + e.getMessage());
            return "admin";
        }
    }
}