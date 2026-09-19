package com.smartsched.scheduler.engine;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.common.enums.RoomType;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.room.entity.Room;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.scheduler.repository.TimetableEntryRepository;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerValidatorImpl
        implements SchedulerValidator {

    private final TimetableEntryRepository timetableEntryRepository;

    @Override
    public boolean isAvailable(
            StudentClass studentClass,
            Faculty faculty,
            Room room,
            AcademicYear academicYear,
            WorkingDay day,
            Integer period
    ) {

        if (timetableEntryRepository
                .existsByStudentClassIdAndAcademicYearIdAndDayAndPeriodNumber(
                        studentClass.getId(),
                        academicYear.getId(),
                        day,
                        period
                )) {

            return false;
        }

        if (timetableEntryRepository
                .existsByFacultyIdAndAcademicYearIdAndDayAndPeriodNumber(
                        faculty.getId(),
                        academicYear.getId(),
                        day,
                        period
                )) {

            return false;
        }

        if (timetableEntryRepository
                .existsByRoomIdAndAcademicYearIdAndDayAndPeriodNumber(
                        room.getId(),
                        academicYear.getId(),
                        day,
                        period
                )) {

            return false;
        }

        return true;
    }

    /**
     * Validates that room type is compatible with subject type.
     * This ensures:
     * - Theory subjects are allocated to classroom/seminar rooms
     * - Lab subjects are allocated to lab/project lab rooms
     *
     * @param subject The subject to be scheduled
     * @param room The room to allocate
     * @return true if room type matches subject requirements, false otherwise
     */
    public boolean isRoomTypeCompatible(Subject subject, Room room) {

        if (subject == null || room == null) {
            return false;
        }

        // If subject requires a lab room
        if (subject.getRequiresLabRoom()) {
            // Room must be LAB or PROJECT_LAB type
            return room.getRoomType() == RoomType.LAB
                    || room.getRoomType() == RoomType.PROJECT_LAB;
        }

        // For theory subjects, room must NOT be a lab
        // Valid rooms: CLASSROOM, SEMINAR_HALL
        return room.getRoomType() != RoomType.LAB
                && room.getRoomType() != RoomType.PROJECT_LAB;
    }
}