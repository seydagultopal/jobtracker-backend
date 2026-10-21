package com.seyda.jobtracker.agenda;

import com.seyda.jobtracker.agenda.dto.AgendaDto;
import com.seyda.jobtracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AgendaServiceAuthorizationTest {

    private AgendaRepository agendaRepository;
    private AgendaService service;
    private User currentUser;

    @BeforeEach
    void setUp() {
        agendaRepository = mock(AgendaRepository.class);
        service = new AgendaService(agendaRepository);
        currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(2L);
    }

    @Test
    void agendaLookupIsScopedToAuthenticatedUser() {
        LocalDate date = LocalDate.of(2026, 10, 21);
        when(agendaRepository.findByUserIdAndDate(2L, date)).thenReturn(Optional.empty());

        AgendaDto result = service.getAgendaByDate(date, currentUser);

        assertEquals(date, result.getDate());
        assertEquals("", result.getNotes());
        verify(agendaRepository).findByUserIdAndDate(2L, date);
        verify(agendaRepository, never()).findById(anyLong());
    }

    @Test
    void saveUsesAuthenticatedUserAndPathDateInsteadOfClientAgendaIdentity() {
        LocalDate pathDate = LocalDate.of(2026, 10, 21);
        AgendaDto request = AgendaDto.builder()
                .id(999L)
                .date(LocalDate.of(2020, 1, 1))
                .notes("my notes")
                .todos(new ArrayList<>())
                .build();
        when(agendaRepository.findByUserIdAndDate(2L, pathDate)).thenReturn(Optional.empty());
        when(agendaRepository.save(any(Agenda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgendaDto result = service.saveOrUpdateAgenda(pathDate, request, currentUser);

        assertEquals(pathDate, result.getDate());
        verify(agendaRepository).findByUserIdAndDate(2L, pathDate);
        verify(agendaRepository).save(argThat(saved ->
                saved.getUser() == currentUser
                        && pathDate.equals(saved.getDate())
                        && "my notes".equals(saved.getNotes())));
    }

    @Test
    void clientCannotReuseDatabaseIdForTodoItem() {
        LocalDate date = LocalDate.of(2026, 10, 21);
        Agenda existingAgenda = Agenda.builder()
                .id(50L)
                .date(date)
                .user(currentUser)
                .todos(new ArrayList<>())
                .build();

        TodoItem existingItem = TodoItem.builder()
                .dbId(700L)
                .id(123L)
                .text("old item")
                .agenda(existingAgenda)
                .build();
        existingAgenda.getTodos().add(existingItem);

        TodoItem incomingItem = TodoItem.builder()
                .dbId(700L)
                .id(123L)
                .text("attacker-controlled item")
                .completed(false)
                .build();

        AgendaDto request = AgendaDto.builder()
                .notes("updated")
                .todos(List.of(incomingItem))
                .build();

        when(agendaRepository.findByUserIdAndDate(2L, date)).thenReturn(Optional.of(existingAgenda));
        when(agendaRepository.save(any(Agenda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.saveOrUpdateAgenda(date, request, currentUser);

        verify(agendaRepository).save(argThat(saved -> {
            if (saved.getTodos() == null || saved.getTodos().size() != 1) {
                return false;
            }
            TodoItem persistedCandidate = saved.getTodos().get(0);
            return persistedCandidate.getDbId() == null
                    && persistedCandidate.getAgenda() == saved;
        }));
    }
}
