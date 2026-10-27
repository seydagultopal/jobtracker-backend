package com.seyda.jobtracker.user;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AccountDeletionService accountDeletionService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserProfileResponse.from(userService.getUserProfile(user.getId())));
    }

    @PutMapping("/me/onboarding")
    public ResponseEntity<UserProfileResponse> completeOnboarding(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.ok(UserProfileResponse.from(userService.completeOnboarding(user.getId(), request)));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(@AuthenticationPrincipal User user, @RequestBody User updatedData) {
        User updatedUser = userService.updateProfile(user.getId(), updatedData);
        return ResponseEntity.ok(UserProfileResponse.from(updatedUser));
    }

    @GetMapping("/me/deletion-status")
    public ResponseEntity<Map<String, Object>> getDeletionStatus(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountDeletionService.getStatus(user.getId()));
    }

    @DeleteMapping("/me/deletion")
    public ResponseEntity<Map<String, String>> requestAccountDeletion(@AuthenticationPrincipal User user) {
        var scheduledAt = accountDeletionService.requestDeletion(user.getId());
        return ResponseEntity.ok(Map.of("message", "Account deletion scheduled", "deletionScheduledAt", scheduledAt.toString()));
    }

    @PostMapping("/me/reactivate")
    public ResponseEntity<Map<String, String>> reactivateAccount(@AuthenticationPrincipal User user) {
        accountDeletionService.reactivate(user.getId());
        return ResponseEntity.ok(Map.of("message", "Account reactivated"));
    }

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
            return ResponseEntity.ok(Map.of("message", "Şifre başarıyla değiştirildi!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}