package com.cosmacare.notification_service.consumer;

import com.cosmacare.notification_service.dto.RepairCreatedEvent;
import com.cosmacare.notification_service.entity.Notification;
import com.cosmacare.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;

    @KafkaListener(topics = "repair-created", groupId = "notification-group",  containerFactory = "repairKafkaListenerContainerFactory")
    public void consumeMessage(RepairCreatedEvent event) {
        log.info("📩 Notification Service received event for repair: " + event.getId());

        // Create a simple notification message
        String notificationMsg = String.format(
                "New repair request created by %s for issue: %s",
                event.getStoreWorkerUserName(),
                event.getIssueType()
        );

        // Save Notification
        Notification notification = new Notification();
        notification.setRepairId(event.getId());
        notification.setMessage(notificationMsg);
        notification.setCreatedAt(event.getCreatedAt());

        repository.save(notification);
    }
}
