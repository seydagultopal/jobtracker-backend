package com.seyda.jobtracker.application.dto;

import com.seyda.jobtracker.application.ApplicationStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobApplicationRequest {
    private String companyName;
    private String position;
    private LocalDate applicationDate;
    private ApplicationStatus status;
    private String notes;
}