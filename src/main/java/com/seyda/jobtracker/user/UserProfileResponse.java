package com.seyda.jobtracker.user;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String location,
        String title,
        String careerGoal,
        boolean onboardingCompleted,
        String linkedin,
        String github,
        String bio,
        String photo,
        String coverPhoto,
        String experiences,
        String educations,
        LocalDateTime createdAt
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getLocation(),
                user.getTitle(),
                user.getCareerGoal(),
                user.isOnboardingCompleted(),
                user.getLinkedin(),
                user.getGithub(),
                user.getBio(),
                user.getPhoto(),
                user.getCoverPhoto(),
                user.getExperiences(),
                user.getEducations(),
                user.getCreatedAt()
        );
    }
}