package com.seyda.jobtracker.application.dto;

import com.seyda.jobtracker.application.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class JobApplicationResponse {
    private Long id;
    private String companyName;
    private String position;
    private LocalDate applicationDate;
    private ApplicationStatus status;
    private String notes;
}