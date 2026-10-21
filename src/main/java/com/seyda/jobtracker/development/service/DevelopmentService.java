package com.seyda.jobtracker.development.service;

import com.seyda.jobtracker.development.dto.*;
import com.seyda.jobtracker.development.entity.*;
import com.seyda.jobtracker.development.repository.*;
import com.seyda.jobtracker.user.User;
import com.seyda.jobtracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevelopmentService {

    private final CourseRepository courseRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı."));
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " bulunamadı.");
    }

    private ResponseStatusException forbidden() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu kayıt üzerinde işlem yapma yetkiniz yok.");
    }

    // COURSES
    public List<CourseDto> getAllCourses(Long userId) {
        return courseRepository.findByUser_Id(userId).stream()
                .map(c -> CourseDto.builder()
                        .id(c.getId()).title(c.getTitle()).platform(c.getPlatform())
                        .progress(c.getProgress()).status(c.getStatus()).build())
                .collect(Collectors.toList());
    }

    public CourseDto addCourse(Long userId, CourseDto dto) {
        Course course = Course.builder()
                .user(getUser(userId)).title(dto.getTitle()).platform(dto.getPlatform())
                .progress(dto.getProgress()).status(dto.getStatus()).build();
        Course saved = courseRepository.save(course);
        dto.setId(saved.getId());
        return dto;
    }

    public CourseDto updateCourse(Long id, Long userId, CourseDto dto) {
        Course course = courseRepository.findById(id).orElseThrow(() -> notFound("Eğitim"));
        if (!course.getUser().getId().equals(userId)) throw forbidden();
        course.setTitle(dto.getTitle());
        course.setPlatform(dto.getPlatform());
        course.setProgress(dto.getProgress());
        course.setStatus(dto.getStatus());
        Course updated = courseRepository.save(course);
        dto.setId(updated.getId());
        return dto;
    }

    public void deleteCourse(Long id, Long userId) {
        Course course = courseRepository.findById(id).orElseThrow(() -> notFound("Eğitim"));
        if (!course.getUser().getId().equals(userId)) throw forbidden();
        courseRepository.delete(course);
    }

    // PROJECTS
    public List<ProjectDto> getAllProjects(Long userId) {
        return projectRepository.findByUser_Id(userId).stream()
                .map(p -> ProjectDto.builder()
                        .id(p.getId()).title(p.getTitle()).tech(p.getTech()).status(p.getStatus()).build())
                .collect(Collectors.toList());
    }

    public ProjectDto addProject(Long userId, ProjectDto dto) {
        Project project = Project.builder()
                .user(getUser(userId)).title(dto.getTitle()).tech(dto.getTech()).status(dto.getStatus()).build();
        Project saved = projectRepository.save(project);
        dto.setId(saved.getId());
        return dto;
    }

    public ProjectDto updateProject(Long id, Long userId, ProjectDto dto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> notFound("Proje"));
        if (!project.getUser().getId().equals(userId)) throw forbidden();
        project.setTitle(dto.getTitle());
        project.setTech(dto.getTech());
        project.setStatus(dto.getStatus());
        Project updated = projectRepository.save(project);
        dto.setId(updated.getId());
        return dto;
    }

    public void deleteProject(Long id, Long userId) {
        Project project = projectRepository.findById(id).orElseThrow(() -> notFound("Proje"));
        if (!project.getUser().getId().equals(userId)) throw forbidden();
        projectRepository.delete(project);
    }

    // SKILLS
    public List<SkillDto> getAllSkills(Long userId) {
        return skillRepository.findByUser_Id(userId).stream()
                .map(s -> SkillDto.builder()
                        .id(s.getId()).name(s.getName()).level(s.getLevel()).build())
                .collect(Collectors.toList());
    }

    public SkillDto addSkill(Long userId, SkillDto dto) {
        Skill skill = Skill.builder()
                .user(getUser(userId)).name(dto.getName()).level(dto.getLevel()).build();
        Skill saved = skillRepository.save(skill);
        dto.setId(saved.getId());
        return dto;
    }

    public SkillDto updateSkill(Long id, Long userId, SkillDto dto) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> notFound("Yetenek"));
        if (!skill.getUser().getId().equals(userId)) throw forbidden();
        skill.setName(dto.getName());
        skill.setLevel(dto.getLevel());
        Skill updated = skillRepository.save(skill);
        dto.setId(updated.getId());
        return dto;
    }

    public void deleteSkill(Long id, Long userId) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> notFound("Yetenek"));
        if (!skill.getUser().getId().equals(userId)) throw forbidden();
        skillRepository.delete(skill);
    }
}