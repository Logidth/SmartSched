package com.smartsched.report.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeaveReportResponse {

    private long totalRequests;

    private long pending;

    private long approved;

    private long rejected;
}
