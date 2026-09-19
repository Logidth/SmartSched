package com.smartsched.lecture.service.impl;

import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.leave.enums.LeaveStatus;
import com.smartsched.leave.repository.LeaveRequestRepository;
import com.smartsched.lecture.dto.LectureCancelRequest;
import com.smartsched.lecture.dto.LectureCompleteRequest;
import com.smartsched.lecture.dto.LectureLogResponse;
import com.smartsched.lecture.dto.LectureStartRequest;
import com.smartsched.lecture.entity.LectureLog;
import com.smartsched.lecture.enums.LectureStatus;
import com.smartsched.lecture.mapper.LectureLogMapper;
import com.smartsched.lecture.repository.LectureLogRepository;
import com.smartsched.lecture.service.LectureLogService;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.repository.TimetableEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureLogServiceImpl implements LectureLogService {

    private final LectureLogRepository lectureLogRepository;
    private final LectureLogMapper mapper;
    private final TimetableEntryRepository timetableEntryRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    private TimetableEntry getTimetable(Long id) {

        return timetableEntryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Timetable Entry Not Found"));

    }

    private LectureLog findLecture(Long id) {
        return lectureLogRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lecture Not Found"));
    }

    @Override
    public LectureLogResponse getLecture(Long id) {
        return mapper.map(findLecture(id));
    }

    @Override
    public List<LectureLogResponse> getTodaysLectures() {
        return lectureLogRepository
                .findByLectureDate(LocalDate.now())
                .stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<LectureLogResponse> getAllLectures() {
        return lectureLogRepository
                .findAll()
                .stream()
                .map(mapper::map)
                .toList();
    }


    @Override
    public LectureLogResponse startLecture(LectureStartRequest request) {

        TimetableEntry timetable =
                getTimetable(request.getTimetableEntryId());


        validateTimetable(timetable);

        validateLectureDate(timetable);

        validateCancelled(timetable);

        validateFacultyLeave(timetable);

        validateDuplicate(timetable);

        LectureLog lecture = new LectureLog();

        lecture.setTimetableEntry(timetable);
        lecture.setLectureDate(request.getLectureDate());
        lecture.setStartTime(request.getStartTime());
        lecture.setStatus(LectureStatus.IN_PROGRESS);

        lecture = lectureLogRepository.save(lecture);

        return mapper.map(lecture);
    }
    @Override
    public LectureLogResponse completeLecture(LectureCompleteRequest request) {

        LectureLog lecture = findLecture(request.getLectureLogId());

        if (lecture.getStatus() == LectureStatus.COMPLETED) {
            throw new ResourceNotFoundException("Lecture already completed.");
        }

        lecture.setEndTime(request.getEndTime());
        lecture.setTopicsCovered(request.getTopicsCovered());
        lecture.setRemarks(request.getRemarks());
        lecture.setStatus(LectureStatus.COMPLETED);

        lecture = lectureLogRepository.save(lecture);

        return mapper.map(lecture);
    }


    @Override
    public LectureLogResponse cancelLecture(LectureCancelRequest request) {

        LectureLog lecture = findLecture(request.getLectureLogId());

        if (lecture.getStatus() == LectureStatus.COMPLETED) {
            throw new ResourceNotFoundException(
                    "Completed lecture cannot be cancelled.");
        }

        lecture.setStatus(LectureStatus.CANCELLED);
        lecture.setRemarks(request.getRemarks());

        lecture = lectureLogRepository.save(lecture);

        return mapper.map(lecture);
    }
    private void validateFacultyLeave(
            TimetableEntry timetable
    ) {

        boolean onLeave =
                leaveRequestRepository
                        .existsByFacultyAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                timetable.getFaculty(),
                                LeaveStatus.APPROVED,
                                LocalDate.now(),
                                LocalDate.now()
                        );

        if (onLeave) {

            throw new ResourceNotFoundException(
                    timetable.getFaculty().getName()
                            + " is on approved leave."
            );

        }

    }


    private void validateTimetable(TimetableEntry timetable) {

        if (!Boolean.TRUE.equals(timetable.getApproved())) {
            throw new ResourceNotFoundException("Timetable is not approved.");
        }

    }
    private void validateLectureDate(TimetableEntry timetable) {

        // Placeholder for future validation
        // Example: ensure today's day matches timetable.getDay()

    }
    private void validateCancelled(TimetableEntry timetable) {

        // Placeholder for future cancellation checks
    }

    private void validateDuplicate(TimetableEntry timetable) {

        if (lectureLogRepository.existsByTimetableEntryAndLectureDate(
                timetable,
                LocalDate.now())) {

            throw new ResourceNotFoundException("Lecture already started today.");
        }

    }
}