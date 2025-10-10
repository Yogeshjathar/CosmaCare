package com.cosmacare.notification_service.consumer;

import com.cosmacare.notification_service.dto.RepairCreatedEvent;
import com.cosmacare.notification_service.entity.Notification;
import com.cosmacare.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;

    @KafkaListener(
            topics = "repair-created",
            groupId = "notification-group",
            containerFactory = "repairKafkaListenerContainerFactory"
    )
    public void consumeMessage(ConsumerRecord<String, RepairCreatedEvent> record) {

        // Extract data from ConsumerRecord
        String topic = record.topic();
        int partition = record.partition();
        long offset = record.offset();
        String key = record.key();
        RepairCreatedEvent event = record.value();

        log.info("📩 Received Kafka message -> topic={}, partition={}, offset={}, key={}, event={}",
                topic, partition, offset, key, event);

        // Build notification message
        String notificationMsg = String.format(
                "New repair request created by %s for issue: %s",
                event.getStoreWorkerUserName(),
                event.getIssueType()
        );

        // Save Notification to DB
        Notification notification = new Notification();
        notification.setRepairId(event.getId());
        notification.setMessage(notificationMsg);
        notification.setCreatedAt(event.getCreatedAt());

        repository.save(notification);

        log.info("✅ Notification saved successfully for repair ID {}", event.getId());
    }
}
