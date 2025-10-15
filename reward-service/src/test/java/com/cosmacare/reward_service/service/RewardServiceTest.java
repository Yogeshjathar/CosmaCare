package com.cosmacare.reward_service.service;

import com.cosmacare.reward_service.entity.Reward;
import com.cosmacare.reward_service.entity.RewardStatus;
import com.cosmacare.reward_service.repository.RewardRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter mockCounter;

    @InjectMocks
    private RewardService rewardService;

    private Reward mockReward;

    @BeforeEach
    void setUp() {
        mockReward = new Reward();
        mockReward.setId("R001");
        mockReward.setPoints(100);

        // Use lenient to avoid UnnecessaryStubbingException
        lenient().when(meterRegistry.counter(anyString())).thenReturn(mockCounter);
    }

    @Test
    @DisplayName("Add reward successfully")
    void testAddReward_Success() {
        when(rewardRepository.save(any(Reward.class))).thenAnswer(invocation -> {
            Reward r = invocation.getArgument(0);
            r.setId("R001");
            return r;
        });

        Reward result = rewardService.addReward(mockReward, "U123");

        assertNotNull(result);
        assertEquals("U123", result.getUserId());
        assertEquals(RewardStatus.EARNED, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertEquals("R001", result.getId());

        verify(rewardRepository, times(1)).save(any(Reward.class));
        verify(meterRegistry, times(1)).counter("reward.created.total");
        verify(mockCounter, times(1)).increment();
    }

    @Test
    @DisplayName("Redeem reward successfully")
    void testRedeemReward_Success() {
        mockReward.setStatus(RewardStatus.EARNED);
        when(rewardRepository.findById("R001")).thenReturn(Optional.of(mockReward));
        when(rewardRepository.save(any(Reward.class))).thenReturn(mockReward);

        Optional<Reward> result = rewardService.redeemReward("R001");

        assertTrue(result.isPresent());
        assertEquals(RewardStatus.REDEEMED, result.get().getStatus());
        assertNotNull(result.get().getRedeemedAt());

        verify(rewardRepository, times(1)).findById("R001");
        verify(rewardRepository, times(1)).save(mockReward);
        verify(meterRegistry, times(1)).counter("reward.redeemed.total");
        verify(mockCounter, times(1)).increment();
    }

    @Test
    @DisplayName("Redeem reward when reward not found")
    void testRedeemReward_NotFound() {
        when(rewardRepository.findById("INVALID")).thenReturn(Optional.empty());

        Optional<Reward> result = rewardService.redeemReward("INVALID");

        assertTrue(result.isEmpty());

        verify(rewardRepository, times(1)).findById("INVALID");
        verify(rewardRepository, never()).save(any());
        verify(meterRegistry, never()).counter("reward.redeemed.total");
    }

    @Test
    @DisplayName("Get rewards by user")
    void testGetRewardsByUser() {
        when(rewardRepository.findByUserId("U123")).thenReturn(List.of(mockReward));

        List<Reward> result = rewardService.getRewardsByUser("U123");

        assertEquals(1, result.size());
        assertEquals(mockReward, result.get(0));

        verify(rewardRepository, times(1)).findByUserId("U123");
    }

    @Test
    @DisplayName("Get rewards by user returns empty list")
    void testGetRewardsByUser_Empty() {
        when(rewardRepository.findByUserId("U123")).thenReturn(List.of());

        List<Reward> result = rewardService.getRewardsByUser("U123");

        assertTrue(result.isEmpty());

        verify(rewardRepository, times(1)).findByUserId("U123");
    }

    @Test
    @DisplayName("Get all rewards")
    void testGetAllRewards() {
        when(rewardRepository.findAll()).thenReturn(List.of(mockReward));

        List<Reward> result = rewardService.getAllRewards();

        assertEquals(1, result.size());
        assertEquals(mockReward, result.get(0));

        verify(rewardRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get all rewards returns empty list")
    void testGetAllRewards_Empty() {
        when(rewardRepository.findAll()).thenReturn(List.of());

        List<Reward> result = rewardService.getAllRewards();

        assertTrue(result.isEmpty());

        verify(rewardRepository, times(1)).findAll();
    }
}
