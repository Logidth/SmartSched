package com.smartsched.faculty.controller;

import com.smartsched.auth.entity.User;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.common.response.ApiResponse;
import com.smartsched.common.util.ResponseUtil;
import com.smartsched.dashboard.dto.FacultyDashboardResponse;
import com.smartsched.dashboard.service.DashboardService;
import com.smartsched.facultyassignment.dto.FacultyAssignmentResponse;
import com.smartsched.facultyassignment.service.FacultyAssignmentService;
import com.smartsched.faculty.dto.FacultyResponse;
import com.smartsched.faculty.service.FacultyService;
import com.smartsched.leave.dto.CreateLeaveRequest;
import com.smartsched.leave.dto.LeaveResponse;
import com.smartsched.leave.service.LeaveService;
import com.smartsched.scheduler.dto.FacultyTimetableResponse;
import com.smartsched.scheduler.service.SchedulerService;
import com.smartsched.security.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Faculty self-service endpoints.
 *
 * Unlike the Admin/Principal/HOD controllers (which take an id in
 * the path), every endpoint here resolves "who am I" from the JWT
 * via {@link CurrentUserService} — a faculty member can never pass
 * a facultyId and see someone else's data.
 *
 * Mounted at /api/faculty/**, which SecurityConfig restricts to
 * hasRole("FACULTY").
 */
@RestController
@RequestMapping("/api/faculty/me")
@RequiredArgsConstructor
public class FacultySelfController {

    private final CurrentUserService currentUserService;
    private final FacultyService facultyService;
    private final FacultyAssignmentService facultyAssignmentService;
    private final SchedulerService schedulerService;
    private final LeaveService leaveService;
    private final DashboardService dashboardService;

    /**
     * Resolves the Faculty id linked to the currently logged-in
     * User. Throws if this account has no linked faculty record
     * (e.g. it was created without one, or the wrong role logged in).
     */
    private Long getCurrentFacultyId() {

        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getFaculty() == null) {
            throw new ResourceNotFoundException(
                    "No faculty profile is linked to this account.");
        }

        return currentUser.getFaculty().getId();
    }

    /**
     * Own profile — name, employee id, department, designation, etc.
     */
    @GetMapping
    public ApiResponse<FacultyResponse> getMyProfile() {

        FacultyResponse response =
                facultyService.getById(getCurrentFacultyId());

        return ResponseUtil.success(
                "Faculty Profile Retrieved Successfully",
                response
        );
    }

    /**
     * Subjects/classes currently allocated to this faculty member.
     */
    @GetMapping("/assignments")
    public ApiResponse<List<FacultyAssignmentResponse>> getMyAssignments() {

        List<FacultyAssignmentResponse> response =
                facultyAssignmentService.getAssignmentsByFaculty(
                        getCurrentFacultyId()
                );

        return ResponseUtil.success(
                "My Subject Assignments Retrieved Successfully",
                response
        );
    }

    /**
     * This faculty member's own weekly timetable.
     */
    @GetMapping("/timetable")
    public ApiResponse<List<FacultyTimetableResponse>> getMyTimetable() {

        List<FacultyTimetableResponse> response =
                schedulerService.getFacultyTimetable(
                        getCurrentFacultyId()
                );

        return ResponseUtil.success(
                "My Timetable Retrieved Successfully",
                response
        );
    }

    /**
     * Dashboard summary — assigned subjects, weekly periods,
     * completed/pending lectures, completion percentage.
     */
    @GetMapping("/dashboard")
    public ApiResponse<FacultyDashboardResponse> getMyDashboard() {

        FacultyDashboardResponse response =
                dashboardService.facultyDashboard(
                        getCurrentFacultyId()
                );

        return ResponseUtil.success(
                "My Dashboard Retrieved Successfully",
                response
        );
    }

    /**
     * This faculty member's own leave request history.
     */
    @GetMapping("/leaves")
    public ApiResponse<List<LeaveResponse>> getMyLeaves() {

        List<LeaveResponse> response =
                leaveService.getFacultyLeaves(
                        getCurrentFacultyId()
                );

        return ResponseUtil.success(
                "My Leave Requests Retrieved Successfully",
                response
        );
    }

    /**
     * Apply for leave. facultyId is always overwritten with the
     * caller's own id — whatever the client sends (or omits) is
     * ignored, so a faculty member can never apply leave on
     * someone else's behalf.
     */
    @PostMapping("/leaves")
    public ApiResponse<LeaveResponse> applyLeave(
            @Valid @RequestBody CreateLeaveRequest request) {

        request.setFacultyId(getCurrentFacultyId());

        LeaveResponse response = leaveService.applyLeave(request);

        return ResponseUtil.success(
                "Leave Applied Successfully",
                response
        );
    }
}