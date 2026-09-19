package com.smartsched.block.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockRequest {

    @NotBlank
    private String name;

    private String description;

}