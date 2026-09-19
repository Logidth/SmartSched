package com.smartsched.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class HodDashboardResponse {

    private String branch;

    private long facultyCount;

    private long classCount;

    private long subjectCount;

    private long approvedTimetables;

    private long pendingTimetables;

    private double completionPercentage;

}