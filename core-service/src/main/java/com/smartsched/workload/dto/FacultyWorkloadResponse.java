package com.smartsched.workload.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FacultyWorkloadResponse {

    private Long facultyId;

    private String employeeId;

    private String facultyName;

    private Integer theoryHours;

    private Integer labHours;

    private Integer totalHours;

}