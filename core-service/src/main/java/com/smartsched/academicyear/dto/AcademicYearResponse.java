package com.smartsched.academicyear.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcademicYearResponse {

    private Long id;

    private String name;

    private String status;
}