package com.smartsched.attendance.entity;

import com.smartsched.attendance.enums.AttendanceStatus;
import com.smartsched.common.entity.BaseEntity;
import com.smartsched.faculty.entity.Faculty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "faculty_attendance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "faculty_id",
                        "attendance_date"
                })
        }
)
public class FacultyAttendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(length = 500)
    private String remarks;

}