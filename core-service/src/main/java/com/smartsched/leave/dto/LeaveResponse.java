package com.smartsched.leave.dto;

import com.smartsched.leave.enums.LeaveStatus;
import com.smartsched.leave.enums.LeaveType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class LeaveResponse {

    private Long id;

    private String faculty;

    private String department;

    private LeaveType leaveType;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;

    private LeaveStatus status;

    private String approvalRemarks;

}