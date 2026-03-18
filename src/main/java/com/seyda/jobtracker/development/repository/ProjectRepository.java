package com.seyda.jobtracker.development.repository;

import com.seyda.jobtracker.development.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUser_Id(Long userId);
}