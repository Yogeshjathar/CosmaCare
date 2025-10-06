package com.cosmacare.reward_service.repository;

import com.cosmacare.reward_service.entity.Reward;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends MongoRepository<Reward, String> {
    List<Reward> findByUserId(String userId);
}
