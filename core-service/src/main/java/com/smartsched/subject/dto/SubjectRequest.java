package com.smartsched.subject.dto;

import com.smartsched.common.enums.SubjectCategory;
import com.smartsched.common.enums.SubjectType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectRequest {

    @NotBlank(message = "Subject code is required.")
    @Size(max = 20)
    private String subjectCode;

    @NotBlank(message = "Subject name is required.")
    @Size(max = 150)
    private String subjectName;

    @NotNull(message = "Regulation is required.")
    private Long regulationId;

    @NotNull(message = "Credits are required.")
    @Min(1)
    private Integer credits;

    @NotNull(message = "Theory hours are required.")
    @Min(0)
    private Integer theoryHours;

    @NotNull(message = "Lab hours are required.")
    @Min(0)
    private Integer labHours;

    @NotNull(message = "Total hours are required.")
    @Min(1)
    private Integer totalHours;

    /**
     * Hours to schedule per week for this subject. Distinct from
     * totalHours (theoryHours + labHours). Used by the timetable
     * scheduler to determine remaining hours to allocate.
     */
    @NotNull(message = "Hours per week is required.")
    @Min(1)
    private Integer hoursPerWeek;

    @NotNull(message = "Subject type is required.")
    private SubjectType subjectType;

    @NotNull(message = "Requires lab room is required.")
    private Boolean requiresLabRoom;
    @NotNull(message = "Subject category is required.")
    private SubjectCategory subjectCategory;

}