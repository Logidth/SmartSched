package com.smartsched.scheduler.service;

import com.smartsched.scheduler.dto.*;
import com.smartsched.scheduler.enums.WorkingDay;

import java.util.List;

public interface SchedulerService {

    TimetableGenerationResponse generateTimetable(
            GenerateTimetableRequest request
    );

    List<TimetableResponse> getTimetable(
            Long studentClassId,
            Long academicYearId
    );

    void deleteTimetable(
            Long studentClassId,
            Long academicYearId
    );

    /**
     * Deletes every timetable entry (across all classes and academic
     * years) belonging to the given branch. ADMIN callers may only
     * target their own department's branch; PRINCIPAL callers may
     * target any branch.
     */
    void deleteAllTimetablesForBranch(Long branchId);

    void submitTimetable(
            SubmitTimetableRequest request
    );

    List<FacultyTimetableResponse> getFacultyTimetable(
            Long facultyId
    );

    TimetableApprovalResponse approveTimetable(
            ApproveTimetableRequest request
    );

    TimetableValidationResponse validateTimetable(
            Long studentClassId,
            Long academicYearId
    );

    void approveTimetable(
            Long classId,
            String remarks
    );

    void rejectTimetable(
            Long classId,
            String remarks
    );

    void regenerateTimetable(
            Long classId
    );

    List<TimetableResponse> getClassTimetable(
            Long classId
    );

    List<TimetableResponse> getRoomTimetable(
            Long roomId
    );

    List<TimetableResponse> getDayTimetable(
            WorkingDay day
    );

    List<TimetableResponse> getDepartmentTimetable(
            Long branchId
    );

    List<TimetableResponse> getPendingTimetables();

    void hodApprove(
            HODApprovalRequest request
    );

    void hodReject(
            HODApprovalRequest request
    );

    /**
     * Timetables (grouped one row per class + academic year) that
     * this HOD has approved, most recently decided first.
     */
    List<HodTimetableHistoryResponse> getHodApprovedTimetables();

    /**
     * Timetables (grouped one row per class + academic year) that
     * this HOD has rejected, most recently decided first.
     */
    List<HodTimetableHistoryResponse> getHodRejectedTimetables();
}