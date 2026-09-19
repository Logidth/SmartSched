package com.smartsched.report.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FacultyWorkloadReportResponse {

    private Long facultyId;

    private String employeeId;

    private String facultyName;

    private String branchName;

    private String designation;

    private long weeklyPeriods;

    private long completedLectures;

    private long pendingLectures;

    private double completionPercentage;
}
