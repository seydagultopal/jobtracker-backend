package com.seyda.jobtracker.development.repository;

import com.seyda.jobtracker.development.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUser_Id(Long userId);
}