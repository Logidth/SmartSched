package com.smartsched.attendance.controller;

import com.smartsched.attendance.dto.AttendanceResponse;
import com.smartsched.attendance.dto.MarkAttendanceRequest;
import com.smartsched.attendance.service.AttendanceService;
import com.smartsched.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    public ApiResponse<AttendanceResponse> markAttendance(
            @RequestBody MarkAttendanceRequest request) {

        return new ApiResponse<>(

                true,

                "Attendance Marked Successfully",

                attendanceService.markAttendance(request),

                LocalDateTime.now()

        );

    }

    @GetMapping("/{id}")
    public ApiResponse<AttendanceResponse> getAttendance(
            @PathVariable Long id) {

        return new ApiResponse<>(

                true,

                "Attendance Retrieved Successfully",

                attendanceService.getAttendance(id),

                LocalDateTime.now()

        );

    }

    @GetMapping("/faculty/{facultyId}")
    public ApiResponse<List<AttendanceResponse>> getFacultyAttendance(
            @PathVariable Long facultyId) {

        return new ApiResponse<>(

                true,

                "Faculty Attendance Retrieved Successfully",

                attendanceService.getFacultyAttendance(facultyId),

                LocalDateTime.now()

        );

    }

    @GetMapping("/today")
    public ApiResponse<List<AttendanceResponse>> getTodayAttendance() {

        return new ApiResponse<>(

                true,

                "Today's Attendance Retrieved Successfully",

                attendanceService.getTodayAttendance(),

                LocalDateTime.now()

        );

    }

}