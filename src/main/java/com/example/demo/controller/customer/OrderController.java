package com.example.demo.controller.customer;

import com.example.demo.entity.*;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.enums.PaymentMethod;
import com.example.demo.model.enums.Role;
import com.example.demo.model.enums.ShippingMethodType;
import com.example.demo.service.CartService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final CartService cartService;
    private final NotificationService notificationService;

    public OrderController(OrderService orderService, UserService userService, CartService cartService, NotificationService notificationService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.notificationService = notificationService;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());

        // Kiểm tra giỏ hàng
        List<CartItem> cartItems = cartService.getItemsByUser(user);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm trước khi đặt hàng.");
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setUser(user);
        order.setFullName(user.getFullName());
        order.setPhoneNumber(user.getPhoneNumber());

        model.addAttribute("order", order);
        model.addAttribute("shippingMethodTypes", ShippingMethodType.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());

        return "order-create";
    }

    // Hiển thị form tạo đơn hàng mới
    @PostMapping("/create")
    public String createOrder(@Valid @ModelAttribute("order") Order order,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("shippingMethodTypes", ShippingMethodType.values());
            model.addAttribute("paymentMethods", PaymentMethod.values());
            return "order-create";
        }

        User user = userService.findByUsername(userDetails.getUsername());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<CartItem> cartItems = cartService.getItemsByUser(user);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng của bạn đang trống.");
            return "redirect:/cart";
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getPriceAfterDiscount().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }
        order.setTotalPrice(totalPrice);

        // Lưu đơn hàng
        orderService.save(order);

        // Gửi notification cho ADMIN
        List<User> admins = userService.findByRole(Role.ADMIN);
        String adminTitle = "Đơn hàng mới #" + order.getId();
        String adminContent = "Khách hàng " + user.getUsername() + " vừa đặt đơn hàng #" + order.getId() + " trị giá " + totalPrice + " VND.";
        for (User admin : admins) {
            notificationService.createNotification(admin, adminTitle, adminContent);
        }

        // Gửi notification cho khách hàng
        String customerTitle = "Đặt hàng thành công";
        String customerContent = "Cảm ơn bạn đã đặt hàng! Đơn hàng #" + order.getId() + " đang được xử lý.";
        notificationService.createNotification(user, customerTitle, customerContent);

        // Xử lý giỏ hàng + chi tiết đơn hàng
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            int quantityOrdered = item.getQuantity();

            int currentStock = product.getStock();
            product.setStock(currentStock - quantityOrdered);
            orderService.updateProduct(product);

            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantityOrdered)
                    .price(product.getPrice())
                    .finalPrice(product.getPriceAfterDiscount())
                    .build();
            orderService.saveOrderDetail(detail);
        }

        cartService.clearCart(user);

        redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
        return "redirect:/orders";
    }

    @GetMapping
    public String viewOrders(Model model,
                             @AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(name = "status", required = false) OrderStatus status,
                             @RequestParam(name = "createdDate", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
                             @RequestParam(name = "sort", defaultValue = "desc") String sort) {

        User user = userService.findByUsername(userDetails.getUsername());
        List<Order> orders = orderService.findByUserWithFilter(user, status, createdDate, sort);

        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("createdDate", createdDate != null ? createdDate.toString() : null);
        model.addAttribute("sort", sort);
        return "orders"; // trang Thymeleaf
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        Optional<Order> optionalOrder = orderService.findByIdAndUser(id, user);

        if (optionalOrder.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
            return "redirect:/orders";
        }

        Order order = optionalOrder.get();

        if (order.getStatus() != OrderStatus.PENDING) {
            redirectAttributes.addFlashAttribute("error", "Không thể huỷ đơn hàng đã được xử lý.");
            return "redirect:/orders";
        }

        // Đổi trạng thái và thời gian cập nhật
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderService.save(order);

        // Cộng lại số lượng sản phẩm
        List<OrderDetail> orderDetails = orderService.findOrderDetailsByOrder(order);
        for (OrderDetail detail : orderDetails) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            orderService.updateProduct(product);
        }


        redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được huỷ.");
        return "redirect:/orders";
    }

    @GetMapping("/{orderId}")
    public String getOrderDetail(@PathVariable Integer orderId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Order> optionalOrder = orderService.findById(orderId);

        if (optionalOrder.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng với mã: " + orderId);
            return "redirect:/orders";  // quay về trang danh sách đơn hàng
        }

        Order order = optionalOrder.get();
        model.addAttribute("order", order);
        return "order-detail"; // file Thymeleaf hiển thị chi tiết đơn hàng
    }
}
