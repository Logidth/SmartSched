package com.smartsched.studentclass.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentClassRequest {

    @NotNull(message = "Branch id is required")
    private Long branchId;

    @NotNull
    @Min(value = 1)
    @Max(value = 4)
    private Integer year;

    @NotNull
    @Min(value = 1)
    @Max(value = 8)
    private Integer semester;

    @NotBlank
    private String section;

    @NotNull
    @Min(value = 1)
    private Integer strength;

    @NotNull
    private Long academicYearId;

    @NotNull
    private Long regulationId;



}