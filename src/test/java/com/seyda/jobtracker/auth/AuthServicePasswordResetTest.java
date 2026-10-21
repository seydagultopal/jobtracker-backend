package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.user.User;
import com.seyda.jobtracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.mockito.Mockito.*;

class AuthServicePasswordResetTest {

    private UserRepository userRepository;
    private PasswordResetTokenRepository tokenRepository;
    private KafkaTemplate<Object, Object> kafkaTemplate;
    private AuthService authService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(PasswordResetTokenRepository.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        authService = new AuthService(
                userRepository,
                mock(org.springframework.security.crypto.password.PasswordEncoder.class),
                mock(JwtService.class),
                mock(org.springframework.security.authentication.AuthenticationManager.class),
                tokenRepository,
                kafkaTemplate
        );
    }

    @Test
    void forgotPasswordDoesNotRevealOrCreateResetDataForUnknownEmail() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        authService.forgotPassword("unknown@example.com");

        verify(userRepository).findByEmail("unknown@example.com");
        verifyNoInteractions(tokenRepository, kafkaTemplate);
    }

    @Test
    void forgotPasswordCreatesResetTokenAndSendsEventForKnownEmail() {
        User user = User.builder().id(1L).email("known@example.com").build();
        when(userRepository.findByEmail("known@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());

        authService.forgotPassword("known@example.com");

        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(kafkaTemplate).send(eq("password-reset-topic"), any(PasswordResetEvent.class));
    }
}
