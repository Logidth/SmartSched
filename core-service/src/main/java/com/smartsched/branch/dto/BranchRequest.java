package com.smartsched.branch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchRequest {

    @NotBlank(message = "Branch name is required")
    @Size(max = 50)
    private String name;

    @NotBlank(message = "Branch code is required")
    @Size(max = 10)
    private String code;

    @Size(max = 255)
    private String description;
}