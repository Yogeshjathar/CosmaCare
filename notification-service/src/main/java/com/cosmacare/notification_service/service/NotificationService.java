package com.cosmacare.notification_service.service;

import com.cosmacare.notification_service.entity.Notification;
import com.cosmacare.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public List<Notification> getAllNotifications(){
        return repository.findAll();
    }
}
