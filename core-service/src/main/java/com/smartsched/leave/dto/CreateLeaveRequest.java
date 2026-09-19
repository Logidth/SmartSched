package com.smartsched.leave.dto;

import com.smartsched.leave.enums.LeaveType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateLeaveRequest {

    private Long facultyId;

    private LeaveType leaveType;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;

}