package com.cosmacare.notification_service.service;

import com.cosmacare.notification_service.entity.Notification;
import com.cosmacare.notification_service.repository.NotificationRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final MeterRegistry meterRegistry;

    public NotificationService(NotificationRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    public List<Notification> getAllNotifications(){
        log.info("Fetching all notifications from the database...");

        List<Notification> notifications = repository.findAll();

        log.info("Total notifications fetched: {}", notifications.size());

        // Increment metric for total notifications fetched
        meterRegistry.counter("notifications.fetched.total").increment();

        return notifications;
    }
}
