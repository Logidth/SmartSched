package com.smartsched.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AttendanceResponse {

    private Long id;

    private String faculty;

    private String department;

    private LocalDate attendanceDate;

    private String status;

    private String remarks;

}