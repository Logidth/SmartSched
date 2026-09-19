package com.smartsched.curriculumsubject.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurriculumSubjectRequest {

    @NotNull(message = "Curriculum is required.")
    private Long curriculumId;

    @NotNull(message = "Subject is required.")
    private Long subjectId;

    @NotNull(message = "Year is required.")
    @Min(1)
    @Max(4)
    private Integer year;

    @NotNull(message = "Semester is required.")
    @Min(1)
    @Max(8)
    private Integer semester;

    @NotNull(message = "Display order is required.")
    @Min(1)
    private Integer displayOrder;

    /**
     * Optional override for weekly scheduled hours in this curriculum.
     * When omitted, the subject's default hoursPerWeek is used.
     */
    @Min(1)
    private Integer hoursPerWeek;
}