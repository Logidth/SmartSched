package com.smartsched.leave.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.leave.dto.CreateLeaveRequest;
import com.smartsched.leave.dto.LeaveApprovalRequest;
import com.smartsched.leave.dto.LeaveResponse;
import com.smartsched.leave.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    public ApiResponse<LeaveResponse> applyLeave(
            @RequestBody CreateLeaveRequest request) {

        return new ApiResponse<>(
                true,
                "Leave Applied Successfully",
                leaveService.applyLeave(request),
                LocalDateTime.now()
        );
    }

    @PutMapping("/approve")
    public ApiResponse<LeaveResponse> approveLeave(
            @RequestBody LeaveApprovalRequest request) {

        return new ApiResponse<>(
                true,
                "Leave Approved Successfully",
                leaveService.approveLeave(request),
                LocalDateTime.now()
        );
    }

    @PutMapping("/reject")
    public ApiResponse<LeaveResponse> rejectLeave(
            @RequestBody LeaveApprovalRequest request) {

        return new ApiResponse<>(
                true,
                "Leave Rejected Successfully",
                leaveService.rejectLeave(request),
                LocalDateTime.now()
        );
    }

    @GetMapping("/{leaveId}")
    public ApiResponse<LeaveResponse> getLeave(
            @PathVariable Long leaveId) {

        return new ApiResponse<>(
                true,
                "Leave Retrieved Successfully",
                leaveService.getLeave(leaveId),
                LocalDateTime.now()
        );
    }

    @GetMapping("/faculty/{facultyId}")
    public ApiResponse<List<LeaveResponse>> getFacultyLeaves(
            @PathVariable Long facultyId) {

        return new ApiResponse<>(
                true,
                "Faculty Leaves Retrieved Successfully",
                leaveService.getFacultyLeaves(facultyId),
                LocalDateTime.now()
        );
    }

    @GetMapping("/pending")
    public ApiResponse<List<LeaveResponse>> getPendingLeaves() {

        return new ApiResponse<>(
                true,
                "Pending Leaves Retrieved Successfully",
                leaveService.getPendingLeaves(),
                LocalDateTime.now()
        );
    }

    @GetMapping
    public ApiResponse<List<LeaveResponse>> getAllLeaves() {

        return new ApiResponse<>(
                true,
                "All Leaves Retrieved Successfully",
                leaveService.getAllLeaves(),
                LocalDateTime.now()
        );
    }

}