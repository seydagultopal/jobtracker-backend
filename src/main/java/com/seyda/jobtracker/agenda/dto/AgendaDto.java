package com.seyda.jobtracker.agenda.dto;

import com.seyda.jobtracker.agenda.TodoItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor // JSON dönüşümü (Jackson) için zorunlu olan boş kurucu
@AllArgsConstructor // Builder deseninin düzgün çalışması için gerekli kurucu
public class AgendaDto {
    private Long id;
    private LocalDate date;
    private String notes;
    private String mood;
    private List<TodoItem> todos;
}