package com.cosmacare.notification_service.consumer;

import com.cosmacare.notification_service.entity.Notification;
import com.cosmacare.notification_service.entity.RepairCreatedEvent;
import com.cosmacare.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final JavaMailSender mailSender;

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

        log.info("Received Kafka message -> topic={}, partition={}, offset={}, key={}, event={}",
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

        log.info("Notification saved successfully for repair ID {}", event.getId());

        // Send Email
        sendEmailNotification("jatharyv@gmail.com", "New Repair Request", event);
    }

/*    private void sendEmailNotification(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        log.info("Email sent to {}", to);
    }*/

    private void sendEmailNotification(String to, String subject, RepairCreatedEvent event) {
        String text = String.format(
                "New Repair Request Notification\n\n" +
                        "Hello Team,\n\n" +
                        "A new repair request has been created. Please review the details below:\n\n" +
                        "------------------------------------------------------------\n" +
                        "Repair ID     : %s\n" +
                        "Worker Name   : %s\n" +
                        "Store ID      : %s\n" +
                        "Issue Type    : %s\n" +
                        "Created At    : %s\n" +
                        "------------------------------------------------------------\n\n" +
                        "Kindly take the necessary actions.\n\n" +
                        "Best Regards,\n" +
                        "CosmaCare Notification Service",
                event.getId(),
                event.getStoreWorkerUserName(),
                event.getStoreWorkerId(),
                event.getIssueType(),
                event.getCreatedAt()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);

        log.info("Structured email sent to {}", to);
    }
}
