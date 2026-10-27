package com.seyda.jobtracker.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByGoogleSubject(String googleSubject);
    List<User> findAllByDeletionScheduledAtLessThanEqual(LocalDateTime cutoff);
}