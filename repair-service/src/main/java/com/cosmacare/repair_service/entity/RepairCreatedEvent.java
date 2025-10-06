package com.cosmacare.repair_service.entity;

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
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    private String assignedTo;
    private LocalDateTime createdAt;
}
