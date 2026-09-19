package com.smartsched.subject.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectResponse {

    private Long id;

    private String subjectCode;

    private String subjectName;

    private Long regulationId;

    private String regulation;

    private Integer credits;

    private Integer theoryHours;

    private Integer labHours;

    private Integer totalHours;

    private Integer hoursPerWeek;

    private String subjectType;

    private Boolean requiresLabRoom;

    private String status;
    private String subjectCategory;

}