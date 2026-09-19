package com.smartsched.leave.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveApprovalRequest {

    private Long leaveRequestId;

    private Boolean approved;

    private String remarks;

}