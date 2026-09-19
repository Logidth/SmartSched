package com.smartsched.studentclass.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentClassResponse {

    private Long id;

    private Long branchId;

    private String branchName;

    private Integer year;

    private Integer semester;

    private String section;

    private Integer strength;

    private String status;

    private Long academicYearId;

    private String academicYear;

    private Long regulationId;

    private String regulation;




}