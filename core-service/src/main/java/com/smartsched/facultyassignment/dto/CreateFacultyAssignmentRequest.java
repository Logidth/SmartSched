package com.smartsched.facultyassignment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFacultyAssignmentRequest {

    @NotNull(message = "Student Class Id is required")
    private Long studentClassId;

    @NotNull(message = "Curriculum Subject Id is required")
    private Long curriculumSubjectId;

    @NotNull(message = "Faculty Id is required")
    private Long facultyId;
    private Integer priority;

}