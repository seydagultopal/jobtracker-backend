package com.seyda.jobtracker.user;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserProfileResponse.from(userService.getUserProfile(user.getId())));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(@AuthenticationPrincipal User user, @RequestBody User updatedData) {
        User updatedUser = userService.updateProfile(user.getId(), updatedData);
        return ResponseEntity.ok(UserProfileResponse.from(updatedUser));
    }

    // YENİ EKLENEN GÜVENLİ E-POSTA GÜNCELLEME ENDPOINT'I
    @PutMapping("/me/email")
    public ResponseEntity<?> updateMyEmail(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateEmailRequest request) {
        try {
            userService.updateEmail(user.getId(), request.getNewEmail(), request.getCurrentPassword());
            return ResponseEntity.ok(Map.of("message", "E-posta başarıyla güncellendi!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> updateMyPassword(@AuthenticationPrincipal User user, @Valid @RequestBody UpdatePasswordRequest passwords) {
        try {
            userService.updatePassword(user.getId(), passwords.getCurrentPassword(), passwords.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Şifre başarıyla güncellendi!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}