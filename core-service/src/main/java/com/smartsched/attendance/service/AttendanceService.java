package com.smartsched.attendance.service;

import com.smartsched.attendance.dto.AttendanceResponse;
import com.smartsched.attendance.dto.MarkAttendanceRequest;

import java.util.List;

public interface AttendanceService {

    AttendanceResponse markAttendance(
            MarkAttendanceRequest request
    );

    AttendanceResponse getAttendance(
            Long id
    );

    List<AttendanceResponse> getFacultyAttendance(
            Long facultyId
    );

    List<AttendanceResponse> getTodayAttendance();

}