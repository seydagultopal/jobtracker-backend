package com.seyda.jobtracker.development.entity;

import com.seyda.jobtracker.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String tech; // Örn: "React, Spring Boot"
    
    private String status; // Örn: "Devam Ediyor", "Tamamlandı"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}