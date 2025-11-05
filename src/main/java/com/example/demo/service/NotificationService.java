package com.example.demo.service;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    public void createNotification(User user, String title, String content) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void addNotificationsToModel(Model model, Principal principal) {
        List<Notification> notifications = new ArrayList<>();
        long unreadCount = 0;

        if (principal != null) {
            try {
                User user = userService.findByUsername(principal.getName());
                if (user != null) {
                    notifications = notificationRepository.findByUser(user);
                    unreadCount = notificationRepository.countByUserAndIsReadFalse(user);
                    System.out.println("User: " + user.getUsername() + ", Unread Count: " + unreadCount);
                } else {
                    System.out.println("Không tìm thấy người dùng: " + principal.getName());
                }
            } catch (Exception e) {
                System.out.println("Lỗi khi lấy thông báo: " + e.getMessage());
            }
        } else {
            System.out.println("Người dùng chưa đăng nhập");
        }

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
    }
}
