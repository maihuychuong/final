package com.example.demo.controller.admin;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.service.NotificationService;
import com.example.demo.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    private final OrderService orderService;
    private final NotificationService notificationService;

    public AdminOrderController(OrderService orderService, NotificationService notificationService) {
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Integer id, Model model) {
        Optional<Order> optionalOrder = orderService.findById(id);
        if (optionalOrder.isEmpty()) {
            return "redirect:/admin/orders?error=notfound";
        }

        Order order = optionalOrder.get();

        // Các trạng thái có thể cập nhật (ngoại trừ PENDING và CANCELLED)
        List<OrderStatus> availableStatuses = Arrays.asList(
                OrderStatus.CONFIRMED,
                OrderStatus.SHIPPED,
                OrderStatus.DELIVERED
        );

        model.addAttribute("order", order);
        model.addAttribute("availableStatuses", availableStatuses);

        return "order-detail";
    }

    // Cập nhật trạng thái đơn
    @PostMapping("/update-status/{id}")
    public String updateOrderStatus(@PathVariable Integer id,
                                    @RequestParam("status") String status) {
        Optional<Order> optionalOrder = orderService.findById(id);
        if (optionalOrder.isEmpty()) {
            return "redirect:/admin/orders?error=notfound";
        }

        Order order = optionalOrder.get();

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            orderService.save(order);

            // === Gửi notification cho khách hàng ===
            User customer = order.getUser();
            String title = "Cập nhật trạng thái đơn hàng #" + order.getId();
            String content = "Trạng thái đơn hàng của bạn đã được cập nhật thành: " + newStatus.name();
            notificationService.createNotification(customer, title, content);

        } catch (IllegalArgumentException e) {
            return "redirect:/admin/orders/" + id + "?error=invalidstatus";
        }

        return "redirect:/admin/orders/" + id + "?success=updated";
    }

    // Huỷ đơn hàng
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id) {
        Optional<Order> optionalOrder = orderService.findById(id);
        if (optionalOrder.isEmpty()) {
            return "redirect:/admin/orders?error=notfound";
        }

        Order order = optionalOrder.get();

        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.CANCELLED) {
            order.setStatus(OrderStatus.CANCELLED);
            orderService.save(order);
        }

        return "redirect:/admin/orders/" + id + "?success=cancelled";
    }
}
