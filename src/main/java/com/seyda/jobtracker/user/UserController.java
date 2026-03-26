package com.seyda.jobtracker.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(@AuthenticationPrincipal User user, @RequestBody User updatedData) {
        return ResponseEntity.ok(userService.updateProfile(user.getId(), updatedData));
    }

    // YENİ EKLENEN GÜVENLİ E-POSTA GÜNCELLEME ENDPOINT'I
    @PutMapping("/me/email")
    public ResponseEntity<?> updateMyEmail(@AuthenticationPrincipal User user, @RequestBody Map<String, String> request) {
        try {
            userService.updateEmail(user.getId(), request.get("newEmail"), request.get("currentPassword"));
            return ResponseEntity.ok(Map.of("message", "E-posta başarıyla güncellendi!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> updateMyPassword(@AuthenticationPrincipal User user, @RequestBody Map<String, String> passwords) {
        try {
            userService.updatePassword(user.getId(), passwords.get("currentPassword"), passwords.get("newPassword"));
            return ResponseEntity.ok(Map.of("message", "Şifre başarıyla güncellendi!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}