package com.smartsched.branch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchResponse {

    private Long id;

    private String name;

    private String code;

    private String description;

    private boolean active;
}