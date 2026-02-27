package com.seyda.jobtracker.agenda;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todo_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItem {
    
    // Veritabanı için kendi benzersiz ID'sini oluşturuyoruz
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId; 

    private Long id; // Frontend'den gelen Date.now() ID'sini tutmaya devam edeceğiz
    
    private String text;
    private boolean completed;

    // Hangi ajandaya ait olduğunu bağlayan ilişki
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id")
    @JsonIgnore // JSON'a çevrilirken sonsuz döngüye girmemesi için gizliyoruz
    private Agenda agenda;
}