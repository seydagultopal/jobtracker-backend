package com.seyda.jobtracker.application;

import com.seyda.jobtracker.application.dto.JobApplicationRequest;
import com.seyda.jobtracker.application.dto.JobApplicationResponse;
import com.seyda.jobtracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    // Yeni Başvuru Ekleme
    @PostMapping
    public ResponseEntity<JobApplicationResponse> createApplication(
            @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(jobApplicationService.createApplication(request, user));
    }

    // Kullanıcının Tüm Başvurularını Listeleme
    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAllApplications(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(jobApplicationService.getAllApplications(user));
    }

    // Başvuru Güncelleme
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> updateApplication(
            @PathVariable Long id,
            @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(jobApplicationService.updateApplication(id, request, user));
    }

    // Başvuru Silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        jobApplicationService.deleteApplication(id, user);
        return ResponseEntity.noContent().build(); // 204 No Content döner
    }
}