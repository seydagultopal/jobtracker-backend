package com.seyda.jobtracker.development.service;

import com.seyda.jobtracker.development.dto.CourseDto;
import com.seyda.jobtracker.development.dto.ProjectDto;
import com.seyda.jobtracker.development.dto.SkillDto;
import com.seyda.jobtracker.development.entity.Course;
import com.seyda.jobtracker.development.entity.Project;
import com.seyda.jobtracker.development.entity.Skill;
import com.seyda.jobtracker.development.repository.CourseRepository;
import com.seyda.jobtracker.development.repository.ProjectRepository;
import com.seyda.jobtracker.development.repository.SkillRepository;
import com.seyda.jobtracker.user.User;
import com.seyda.jobtracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DevelopmentServiceAuthorizationTest {

    private CourseRepository courseRepository;
    private ProjectRepository projectRepository;
    private SkillRepository skillRepository;
    private UserRepository userRepository;
    private DevelopmentService service;
    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        projectRepository = mock(ProjectRepository.class);
        skillRepository = mock(SkillRepository.class);
        userRepository = mock(UserRepository.class);
        service = new DevelopmentService(courseRepository, projectRepository, skillRepository, userRepository);

        owner = User.builder().id(1L).email("owner@example.com").build();
        otherUser = User.builder().id(2L).email("other@example.com").build();
    }

    @Test
    void updateCourseRejectsAnotherUsersRecordWithoutSaving() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(
                Course.builder().id(10L).title("Private course").user(owner).build()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.updateCourse(10L, otherUser.getId(), new CourseDto()));

        assertEquals(403, exception.getStatusCode().value());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void deleteCourseRejectsAnotherUsersRecordWithoutDeleting() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(
                Course.builder().id(10L).title("Private course").user(owner).build()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.deleteCourse(10L, otherUser.getId()));

        assertEquals(403, exception.getStatusCode().value());
        verify(courseRepository, never()).delete(any(Course.class));
    }

    @Test
    void updateProjectRejectsAnotherUsersRecordWithoutSaving() {
        when(projectRepository.findById(20L)).thenReturn(Optional.of(
                Project.builder().id(20L).title("Private project").user(owner).build()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.updateProject(20L, otherUser.getId(), new ProjectDto()));

        assertEquals(403, exception.getStatusCode().value());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void deleteProjectRejectsAnotherUsersRecordWithoutDeleting() {
        when(projectRepository.findById(20L)).thenReturn(Optional.of(
                Project.builder().id(20L).title("Private project").user(owner).build()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.deleteProject(20L, otherUser.getId()));

        assertEquals(403, exception.getStatusCode().value());
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    void updateSkillRejectsAnotherUsersRecordWithoutSaving() {
        when(skillRepository.findById(30L)).thenReturn(Optional.of(
                Skill.builder().id(30L).name("Java").user(owner).build()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.updateSkill(30L, otherUser.getId(), new SkillDto()));

        assertEquals(403, exception.getStatusCode().value());
        verify(skillRepository, never()).save(any(Skill.class));
    }

    @Test
    void deleteSkillRejectsAnotherUsersRecordWithoutDeleting() {
        when(skillRepository.findById(30L)).thenReturn(Optional.of(
                Skill.builder().id(30L).name("Java").user(owner).build()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.deleteSkill(30L, otherUser.getId()));

        assertEquals(403, exception.getStatusCode().value());
        verify(skillRepository, never()).delete(any(Skill.class));
    }
}
