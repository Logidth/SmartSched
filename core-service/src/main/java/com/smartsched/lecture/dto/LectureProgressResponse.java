package com.smartsched.lecture.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LectureProgressResponse {

    private String subject;

    private Integer requiredHours;

    private Long completedHours;

    private Integer remainingHours;

    private Double completionPercentage;

}