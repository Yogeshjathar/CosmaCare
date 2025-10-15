package com.cosmacare.security_service.service;

import com.cosmacare.security_service.client.UserServiceClient;
import com.cosmacare.security_service.dto.UserDTO;
import com.cosmacare.security_service.dto.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private UserDTO mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new UserDTO();
        mockUser.setUserId(UUID.randomUUID());
        mockUser.setUsername("vaibhav");      // username
        mockUser.setPassword("2025");         // password
        mockUser.setRole("STORE_MANAGER");    // updated role
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void testLoadUserByUsername_Success() {
        // Arrange
        when(userServiceClient.getUserByUsername("vaibhav")).thenReturn(mockUser);

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("vaibhav");

        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof UserPrincipal);
        assertEquals("vaibhav", userDetails.getUsername());
        assertEquals("2025", userDetails.getPassword());
        assertEquals("ROLE_STORE_MANAGER", userDetails.getAuthorities().iterator().next().getAuthority());

        verify(userServiceClient, times(1)).getUserByUsername("vaibhav");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userServiceClient.getUserByUsername("unknown")).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown")
        );

        assertEquals("User not found: unknown", exception.getMessage());
        verify(userServiceClient, times(1)).getUserByUsername("unknown");
    }

    @Test
    @DisplayName("Fallback should return guest user when circuit breaker triggers")
    void testFallbackUser() {
        // Act
        UserDetails fallbackUser = userDetailsService.fallbackUser("guest_user", new RuntimeException("Service down"));

        // Assert
        assertNotNull(fallbackUser);
        assertTrue(fallbackUser instanceof UserPrincipal);
        assertEquals("guest_user", fallbackUser.getUsername());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), ((UserPrincipal) fallbackUser).getId());
        assertEquals("ROLE_GUEST", fallbackUser.getAuthorities().iterator().next().getAuthority());
    }
}
