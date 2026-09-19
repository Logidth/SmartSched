package com.smartsched.curriculum.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurriculumResponse {

    private Long id;

    private Long branchId;

    private String branch;

    private Long regulationId;

    private String regulation;

    private Long academicYearId;

    private String academicYear;

    private String status;

}