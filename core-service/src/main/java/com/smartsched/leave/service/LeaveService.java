package com.smartsched.leave.service;

import com.smartsched.leave.dto.CreateLeaveRequest;
import com.smartsched.leave.dto.LeaveApprovalRequest;
import com.smartsched.leave.dto.LeaveResponse;

import java.util.List;

public interface LeaveService {

    LeaveResponse applyLeave(
            CreateLeaveRequest request
    );

    LeaveResponse approveLeave(
            LeaveApprovalRequest request
    );

    LeaveResponse rejectLeave(
            LeaveApprovalRequest request
    );

    LeaveResponse getLeave(
            Long id
    );

    List<LeaveResponse> getFacultyLeaves(
            Long facultyId
    );

    List<LeaveResponse> getPendingLeaves();

    List<LeaveResponse> getAllLeaves();

    // ============================================================
    // HOD (department-scoped) variants — restrict every read/write
    // to leave requests raised by faculty within the given branch,
    // so an HOD can never see or act on another department's leaves.
    // ============================================================

    List<LeaveResponse> getPendingLeavesForBranch(
            Long branchId
    );

    LeaveResponse approveLeaveForBranch(
            LeaveApprovalRequest request,
            Long branchId
    );

    LeaveResponse rejectLeaveForBranch(
            LeaveApprovalRequest request,
            Long branchId
    );

}