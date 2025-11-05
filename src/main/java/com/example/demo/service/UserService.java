package com.example.demo.service;

import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.User;
import com.example.demo.model.enums.Role;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService
{
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    // Tìm user theo username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));
    }

    // Tìm user theo email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        System.out.println("User role: " + user.getRole().name());
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(user.getRole().name()))
                .accountLocked(Boolean.TRUE.equals(user.getIsLocked())) // Đánh dấu tài khoản bị khóa
                .build();
    }

    // Tạo token đặt lại mật khẩu
    public void createPasswordResetToken(User user, String token) {
        // Kiểm tra xem user đã có token chưa
        PasswordResetToken existingToken = tokenRepository.findByUser(user);

        if (existingToken != null) {
            // Nếu đã có → cập nhật token và hạn dùng
            existingToken.setToken(token);
            existingToken.setExpiryDate(LocalDateTime.now().plusHours(1));
            tokenRepository.save(existingToken);
        } else {
            // Nếu chưa có → tạo mới
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
            tokenRepository.save(resetToken);
        }
    }


    // Kiểm tra token có hợp lệ (còn hạn, tồn tại)
    public boolean isValidToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null) return false;
        return resetToken.getExpiryDate().isAfter(LocalDateTime.now());
    }

    // Đổi mật khẩu dựa trên token
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // token không hợp lệ hoặc hết hạn
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xoá token sau khi đổi mật khẩu thành công
        tokenRepository.delete(resetToken);
        return true;
    }

    // Gửi mail link đặt lại mật khẩu
    public boolean sendPasswordResetEmail(User user, String appUrl, String token) {
        try {
            String resetUrl = appUrl + "/reset-password?token=" + token;

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail()); // Email người nhận
            mailMessage.setSubject("Yêu cầu đặt lại mật khẩu");
            mailMessage.setText("Chào " + user.getUsername() + ",\n\n"
                    + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấp vào liên kết dưới đây để đặt lại:\n"
                    + resetUrl + "\n\n"
                    + "Nếu bạn không yêu cầu, hãy bỏ qua email này.");

            mailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method cập nhật profile người dùng
    public void updateUserProfile(Integer userId, String fullName, String email, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra email có bị trùng với người dùng khác không
        userRepository.findByEmail(email).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(userId)) {
                throw new RuntimeException("Email đã được sử dụng");
            }
        });

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);

        userRepository.save(user);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    // Khoá tài khoản
    public void lockUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        user.setIsLocked(true);
        userRepository.save(user);
    }

    // Mở khoá tài khoản
    public void unlockUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        user.setIsLocked(false);
        userRepository.save(user);
    }

}
