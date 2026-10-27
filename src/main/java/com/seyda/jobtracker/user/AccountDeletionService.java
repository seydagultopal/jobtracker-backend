package com.seyda.jobtracker.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountDeletionService {
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.account-deletion.grace-days:30}")
    private int graceDays;

    @Transactional(readOnly = true)
    public Map<String, Object> getStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));
        return Map.of(
                "deletionPending", user.isDeletionPending(),
                "deletionRequestedAt", user.getDeletionRequestedAt() == null ? "" : user.getDeletionRequestedAt().toString(),
                "deletionScheduledAt", user.getDeletionScheduledAt() == null ? "" : user.getDeletionScheduledAt().toString()
        );
    }

    @Transactional
    public LocalDateTime requestDeletion(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));
        if (user.isDeletionPending()) return user.getDeletionScheduledAt();
        LocalDateTime now = LocalDateTime.now();
        user.setDeletionRequestedAt(now);
        user.setDeletionScheduledAt(now.plusDays(Math.max(1, graceDays)));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
        return user.getDeletionScheduledAt();
    }

    @Transactional
    public void reactivate(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));
        if (!user.isDeletionPending()) return;
        user.setDeletionRequestedAt(null);
        user.setDeletionScheduledAt(null);
        userRepository.save(user);
    }

    @Scheduled(cron = "${app.account-deletion.purge-cron:0 15 3 * * *}")
    @Transactional
    public void purgeExpiredAccounts() {
        var expiredUsers = userRepository.findAllByDeletionScheduledAtLessThanEqual(LocalDateTime.now());
        for (User user : expiredUsers) {
            purgeOneAccount(user);
        }
    }

    private void purgeOneAccount(User user) {
        Long userId = user.getId();
        jdbcTemplate.update("DELETE FROM todo_items WHERE agenda_id IN (SELECT id FROM agendas WHERE user_id = ?)", userId);
        jdbcTemplate.update("DELETE FROM agendas WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM courses WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM job_applications WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM password_reset_token WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM projects WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM skills WHERE user_id = ?", userId);
        userRepository.delete(user);
    }
}
