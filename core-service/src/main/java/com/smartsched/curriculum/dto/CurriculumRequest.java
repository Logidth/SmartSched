package com.smartsched.curriculum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurriculumRequest {

    @NotNull(message = "Branch is required.")
    private Long branchId;

    @NotNull(message = "Regulation is required.")
    private Long regulationId;

    @NotNull(message = "Academic year is required.")
    private Long academicYearId;

}