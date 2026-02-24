package com.seyda.jobtracker.application;

import com.seyda.jobtracker.application.dto.JobApplicationRequest;
import com.seyda.jobtracker.application.dto.JobApplicationResponse;
import com.seyda.jobtracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .applicationDate(request.getApplicationDate())
                .status(request.getStatus())
                .notes(request.getNotes())
                .user(user) // Başvuruyu giriş yapan kullanıcıya bağlıyoruz
                .build();

        JobApplication savedApplication = repository.save(application);
        return mapToResponse(savedApplication);
    }

    public List<JobApplicationResponse> getAllApplications(User user) {
        return repository.findAllByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobApplicationResponse updateApplication(Long id, JobApplicationRequest request, User user) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı!"));

        // Güvenlik Kontrolü: Bu başvuru gerçekten isteği atan kullanıcıya mı ait?
        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu başvuruyu güncelleme yetkiniz yok!");
        }

        application.setCompanyName(request.getCompanyName());
        application.setPosition(request.getPosition());
        application.setApplicationDate(request.getApplicationDate());
        application.setStatus(request.getStatus());
        application.setNotes(request.getNotes());

        JobApplication updatedApplication = repository.save(application);
        return mapToResponse(updatedApplication);
    }

    public void deleteApplication(Long id, User user) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı!"));

        // Güvenlik Kontrolü: Bu başvuru gerçekten isteği atan kullanıcıya mı ait?
        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu başvuruyu silme yetkiniz yok!");
        }

        repository.delete(application);
    }

    // Entity'yi DTO'ya çeviren yardımcı metodumuz
    private JobApplicationResponse mapToResponse(JobApplication application) {
        return JobApplicationResponse.builder()
                .id(application.getId())
                .companyName(application.getCompanyName())
                .position(application.getPosition())
                .applicationDate(application.getApplicationDate())
                .status(application.getStatus())
                .notes(application.getNotes())
                .build();
    }
}