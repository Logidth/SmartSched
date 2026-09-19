package com.smartsched.scheduler.dto;

import lombok.Data;

@Data
public class HODApprovalRequest {

    private Long studentClassId;

    private Long academicYearId;

    private String remarks;
}