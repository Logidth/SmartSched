package com.smartsched.attendance.mapper;

import com.smartsched.attendance.dto.AttendanceResponse;
import com.smartsched.attendance.entity.FacultyAttendance;
import org.springframework.stereotype.Component;

@Component
public class FacultyAttendanceMapper {

    public AttendanceResponse map(FacultyAttendance attendance) {

        return AttendanceResponse.builder()

                .id(attendance.getId())

                .faculty(
                        attendance.getFaculty().getName())

                .department(
                        attendance.getFaculty()
                                .getBranch()
                                .getName())

                .attendanceDate(
                        attendance.getAttendanceDate())

                .status(
                        attendance.getStatus().name())

                .remarks(
                        attendance.getRemarks())

                .build();
    }
}