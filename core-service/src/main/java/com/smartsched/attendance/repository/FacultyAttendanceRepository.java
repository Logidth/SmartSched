package com.smartsched.attendance.repository;

import com.smartsched.attendance.entity.FacultyAttendance;
import com.smartsched.attendance.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacultyAttendanceRepository
        extends JpaRepository<FacultyAttendance, Long> {

    Optional<FacultyAttendance> findByFacultyIdAndAttendanceDate(
            Long facultyId,
            LocalDate attendanceDate
    );

    List<FacultyAttendance> findByFacultyIdOrderByAttendanceDateDesc(
            Long facultyId
    );

    List<FacultyAttendance> findByAttendanceDate(
            LocalDate attendanceDate
    );

    long countByFacultyIdAndStatus(
            Long facultyId,
            AttendanceStatus status
    );

    boolean existsByFacultyIdAndAttendanceDate(
            Long facultyId,
            LocalDate attendanceDate
    );

    @Query("""
SELECT COUNT(f)
FROM FacultyAttendance f
WHERE f.attendanceDate = CURRENT_DATE
AND f.status = com.smartsched.attendance.enums.AttendanceStatus.PRESENT
""")
    long countPresentToday();

}