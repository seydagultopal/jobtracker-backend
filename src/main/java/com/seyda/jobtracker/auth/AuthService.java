package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.auth.dto.AuthResponse;
import com.seyda.jobtracker.auth.dto.LoginRequest;
import com.seyda.jobtracker.user.User;
import com.seyda.jobtracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository tokenRepository;
    
    // ÇÖZÜM: Spring Boot'un otomatik oluşturduğu tipe (<Object, Object>) uygun hale getirdik.
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    private String hashResetToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }

        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = tokenRepository.findByUser(user)
                .orElse(PasswordResetToken.builder().user(user).build());

        resetToken.setToken(hashResetToken(token));
        resetToken.setExpiryDate(expiry);

        tokenRepository.save(resetToken);

        // Kafka'ya mesaj bırakıyoruz
        PasswordResetEvent event = new PasswordResetEvent(user.getEmail(), token);
        kafkaTemplate.send("password-reset-topic", event);

        System.out.println("Kafka'ya mesaj bırakıldı: " + user.getEmail());
    }

    @Transactional
    public void revokeTokens(User user) {
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(hashResetToken(token))
                .orElseThrow(() -> new RuntimeException("Geçersiz veya süresi dolmuş link!"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Bu linkin süresi dolmuş. Lütfen tekrar şifre sıfırlama talebi oluşturun.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}