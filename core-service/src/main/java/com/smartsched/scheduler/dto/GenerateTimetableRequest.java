package com.smartsched.scheduler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerateTimetableRequest {

    @NotNull
    private Long academicYearId;

    @NotNull
    private Long studentClassId;

}