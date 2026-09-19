package com.smartsched.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrincipalDashboardResponse {

    private long totalBranches;

    private long totalFaculties;

    private long totalStudentClasses;

    private long totalSubjects;

    private long totalRooms;

    private long totalApprovedTimetables;

    private long totalPendingTimetables;

    private long totalCompletedLectures;

    private double overallProgress;

}