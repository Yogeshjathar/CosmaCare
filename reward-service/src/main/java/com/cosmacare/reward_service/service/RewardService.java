package com.cosmacare.reward_service.service;

import com.cosmacare.reward_service.entity.Reward;
import com.cosmacare.reward_service.entity.RewardStatus;
import com.cosmacare.reward_service.entity.RewardType;
import com.cosmacare.reward_service.repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;

    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    // Add points / create reward
    public Reward addReward(Reward reward, String userId) {
        reward.setUserId(userId);
        reward.setStatus(RewardStatus.EARNED);
        reward.setCreatedAt(LocalDateTime.now());
        return rewardRepository.save(reward);
    }

    // Redeem points
    public Optional<Reward> redeemReward(String rewardId) {
        Optional<Reward> rewardOpt = rewardRepository.findById(rewardId);
        rewardOpt.ifPresent(reward -> {
            reward.setStatus(RewardStatus.REDEEMED);
            reward.setRedeemedAt(LocalDateTime.now());
            rewardRepository.save(reward);
        });
        return rewardOpt;
    }

    // Get rewards by user
    public List<Reward> getRewardsByUser(String userId) {
        return rewardRepository.findByUserId(userId);
    }

    // Get all rewards
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }
}
