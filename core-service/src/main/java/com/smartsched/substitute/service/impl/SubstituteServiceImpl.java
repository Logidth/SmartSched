package com.smartsched.substitute.service.impl;

import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.lecture.entity.LectureLog;
import com.smartsched.lecture.repository.LectureLogRepository;
import com.smartsched.leave.enums.LeaveStatus;
import com.smartsched.leave.repository.LeaveRequestRepository;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.repository.TimetableEntryRepository;
import com.smartsched.substitute.dto.AssignSubstituteRequest;
import com.smartsched.substitute.dto.SubstituteResponse;
import com.smartsched.substitute.entity.SubstituteAssignment;
import com.smartsched.substitute.mapper.SubstituteMapper;
import com.smartsched.substitute.repository.SubstituteAssignmentRepository;
import com.smartsched.substitute.service.SubstituteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubstituteServiceImpl
        implements SubstituteService {

    private final SubstituteAssignmentRepository substituteRepository;

    private final LectureLogRepository lectureRepository;

    private final FacultyRepository facultyRepository;

    private final LeaveRequestRepository leaveRepository;

    private final TimetableEntryRepository timetableRepository;

    private final SubstituteMapper mapper;

    @Override
    @Transactional
    public SubstituteResponse assignSubstitute(
            AssignSubstituteRequest request) {

        LectureLog lecture =
                lectureRepository.findById(
                                request.getLectureLogId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lecture Not Found"));

        Faculty substituteFaculty =
                facultyRepository.findById(
                                request.getSubstituteFacultyId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Faculty Not Found"));

        Faculty originalFaculty =
                lecture.getTimetableEntry()
                        .getFaculty();

        validateSameFaculty(
                originalFaculty,
                substituteFaculty
        );

        validateLeave(
                originalFaculty,
                lecture.getLectureDate()
        );

        validateDuplicate(
                lecture
        );

        validateTimetableConflict(
                substituteFaculty,
                lecture.getTimetableEntry(),
                lecture.getLectureDate()
        );

        SubstituteAssignment assignment =
                new SubstituteAssignment();

        assignment.setLectureLog(lecture);

        assignment.setOriginalFaculty(originalFaculty);

        assignment.setSubstituteFaculty(substituteFaculty);

        assignment.setAssignmentDate(
                lecture.getLectureDate());

        assignment.setReason(
                request.getReason());

        assignment.setAccepted(false);

        assignment.setAssignedAt(
                LocalDateTime.now());

        substituteRepository.save(
                assignment);

        return mapper.map(
                assignment);
    }

    private void validateSameFaculty(
            Faculty original,
            Faculty substitute) {

        if (original.getId()
                .equals(substitute.getId())) {

            throw new ResourceNotFoundException(
                    "Original and Substitute Faculty cannot be same.");
        }
    }

    private void validateLeave(
            Faculty faculty,
            LocalDate lectureDate) {

        boolean onLeave =
                leaveRepository
                        .existsByFacultyAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                faculty,
                                LeaveStatus.APPROVED,
                                lectureDate,
                                lectureDate);

        if (!onLeave) {

            throw new ResourceNotFoundException(
                    "Faculty is not on approved leave.");
        }
    }

    private void validateDuplicate(
            LectureLog lecture) {

        if (substituteRepository
                .findByLectureLog(lecture)
                .isPresent()) {

            throw new ResourceNotFoundException(
                    "Substitute already assigned.");
        }
    }

    private void validateTimetableConflict(
            Faculty faculty,
            TimetableEntry timetable,
            LocalDate lectureDate) {

        boolean conflict =
                timetableRepository
                        .existsByFacultyIdAndAcademicYearIdAndDayAndPeriodNumber(
                                faculty.getId(),
                                timetable.getAcademicYear().getId(),
                                timetable.getDay(),
                                timetable.getPeriodNumber()
                        );

        if (conflict) {

            throw new ResourceNotFoundException(
                    "Substitute Faculty already has another class.");
        }
    }

    @Override
    public SubstituteResponse getAssignment(
            Long id) {

        return mapper.map(

                substituteRepository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment Not Found"))
        );
    }

    @Override
    public List<SubstituteResponse> getFacultyAssignments(
            Long facultyId) {

        Faculty faculty =
                facultyRepository.findById(facultyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Faculty Not Found"));

        return substituteRepository
                .findBySubstituteFaculty(faculty)
                .stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<SubstituteResponse> getAllAssignments() {

        return substituteRepository
                .findAll()
                .stream()
                .map(mapper::map)
                .toList();
    }
}