package com.seyda.jobtracker.application;

import com.seyda.jobtracker.application.dto.JobApplicationRequest;
import com.seyda.jobtracker.application.dto.JobApplicationResponse;
import com.seyda.jobtracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationResponse createApplication(JobApplicationRequest request, User user) {
        JobApplication application = JobApplication.builder()
                .companyName(request.getCompanyName())
                .position(request.getPosition())
                .applicationType(request.getApplicationType())
                .workMode(request.getWorkMode())
                .location(request.getLocation())
                .salary(request.getSalary())
                .description(request.getDescription())
                .applicationQuestions(request.getApplicationQuestions())
                .platform(request.getPlatform())
                .applicationDate(request.getApplicationDate())
                .status(request.getStatus())
                .notes(request.getNotes())
                .user(user)
                .build();

        return mapToResponse(repository.save(application));
    }

    public List<JobApplicationResponse> getAllApplications(User user) {
        return repository.findAllByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobApplicationResponse updateApplication(Long id, JobApplicationRequest request, User user) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Başvuru bulunamadı."));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu başvuruyu güncelleme yetkiniz yok.");
        }

        application.setCompanyName(request.getCompanyName());
        application.setPosition(request.getPosition());
        application.setApplicationType(request.getApplicationType());
        application.setWorkMode(request.getWorkMode());
        application.setLocation(request.getLocation());
        application.setSalary(request.getSalary());
        application.setDescription(request.getDescription());
        application.setApplicationQuestions(request.getApplicationQuestions());
        application.setPlatform(request.getPlatform());
        application.setApplicationDate(request.getApplicationDate());
        application.setStatus(request.getStatus());
        application.setNotes(request.getNotes());

        return mapToResponse(repository.save(application));
    }

    public void deleteApplication(Long id, User user) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Başvuru bulunamadı."));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu başvuruyu silme yetkiniz yok.");
        }

        repository.delete(application);
    }

    private JobApplicationResponse mapToResponse(JobApplication application) {
        return JobApplicationResponse.builder()
                .id(application.getId())
                .companyName(application.getCompanyName())
                .position(application.getPosition())
                .applicationType(application.getApplicationType())
                .workMode(application.getWorkMode())
                .location(application.getLocation())
                .salary(application.getSalary())
                .description(application.getDescription())
                .applicationQuestions(application.getApplicationQuestions())
                .platform(application.getPlatform())
                .applicationDate(application.getApplicationDate())
                .status(application.getStatus())
                .notes(application.getNotes())
                .build();
    }
}