package com.seyda.jobtracker.application.dto;

import com.seyda.jobtracker.application.ApplicationStatus;
import com.seyda.jobtracker.application.ApplicationType;
import com.seyda.jobtracker.application.WorkMode;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobApplicationRequest {
    private String companyName;
    private String position;
    private ApplicationType applicationType;
    private WorkMode workMode;
    private String location;
    private LocalDate applicationDate;
    private ApplicationStatus status;
    private String salary;
    private String description;
    private String applicationQuestions; // Yeni eklenen alan
    private String platform;             // Yeni eklenen alan
    private String notes;
}