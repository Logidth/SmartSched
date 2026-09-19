package com.smartsched.attendance.dto;

import com.smartsched.attendance.enums.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MarkAttendanceRequest {

    private Long facultyId;

    private LocalDate attendanceDate;

    private AttendanceStatus status;

    private String remarks;

}