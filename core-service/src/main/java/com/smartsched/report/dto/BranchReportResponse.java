package com.smartsched.report.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BranchReportResponse {

    private Long branchId;

    private String branchName;

    private String branchCode;

    private long facultyCount;

    private long classCount;

    private long subjectCount;

    private long roomCount;

    private long approvedTimetables;

    private long pendingTimetables;

    private long completedLectures;

    private long totalLectures;

    private double completionPercentage;
}
