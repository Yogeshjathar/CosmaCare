package com.cosmacare.notification_service.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;
    private String message;
    private String RepairId;
    private LocalDateTime createdAt = LocalDateTime.now();
}
