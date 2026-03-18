package com.seyda.jobtracker.development.controller;

import com.seyda.jobtracker.development.dto.*;
import com.seyda.jobtracker.development.service.DevelopmentService;
import com.seyda.jobtracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/development")
@RequiredArgsConstructor
public class DevelopmentController {

    private final DevelopmentService developmentService;

    // --- COURSES ENDPOINTS ---
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto>> getCourses(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(developmentService.getAllCourses(user.getId()));
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseDto> addCourse(@AuthenticationPrincipal User user, @RequestBody CourseDto dto) {
        return ResponseEntity.ok(developmentService.addCourse(user.getId(), dto));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseDto> updateCourse(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody CourseDto dto) {
        return ResponseEntity.ok(developmentService.updateCourse(id, user.getId(), dto));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@AuthenticationPrincipal User user, @PathVariable Long id) {
        developmentService.deleteCourse(id, user.getId());
        return ResponseEntity.ok().build();
    }

    // --- PROJECTS ENDPOINTS ---
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getProjects(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(developmentService.getAllProjects(user.getId()));
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectDto> addProject(@AuthenticationPrincipal User user, @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(developmentService.addProject(user.getId(), dto));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectDto> updateProject(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(developmentService.updateProject(id, user.getId(), dto));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@AuthenticationPrincipal User user, @PathVariable Long id) {
        developmentService.deleteProject(id, user.getId());
        return ResponseEntity.ok().build();
    }

    // --- SKILLS ENDPOINTS ---
    @GetMapping("/skills")
    public ResponseEntity<List<SkillDto>> getSkills(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(developmentService.getAllSkills(user.getId()));
    }

    @PostMapping("/skills")
    public ResponseEntity<SkillDto> addSkill(@AuthenticationPrincipal User user, @RequestBody SkillDto dto) {
        return ResponseEntity.ok(developmentService.addSkill(user.getId(), dto));
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<SkillDto> updateSkill(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody SkillDto dto) {
        return ResponseEntity.ok(developmentService.updateSkill(id, user.getId(), dto));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@AuthenticationPrincipal User user, @PathVariable Long id) {
        developmentService.deleteSkill(id, user.getId());
        return ResponseEntity.ok().build();
    }
}