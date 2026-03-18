package com.seyda.jobtracker.development.repository;

import com.seyda.jobtracker.development.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUser_Id(Long userId);
}