package com.smartsched.scheduler.engine;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.room.entity.Room;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;

public interface SchedulerValidator {

    /*boolean isAvailable(
            StudentClass studentClass,
            Faculty faculty,
            Room room,
            WorkingDay day,
            Integer period
    );*/

    /**
     * Checks if all resources are available at a given time slot.
     *
     * @param studentClass The student class
     * @param faculty The faculty member
     * @param room The room
     * @param academicYear The academic year
     * @param day The day of the week
     * @param period The period number
     * @return true if all resources are free, false otherwise
     */
    boolean isAvailable(
            StudentClass studentClass,
            Faculty faculty,
            Room room,
            AcademicYear academicYear,
            WorkingDay day,
            Integer period
    );

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
    boolean isRoomTypeCompatible(Subject subject, Room room);
}