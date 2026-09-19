package com.smartsched.scheduler.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimetableValidationResponse {

    private int facultyConflicts;

    private int roomConflicts;

    private int classConflicts;

    private int missingHours;

    private int extraHours;

    private boolean valid;

}