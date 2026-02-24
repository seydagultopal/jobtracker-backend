package com.seyda.jobtracker.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    // Sadece giriş yapan kullanıcının başvurularını getirmek için özel metodumuz
    List<JobApplication> findAllByUserId(Long userId);
}