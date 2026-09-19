package com.smartsched.regulation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegulationRequest {

    @NotBlank
    private String code;

    private String description;
}