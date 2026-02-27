package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.auth.dto.AuthResponse;
import com.seyda.jobtracker.auth.dto.LoginRequest;
import com.seyda.jobtracker.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // YENİ EKLENEN: Şifre sıfırlama ucu (JSON dönecek şekilde ayarlandı)
    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, String>> resetPassword(@RequestBody java.util.Map<String, String> request) {
        authService.resetPassword(request.get("email"), request.get("newPassword"));
        // Axios'un hata vermemesi için düz String yerine JSON objesi (Map) dönüyoruz.
        return ResponseEntity.ok(java.util.Map.of("message", "Şifre başarıyla güncellendi"));
    }
}