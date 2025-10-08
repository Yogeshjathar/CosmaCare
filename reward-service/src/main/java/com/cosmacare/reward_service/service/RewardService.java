package com.cosmacare.reward_service.service;

import com.cosmacare.reward_service.entity.Reward;
import com.cosmacare.reward_service.entity.RewardStatus;
import com.cosmacare.reward_service.repository.RewardRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RewardService {

    private final RewardRepository rewardRepository;
    private final MeterRegistry meterRegistry;

    public RewardService(RewardRepository rewardRepository, MeterRegistry meterRegistry) {
        this.rewardRepository = rewardRepository;
        this.meterRegistry = meterRegistry;
    }

    // Add points / create reward
    public Reward addReward(Reward reward, String userId) {
        log.info("Adding reward for userId: {}", userId);

        reward.setUserId(userId);
        reward.setStatus(RewardStatus.EARNED);
        reward.setCreatedAt(LocalDateTime.now());

        Reward savedReward = rewardRepository.save(reward);
        log.info("Reward created with id: {} for userId: {}", savedReward.getId(), userId);

        // Increment metric for total rewards created
        meterRegistry.counter("reward.created.total").increment();

        return savedReward;
    }

    // Redeem points
    public Optional<Reward> redeemReward(String rewardId) {
        log.info("Redeeming reward with id: {}", rewardId);

        Optional<Reward> rewardOpt = rewardRepository.findById(rewardId);
        rewardOpt.ifPresentOrElse(reward -> {
            reward.setStatus(RewardStatus.REDEEMED);
            reward.setRedeemedAt(LocalDateTime.now());
            rewardRepository.save(reward);

            log.info("Reward with id: {} redeemed successfully", rewardId);
            meterRegistry.counter("reward.redeemed.total").increment();

        }, () -> log.warn("Reward with id: {} not found", rewardId));

        return rewardOpt;
    }

    // Get rewards by user
    public List<Reward> getRewardsByUser(String userId) {
        log.info("Fetching rewards for userId: {}", userId);
        List<Reward> rewards = rewardRepository.findByUserId(userId);
        log.info("Total rewards fetched for userId {}: {}", userId, rewards.size());
        return rewards;
    }

    // Get all rewards
    public List<Reward> getAllRewards() {
        log.info("Fetching all rewards");
        List<Reward> rewards = rewardRepository.findAll();
        log.info("Total rewards fetched: {}", rewards.size());
        return rewards;
    }
}
