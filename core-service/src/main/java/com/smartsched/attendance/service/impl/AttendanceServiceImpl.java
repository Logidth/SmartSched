package com.smartsched.attendance.service.impl;

import com.smartsched.attendance.dto.AttendanceResponse;
import com.smartsched.attendance.dto.MarkAttendanceRequest;
import com.smartsched.attendance.entity.FacultyAttendance;
import com.smartsched.attendance.enums.AttendanceStatus;
import com.smartsched.attendance.mapper.FacultyAttendanceMapper;
import com.smartsched.attendance.repository.FacultyAttendanceRepository;
import com.smartsched.attendance.service.AttendanceService;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.leave.enums.LeaveStatus;
import com.smartsched.leave.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl
        implements AttendanceService {

    private final FacultyAttendanceRepository attendanceRepository;

    private final FacultyRepository facultyRepository;

    private final LeaveRequestRepository leaveRepository;

    private final FacultyAttendanceMapper mapper;

    private Faculty getFaculty(Long id) {

        return facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Faculty Not Found"));

    }

    private FacultyAttendance getAttendanceEntity(Long id) {

        return attendanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance Not Found"));

    }

    @Override
    @Transactional
    public AttendanceResponse markAttendance(
            MarkAttendanceRequest request) {

        Faculty faculty =
                getFaculty(request.getFacultyId());

        LocalDate date =
                request.getAttendanceDate();

        if (date == null) {

            date = LocalDate.now();

        }

        if (attendanceRepository
                .existsByFacultyIdAndAttendanceDate(
                        faculty.getId(),
                        date)) {

            throw new ResourceNotFoundException(
                    "Attendance already marked.");

        }

        AttendanceStatus status =
                request.getStatus();

        boolean onLeave =
                leaveRepository
                        .existsByFacultyAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                faculty,
                                LeaveStatus.APPROVED,
                                date,
                                date
                        );

        if (onLeave) {

            status = AttendanceStatus.ON_LEAVE;

        }

        FacultyAttendance attendance =
                new FacultyAttendance();

        attendance.setFaculty(faculty);

        attendance.setAttendanceDate(date);

        attendance.setStatus(status);

        attendance.setRemarks(
                request.getRemarks());

        attendance =
                attendanceRepository.save(attendance);

        return mapper.map(attendance);

    }

    @Override
    public AttendanceResponse getAttendance(
            Long id) {

        return mapper.map(
                getAttendanceEntity(id));

    }

    @Override
    public List<AttendanceResponse> getFacultyAttendance(
            Long facultyId) {

        return attendanceRepository
                .findByFacultyIdOrderByAttendanceDateDesc(
                        facultyId)
                .stream()
                .map(mapper::map)
                .toList();

    }

    @Override
    public List<AttendanceResponse> getTodayAttendance() {

        return attendanceRepository
                .findByAttendanceDate(
                        LocalDate.now())
                .stream()
                .map(mapper::map)
                .toList();

    }

}