package com.seyda.jobtracker.application;

import com.seyda.jobtracker.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private LocalDate applicationDate;

    // Veritabanına 0, 1, 2 diye değil, direkt APPLIED, INTERVIEW şeklinde string olarak kaydeder
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(length = 1000) // Notlar uzun olabilir
    private String notes;

    // Her başvurunun bir sahibi (Kullanıcısı) olmak zorundadır
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}