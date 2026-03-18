package com.seyda.jobtracker.development.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long id;
    private String title;
    private String platform;
    private Integer progress;
    private String status;
}