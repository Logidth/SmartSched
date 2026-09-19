package com.smartsched.progress.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectProgressResponse {

    private Long studentClassId;

    private String className;

    private Long subjectId;

    private String subjectCode;

    private String subjectName;

    private Integer totalHours;

    private Integer completedHours;

    private Integer remainingHours;

    private Double completionPercentage;

}