package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.auth.dto.AuthResponse;
import com.seyda.jobtracker.auth.dto.LoginRequest;
import com.seyda.jobtracker.auth.dto.RegisterRequest;
import com.seyda.jobtracker.auth.dto.ResetPasswordRequest;
import com.seyda.jobtracker.user.UserService;
import com.seyda.jobtracker.user.User;
import com.seyda.jobtracker.user.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.ok(Map.of("message", "Kayıt işlemi başarıyla tamamlandı!"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.authenticate(request);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));
        ResponseCookie authCookie = ResponseCookie.from("PEBBLE_AUTH", authResponse.getToken())
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofHours(24))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .body(Map.of(
                        "accountPendingDeletion", user.isDeletionPending(),
                        "deletionScheduledAt", user.getDeletionScheduledAt() == null ? "" : user.getDeletionScheduledAt().toString()
                ));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user) {
        if (user != null) {
            authService.revokeTokens(user);
        }
        ResponseCookie expiredCookie = ResponseCookie.from("PEBBLE_AUTH", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }

    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf(CsrfToken csrfToken) {
        csrfToken.getToken();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            authService.forgotPassword(request.get("email"));
        } catch (RuntimeException e) {
            // Do not reveal account existence or internal delivery failures to the caller.
            log.warn("Password reset request could not be processed");
        }
        return ResponseEntity.ok(Map.of(
                "message",
                "E-posta adresi sistemde kayıtlıysa şifre sıfırlama bağlantısı gönderilecektir."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Şifreniz başarıyla yenilendi!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
