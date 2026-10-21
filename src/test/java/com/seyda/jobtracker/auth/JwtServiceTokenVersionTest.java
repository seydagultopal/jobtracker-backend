package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTokenVersionTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        String secret = Base64.getEncoder().encodeToString(
                "01234567890123456789012345678901".getBytes()
        );
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .tokenVersion(0)
                .build();
    }

    @Test
    void acceptsTokenWhenTokenVersionMatchesUser() {
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void rejectsPreviouslyIssuedTokenAfterTokenVersionChanges() {
        String token = jwtService.generateToken(user);
        user.setTokenVersion(1);

        assertFalse(jwtService.isTokenValid(token, user));
    }

    @Test
    void rejectsTokenWithoutTokenVersionClaim() {
        String legacyToken = io.jsonwebtoken.Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 60_000))
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                                Base64.getDecoder().decode(
                                        Base64.getEncoder().encodeToString(
                                                "01234567890123456789012345678901".getBytes()
                                        )
                                )
                        ),
                        io.jsonwebtoken.SignatureAlgorithm.HS256
                )
                .compact();

        assertFalse(jwtService.isTokenValid(legacyToken, user));
    }
}
