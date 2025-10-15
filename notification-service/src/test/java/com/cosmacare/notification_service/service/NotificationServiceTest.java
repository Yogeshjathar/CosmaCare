package com.cosmacare.notification_service.service;

import com.cosmacare.notification_service.entity.Notification;
import com.cosmacare.notification_service.repository.NotificationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter mockCounter;

    @InjectMocks
    private NotificationService notificationService;

    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        mockNotification = new Notification();
        mockNotification.setId("N001");
        mockNotification.setMessage("Test notification");

        // Default behavior for counter
        when(meterRegistry.counter(anyString())).thenReturn(mockCounter);
    }

    @Test
    @DisplayName("Should fetch all notifications successfully")
    void testGetAllNotifications_Success() {
        // Arrange
        when(notificationRepository.findAll()).thenReturn(List.of(mockNotification));

        // Act
        List<Notification> result = notificationService.getAllNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test notification", result.get(0).getMessage());

        // Verify interactions
        verify(notificationRepository, times(1)).findAll();
        verify(meterRegistry, times(1)).counter("notifications.fetched.total");
        verify(mockCounter, times(1)).increment();
    }

    @Test
    @DisplayName("Should return empty list when no notifications found")
    void testGetAllNotifications_EmptyList() {
        // Arrange
        when(notificationRepository.findAll()).thenReturn(List.of());

        // Act
        List<Notification> result = notificationService.getAllNotifications();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions
        verify(notificationRepository, times(1)).findAll();
        verify(meterRegistry, times(1)).counter("notifications.fetched.total");
        verify(mockCounter, times(1)).increment();
    }
}
