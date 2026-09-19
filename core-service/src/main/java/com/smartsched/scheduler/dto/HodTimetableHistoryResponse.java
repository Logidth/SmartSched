package com.smartsched.scheduler.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * One row per (studentClass, academicYear) timetable that this HOD
 * has already approved or rejected - NOT one row per period. A single
 * timetable is made up of many TimetableEntry rows (one per
 * day/period), all sharing the same decision, so they're grouped
 * together for this history view.
 */
@Data
@Builder
public class HodTimetableHistoryResponse {

    private Long studentClassId;

    private Long academicYearId;

    private String className;

    private String academicYear;

    private String status;

    private String remarks;

    private LocalDateTime decidedAt;

    private Integer periodCount;
}
