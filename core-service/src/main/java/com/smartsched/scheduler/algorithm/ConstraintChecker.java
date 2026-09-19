package com.smartsched.scheduler.algorithm;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.facultyavailability.repository.FacultyAvailabilityRepository;
import com.smartsched.room.entity.Room;
import com.smartsched.scheduler.engine.SchedulerState;
import com.smartsched.scheduler.engine.SchedulerValidator;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConstraintChecker {
    private final FacultyAvailabilityRepository availabilityRepository;
    private final SchedulerValidator validator;

    /**
     * Main method to check if allocation is possible.
     * Validates:
     * - Room type compatibility with subject type
     * - Faculty availability
     * - Resource availability (class, faculty, room)
     *
     * @param studentClass The student class
     * @param faculty The faculty member
     * @param room The room to allocate
     * @param day The day of the week
     * @param period The period number
     * @param state Current scheduler state
     * @param subject The subject to schedule
     * @return true if allocation is possible, false otherwise
     */
    public boolean canAllocate(
            StudentClass studentClass,
            Faculty faculty,
            Room room,
            WorkingDay day,
            int period,
            SchedulerState state,
            Subject subject
    ) {

        // FIX: Check room type compatibility with subject type
        // This prevents theory subjects from being allocated to lab rooms and vice versa
        if (!validator.isRoomTypeCompatible(subject, room)) {
            System.out.println(
                    "[REJECT] Room Type Incompatible | "
                            + subject.getSubjectCode()
                            + " | "
                            + "Subject requires Lab: " + subject.getRequiresLabRoom()
                            + " | Room Type: " + room.getRoomType()
            );
            return false;
        }

        // Check faculty availability
        if (availabilityRepository
                .existsByFacultyIdAndDayAndPeriodAndAvailableFalse(
                        faculty.getId(),
                        day,
                        period
                )) {
            return false;
        }

        // Check resource availability
        return state.isClassFree(
                studentClass.getId(),
                day,
                period
        )
                && state.isFacultyFree(
                faculty.getId(),
                day,
                period
        )
                && state.isRoomFree(
                room.getId(),
                day,
                period
        );
    }

    /**
     * Backward compatibility method (without subject parameter).
     * Used when subject type validation is not available.
     * Note: This method does NOT validate room type compatibility.
     *
     * @param studentClass The student class
     * @param faculty The faculty member
     * @param room The room to allocate
     * @param day The day of the week
     * @param period The period number
     * @param state Current scheduler state
     * @return true if resources are available, false otherwise
     */
    public boolean canAllocate(
            StudentClass studentClass,
            Faculty faculty,
            Room room,
            WorkingDay day,
            int period,
            SchedulerState state
    ) {
        // Check faculty availability
        if (availabilityRepository
                .existsByFacultyIdAndDayAndPeriodAndAvailableFalse(
                        faculty.getId(),
                        day,
                        period
                )) {
            return false;
        }

        // Check resource availability
        return state.isClassFree(
                studentClass.getId(),
                day,
                period
        )
                && state.isFacultyFree(
                faculty.getId(),
                day,
                period
        )
                && state.isRoomFree(
                room.getId(),
                day,
                period
        );
    }
}