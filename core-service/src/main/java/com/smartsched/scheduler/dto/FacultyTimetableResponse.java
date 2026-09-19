package com.smartsched.scheduler.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FacultyTimetableResponse {

    private String day;

    private Integer period;

    private String subject;

    private String branch;

    private Integer year;

    private Integer semester;

    private String section;

    private String room;

}