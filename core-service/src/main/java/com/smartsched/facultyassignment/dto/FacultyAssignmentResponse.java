package com.smartsched.facultyassignment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FacultyAssignmentResponse {

    private Long id;

    private Long studentClassId;

    private String branch;

    private Integer year;

    private Integer semester;

    private String section;

    private Long curriculumSubjectId;

    private String subjectCode;

    private String subjectName;

    private Long facultyId;

    private String facultyName;

    private String employeeId;

}