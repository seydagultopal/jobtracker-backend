package com.seyda.jobtracker.agenda;

import com.seyda.jobtracker.agenda.dto.AgendaDto;
import com.seyda.jobtracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @GetMapping("/{date}")
    public ResponseEntity<AgendaDto> getAgenda(
            @PathVariable LocalDate date,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(agendaService.getAgendaByDate(date, user));
    }

    @PostMapping("/{date}")
    public ResponseEntity<AgendaDto> saveAgenda(
            @PathVariable LocalDate date,
            @RequestBody AgendaDto request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(agendaService.saveOrUpdateAgenda(date, request, user));
    }
}