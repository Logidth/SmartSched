package com.smartsched.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryResponse {

    private long facultyCount;

    private long branchCount;

    private long subjectCount;

    private long classCount;

    private long pendingLeaves;

    private long pendingTimetables;

    private long todayAttendance;

}