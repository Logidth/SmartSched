package com.smartsched.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class FacultyDashboardResponse {

    private Long facultyId;

    private String facultyName;

    private long assignedSubjects;

    private long weeklyPeriods;

    private long completedLectures;

    private long pendingLectures;

    private double completionPercentage;

}