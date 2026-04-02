package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    
    // YENİ EKLENEN SATIR: Kullanıcıya göre token bulma
    Optional<PasswordResetToken> findByUser(User user); 
    
    void deleteByUser_Id(Long userId);
}