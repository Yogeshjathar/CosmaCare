package com.cosmacare.reward_service.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "rewards")
public class Reward {

    @Id
    private String id;

    private String userId;           // Who earns the reward
    private int points;              // Number of points
    private RewardType rewardType;   // Enum instead of String
    private RewardStatus status;     // Enum instead of String
    private LocalDateTime createdAt;
    private LocalDateTime redeemedAt;
}
