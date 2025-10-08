package com.cosmacare.notification_service.controller;

import com.cosmacare.notification_service.entity.Notification;
import com.cosmacare.notification_service.service.NotificationService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/getAllNotifications")
    @Timed(value = "notification.getAll", description = "Time taken to fetch all notifications")
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }
}
