package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.model.enums.CartStatus;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public void addToCart(User user, Integer productId, Integer quantity) {
        // Tìm cart ACTIVE theo userId và status
        Cart cart = cartRepository.findByUser_IdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .status(CartStatus.ACTIVE)
                            .totalPrice(BigDecimal.ZERO)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Kiểm tra sản phẩm đã có trong giỏ chưa
        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .addedAt(LocalDateTime.now())
                    .build();
            cartItemRepository.save(newItem);
        }

        // Lấy lại danh sách CartItem mới nhất để tính tổng tiền chính xác
        var updatedCartItems = cartItemRepository.findByCart(cart);

        BigDecimal newTotal = updatedCartItems.stream()
                .map(item -> item.getProduct().getPriceAfterDiscount().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(newTotal);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }


    // Phương thức lấy giỏ hàng ACTIVE của user, nếu chưa có thì tạo mới
    public Cart getActiveCartByUser(User user) {
        return cartRepository.findByUser_IdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .status(CartStatus.ACTIVE)
                            .totalPrice(BigDecimal.ZERO)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    // Tìm CartItem theo id và user
    public CartItem findCartItemByIdAndUser(Long cartItemId, User user) {
        return cartItemRepository.findByIdAndCart_User_Id(cartItemId, user.getId())
                .orElse(null);
    }

    // Phương thức tính tổng tiền giỏ hàng
    public BigDecimal calculateTotalPrice(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getCartItems()) {
            BigDecimal priceAfterDiscount = item.getProduct().getPriceAfterDiscount();
            total = total.add(priceAfterDiscount.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    // Cập nhật số lượng CartItem
    @Transactional
    public void updateQuantity(CartItem cartItem, int newQuantity) {
        if (newQuantity < 1) {
            newQuantity = 1;
        }
        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        // Cập nhật lại tổng tiền giỏ hàng
        Cart cart = cartItem.getCart();
        BigDecimal newTotal = calculateTotalPrice(cart);
        cart.setTotalPrice(newTotal);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Transactional
    public void deleteCartItemByIdAndUser(Integer cartItemId, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem không tồn tại"));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền xóa mục này");
        }

        Cart cart = cartItem.getCart();

        // Xóa khỏi danh sách giỏ hàng trước khi xóa khỏi DB
        cart.getCartItems().remove(cartItem);

        // Xóa khỏi database
        cartItemRepository.delete(cartItem);

        // Tính lại tổng tiền từ danh sách cartItems mới
        BigDecimal newTotal = calculateTotalPrice(cart);
        cart.setTotalPrice(newTotal);
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
    }

    public List<CartItem> getItemsByUser(User user) {
        // Lấy cart ACTIVE
        Cart cart = cartRepository.findByUser_IdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElse(null);

        if (cart == null) {
            return List.of(); // Trả về danh sách rỗng nếu không có giỏ hàng
        }

        // Trả về danh sách cart item từ giỏ hàng
        return cartItemRepository.findByCart(cart);
    }

    @Transactional
    public void clearCart(User user) {
        // Tìm giỏ hàng ACTIVE hiện tại của user
        Cart cart = cartRepository.findByUser_IdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElse(null);

        if (cart != null) {
            // Xoá tất cả các CartItem thuộc cart này
            cartItemRepository.deleteByCart(cart);

            // Cập nhật trạng thái giỏ hàng thành ORDERED
            cart.setStatus(CartStatus.ORDERED);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}
