package com.seyda.jobtracker.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OnboardingRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Size(max = 120) String targetRole,
        @Size(max = 160) String location,
        @NotBlank @Size(max = 80) String careerGoal
) {}