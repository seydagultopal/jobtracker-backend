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

    @Enumerated(EnumType.STRING)
    private ApplicationType applicationType;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;

    private String location;
    private String salary;

    @Column(columnDefinition = "TEXT")
    private String description;

    // YENİ: Başvuru sırasında sorulan özel sorular ve cevapların (TEXT)
    @Column(columnDefinition = "TEXT")
    private String applicationQuestions;

    // YENİ: Başvurulan platform (LinkedIn, Kariyer.net veya Özel Link)
    private String platform;

    @Column(nullable = false)
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}