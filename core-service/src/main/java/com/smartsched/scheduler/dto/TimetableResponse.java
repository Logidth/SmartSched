package com.smartsched.scheduler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimetableResponse {

    private Long id;

    private Long studentClassId;

    private Long academicYearId;

    private String className;

    private String subject;

    private String faculty;

    private String room;

    private String day;

    private Integer periodNumber;

    private String academicYear;

    private Boolean approved;

}