package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class ForgotPasswordEnumerationTest {

    @Test
    void unknownEmailDoesNotThrowOrCreateTokenOrPublishEvent() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        PasswordResetTokenRepository tokens = mock(PasswordResetTokenRepository.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<Object, Object> kafka = mock(KafkaTemplate.class);

        when(users.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        AuthService service = new AuthService(users, encoder, jwtService, authenticationManager, tokens, kafka);

        assertDoesNotThrow(() -> service.forgotPassword("unknown@example.com"));
        verify(tokens, never()).save(any());
        verify(kafka, never()).send(anyString(), any());
    }
}
