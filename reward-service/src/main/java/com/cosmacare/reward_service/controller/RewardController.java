package com.cosmacare.reward_service.controller;

import com.cosmacare.reward_service.entity.Reward;
import com.cosmacare.reward_service.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    // Add points / create reward
    @PostMapping("/add")
    public ResponseEntity<Reward> addReward(@RequestBody Reward reward,@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(rewardService.addReward(reward, userId));
    }

    // Redeem reward
    @PutMapping("/redeem/{rewardId}")
    public ResponseEntity<Reward> redeemReward(@PathVariable String rewardId) {
        return rewardService.redeemReward(rewardId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get rewards by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reward>> getRewardsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(rewardService.getRewardsByUser(userId));
    }

    // Get all rewards
    @GetMapping
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }
}
