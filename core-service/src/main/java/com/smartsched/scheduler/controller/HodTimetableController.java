package com.smartsched.scheduler.controller;

import com.smartsched.auth.entity.User;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.common.response.ApiResponse;
import com.smartsched.faculty.dto.FacultyResponse;
import com.smartsched.faculty.service.FacultyService;
import com.smartsched.leave.dto.LeaveApprovalRequest;
import com.smartsched.leave.dto.LeaveResponse;
import com.smartsched.leave.service.LeaveService;
import com.smartsched.scheduler.dto.HODApprovalRequest;
import com.smartsched.scheduler.dto.HodTimetableHistoryResponse;
import com.smartsched.scheduler.dto.TimetableResponse;
import com.smartsched.scheduler.service.SchedulerService;
import com.smartsched.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/hod/timetable")
@RequiredArgsConstructor
public class HodTimetableController {

    private final SchedulerService schedulerService;
    private final LeaveService leaveService;
    private final FacultyService facultyService;
    private final CurrentUserService currentUserService;

    /**
     * Helper: resolves the branch (department) id of the currently
     * logged-in HOD from their User -> Branch relationship.
     */
    private Long getCurrentHodBranchId() {

        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getBranch() == null) {
            throw new ResourceNotFoundException(
                    "No department/branch is associated with the current HOD account.");
        }

        return currentUser.getBranch().getId();
    }

    /**
     * Get all pending timetables for HOD approval
     */
    @GetMapping("/pending")
    public ApiResponse<List<TimetableResponse>> pending() {
        return new ApiResponse<>(
                true,
                "Pending Timetables",
                schedulerService.getPendingTimetables(),
                LocalDateTime.now()
        );
    }

    /**
     * HOD approves timetable submission from admin
     */
    @PutMapping("/approve")
    public ApiResponse<String> approve(
            @RequestBody HODApprovalRequest request) {

        schedulerService.hodApprove(request);

        return new ApiResponse<>(
                true,
                "Timetable Approved Successfully",
                "SUCCESS",
                LocalDateTime.now()
        );
    }

    /**
     * HOD rejects timetable submission from admin
     */
    @PutMapping("/reject")
    public ApiResponse<String> reject(
            @RequestBody HODApprovalRequest request) {

        schedulerService.hodReject(request);

        return new ApiResponse<>(
                true,
                "Timetable Rejected Successfully",
                "SUCCESS",
                LocalDateTime.now()
        );
    }

    /**
     * Timetables this HOD has approved, most recently decided first.
     * Grouped one entry per (class, academic year) - not one per
     * period - since that's the unit the HOD actually decided on.
     */
    @GetMapping("/approved")
    public ApiResponse<List<HodTimetableHistoryResponse>> approvedHistory() {
        return new ApiResponse<>(
                true,
                "Approved Timetables Retrieved Successfully",
                schedulerService.getHodApprovedTimetables(),
                LocalDateTime.now()
        );
    }

    /**
     * Timetables this HOD has rejected, most recently decided first.
     */
    @GetMapping("/rejected")
    public ApiResponse<List<HodTimetableHistoryResponse>> rejectedHistory() {
        return new ApiResponse<>(
                true,
                "Rejected Timetables Retrieved Successfully",
                schedulerService.getHodRejectedTimetables(),
                LocalDateTime.now()
        );
    }

    /**
     * HOD approves leave request for their department faculty
     */
    @PutMapping("/leave/approve")
    public ResponseEntity<ApiResponse<LeaveResponse>> approveLeave(
            @RequestBody LeaveApprovalRequest request) {

        LeaveResponse response = leaveService.approveLeave(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Leave Approved Successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    /**
     * HOD rejects leave request for their department faculty
     */
    @PutMapping("/leave/reject")
    public ResponseEntity<ApiResponse<LeaveResponse>> rejectLeave(
            @RequestBody LeaveApprovalRequest request) {

        LeaveResponse response = leaveService.rejectLeave(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Leave Rejected Successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    /**
     * Get all pending leave requests for HOD's department
     */
    @GetMapping("/leave/pending")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getPendingLeaves() {

        List<LeaveResponse> pendingLeaves = leaveService.getPendingLeaves();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Pending Leave Requests Retrieved Successfully",
                        pendingLeaves,
                        LocalDateTime.now()
                )
        );
    }

    /**
     * View all faculty in HOD's department
     */
    @GetMapping("/department/faculty")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getDepartmentFaculty() {

        Long branchId = getCurrentHodBranchId();

        List<FacultyResponse> facultyList = facultyService.getByBranch(branchId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Department Faculty Retrieved Successfully",
                        facultyList,
                        LocalDateTime.now()
                )
        );
    }

    /**
     * View specific faculty details in HOD's department
     */
    @GetMapping("/department/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<FacultyResponse>> getDepartmentFacultyDetails(
            @PathVariable Long facultyId) {

        FacultyResponse faculty = facultyService.getById(facultyId);

        Long hodBranchId = getCurrentHodBranchId();
        if (faculty.getBranchId() != null && !faculty.getBranchId().equals(hodBranchId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            false,
                            "Faculty does not belong to your department",
                            null,
                            LocalDateTime.now()
                    ));
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Faculty Details Retrieved Successfully",
                        faculty,
                        LocalDateTime.now()
                )
        );
    }

    /**
     * Get faculty leave history for HOD's department
     */
    @GetMapping("/department/faculty/{facultyId}/leaves")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getFacultyLeaves(
            @PathVariable Long facultyId) {

        FacultyResponse faculty = facultyService.getById(facultyId);

        Long hodBranchId = getCurrentHodBranchId();
        if (faculty.getBranchId() != null && !faculty.getBranchId().equals(hodBranchId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            false,
                            "Faculty does not belong to your department",
                            null,
                            LocalDateTime.now()
                    ));
        }

        List<LeaveResponse> leaves = leaveService.getFacultyLeaves(facultyId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Faculty Leaves Retrieved Successfully",
                        leaves,
                        LocalDateTime.now()
                )
        );
    }
}