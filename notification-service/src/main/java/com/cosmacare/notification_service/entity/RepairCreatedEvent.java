package com.cosmacare.notification_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairCreatedEvent {
    private String id;
    private String storeWorkerId;
    private String storeWorkerUserName;
    private String issueType;
    private String description;
    private String status;
    private String assignedTo;
    private LocalDateTime createdAt;
}
