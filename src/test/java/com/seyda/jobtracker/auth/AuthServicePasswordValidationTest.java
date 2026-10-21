package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServicePasswordValidationTest {

    private PasswordResetTokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        tokenRepository = mock(PasswordResetTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);
        service = new AuthService(
                userRepository,
                passwordEncoder,
                mock(JwtService.class),
                mock(AuthenticationManager.class),
                tokenRepository,
                mock(KafkaTemplate.class)
        );
    }

    @Test
    void resetPasswordRejectsBlankTokenBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class, () -> service.resetPassword("  ", "ValidPass123"));
        verifyNoInteractions(tokenRepository, passwordEncoder, userRepository);
    }

    @Test
    void resetPasswordRejectsNullTokenBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class, () -> service.resetPassword(null, "ValidPass123"));
        verifyNoInteractions(tokenRepository, passwordEncoder, userRepository);
    }

    @Test
    void resetPasswordRejectsShortPasswordBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class, () -> service.resetPassword("token", "short"));
        verifyNoInteractions(tokenRepository, passwordEncoder, userRepository);
    }

    @Test
    void resetPasswordRejectsBlankPasswordBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class, () -> service.resetPassword("token", "   "));
        verifyNoInteractions(tokenRepository, passwordEncoder, userRepository);
    }

    @Test
    void resetPasswordRejectsNullPasswordBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class, () -> service.resetPassword("token", null));
        verifyNoInteractions(tokenRepository, passwordEncoder, userRepository);
    }
}
