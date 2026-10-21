package com.seyda.jobtracker.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServicePasswordChangeTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
        user = User.builder()
                .id(10L)
                .email("user@example.com")
                .password("old-hash")
                .tokenVersion(2)
                .build();
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");
    }

    @Test
    void incrementsTokenVersionWhenPasswordChanges() {
        userService.updatePassword(10L, "old-password", "new-password");

        assertEquals("new-hash", user.getPassword());
        assertEquals(3, user.getTokenVersion());
        verify(userRepository).save(user);
    }

    @Test
    void doesNotChangePasswordOrRevokeTokensWhenCurrentPasswordIsWrong() {
        when(passwordEncoder.matches("wrong-password", "old-hash")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.updatePassword(10L, "wrong-password", "new-password"));

        assertEquals("old-hash", user.getPassword());
        assertEquals(2, user.getTokenVersion());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode("new-password");
    }
}
