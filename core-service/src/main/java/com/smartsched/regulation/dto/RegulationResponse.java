package com.smartsched.regulation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegulationResponse {

    private Long id;

    private String code;

    private String description;

    private String status;
}