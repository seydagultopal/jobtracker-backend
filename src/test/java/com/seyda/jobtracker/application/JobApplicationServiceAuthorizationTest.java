package com.seyda.jobtracker.application;

import com.seyda.jobtracker.application.dto.JobApplicationRequest;
import com.seyda.jobtracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class JobApplicationServiceAuthorizationTest {

    private JobApplicationRepository repository;
    private JobApplicationService service;
    private User owner;
    private User otherUser;
    private JobApplication application;

    @BeforeEach
    void setUp() {
        repository = mock(JobApplicationRepository.class);
        service = new JobApplicationService(repository);

        owner = User.builder().id(1L).email("owner@example.com").build();
        otherUser = User.builder().id(2L).email("other@example.com").build();
        application = JobApplication.builder()
                .id(10L)
                .companyName("Example Ltd")
                .position("Backend Developer")
                .user(owner)
                .build();
    }

    @Test
    void updateApplicationRejectsAnotherUsersRecord() {
        when(repository.findById(10L)).thenReturn(Optional.of(application));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateApplication(10L, new JobApplicationRequest(), otherUser)
        );

        assertEquals(403, exception.getStatusCode().value());
        verify(repository, never()).save(any(JobApplication.class));
    }

    @Test
    void deleteApplicationRejectsAnotherUsersRecord() {
        when(repository.findById(10L)).thenReturn(Optional.of(application));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteApplication(10L, otherUser)
        );

        assertEquals(403, exception.getStatusCode().value());
        verify(repository, never()).delete(any(JobApplication.class));
    }

    @Test
    void updateApplicationReturnsNotFoundForMissingRecord() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateApplication(999L, new JobApplicationRequest(), owner)
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(repository, never()).save(any(JobApplication.class));
    }

    @Test
    void deleteApplicationReturnsNotFoundForMissingRecord() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteApplication(999L, owner)
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(repository, never()).delete(any(JobApplication.class));
    }

    @Test
    void listingApplicationsQueriesOnlyCurrentUsersRecords() {
        when(repository.findAllByUserId(2L)).thenReturn(List.of());

        service.getAllApplications(otherUser);

        verify(repository).findAllByUserId(2L);
        verify(repository, never()).findAll();
    }
}
