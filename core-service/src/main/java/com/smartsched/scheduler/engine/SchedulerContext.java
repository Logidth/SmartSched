package com.smartsched.scheduler.engine;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import com.smartsched.room.entity.Room;
import com.smartsched.studentclass.entity.StudentClass;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SchedulerContext {

    private StudentClass studentClass;

    private AcademicYear academicYear;

    private List<FacultyAssignment> assignments;

    private List<Room> rooms;

    private int workingDays;

    private int periodsPerDay;

    private SchedulerState state;
}