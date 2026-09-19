package com.smartsched.curriculumsubject.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurriculumSubjectResponse {

    private Long id;

    private Long curriculumId;

    private Long subjectId;

    private String subjectCode;

    private String subjectName;

    private String subjectType;

    private Integer credits;

    private Integer year;

    private Integer semester;

    private Integer displayOrder;

    /** Effective override, if any, set on this curriculum subject. */
    private Integer hoursPerWeek;

    /** Effective hours/week actually used by the scheduler (override or subject default). */
    private Integer effectiveHoursPerWeek;

    private String status;
}