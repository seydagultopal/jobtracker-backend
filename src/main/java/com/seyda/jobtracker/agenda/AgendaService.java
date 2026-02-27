package com.seyda.jobtracker.agenda;

import com.seyda.jobtracker.agenda.dto.AgendaDto;
import com.seyda.jobtracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public AgendaDto getAgendaByDate(LocalDate date, User user) {
        Agenda agenda = agendaRepository.findByUserIdAndDate(user.getId(), date)
                .orElse(Agenda.builder()
                        .date(date)
                        .user(user)
                        .notes("")
                        .mood(null)
                        .todos(new ArrayList<>())
                        .build());

        return mapToDto(agenda);
    }

    public AgendaDto saveOrUpdateAgenda(LocalDate date, AgendaDto request, User user) {
        Agenda agenda = agendaRepository.findByUserIdAndDate(user.getId(), date)
                .orElse(Agenda.builder()
                        .date(date)
                        .user(user)
                        .build());

        agenda.setNotes(request.getNotes());
        agenda.setMood(request.getMood());
        
        // Mevcut listeyi temizliyoruz (orphanRemoval = true sayesinde veritabanından da silinecek)
        if (agenda.getTodos() == null) {
            agenda.setTodos(new ArrayList<>());
        } else {
            agenda.getTodos().clear();
        }
        
        // Frontend'den yeni gelen maddeleri sırayla ekleyip Ajanda ile bağlıyoruz
        if (request.getTodos() != null) {
            for (TodoItem item : request.getTodos()) {
                item.setAgenda(agenda); // Bu satır kritik: İlişkiyi veritabanına bildirir
                agenda.getTodos().add(item);
            }
        }

        Agenda savedAgenda = agendaRepository.save(agenda);
        return mapToDto(savedAgenda);
    }

    private AgendaDto mapToDto(Agenda agenda) {
        return AgendaDto.builder()
                .id(agenda.getId())
                .date(agenda.getDate())
                .notes(agenda.getNotes())
                .mood(agenda.getMood())
                .todos(agenda.getTodos() != null ? new ArrayList<>(agenda.getTodos()) : new ArrayList<>())
                .build();
    }
}