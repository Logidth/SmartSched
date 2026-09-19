package com.smartsched.leave.mapper;

import com.smartsched.leave.dto.LeaveResponse;
import com.smartsched.leave.entity.LeaveRequest;
import org.springframework.stereotype.Component;

@Component
public class LeaveMapper {

    public LeaveResponse map(LeaveRequest leave) {

        return LeaveResponse.builder()
                .id(leave.getId())
                .faculty(leave.getFaculty().getName())
                .department(
                        leave.getFaculty()
                                .getBranch()
                                .getName()
                )
                .leaveType(leave.getLeaveType())
                .fromDate(leave.getFromDate())
                .toDate(leave.getToDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .approvalRemarks(leave.getApprovalRemarks())
                .build();
    }
}