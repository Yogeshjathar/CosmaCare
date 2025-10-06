package com.cosmacare.repair_service.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "repairs")
public class Repair {

    @Id
    private String id;
    private String storeWorkerId;   // User ID of the worker raising the repair
    private String storeWorkerUserName;     // Username of the worker raising the repair
    private String issueType;       // e.g., "broken product", "store damage"
    private String description;     // detailed description
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED
    private String assignedTo;      // Technician/Manager assigned
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    // Getter and setters
}
