package com.smartsched.scheduler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitTimetableRequest {

    @NotNull
    private Long studentClassId;

    @NotNull
    private Long academicYearId;

    // Remarks field - optional, can be blank
    private String remarks;
}