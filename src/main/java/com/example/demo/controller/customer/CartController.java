package com.example.demo.controller.customer;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.User;
import com.example.demo.model.enums.CartStatus;
import com.example.demo.service.CartService;
import com.example.demo.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.security.Principal;
import java.util.Collections;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String showCart(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(principal.getName());
        Cart cart = cartService.getActiveCartByUser(user);

        if (cart == null) {
            cart = Cart.builder()
                    .cartItems(Collections.emptyList())
                    .totalPrice(BigDecimal.ZERO)
                    .build();
        } else {
            if (cart.getCartItems() == null) {
                cart.setCartItems(Collections.emptyList());
            }
            // Nếu tổng tiền chưa chính xác (null hoặc 0), tính lại
            if (cart.getTotalPrice() == null || cart.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal totalPrice = cartService.calculateTotalPrice(cart);
                cart.setTotalPrice(totalPrice);
                cartService.save(cart); // Gợi ý: Lưu lại nếu đã tính lại tổng tiền
            }
        }

        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());

        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam("quantity") Integer quantity,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn chưa đăng nhập.");
            return "redirect:/login";
        }


        String username = principal.getName();
        User user = userService.findByUsername(username);

        cartService.addToCart(user, productId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng.");
        return "redirect:/products";
    }

    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam Long cartItemId,
                                 @RequestParam String action,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        CartItem cartItem = cartService.findCartItemByIdAndUser(cartItemId, user);
        if (cartItem != null) {
            int quantity = cartItem.getQuantity();
            int maxStock = cartItem.getProduct().getStock(); // lấy số lượng tồn kho sản phẩm
            if ("increase".equals(action)) {
                if (quantity >= maxStock) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Số lượng sản phẩm đã đạt tối đa trong kho.");
                } else {
                    cartService.updateQuantity(cartItem, quantity + 1);
                    redirectAttributes.addFlashAttribute("successMessage", "Cập nhật số lượng thành công.");
                }
            } else if ("decrease".equals(action)) {
                if (quantity <= 1) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Số lượng sản phẩm không thể giảm xuống 0.");
                } else {
                    cartService.updateQuantity(cartItem, quantity - 1);
                    redirectAttributes.addFlashAttribute("successMessage", "Cập nhật số lượng thành công.");
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại trong giỏ hàng.");
        }
        return "redirect:/cart";
    }


    @PostMapping("/delete")
    public String deleteCartItem(@RequestParam Integer cartItemId, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(principal.getName());

        cartService.deleteCartItemByIdAndUser(cartItemId, user);

        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        return "redirect:/cart";
    }

    @PostMapping("/cancel")
    public String cancelCart(@AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());

        Cart currentCart = cartService.getActiveCartByUser(user); // Lấy giỏ hàng ACTIVE hiện tại
        if (currentCart != null) {
            currentCart.setStatus(CartStatus.CANCELLED);  // Đổi trạng thái thành CANCELLED
            cartService.save(currentCart);  // Lưu lại thay đổi

            // Tạo giỏ hàng mới trạng thái ACTIVE
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setStatus(CartStatus.ACTIVE);
            newCart.setCreatedAt(LocalDateTime.now());
            newCart.setUpdatedAt(LocalDateTime.now());
            cartService.save(newCart);
        }

        redirectAttributes.addFlashAttribute("success", "Giỏ hàng đã được hủy và tạo mới thành công.");
        return "redirect:/cart";
    }
}

