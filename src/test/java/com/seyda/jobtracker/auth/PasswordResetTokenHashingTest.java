package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.user.User;
import com.seyda.jobtracker.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordResetTokenHashingTest {

    @Test
    void storesHashInsteadOfRawTokenAndSendsRawTokenOnlyInResetEvent() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        PasswordResetTokenRepository tokens = mock(PasswordResetTokenRepository.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<Object, Object> kafka = mock(KafkaTemplate.class);

        User user = User.builder().id(5L).email("person@example.com").password("hash").build();
        when(users.findByEmail("person@example.com")).thenReturn(Optional.of(user));
        when(tokens.findByUser(user)).thenReturn(Optional.empty());
        when(tokens.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthService service = new AuthService(users, encoder, jwtService, authenticationManager, tokens, kafka);
        service.forgotPassword("person@example.com");

        var captor = org.mockito.ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokens).save(captor.capture());
        String storedToken = captor.getValue().getToken();

        assertNotNull(storedToken);
        assertEquals(64, storedToken.length(), "SHA-256 hex digest should be stored");
        assertTrue(storedToken.matches("[0-9a-f]{64}"));
        verify(kafka).send(eq("password-reset-topic"), any(PasswordResetEvent.class));
    }

    @Test
    void resetLookupUsesHashRatherThanSubmittedRawToken() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        PasswordResetTokenRepository tokens = mock(PasswordResetTokenRepository.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<Object, Object> kafka = mock(KafkaTemplate.class);

        AuthService service = new AuthService(users, encoder, jwtService, authenticationManager, tokens, kafka);
        when(tokens.findByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.resetPassword("raw-reset-token", "NewPassword123"));
        verify(tokens).findByToken(argThat(value -> value.matches("[0-9a-f]{64}") && !value.equals("raw-reset-token")));
    }
}
