package com.seyda.jobtracker.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // "user" PostgreSQL'de ayrılmış bir kelime olabileceği için "users" yapıyoruz.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Veritabanına ilk kaydedilmeden hemen önce createdAt tarihini otomatik atar.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}