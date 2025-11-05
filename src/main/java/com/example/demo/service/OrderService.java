package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;


    public OrderService(OrderDetailRepository orderDetailRepository,
                        OrderRepository orderRepository,
                        ProductRepository productRepository, NotificationService notificationService) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    public List<Order> findByUserAndStatus(User user, OrderStatus status) {
        if (status != null) {
            return orderRepository.findByUserAndStatusOrderByCreatedAtDesc(user, status);
        } else {
            return orderRepository.findByUserOrderByCreatedAtDesc(user);
        }
    }

    public Optional<Order> findByIdAndUser(Integer id, User user) {
        return orderRepository.findByIdAndUser(id, user);
    }

    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void saveOrderDetail(OrderDetail detail) {
        orderDetailRepository.save(detail);
    }

    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    public List<OrderDetail> findOrderDetailsByOrder(Order order) {
        return orderDetailRepository.findByOrder(order);
    }

    public List<Order> findByUserWithFilter(User user, OrderStatus status, LocalDate createdDate, String sort) {
        List<Order> orders = orderRepository.findByUser(user); // lấy danh sách theo user

        List<Order> filtered = new ArrayList<>();

        // Lọc đơn hàng theo điều kiện
        for (Order order : orders) {
            boolean match = true;

            if (status != null && order.getStatus() != status) {
                match = false;
            }

            if (createdDate != null && !order.getCreatedAt().toLocalDate().isEqual(createdDate)) {
                match = false;
            }

            if (match) {
                filtered.add(order);
            }
        }

        // Sắp xếp bằng Comparator (không cần for i-j)
        Collections.sort(filtered, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if ("asc".equalsIgnoreCase(sort)) {
                    return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                } else {
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                }
            }
        });

        return filtered;
    }

    public Page<Order> findAll(Pageable pageable, String status) {
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByStatus(OrderStatus.valueOf(status), pageable);
        }
        return orderRepository.findAll(pageable);
    }

    public List<Order> findAllWithPayments() {
        return orderRepository.findAll();
    }

    public Order updateStatus(Integer id, String status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.valueOf(status));
        return orderRepository.save(order);
    }

    public Map<String, Long> getOrderStatusCounts() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getStatus().name(),
                        Collectors.counting()
                ));
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 phút một lần
    @Transactional
    public void autoConfirmPendingOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        List<Order> pendingOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, cutoff);

        for (Order order : pendingOrders) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            // Tạo thông báo cho người dùng
            notificationService.createNotification(
                    order.getUser(),
                    "Đơn hàng đã được xác nhận",
                    "Đơn hàng #" + order.getId() + " của bạn đã được xác nhận tự động sau 1 giờ chờ xử lý."
            );
        }
    }
}
