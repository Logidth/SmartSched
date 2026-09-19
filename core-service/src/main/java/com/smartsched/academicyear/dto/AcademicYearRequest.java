package com.smartsched.academicyear.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcademicYearRequest {

    @NotBlank(message = "Academic year is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{4}$",
            message = "Format must be YYYY-YYYY"
    )
    private String name;
}