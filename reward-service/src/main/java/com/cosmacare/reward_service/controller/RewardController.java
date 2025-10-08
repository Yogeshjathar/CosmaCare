package com.cosmacare.reward_service.controller;

import com.cosmacare.reward_service.entity.Reward;
import com.cosmacare.reward_service.service.RewardService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
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
    @Timed(value = "reward.add.timer", description = "Time taken to add a reward")
    @Counted(value = "reward.add.count", description = "Number of rewards added")
    public ResponseEntity<Reward> addReward(@RequestBody Reward reward,@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(rewardService.addReward(reward, userId));
    }

    // Redeem reward
    @PutMapping("/redeem/{rewardId}")
    @Timed(value = "reward.redeem.timer", description = "Time taken to redeem a reward")
    @Counted(value = "reward.redeem.count", description = "Number of rewards redeemed")
    public ResponseEntity<Reward> redeemReward(@PathVariable String rewardId) {
        return rewardService.redeemReward(rewardId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get rewards by user
    @GetMapping("/user/{userId}")
    @Timed(value = "reward.getByUser.timer", description = "Time taken to get rewards by user")
    @Counted(value = "reward.getByUser.count", description = "Number of times rewards are fetched by user")
    public ResponseEntity<List<Reward>> getRewardsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(rewardService.getRewardsByUser(userId));
    }

    // Get all rewards
    @GetMapping
    @Timed(value = "reward.getAll.timer", description = "Time taken to get all rewards")
    @Counted(value = "reward.getAll.count", description = "Number of times all rewards are fetched")
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }
}
